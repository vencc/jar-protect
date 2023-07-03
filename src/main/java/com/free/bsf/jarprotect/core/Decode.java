package com.free.bsf.jarprotect.core;

import com.free.bsf.jarprotect.core.encrypt.EncryptFactory;
import com.free.bsf.jarprotect.core.util.*;

public class Decode {
    public static byte[] run(String className){
        String filename=CommonUtils.getClassEncodePath(null,className.replace("\\",".").replace("/","."));
        if(Decode.class.getResource(filename)==null)
            return null;
        LogUtils.debug("解密路径:"+Decode.class.getResource(filename));
        byte[] data = FileUtils.toBytes(Decode.class.getResourceAsStream(filename));
        if (data == null) {
            LogUtils.error("解密失败:" + filename);
            return null;
        }
        LogUtils.debug("解密成功:" + filename);
        return EncryptFactory.get().d(data);
    }
}
