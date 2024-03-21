package com.free.bsf.jarprotect.core.base;

import com.free.bsf.jarprotect.core.encrypt.IEncrypt;
import com.free.bsf.jarprotect.core.util.*;
import javassist.ClassPool;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class Context {
    private Properties property;

    public Context(Map<String, String> from) {
        this.property = load(from);
        if(!StringUtils.isEmpty(this.getRemoteToken())){
            HttpUtils.HttpRequest request = new HttpUtils.HttpRequest();
            request.URL=Context.Default.getRemoteUrl();
            request.Method="POST";
            request.Body=new String(getRemoteToken()).getBytes(IEncrypt.UTF8);
            HttpUtils.HttpResponse response = HttpUtils.request(request);
            if(response.isSuccess()) {
                try {
                    Properties ps = new Properties();
                    ps.load(new ByteArrayInputStream(response.Body));
                    for (Map.Entry<Object, Object> kv : ps.entrySet()) {
                        this.property.put(kv.getKey(), kv.getValue());
                    }
                }catch (Exception e){
                    throw new BsfException("远程加解密配置出错",e);
                }
            }
        }
        LogUtils.info("加载配置完毕!");
//        for(Map.Entry<Object,Object> kv:this.property.entrySet()){
//            LogUtils.info("[配置信息]"+StringUtils.print(kv.getKey())+":"+StringUtils.print(kv.getValue()));
//        }
    }

    public static Context Default;


    private static Properties load(Map<String, String> from) {
        Properties ps = new Properties();
        //本地配置
        File configFile = new File(CommonUtils.getConfigFile());
        if (configFile.exists()) {
            try {
                ps = PropertiesUtils.load(configFile.getAbsolutePath());
            } catch (Exception e) {
                LogUtils.error("远程加解密配置出错", e);
            }
        }
        //命令配置
        if(from!=null) {
            ps.putAll(from);
        }
       return ps;
    }

    public String getExpireTime() {
        return get("expireTime","9999-01-01 00:00:00").toString();
    }

    private String RandomString = UUID.randomUUID().toString().replace("-","");
    public String getPassword() {
        String v = get("password",RandomString).toString();
        if(StringUtils.isEmpty(v)){
            return RandomString;
        }
        return v;
    }
    public String getMyEncryptCodeFile(){
        return get("myEncryptCodeFile","").toString();
    }
    public String getMyDecryptCode(){
        return get("myDecryptCode","").toString();
    }
    public String getRemoteUrl(){
        return get("remoteUrl","https://password.linkmore.com/").toString();
    }
    public String getRemoteToken(){
        return get("remoteToken","").toString();
    }

    public String isLogFile(){
        return get("logFile","false").toString();
    }
    public String isDebug(){
        return get("debug","false").toString();
    }

    public String getMyVersionInfo(){
        return get("myVersionInfo","").toString();
    }

    private Object get(String key,Object defaultValue){
        return this.property.get(key)==null?defaultValue:this.property.get(key);
    }

    public ClassPool Pool = ClassPool.getDefault();
}
