package com.cliqz.nove;

import javax.tools.JavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.google.testing.compile.Compiler.javac;

public class TestClassLoader extends ClassLoader {

    public Class<?> fromJavaFileObject(JavaFileObject fo) {
        final byte[] buffer = new byte[1024];
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        try {
            final InputStream is = fo.openInputStream();
            while ((read = is.read(buffer)) > 0) {
                baos.write(buffer, 0, read);
            }
            is.close();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return defineClass("Test", baos.toByteArray(), 0, baos.size());
    }
}
