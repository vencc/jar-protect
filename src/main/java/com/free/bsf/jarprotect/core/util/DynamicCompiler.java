package com.free.bsf.jarprotect.core.util;

import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;
import javassist.ClassPool;
import javassist.CtClass;

import javax.tools.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;

/**
 * 来源于网络动态内存编译修改
 */
public class DynamicCompiler {
    public static final DynamicCompiler Default = new DynamicCompiler();

    private DynamicCompiler() {
    }

    public byte[] getClassBytes(String name, String code) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // 错误监听信息
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        // 文件管理器，管理源代码和类文件，负责确定源代码和类文件的实例
        try (ClassFileManager classFileManager = new ClassFileManager(
                compiler.getStandardFileManager(diagnostics, null, null))) {
            // Java源文件，可用于读取磁盘之外的数据，如，文本文件，字符串，数据库数据
            JavaSourceFromString sourceFromString = new JavaSourceFromString(name, code);
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null, classFileManager, diagnostics, null, null, Arrays.asList(sourceFromString));
            Boolean result = task.call();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                throw new BsfException(diagnostic.toString());
            }
            if (result) {
                try {
                    ByteArrayClassFileObject javaFileObject = classFileManager.getByteArrayClassFileObject();
                    return javaFileObject.getBytes();
                }catch (Exception e){
                    throw new BsfException("动态编译失败",e);
                }
//                DynamicClassLoader dynamicClassLoader = new DynamicClassLoader();
//                Class<?> clazz = dynamicClassLoader.loadClass(name, javaFileObject);
//                try {
//                    return clazz.newInstance();
//                } catch (ReflectiveOperationException e) {
//                   throw new BsfException("动态编译失败",e);
//                }
            }
        }
        return null;
    }

    private class ClassFileManager extends ForwardingJavaFileManager {
        public ClassFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        private ByteArrayClassFileObject byteArrayClassFileObject;

        public ByteArrayClassFileObject getByteArrayClassFileObject() {
            return byteArrayClassFileObject;
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            return byteArrayClassFileObject = new ByteArrayClassFileObject(className);
        }
    }

    private class ByteArrayClassFileObject extends SimpleJavaFileObject {
        private final ByteArrayOutputStream outputStream;

        public ByteArrayClassFileObject(String name) {
            super(URI.create("bytes:///" + name), Kind.CLASS);
            outputStream = new ByteArrayOutputStream();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return outputStream;
        }

        public byte[] getBytes() {
            return outputStream.toByteArray();
        }
    }

    /**
     * 该类提供了一个基本的文件对象实现，可以用作创建文件对象的构建块。
     * 例如，这里是如何定义一个代表存储在一个字符串中的源代码的文件对象：
     */
    private class JavaSourceFromString extends SimpleJavaFileObject {

        private final CharSequence code;

        public JavaSourceFromString(String name, CharSequence code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return code;
        }
    }

//    private class DynamicClassLoader extends ClassLoader {
//
//        public Class loadClass(String fullName, ByteArrayClassFileObject byteArrayClass) {
//            byte[] classData = byteArrayClass.getBytes();
//            return this.defineClass(fullName, classData, 0, classData.length);
//        }
//    }
}
