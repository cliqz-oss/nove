package com.cliqz.nove;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes("com.cliqz.nove.Subscribe")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class Processor extends AbstractProcessor{

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        final Set<? extends Element> annotatedElements =
                roundEnv.getElementsAnnotatedWith(Subscribe.class);
        if (annotatedElements.isEmpty()) {
            return true;
        }

        final HashMap<TypeElement, DispatcherWriter.Builder> builders = new HashMap<>();
        for (Element e: annotatedElements) {
            final TypeElement clazz = (TypeElement) e.getEnclosingElement();
            DispatcherWriter.Builder builder = builders.get(clazz);
            if (builder == null) {
                builder = DispatcherWriter.builder().setTypeElement(clazz);
                builders.put(clazz, builder);
            }
            if (ElementKind.METHOD.equals(e.getKind())) {
                final ExecutableElement ee = (ExecutableElement) e;
                checkModifiers(ee);
                checkReturnVoid(ee);
                checkParameters(ee);
                builder.addSubscriberMethod(ee);
            }
        }

        for (DispatcherWriter.Builder builder: builders.values()) {
            final DispatcherWriter writer = builder.build();
            writer.write(processingEnv.getFiler());
        }
        return true;
    }

    private void checkModifiers(ExecutableElement e) {
        for (Modifier modifier: e.getModifiers()) {
            if (modifier.equals(Modifier.PRIVATE) ||
                    modifier.equals(Modifier.PROTECTED) ||
                    modifier.equals(Modifier.ABSTRACT)) {
                processingEnv.getMessager()
                        .printMessage(Kind.ERROR, ProcessorMessages
                                .getInvalidModifierMessage(modifier), e);
            }
        }
    }

    private void checkParameters(ExecutableElement e) {
        final List<? extends VariableElement> params = e.getParameters();
        final int size = params.size();
        if (size != 1) {
            processingEnv.getMessager()
                    .printMessage(Kind.ERROR, ProcessorMessages.ERROR_TOO_MANY_ARGUMENTS, e);
        }

        final VariableElement param = params.get(0);
        if (param.asType().getKind().isPrimitive()) {
            processingEnv.getMessager()
                    .printMessage(Kind.ERROR, ProcessorMessages.ERROR_PRIMITIVE_PARAMETERS, e);
        }
    }

    private void checkReturnVoid(ExecutableElement e) {
        final TypeMirror returnType = e.getReturnType();
        if (!returnType.getKind().equals(TypeKind.VOID)) {
            processingEnv.getMessager()
                    .printMessage(Kind.WARNING, ProcessorMessages.WARNING_NON_VOID_RESULT, e);
        }
    }
}
