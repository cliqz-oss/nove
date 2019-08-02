package com.cliqz.nove;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.Assert.*;

public class IntegrationTest {

    @Test
    public void integrationTest() throws Exception {
        // 1. Need to compile every java class in the runtime library
        // 1.1 We need to find the runtime source folder
        final URL testResourceURL = getClass().getClassLoader().getResource("AbstractSubscriber.java");
        assertNotNull(testResourceURL);
        final String testResourceFile = testResourceURL.getFile();
        final File noveRuntimePath = new File(testResourceFile.substring(0, testResourceFile.indexOf("nove") + 4));
        final File noveRuntimeSourcePath = new File(noveRuntimePath, String.join(File.separator,
                "nove", "src", "main", "java"));
        // 1.2 Then build a list of JavaFileObject for them
        final List<SourceFile> noveSources = new ArrayList<>();
        findSourceFile(noveRuntimeSourcePath, "", noveSources);
        assertTrue(noveSources.size() > 0);
        final List<JavaFileObject> testJavaFileObjects = noveSources.stream()
                .map(SourceFile::toJavaFileObject)
                .collect(Collectors.toList());

        // 2. Add the test classes
        testJavaFileObjects.add(JavaFileObjects.forResource("IntegrationTestRunnable.java"));

        // 3. Compile everything together
        final Compilation compilation = javac()
                .withProcessors(new Processor())
                .compile(testJavaFileObjects);
        assertThat(compilation).succeeded();

        // 4. Run the test class
        final TestClassLoader loader = new TestClassLoader(compilation);
        final Class testClass = loader.loadClass("com.cliqz.nove.integration.IntegrationTestRunnable");
        final Runnable testRunnable = (Runnable) testClass.newInstance();
        testRunnable.run();

        // 5. Check the results
        final Field message1Received = testClass.getDeclaredField("message1Received");
        final Field message2Received = testClass.getDeclaredField("message2Received");
        final Field message3Received = testClass.getDeclaredField("message3Received");
        assertTrue("The first message type should have been received", message1Received.getBoolean(testRunnable));
        assertTrue("The second message type should have been received", message2Received.getBoolean(testRunnable));
        assertFalse("The third message type should not have been received", message3Received.getBoolean(testRunnable));
    }

    private void findSourceFile(File searchPath, String packageName, List<SourceFile> outSources) {
        assertTrue(searchPath.isDirectory());
        final File[] files = searchPath.listFiles();
        assertNotNull(files);
        Arrays.stream(files).forEach(file -> {
            final String fileName = file.getName();
            if (file.isFile() && fileName.endsWith(".java")) {
                final String className = fileName.substring(0, fileName.length() - 5);
                outSources.add(new SourceFile(packageName, className, file));
            } else {
                final String nextPackageName = packageName.isEmpty() ? fileName : packageName + "." + fileName;
                findSourceFile(file, nextPackageName, outSources);
            }
        });
    }

    private final static class SourceFile {
        final String packageName;
        final String className;
        final File path;

        SourceFile(String packageName, String className, File path) {
            this.packageName = packageName;
            this.className = className;
            this.path = path;
        }

        JavaFileObject toJavaFileObject() {
            try (FileInputStream inputStream = new FileInputStream(path)) {
                final String source = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                return JavaFileObjects.forSourceString(packageName + "." + className, source);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
