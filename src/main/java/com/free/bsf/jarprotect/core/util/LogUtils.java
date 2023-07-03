package com.free.bsf.jarprotect.core.util;

import com.free.bsf.jarprotect.core.base.Context;

public class LogUtils {
    public static void debug(String msg){
        if(Context.Default!=null&&"true".equalsIgnoreCase(Context.Default.isDebug())) {
            String m = "[debug]" + StringUtils.nullToEmpty(msg);
            System.out.println(m);
            file(m);
        }
    }

    public static void info(String msg){
        String m = "[info]"+StringUtils.nullToEmpty(msg);
        System.out.println(m);
        file(m);
    }

    public static void error(String msg) {
        error(msg,null);
    }

    public static void error(String msg,Exception e){
        String m = "[error]"+StringUtils.nullToEmpty(msg);
        System.err.println("[error]"+StringUtils.nullToEmpty(msg));
        file(m);
        if(e!=null) {
            e.printStackTrace();
            file(e.getMessage());
        }
    }

    private static void file(String msg){
        if(Context.Default!=null&&"true".equalsIgnoreCase(Context.Default.isLogFile())) {
            FileUtils.appendAllText("log.txt", msg);
        }
    }
}
