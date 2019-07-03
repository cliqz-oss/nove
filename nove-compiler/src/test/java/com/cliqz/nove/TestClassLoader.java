package com.cliqz.nove;

import com.google.common.base.Optional;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestClassLoader extends ClassLoader {

    private final Compilation compilation;

    TestClassLoader(Compilation compilation) {
        super(null);
        this.compilation = compilation;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (JavaFileObject jfo: compilation.generatedFiles()) {
            final String queryName = "/CLASS_OUTPUT/" + name.replace('.', '/');
            if (jfo.isNameCompatible(queryName, JavaFileObject.Kind.CLASS)) {
                try {
                    return getClassFrom(name, jfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new ClassNotFoundException();
    }

    private Class<?> getClassFrom(String name, JavaFileObject jfo) throws IOException {
        final InputStream is = jfo.openInputStream();
        final byte[] buffer = new byte[1024];
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        while ((read = is.read(buffer)) > 0) {
            baos.write(buffer, 0, read);
        }
        is.close();
        return defineClass(name, baos.toByteArray(), 0, baos.size());
    }

}
