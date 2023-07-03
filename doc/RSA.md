# 自定义加密(RSA非对称加密)

```
package com.free.bsf.jarprotect.core.encrypt;
import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;
import com.free.bsf.jarprotect.core.encrypt.IEncrypt;
/**
 * 引用base64编码
 */
import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/*MyEncrypt类名不能更改,注意引用相应的包(仅支持jdk自身的类库,不能使用第三方类库)*/

/**
 * RSA非对称加密,将private和public秘钥直接写里面！解密时myDecryptCode秘钥仅包含private秘钥的相关信息
 */
public class MyEncrypt implements IEncrypt {
    @Override
    public byte[] e(byte[] d) {
        try {
            //Context.Default.getPassword()
            String pubKey ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCVRiDkEKXy/KBTe+UmkA+feq1zGWIgBxkgbz7aBJGb5+eMKKoiDRoEHzlGndwFKm4mQWNftuMOfNcogzYpGKSEfC7sqfBPDHsGPZixMWzL3J10zkMTWo6MDIXKKqMG1Pgeq1wENfJjcYSU/enYSZkg3rFTOaBSFId+rrPjPo7Y4wIDAQAB";
            java.security.spec.X509EncodedKeySpec bobPubKeySpec = new java.security.spec.X509EncodedKeySpec(
                    Base64.getDecoder().decode(pubKey));
            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            PublicKey publicKey = keyFactory.generatePublic(bobPubKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(d);
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    @Override
    public byte[] d(byte[] d) {
        try {
            //Context.Default.getPassword()
            String priKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJVGIOQQpfL8oFN75SaQD596rXMZYiAHGSBvPtoEkZvn54woqiINGgQfOUad3AUqbiZBY1+24w581yiDNikYpIR8Luyp8E8MewY9mLExbMvcnXTOQxNajowMhcoqowbU+B6rXAQ18mNxhJT96dhJmSDesVM5oFIUh36us+M+jtjjAgMBAAECgYABtnxKIabF0wBD9Pf8KUsEmXPEDlaB55LyPFSMS+Ef2NlfUlgha+UQhwsxND6CEKqS5c0uG/se/2+4l0jXz+CTYBEh+USYB3gxcMKEo5XDFOGaM2Ncbc7FAKJIkYYN2DHmr4voSM5YkVibw5Lerw0kKdYyr0Xd0kmqTok3JLiLgQJBAOGZ1ao9oqWUzCKnpuTmXre8pZLmpWPhm6S1FU0vHjI0pZh/jusc8UXSRPnx1gLsgXq0ux30j968x/DmkESwxX8CQQCpY1+2p1aX2EzYO3UoTbBUTg7lCsopVNVf41xriek7XF1YyXOwEOSokp2SDQcRoKJ2PyPc2FJ/f54pigdsW0adAkAM8JTnydc9ZhZ7WmBhOrFuGnzoux/7ZaJWxSguoCg8OvbQk2hwJd3U4mWgbHWY/1XB4wHkivWBkhRpxd+6gOUjAkBH9qscS52zZzbGiwQsOk1Wk88qKdpXku4QDeUe3vmSuZwC85tNyu+KWrfM6/H74DYFbK/MzK7H8iz80uJye5jVAkAEqEB/LwlpXljFAxTID/SLZBb+bCIoV/kvg+2145F+CSSUjEWRhG/+OH0cQfqomfg36WrvHl0g/Xw06fg31HgK";
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.getDecoder().decode(priKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyf.generatePrivate(priPKCS8);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(d);
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

}
```

##### by 车江毅
