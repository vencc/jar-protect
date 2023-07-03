package com.free.bsf.jarprotect.core.encrypt;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface IEncrypt {
    Charset UTF8= StandardCharsets.UTF_8;
    byte[] e(byte[] d);
    byte[] d(byte[] d);
}
