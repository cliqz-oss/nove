package com.cliqz.nove;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import java.net.URL;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

/**
 * @author Stefano Pacifici
 * @date 2016/12/29
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
        testForFail("PrivateSubscriber.java", "Invalid modifier: PRIVATE");
    }

    @Test
    public void shouldFailIfProtectedSubscriber() {
        testForFail("ProtectedSubscriber.java", "Invalid modifier: PROTECTED");
    }

    @Test
    public void shouldFailIfAbstractSubscriber() {
        testForFail("AbstractSubscriber.java", "Invalid modifier: ABSTRACT");
    }

    @Test
    public void shouldFailIfMultipleParameters() {
        testForFail("MultipleParams.java", "Subscriber must have a single parameter");
    }

    @Test
    public void shouldFailIfPrimitiveTypeParameter() {
        testForFail("PrimitiveParams.java", "Subscriber can't use primitives as parameters");
    }

    @Test
    public void shouldWarnIfSubscriberReturns() {
        final Compilation compilation = compileResource("NonVoidResultSubscriber.java");
        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningContaining("Subscriber should return void");
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
