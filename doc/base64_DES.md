# 自定义加密(base64+des)

```
package com.free.bsf.jarprotect.core.encrypt;
import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;
import com.free.bsf.jarprotect.core.encrypt.IEncrypt;
/**
 * 引用base64编码
 */
import java.util.Base64;

/*MyEncrypt类名不能更改,注意引用相应的包(仅支持jdk自身的类库,不能使用第三方类库)*/

/**
 * 使用base64做二次编码
 */
public class MyEncrypt implements IEncrypt {
    @Override
    public byte[] e(byte[] d) {
        try {
            //Context.Default.getPassword()
            return Base64.getEncoder().encode(new EncryptDES().e(d));
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    @Override
    public byte[] d(byte[] d) {
        try {
            //Context.Default.getPassword()
            return Base64.getDecoder().decode(new EncryptDES().d(d));
        }catch (Exception e){
            throw new BsfException(e);
        }
    }
}
```

##### by 车江毅
