package com.free.bsf.jarprotect.core;

import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;
import com.free.bsf.jarprotect.core.base.JarFileInfo;
import com.free.bsf.jarprotect.core.encrypt.EncryptFactory;
import com.free.bsf.jarprotect.core.encrypt.EncryptTime;
import com.free.bsf.jarprotect.core.encrypt.IEncrypt;
import com.free.bsf.jarprotect.core.util.*;
import com.free.bsf.jarprotect.tool.Agent;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.jar.Manifest;

public class Encode {
    public static void run(String from, String to, String excludeClassCondition, String includeJarCondition,String includeConfigCondition) {
        String targetDir = CommonUtils.gtJarUnJarPath(from);
        List<JarFileInfo> files = JarUtils.unJar(1,from, targetDir, includeJarCondition);
        LogUtils.info("jar释放完毕,开始加密....");
        String manifestPath = new File(targetDir, "META-INF/MANIFEST.MF").getAbsolutePath();
        Manifest manifest = ManifestUtils.read(manifestPath);
        ClassPool pool = Context.Default.Pool;
        CommonUtils.loadClassPath(pool, files);
        for (JarFileInfo file : files) {
            String fileNameLower= file.getFileName().toLowerCase();
            if (fileNameLower.endsWith(".class")) {
                String fileNameNoExt = FileUtils.getFileNameWithoutSuffix(file.getFileName());
                if (StringUtils.hitCondition(excludeClassCondition, fileNameNoExt)) {
                    LogUtils.info("跳过类:"+file.getClassName());
                    continue;
                }
                String newFile = CommonUtils.getClassEncodePath(targetDir, file.getClassName());
                FileUtils.deleteFile(newFile);
                FileUtils.createDirectory(newFile);
                byte[] data = FileUtils.toBytes(new File(file.FilePath));
                FileUtils.saveStream(EncryptFactory.get().e(data), newFile);
                clearMethod(pool, file);
                LogUtils.info("加密类:"+file.getClassName());
            }else if(file.Level==1&&(fileNameLower.endsWith(".yml")||fileNameLower.endsWith(".properties")||fileNameLower.endsWith(".xml"))){
                String fileName = FileUtils.getFileName(file.getFileName());
                if (!StringUtils.hitCondition(includeConfigCondition, fileName)) {
                    LogUtils.info("跳过配置:"+fileName);
                    continue;
                }
                encodeSpringConfig(targetDir,pool);
                String newFile = CommonUtils.getClassEncodePath(targetDir, file.getFileName());
                FileUtils.deleteFile(newFile);
                FileUtils.createDirectory(newFile);
                byte[] data = FileUtils.toBytes(new File(file.FilePath));
                FileUtils.saveStream(EncryptFactory.get().e(data), newFile);
                FileUtils.writeAllText(file.FilePath,"");
                LogUtils.info("加密配置:"+file.getFileName());
            }
        }
        LogUtils.info("类加密完毕!");
        addAgent(targetDir, manifest);
        LogUtils.info("agent注入完毕!");
        ManifestUtils.save(manifest, manifestPath);
        LogUtils.info("改写manifest启动文件完毕!");
        FileUtils.deleteDir(new File(targetDir, "META-INF/maven"));
        JarUtils.zipJar(targetDir, to);
        LogUtils.info("重写生成jar完毕!路径:"+to);
        String securityConfig = createSecurityConfig(pool,to);
        LogUtils.info("生成秘钥文件完毕!路径:"+securityConfig);
    }

