package com.free.bsf.jarprotect.core.util;

import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.JarFileInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.*;

public class JarUtils {
    public static String JARUNJARTAG="---UNJAR---";
    public static List<JarFileInfo> unJar(int level,String jarPath, String targetDir,String includeLibJarsCondition) {
        if(targetDir==null)
        {
            targetDir=CommonUtils.gtJarUnJarPath(jarPath);
            FileUtils.deleteFile(targetDir);
        }
        List<JarFileInfo> list = new ArrayList<>();
        FileUtils.createDirectory(targetDir);
        File target = new File(targetDir);
        Enumeration<?> entries;
        File targetFile;
        try {
           try(ZipFile zipFile = new ZipFile(new File(jarPath))){
               entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    targetFile = new File(target, entry.getName());
                    FileUtils.createDirectory(targetFile.getAbsolutePath());
                    if (entry.isDirectory()) {
                        continue;
                    }
                    try (InputStream input = zipFile.getInputStream(entry)) {
                        byte[] bytes = FileUtils.toBytes(input);
                        FileUtils.saveStream(bytes, targetFile.getAbsolutePath());
                        JarFileInfo info = new JarFileInfo(level,targetFile.getAbsolutePath(),jarPath);
                        list.add(info);
                    }
                }
            }
        } catch (Exception e) {
            throw new BsfException("解压失败",e);
        }
        //解压lib
        for(JarFileInfo jar: new ArrayList<>(list)){
            //String parentDir= new File(jar.FilePath).getParent();
            String fileNameNoExt = FileUtils.getFileNameWithoutSuffix(jar.getFileName());
            if (StringUtils.hitCondition(includeLibJarsCondition, fileNameNoExt)) {
                //String jarDir = new File(parentDir, jar.getFileName()+JARUNJARTAG).getAbsolutePath();
                List<JarFileInfo> temp = unJar(level+1,jar.FilePath, null, null);
                list.addAll(temp);
                list.remove(jar);
                FileUtils.deleteFile(jar.FilePath);
            }
        }
        return list;
    }

    public static String zipJar(String jarDir, String targetJar) {
        File jarDirFile = new File(jarDir);
        //压缩lib
        for(File file : FileUtils.getAllFiles(jarDirFile)){
            if(file.isDirectory()&&file.getName().endsWith(JARUNJARTAG)){
                zipJar(file.getAbsolutePath(),StringUtils.stripEnd(file.getAbsolutePath(),JARUNJARTAG));
            }
        }
        File jar = new File(targetJar);
        FileUtils.deleteFile(jar.getAbsolutePath());
        try {
            try (FileOutputStream fileOutputStream = new FileOutputStream(jar)) {
                    try (ZipOutputStream out = new ZipOutputStream(fileOutputStream)) {
                        for (File file : FileUtils.getAllFiles(jarDirFile)) {
                            String fileName = file.getAbsolutePath().substring(jarDirFile.getAbsolutePath().length() + 1);
                            fileName = fileName.replace(File.separator, "/");
                            if(file.isDirectory()) {
                                ZipEntry e = new ZipEntry(fileName + "/");
                                out.putNextEntry(e);
                                out.closeEntry();
                            }else {
                                ZipEntry e = new ZipEntry(fileName);
                                byte[] data = FileUtils.toBytes(file);
                                if(fileName.toLowerCase().endsWith(".jar")) {
                                    e.setMethod(ZipEntry.STORED);
                                    e.setSize(data.length);
                                    e.setCrc(FileUtils.crc32(data));
                                }
                                out.putNextEntry(e);
                                out.write(data);
                                out.closeEntry();
                            }
                        }
                }
            }
        }catch (Exception e){
            throw new BsfException("压缩失败",e);
        }
        FileUtils.deleteFile(jarDirFile.getAbsolutePath());
        return targetJar;
    }

}
