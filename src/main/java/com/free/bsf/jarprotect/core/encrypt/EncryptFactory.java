package com.free.bsf.jarprotect.core.encrypt;

import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;
import com.free.bsf.jarprotect.core.util.*;
import java.io.File;

public class EncryptFactory {
    private static IEncrypt OBJ=null;
   public static synchronized IEncrypt get(){
       if(OBJ!=null){
           return OBJ;
       }
       if(!StringUtils.isEmpty(Context.Default.getMyEncryptCodeFile())) {
           File file = new File(Context.Default.getMyEncryptCodeFile());
           String code=null;
           if(file.exists()) {
               code = FileUtils.readAllText(file.getAbsolutePath());
           }else{
               throw new BsfException("加密文件不存在:"+file.getAbsolutePath());
           }
           try {
               byte[] classBytes= DynamicCompiler.Default.getClassBytes("com.free.bsf.jarprotect.core.encrypt.MyEncrypt", code);
               Object encrypt= CommonUtils.loadClass(Context.Default.Pool,classBytes).toClass().newInstance();
               OBJ = (IEncrypt) encrypt;
           }catch (Exception e){
               throw new BsfException("初始化动态加密算法失败",e);
           }
       }
       if(!StringUtils.isEmpty(Context.Default.getMyDecryptCode())) {
           byte[] classBytes =CommonUtils.decryptCode(Context.Default.getMyDecryptCode());;
           try {
               Object encrypt = CommonUtils.loadClass(Context.Default.Pool,classBytes).toClass().newInstance();
               OBJ = (IEncrypt) encrypt;
           }catch (Exception e){
               throw new BsfException("初始化动态解密算法失败",e);
           }
       }
       OBJ = new EncryptDES();
       return OBJ;
   }
}
