# 自定义加密(远程加密)
未测试!!!
```
package com.free.bsf.jarprotect.core.encrypt;

import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;
import com.free.bsf.jarprotect.core.base.HttpUtils;
import java.util.*;

/*MyEncrypt类名不能更改,注意引用相应的包(仅支持jdk自身的类库,不能使用第三方类库)*/

/**
 * 使用远程服务进行加解密服务
 * 考虑性能可以仅加密部分类: 加密结果数组前缀加一些标识区分,加密结果含有这些标识的跳过解密即可。
 */
public class MyEncrypt implements IEncrypt {
    private String hostUrl="http://www.xxxx.com";//需要自行写一个加解密服务
    @Override
    public byte[] e(byte[] d) {
        try {
            return httpPost(hostUrl+"/encode/",Context.Default.getPassword(),d);
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    @Override
    public byte[] d(byte[] d) {
        try {
            return httpPost(hostUrl+"/decode/",Context.Default.getPassword(),d);
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    private byte[] httpPost(String url,String password,byte[] d){
        HttpUtils.HttpRequest request = new HttpUtils.HttpRequest();
        request.URL=url;
        request.Method="POST";
        request.Header=new HashMap<>();
        request.Header.put("password",password);
        request.Body=d;
        HttpUtils.HttpResponse response = HttpUtils.request(request);
        if(response.isSuccess()) {
           return response.Body;
        }else{
            throw new BsfException("访问出错:"+url);
        }
    }

}

```

##### by 车江毅
