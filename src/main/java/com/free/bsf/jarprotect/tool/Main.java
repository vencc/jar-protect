package com.free.bsf.jarprotect.tool;

import com.free.bsf.jarprotect.core.Encode;
import com.free.bsf.jarprotect.core.base.Context;
import com.free.bsf.jarprotect.core.util.CommonUtils;
import com.free.bsf.jarprotect.core.util.FileUtils;
import com.free.bsf.jarprotect.core.util.LogUtils;

import java.io.File;
import java.util.Map;

public class Main {
    public static void main(String[] args){
        Context.Default=new Context(null);
        Map<String,String> params =  CommonUtils.parse("--",args);
        File from = new File(params.get("fromJar"));
        if(!from.exists()){
            LogUtils.info("加密前jar:"+from.getAbsolutePath());
        }
        String to= new File(from.getParent(), "encrypt-"+FileUtils.getFileName(from.getAbsolutePath())).getAbsolutePath();
        LogUtils.info("加密后jar:"+to);
       new Encode().run(from.getAbsolutePath(),to,params.get("excludeClass"),params.get("includeJar"),params.get("includeConfig"));
    }
}
