package com.cliqz.nove;

import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.*;

/**
 * Helper class to generate the SubscribersRegister class
 */
class SubscribersRegisterWriter {

    private static final String MESSAGE_TO_DISPATCHERS_FIELD_NAME = "messageToDispatchers";
    private static final String REGISTER_METHOD_NAME = "register";
    private static final String CLASS_PARAMETER_NAME = "clazz";
    private static final String DISPATCHER_PARAMETER_NAME = "dispatcher";
    private static final String DISPATCHERS_SET_VAR_NAME = "dispatchersSet";
    public static final String SUBSCRIBERS_REGISTER_IMPL_CLASS_NAME = "SubscribersRegisterImpl";
    public static final String PACKAGE_NAME = "com.cliqz.nove";
    private static final String UNREGISTER_METHOD_NAME = "unregister";
    private static final String MESSAGE_PARAMETER_NAME = "msg";
    private static final Object CLASS_NAME_VAR_NAME = "className";
    private static final String FIND_DISPACHERS_METHOD_NAME = "findDispatchers";

    private HashSet<TypeMirror> messageTypes = new HashSet<>();

    void addMessageType(TypeMirror typeMirror) {
        messageTypes.add(typeMirror);
    }

    void write(Filer filer) {
        final ClassName dispatcherCN = ClassName.get(Dispatcher.class);
        final TypeName setOfDispatchersTN = ParameterizedTypeName.get(ClassName.get(LinkedHashSet.class), dispatcherCN);
        final TypeName msgToDispachersTN = ParameterizedTypeName
                .get(ClassName.get(HashMap.class), ClassName.get(String.class), setOfDispatchersTN);
        final FieldSpec msgToDispatchersField = FieldSpec.builder(msgToDispachersTN, MESSAGE_TO_DISPATCHERS_FIELD_NAME)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        final MethodSpec.Builder constructorSpecBuilder = MethodSpec
                .constructorBuilder()
                .addStatement("$N = new $T($L)", msgToDispatchersField, msgToDispachersTN, messageTypes.size());
        for (TypeMirror typeMirror : messageTypes) {
            final String name = typeMirror.toString();
            constructorSpecBuilder.addStatement("$N.put($S, new $T(1))",
                    msgToDispatchersField, name, setOfDispatchersTN);
        }
        final MethodSpec constructorSpec = constructorSpecBuilder.build();

        final MethodSpec registerDispatcherSpec = MethodSpec.methodBuilder(REGISTER_METHOD_NAME)
                .addAnnotation(ClassName.get(Override.class))
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(Class.class), CLASS_PARAMETER_NAME)
                .addParameter(dispatcherCN, DISPATCHER_PARAMETER_NAME)
                .addStatement("assert $L != null", CLASS_PARAMETER_NAME)
                .addStatement("assert $L != null", DISPATCHER_PARAMETER_NAME)
                .addStatement("final $T $L = $N.get($L.getCanonicalName())",
                        setOfDispatchersTN,
                        DISPATCHERS_SET_VAR_NAME,
                        MESSAGE_TO_DISPATCHERS_FIELD_NAME,
                        CLASS_PARAMETER_NAME)
                .addStatement("assert $L != null", DISPATCHERS_SET_VAR_NAME)
                .beginControlFlow("synchronized ($L)", DISPATCHERS_SET_VAR_NAME)
                .addStatement("$L.add($L)", DISPATCHERS_SET_VAR_NAME, DISPATCHER_PARAMETER_NAME)
                .endControlFlow()
                .build();

        final MethodSpec unregisterDispatcherSpec = MethodSpec.methodBuilder(UNREGISTER_METHOD_NAME)
                .addAnnotation(ClassName.get(Override.class))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(Class.class), CLASS_PARAMETER_NAME)
                .addParameter(dispatcherCN, DISPATCHER_PARAMETER_NAME)
                .returns(TypeName.VOID)
                .addStatement("assert $L != null", CLASS_PARAMETER_NAME)
                .addStatement("assert $L != null", DISPATCHER_PARAMETER_NAME)
                .addStatement("final $T $L = $N.get($L.getCanonicalName())",
                        setOfDispatchersTN,
                        DISPATCHERS_SET_VAR_NAME,
                        MESSAGE_TO_DISPATCHERS_FIELD_NAME,
                        CLASS_PARAMETER_NAME)
                .addStatement("assert $L != null", DISPATCHERS_SET_VAR_NAME)
                .beginControlFlow("synchronized ($L)", DISPATCHERS_SET_VAR_NAME)
                .addStatement("$L.remove($L)", DISPATCHERS_SET_VAR_NAME, DISPATCHER_PARAMETER_NAME)
                .endControlFlow()
                .build();

        final TypeName dispatchersCollectionTN = ParameterizedTypeName
                .get(ClassName.get(Collection.class), dispatcherCN);
        final TypeName dispatchersLinkedListTN = ParameterizedTypeName
                .get(ClassName.get(LinkedList.class), dispatcherCN);
        final MethodSpec findDispatchersSpec = MethodSpec.methodBuilder(FIND_DISPACHERS_METHOD_NAME)
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(ClassName.get(Override.class))
                .returns(dispatchersCollectionTN)
                .addParameter(ClassName.get(Object.class), MESSAGE_PARAMETER_NAME)
                .addStatement("assert $L != null", MESSAGE_PARAMETER_NAME)
                //final List<Dispatcher> dispatchers = new LinkedList<Dispatcher>()
                .addStatement("final $T $L = $L.getClass().getCanonicalName()",
                        ClassName.get(String.class),
                        CLASS_NAME_VAR_NAME,
                        MESSAGE_PARAMETER_NAME)
                .addStatement("final $T $L = $N.get($L)",
                        setOfDispatchersTN,
                        DISPATCHERS_SET_VAR_NAME,
                        MESSAGE_TO_DISPATCHERS_FIELD_NAME,
                        CLASS_NAME_VAR_NAME)
                .addStatement("assert $L != null", DISPATCHERS_SET_VAR_NAME)
                .addStatement("return new $T($N)", dispatchersLinkedListTN, DISPATCHERS_SET_VAR_NAME)
                .build();

        final TypeSpec binder = TypeSpec
                .classBuilder(SUBSCRIBERS_REGISTER_IMPL_CLASS_NAME)
                .superclass(TypeName.get(SubscribersRegister.class))
                .addModifiers(Modifier.FINAL)
                .addField(msgToDispatchersField)
                .addMethod(constructorSpec)
                .addMethod(registerDispatcherSpec)
                .addMethod(unregisterDispatcherSpec)
                .addMethod(findDispatchersSpec)
                .build();
        final JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, binder)
                .skipJavaLangImports(true).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
