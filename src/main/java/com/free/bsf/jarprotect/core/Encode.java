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

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;

public class Encode {
    public static void run(String from, String to, String excludeClassCondition, String includeJarCondition) {
        String targetDir = CommonUtils.gtJarUnJarPath(from);
        List<JarFileInfo> files = JarUtils.unJar(from, targetDir, includeJarCondition);
        LogUtils.info("jar释放完毕,开始加密....");
        String manifestPath = new File(targetDir, "META-INF/MANIFEST.MF").getAbsolutePath();
        Manifest manifest = ManifestUtils.read(manifestPath);
        ClassPool pool = Context.Default.Pool;
        CommonUtils.loadClassPath(pool, files);
        for (JarFileInfo file : files) {
            if (file.getFileName().endsWith(".class")) {
                String newFile = CommonUtils.getClassEncodePath(targetDir, file.getClassName());
                FileUtils.deleteFile(newFile);
                FileUtils.createDirectory(newFile);
                byte[] data = FileUtils.toBytes(new File(file.FilePath));
                FileUtils.saveStream(EncryptFactory.get().e(data), newFile);
                clearMethod(pool, file);
                LogUtils.info("加密类:"+file.getClassName());
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
            JarUtils.unJar(thisJarFile.getAbsolutePath(), targetDir, null);
        }
        //开发环境中打包
        else if (agentJar.endsWith("/classes/")) {
            List<File> files = FileUtils.getAllFiles(new File(agentJar));
            files.forEach(file -> {
                String path = FileUtils.relativePath(thisJarFile.getAbsolutePath(), file.getAbsolutePath());
                File newFile = new File(targetDir, path);
                FileUtils.createDirectory(newFile.getAbsolutePath());
                FileUtils.copy(file.getAbsolutePath(), newFile.getAbsolutePath());
            });
        }

        manifest.getMainAttributes().putValue("Premain-Class", Agent.class.getName());
    }

    public static void clearMethod(ClassPool pool, JarFileInfo file) {
        try {
            String msg = "System.out.println(\"文件已加密处理!\");";
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
}