    public static void addAgent(String targetDir, Manifest manifest) {
        String agentJar = CommonUtils.getPath(Encode.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        //把本项目的class文件打包进去
        File thisJarFile = new File(agentJar);
        if (agentJar.endsWith(".jar")) {
            JarUtils.unJar(1,thisJarFile.getAbsolutePath(), targetDir, null);
        }
        //开发环境中打包
        else if (agentJar.endsWith("/classes/")) {
            List<File> files = FileUtils.getAllFiles(new File(agentJar));
            files.forEach(file -> {
                String path = FileUtils.relativePath(thisJarFile.getAbsolutePath(), file.getAbsolutePath());
                File newFile = new File(targetDir, path);
                FileUtils.createDirectory(newFile.getAbsolutePath());
                if(!file.isDirectory()) {
                    FileUtils.copy(file.getAbsolutePath(), newFile.getAbsolutePath());
                }
            });
        }

        manifest.getMainAttributes().putValue("Premain-Class", Agent.class.getName());
    }

    public static void clearMethod(ClassPool pool, JarFileInfo file) {
        try {
            String msg = "System.out.println(\"文件已加密处理!\");System.out.println(\"${myVersionInfo}\");";
            msg = msg.replace("${myVersionInfo}",Context.Default.getMyVersionInfo());
            CtClass ctClass = pool.getOrNull(file.getClassName());
            if (ctClass == null) {
                LogUtils.error("无法清除类方法:" + file.getFileName());
            } else {
                for (CtMethod method : ctClass.getDeclaredMethods()) {

                    if ("void".equals(method.getReturnType().getName())) {
                        method.setBody("{" + msg + "}");
                    } else if (!method.getReturnType().isPrimitive()) {
                        method.setBody("{return null;}");
                    }
                }
                //ctClass.writeFile(file.getClassPath());
                FileUtils.saveStream(ctClass.toBytecode(), file.FilePath);
            }
        } catch (Exception e) {
            throw new BsfException(e);
        }
    }

    public static String createSecurityConfig(ClassPool pool, String jarFileName) {
        try {
            String securityConfig = new File(new File(jarFileName).getParent(),
                    FileUtils.getFileNameWithoutSuffix(jarFileName) + ".security.properties").getAbsolutePath();
            FileUtils.createDirectory(securityConfig);
            Properties ps = new Properties();
            ps.put("password", Context.Default.getPassword());

            String decryptCode = "";
            if (!StringUtils.isEmpty(Context.Default.getMyEncryptCodeFile())) {
                CtClass cls = pool.get(EncryptFactory.get().getClass().getName());
                if(cls.isFrozen()){
                    cls.defrost();
                }
                CtMethod method = cls.getDeclaredMethod("e");
                if (method != null) {
                    method.setBody("{return null;}");
                }
                decryptCode = CommonUtils.encryptCode(cls.toBytecode());
            }
            ps.put("myDecryptCode", decryptCode);
            PropertiesUtils.store(ps,securityConfig,"auto create security.properties,you need it to run jar!");
            return securityConfig;
        } catch (Exception e) {
            throw new BsfException("生成解密配置文件出错", e);
        }
    }

    public static void encodeSpringConfig(String targetDir,ClassPool pool) {
        try {
            String className="org.springframework.core.io.ClassPathResource";
            String method = "getInputStream";
            String methodOld= method+"2";
            CtClass ctClass = pool.getOrNull(className);
            if (ctClass != null) {
                String newFile = CommonUtils.getClassEncodePath(targetDir, className);
                if(!new File(newFile).exists()){
                    if(ctClass.isFrozen()){
                        ctClass.defrost();
                    }
                    CtMethod ctMethod = ctClass.getDeclaredMethod(method);
                    CtMethod ctMethodNew= CtNewMethod.copy(ctMethod, ctClass, null);
                    ctMethod.setName(methodOld);
                    ctMethodNew.setBody("return {path}.decodeSpringConfig(this.path,this.getInputStream2());"
                            .replace("{path}",Decode.class.getName()));
                    ctClass.addMethod(ctMethodNew);
                    FileUtils.saveStream(EncryptFactory.get().e(ctClass.toBytecode()), newFile);
                }
            }
        } catch (Exception e) {
            throw new BsfException(e);
        }
    }
}
