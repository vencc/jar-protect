package com.free.bsf.jarprotect.core.base;

import com.free.bsf.jarprotect.core.util.CommonUtils;
import com.free.bsf.jarprotect.core.util.FileUtils;
import com.free.bsf.jarprotect.core.util.JarUtils;
import com.free.bsf.jarprotect.core.util.StringUtils;

import java.io.File;

public class JarFileInfo {
    public String FilePath;
    public String JarPath;

    public JarFileInfo(String filePath,String jarPath){
        this.FilePath=filePath;
        this.JarPath=jarPath;
    }
    public String getFileName(){
        return FileUtils.getFileName(this.FilePath);
    }

    public String gtJarUnJarPath(){
        return CommonUtils.gtJarUnJarPath(this.JarPath);
    }

    public String getClassName(){
        if(!this.FilePath.endsWith(".class"))
            return null;
        String path = this.FilePath;
        int startIndex = getClassPathStartIndex();
        path = path.substring(startIndex,path.length()-".class".length());
        return path.replace(File.separator, ".");
    }

    public String getClassPath(){
        if(!this.FilePath.endsWith(".class"))
            return null;
        String path = this.FilePath;
        int startIndex = getClassPathStartIndex();
        path = path.substring(0,startIndex);
        return StringUtils.stripEnd(path,File.separator);
    }

    private int getClassPathStartIndex(){
        if(!this.FilePath.endsWith(".class"))
            return -1;
        String classPath=File.separator + "classes" + File.separator;
        String path = this.FilePath;
        int classIndex = path.lastIndexOf(classPath);
        int JarDirIndex = path.lastIndexOf(JarUtils.JARUNJARTAG+File.separator);
        int startIndex=Math.max(classIndex,JarDirIndex);
        if(startIndex==classIndex){
            startIndex = startIndex+classPath.length();

        }else if(startIndex == JarDirIndex){
            startIndex = startIndex+(JarUtils.JARUNJARTAG+File.separator).length();
        }
        return startIndex;
    }

    public String getJarFileName(){
        return new File(this.JarPath).getName();
    }

    @Override
    public String toString() {
        return this.FilePath.toString();
    }
}
