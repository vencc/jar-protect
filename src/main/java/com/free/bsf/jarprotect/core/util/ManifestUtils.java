package com.free.bsf.jarprotect.core.util;

import com.free.bsf.jarprotect.core.base.BsfException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.jar.Manifest;

public class ManifestUtils {
    public static Manifest read(String file){
        try {
            return new Manifest(FileUtils.readStream(new File(file)));
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    public static void save(Manifest manifest,String file){
        try {
            manifest.write(new FileOutputStream(file));
        }catch (Exception e){
            throw new BsfException(e);
        }
    }
}
