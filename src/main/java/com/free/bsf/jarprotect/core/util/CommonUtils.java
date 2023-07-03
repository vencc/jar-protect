package com.free.bsf.jarprotect.core.util;

import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;
import com.free.bsf.jarprotect.core.base.JarFileInfo;
import com.free.bsf.jarprotect.core.encrypt.EncryptTime;
import com.free.bsf.jarprotect.core.encrypt.IEncrypt;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonUtils {
    public static String getPath(String path){
        try {
            if (path == null) {
                path = CommonUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            }
            return URLDecoder.decode(path, "utf-8");
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    public static void loadClassPath(ClassPool pool, List<JarFileInfo> files){
        try {
            List<String> classPathDirs=new ArrayList<>();
            for(JarFileInfo file:files){
                if (file.getFileName().endsWith(".jar")) {
                    pool.appendClassPath(file.FilePath);
                }
                if (file.getFileName().endsWith(".class")) {
                    String dir = file.getClassPath();
                    if (!classPathDirs.contains(dir)) {
                        classPathDirs.add(dir);
                        pool.appendClassPath(dir);
                    }
                }
            }

        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    public static CtClass loadClass(ClassPool pool,byte[] clsBytes){
        try {
            try (ByteArrayInputStream stream = new ByteArrayInputStream(clsBytes)) {
                CtClass ctClass = pool.makeClass(stream);
                return ctClass;
            }
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    public static String gtJarUnJarPath(String jarPath){
        return jarPath+JarUtils.JARUNJARTAG;
    }

    public static String getClassEncodePath(String targetDir,String className){
        String path = "/META-INF/.encode/"+className;
        if(targetDir==null){
            return path;
        }
        return new File(targetDir,path).getAbsolutePath();
    }

    public static String getConfigFile(){
        String jar=CommonUtils.getPath(CommonUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        if(jar.endsWith(".jar")) {
            String jarFileName = FileUtils.getFileNameWithoutSuffix(jar);
            String[] files = new String[]{jarFileName + ".security.conf", jarFileName + ".security.properties"};
            for (String file : files) {
                if (new File(file).exists())
                    return file;
            }
        }
        return "security.conf";
    }

    public static String encryptCode(byte[] code){
        return new String(new EncryptTime().e(code), IEncrypt.UTF8);
    }

    public static byte[] decryptCode(String code){
        return new EncryptTime().d(code.getBytes(IEncrypt.UTF8));
    }

    public static Map<String, String> parse(String tag, String... args) {
        Map<String, String> parseMap = new HashMap(6);
        for (int i = 0; i < args.length; i++) {
            //检查是否是双横杠参数，非双横杠continue
            if (!args[i].contains(tag)) {
                continue;
            }
            //获取双横杠参数对应数据
            String commandData = null;
            if (i + 1 < args.length && !args[i + 1].contains(tag)) {
                commandData = args[i + 1];
            }
            //截取双横杠，获取参数名
            String parameter = args[i].substring(2);
            //存储参数、数据
            parseMap.put(parameter, commandData);
        }
        return parseMap;
    }
}
