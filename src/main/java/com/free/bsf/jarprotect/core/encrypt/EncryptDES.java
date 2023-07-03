package com.free.bsf.jarprotect.core.encrypt;

import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

public class EncryptDES implements IEncrypt {
    private String Algorithm = "DES";
    @Override
    public byte[] e(byte[] d) {
        try {
            DESKeySpec desKey = new DESKeySpec(Context.Default.getPassword().getBytes(UTF8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);
            SecretKey securekey = keyFactory.generateSecret(desKey);
            SecureRandom random = new SecureRandom();
            Cipher cipher = Cipher.getInstance(Algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            return cipher.doFinal(d);
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    @Override
    public byte[] d(byte[] d) {
        try {
            DESKeySpec desKey = new DESKeySpec(Context.Default.getPassword().getBytes(UTF8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);
            SecretKey securekey = keyFactory.generateSecret(desKey);
            SecureRandom random = new SecureRandom();
            Cipher cipher = Cipher.getInstance(Algorithm);
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
            return cipher.doFinal(d);
        }catch (Exception e){
            throw new BsfException(e);
        }
    }
}
