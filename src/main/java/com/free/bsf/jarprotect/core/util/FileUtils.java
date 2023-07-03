package com.free.bsf.jarprotect.core.util;

import com.free.bsf.jarprotect.core.base.BsfException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

public class FileUtils {
    public static void createDirectory(String path) {
        File file = new File(path);
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        //如果文件夹不存在则创建
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    public static byte[] toBytes(InputStream input) {
        try {
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int n = 0;
                while (-1 != (n = input.read(buffer))) {
                    output.write(buffer, 0, n);
                }
                return output.toByteArray();
            }
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    public static byte[] toBytes(File file) {
        try {
            try (InputStream input = new FileInputStream(file)) {
                return toBytes(input);
            }
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    public static long crc32(byte[] bytes) {
        CRC32 crc = new CRC32();
        crc.update(bytes);
        return crc.getValue();
    }

    public static void saveStream(byte[] bs,String fileName){
        try(FileOutputStream out=new FileOutputStream(fileName)) {
            out.write(bs);
            out.flush();
        }catch (Exception e){
            throw new BsfException("保存流出错",e);
        }
    }

    public static String readAllText(String path) {
        try {
            File f = new File(path);
            if (f.exists()) {
                //获取文件长度
                Long filelength = f.length();
                byte[] filecontent = new byte[filelength.intValue()];
                try (FileInputStream in = new FileInputStream(f)) {
                    in.read(filecontent);
                }
                //返回文件内容,默认编码
                return new String(filecontent);
            } else {
                throw new FileNotFoundException(path);
            }
        } catch (IOException exp) {
            throw new BsfException("读文件异常", exp);
        }
    }

    public static void writeAllText(String path, String contents) {
        try {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            } else {
            }
            //f.mkdirs();
            //不存在则创建
            f.createNewFile();
            try (BufferedWriter output = new BufferedWriter(new FileWriter(f))) {
                output.write(contents);
            }
        } catch (IOException exp) {
            throw new BsfException("写文件异常", exp);
        }
    }

    public static void appendAllText(String path, String contents) {
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File(path);
            try (FileWriter fw = new FileWriter(f, true)) {
                try (PrintWriter pw = new PrintWriter(fw)) {
                    pw.println(contents);
                    pw.flush();
                    fw.flush();
                }
            }
        } catch (IOException exp) {
            throw new BsfException("追加文件异常", exp);
        }

    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static void deleteFile(String path){
        File file = new File(path);
        if(file.exists()) {
            if(file.isDirectory()){
                deleteDir(file);
            }else {
                file.delete();
            }
        }
    }

    public static List<File> getAllFiles(File dir){
        List<File> list = new ArrayList<>();
        if(!dir.exists()){
            return list;
        }
        File[] files = dir.listFiles();
        for(File f:files){
            if(f.isDirectory()){
                list.add(f);
                list.addAll(getAllFiles(f));
            }else{
                list.add(f);
            }
        }
        return list;
    }

    /**
     * 路径处理
     * @param dirPath
     * @param filePath
     * @return
     */
    public static String relativePath(String dirPath,String filePath){
        Path currentDir = Paths.get(dirPath);
        Path targetFile = Paths.get(filePath);
        Path relativePath = currentDir.relativize(targetFile);
        return relativePath.toFile().getPath();
    }

    public static void copy(String from,String to){
        try {
            Files.copy(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    public static FileInputStream readStream(File file){
        try {
            return new FileInputStream(file);
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    /**
     * 获取无扩展名的文件名
     * @param fileName
     * @return
     */
    public static String getFileNameWithoutSuffix(String fileName){
        fileName=getFileName(fileName);
        //忽略判断
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getFileName(String filePath){
        File file = new File(filePath);
        return file.getName();
    }
}
