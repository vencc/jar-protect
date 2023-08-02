package com.free.bsf.jarprotect.tool;


import com.free.bsf.jarprotect.core.Encode;
import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        Context.Default=new Context(null);
        try {
            new Encode().run("C:\\tools\\lmc-demo-provider-1.0-SNAPSHOT.jar","C:\\tools\\lmc-demo-provider-1.0-SNAPSHOT-测试.jar","","","*.properties");
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
