package com.cliqz.nove;

import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Helper class to generate java source files
 *
 * @author Stefano Pacifici
 */
class DispatcherWriter {

    private String mPackageName;
    private String mClassName;
    private Map<String, TypeMirror> mMethods;
    private TypeElement mClazz;

    static class Builder {

        private TypeElement mClazz;
        private String mPackageName;
        private String mClassName;
        private final Map<String, TypeMirror> methods = new TreeMap<>();

        Builder setTypeElement(TypeElement clazz) {
            mClazz = clazz;
            final Name qualifiedName = clazz.getQualifiedName();
            mPackageName = getPackage(qualifiedName);
            mClassName = getClassName(qualifiedName);
            return this;
        }

        Builder addSubscriberMethod(ExecutableElement e) {
            final String methodName = e.getSimpleName().toString();
            for (VariableElement ve: e.getParameters()) {
                methods.put(methodName, ve.asType());
            }
            return this;
        }

        DispatcherWriter build() {
            final DispatcherWriter writer = new DispatcherWriter();
            writer.mClazz = mClazz;
            writer.mPackageName = mPackageName;
            writer.mClassName = mClassName;
            writer.mMethods = methods;
            return writer;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder
                    .append("Package Name: ").append(mPackageName).append("\n")
                    .append("Class Name:" ).append(mClassName).append("\n")
                    .append("Subscribers\n");
            for (Map.Entry<String, TypeMirror> e: methods.entrySet()) {
                builder.append(e.getKey())
                        .append(": ").append(e.getValue().toString())
                        .append("\n");
            }
            return builder.toString();
        }
    }

    private DispatcherWriter() {
    }

    static Builder builder() {
        return new Builder();
    }

    void write(Filer filer) {
        final String clazzName = mClassName + Bus.DISPATCHER_POSTFIX;
        final MethodSpec constructor = createConstructor();
        final MethodSpec post = createPostMethod();
        final FieldSpec messageTypes = createMessageTypes();
        final TypeSpec binder = TypeSpec.classBuilder(clazzName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(messageTypes)
                .addField(TypeName.get(mClazz.asType()), "subscriber", Modifier.FINAL, Modifier.PRIVATE)
                .addMethod(constructor)
                .addMethod(post)
                .build();
        final JavaFile javaFile = JavaFile.builder(mPackageName, binder).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FieldSpec createMessageTypes() {
        final ArrayList<TypeMirror> types = new ArrayList<>(mMethods.values());
        final StringBuilder builder = new StringBuilder();
        builder.append(types.get(0).toString()).append(".class");
        for (int i = 1; i < types.size(); i++) {
            builder.append(", ").append(types.get(i).toString()).append(".class");
        }
        final CodeBlock init = CodeBlock.of("new Class[] { $L }", builder.toString());
        return FieldSpec.builder(Class[].class, Bus.MESSAGE_TYPES_FIELD_NAME,
                Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .initializer(init)
                .build();
    }

    private MethodSpec createPostMethod() {
        final CodeBlock code = createPostCodeBlock();
        return MethodSpec.methodBuilder(Bus.POST_METHOD_NAME)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class, "message")
                .addCode(code)
                .build();
    }

    private MethodSpec createConstructor() {
        final TypeName typeName = TypeName.get(mClazz.asType());
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(typeName, "subscriber")
                .addStatement("this.subscriber = subscriber")
                .build();
    }

    private CodeBlock createPostCodeBlock() {
        final CodeBlock.Builder builder = CodeBlock.builder();
        String controlFlow = "if (message instanceof $T)";
        for (Map.Entry<String, TypeMirror> e: mMethods.entrySet()) {
            final TypeName clazzName = TypeName.get(e.getValue());
            builder.beginControlFlow(controlFlow, clazzName)
                    .addStatement("final $1T msg = ($1T) message", clazzName)
                    .addStatement("subscriber.$L(msg)", e.getKey())
                    .endControlFlow();
            controlFlow = "else if (message instanceof $T)";
        }
        return builder.build();
    }

    private static String getPackage(Name qualifiedName) {
        final String str = qualifiedName.toString();
        try {
            return str.substring(0, str.lastIndexOf('.'));
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    private static String getClassName(Name qualifiedName) {
        final String str = qualifiedName.toString();
        return str.substring(str.lastIndexOf('.') + 1);
    }
}
