package com.cliqz.nove;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

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
    public void shouldWarnIfSubscriberReturns() {
        final Compilation compilation = compileResource("NonVoidResultSubscriber.java");
        assertThat(compilation).succeeded();
        //noinspection ResultOfMethodCallIgnored
        assertThat(compilation).hadWarningContaining(ProcessorMessages.WARNING_NON_VOID_RESULT);
    }

    private Compilation compileResource(String resource) {
        return javac()
                .withProcessors(new Processor())
                .compile(JavaFileObjects.forResource(resource));
    }

    private void testForFail(String resource, String expectedErrorMessage) {
        final Compilation compilation = compileResource(resource);
        //noinspection ResultOfMethodCallIgnored
        assertThat(compilation).hadErrorContaining(expectedErrorMessage);
    }
}
