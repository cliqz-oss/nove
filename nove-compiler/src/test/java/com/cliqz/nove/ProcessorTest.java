package com.cliqz.nove;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Assert;
import org.junit.Test;

import javax.tools.JavaFileObject;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

/**
 * @author Stefano Pacifici
 */

public class ProcessorTest {

    @Test
    public void shouldCompileWithoutAnnotations() {
        final Compilation compilation = compileResource("NoAnnotations.java");
        assertThat(compilation).succeeded();
    }

    @Test
    public void shouldGenerateASimpleSubscription() {
        final Compilation compilation = compileResource("SimpleSubscription.java");
        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("SimpleSubscription" + Bus.DISPATCHER_POSTFIX)
                .hasSourceEquivalentTo(JavaFileObjects.forResource("SimpleSubscriptionResult.java"));
    }

    @Test
    public void shouldGenerateAMoreComplexSubscription() {
        final Compilation compilation = compileResource("ComplexSubscription.java");
        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("ComplexSubscription" + Bus.DISPATCHER_POSTFIX)
                .hasSourceEquivalentTo(JavaFileObjects.forResource("ComplexSubscriptionResult.java"));
    }

    @Test
    public void shouldFailIfPrivateSubscriber() {
        testForFail("PrivateSubscriber.java", ProcessorMessages.ERROR_INVALID_MODIFIER_PRIVATE);
    }

    @Test
    public void shouldFailIfProtectedSubscriber() {
        testForFail("ProtectedSubscriber.java", ProcessorMessages.ERROR_INVALID_MODIFIER_PROTECTED);
    }

    @Test
    public void shouldFailIfAbstractSubscriber() {
        testForFail("AbstractSubscriber.java", ProcessorMessages.ERROR_INVALID_MODIFIER_ABSTRACT);
    }

    @Test
    public void shouldFailIfMultipleParameters() {
        testForFail("MultipleParams.java", ProcessorMessages.ERROR_TOO_MANY_ARGUMENTS);
    }

    @Test
    public void shouldFailIfPrimitiveTypeParameter() {
        testForFail("PrimitiveParams.java", ProcessorMessages.ERROR_PRIMITIVE_PARAMETERS);
    }

    @Test
    public void shouldFailIfMethodOverloading() {
        testForFail("MethodOverloading.java", ProcessorMessages.ERROR_METHOD_OVERLOADING);
    }

    @Test
    public void shouldWarnIfSubscriberReturns() {
        final Compilation compilation = compileResource("NonVoidResultSubscriber.java");
        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningContaining(ProcessorMessages.WARNING_NON_VOID_RESULT);
    }

    @Test
    public void shouldGenerateASubscribersRegisterImplClass() {
        final Compilation compilation = compileResource("ComplexSubscription.java");
        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile(SubscribersRegisterWriter.PACKAGE_NAME + "." + SubscribersRegisterWriter.SUBSCRIBERS_REGISTER_IMPL_CLASS_NAME);
    }

    @Test
    public void test123() {
        final List<JavaFileObject> compilation =
                recursiveCompile(Collections.singletonList(JavaFileObjects.forResource("ComplexSubscription.java")));
        Assert.assertNotNull(compilation);
    }

    private List<JavaFileObject> recursiveCompile(List<JavaFileObject> objects) {
        final LinkedList<JavaFileObject> out = new LinkedList<>();
        for (JavaFileObject jfo: objects) {
            if (jfo.getKind() == JavaFileObject.Kind.SOURCE) {
                final Compilation compilation = javac()
                        .withProcessors(new Processor())
                        .compile(jfo);
                out.addAll(recursiveCompile(compilation.generatedFiles()));
            } else {
                out.add(jfo);
            }
        }
        return out;
    }

    private Compilation compileResource(String resource) {
        return javac()
                .withProcessors(new Processor())
                .compile(JavaFileObjects.forResource(resource));
    }

    private void testForFail(String resource, String expectedErrorMessage) {
        final Compilation compilation = compileResource(resource);
        assertThat(compilation).hadErrorContaining(expectedErrorMessage);
    }
}
