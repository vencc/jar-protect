package com.free.bsf.jarprotect.core;

import com.free.bsf.jarprotect.core.util.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;


/**
 */
public class AgentTransformer implements ClassFileTransformer {

    /**
     * 构造方法
     *
     */
    public AgentTransformer() {
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain domain, byte[] classBuffer) {
        if (className == null || domain == null || loader == null) {
            return classBuffer;
        }
        //获取类所在的项目运行路径
        try {
            byte[] data = new Decode().run(className);
            if(data!=null){
                return data;
            }
        }
        catch (Exception e){
            LogUtils.error("解密失败",e);
        }
        return classBuffer;

    }
}
