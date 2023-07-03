package com.free.bsf.jarprotect.tool;


import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;
import com.free.bsf.jarprotect.core.encrypt.EncryptTime;
import com.free.bsf.jarprotect.core.encrypt.IEncrypt;
import com.free.bsf.jarprotect.core.util.*;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws IOException {
        Context.Default=new Context(null);
        try {
            Object encrypt = DynamicCompiler.Default.getClassBytes("com.free.bsf.jarprotect.core.encrypt.MyEncrypt", FileUtils.readAllText("c:\\tools\\加密.java"));
            CtClass ctClass = Context.Default.Pool.get(encrypt.getClass().getName());
            byte[] ds = ctClass.toBytecode();
            ctClass.detach();
            byte[] ds2 = new EncryptTime().d(new EncryptTime().e(ds));//CommonUtils.decryptCode(CommonUtils.encryptCode(ds));
            CtClass cls =CommonUtils.loadClass(Context.Default.Pool,ds2);
            for(CtMethod m:cls.getMethods()){
               System.out.println(m.getName());
            }
        }catch (Exception e){
            throw new BsfException(e);
        }

//        System.out.println(DateUtils.printDistanceTime(new Date().getTime()-2000,new Date().getTime()));
//        System.out.println(DateUtils.printDistanceTime(new Date().getTime()-(long)2000-(long)1000*60*23,new Date().getTime()));
//        System.out.println(DateUtils.printDistanceTime(new Date().getTime()-(long)2000-(long)1000*60*60*24*3,new Date().getTime()));
       // System.out.println(DateUtils.printDistanceTime(new Date().getTime()-(long)2000-(long)1000*60*60*24*1000,new Date().getTime()));
        //JarUtils.fromJar(new File("C:\\tools\\lmc-demo-provider-1.0-SNAPSHOT-测试.jar"),"");
       //LogUtils.debug( StringUtils.stripEnd("bbbbbaaaabbbbbb","bbb"));
        //LogUtils.debug( StringUtils.strip("bbbbbaaaabbbbbb","bbb"));

//        JarUtils.unJar("C:\\tools\\lmc-demo-provider-1.0-SNAPSHOT.jar",
//                null,
//                "HdrHistogram*");
//        JarUtils.zipJar("C:\\tools\\lmc-demo-provider-1.0-SNAPSHOT.jar"+JarUtils.JARUNJARTAG,"C:\\tools\\lmc-demo-provider-1.0-SNAPSHOT-测试.jar");
    }
}
