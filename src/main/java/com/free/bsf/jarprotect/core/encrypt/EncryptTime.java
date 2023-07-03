package com.free.bsf.jarprotect.core.encrypt;

import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;
import com.free.bsf.jarprotect.core.util.DateUtils;
import com.free.bsf.jarprotect.core.util.LogUtils;

import java.util.Base64;
import java.util.Date;

public class EncryptTime implements IEncrypt{
    @Override
    public byte[] e(byte[] d) {
        String s=Context.Default.getExpireTime()+"_"+Base64.getEncoder().encodeToString(d);
        return Base64.getEncoder().encode(s.getBytes(UTF8));
    }

    @Override
    public byte[] d(byte[] d) {
       String s = new String(Base64.getDecoder().decode(d),UTF8);
       String time= s.substring(0,s.indexOf('_'));
       Date date = DateUtils.defaultFormat(time);
       if(date==null||date.getTime()<new Date().getTime()){
           throw new BsfException("秘钥已过期");
       }else{
           LogUtils.info("[秘钥剩余时间]"+DateUtils.printDistanceTime(new Date().getTime(),date.getTime()));
       }
       String c=s.substring(s.indexOf('_')+1);
        return Base64.getDecoder().decode(c);
    }
}
