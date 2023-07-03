package com.free.bsf.jarprotect.core.util;

import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.encrypt.IEncrypt;

import java.io.*;
import java.util.Properties;

public class PropertiesUtils {
    public static Properties load(String filePath){
        Properties ps = new Properties();
        try {
            try(FileInputStream stream = new FileInputStream(filePath)) {
                try (InputStreamReader reader = new InputStreamReader(stream, "utf-8")) {
                    ps.load(reader);
                }
            }
            return ps;
        }catch (Exception e){
            throw new BsfException("properties文件读取出错:"+StringUtils.nullToEmpty(filePath),e);
        }
    }

    public static void store(Properties ps, String filePath,String comments){
        try{
            try(FileOutputStream stream = new FileOutputStream(filePath)){
                try(OutputStreamWriter writer = new OutputStreamWriter(stream,"utf-8")){
                    try (BufferedWriter out = new BufferedWriter(writer)) {
                        ps.store(out, comments);
                    }
                }
            }
        }catch (Exception e){
            throw new BsfException("properties文件保存出错:"+StringUtils.nullToEmpty(filePath),e);
        }
    }
}
