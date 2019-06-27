package com.cliqz.nove;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Helper class to generate the SubscribersRegister class
 */
class SubscribersRegisterWriter {

    private HashSet<TypeMirror> messageTypes = new HashSet<>();

    void addMessageType(TypeMirror typeMirror) {
        messageTypes.add(typeMirror);
    }

    void write(Filer filer) {
        final TypeSpec binder = TypeSpec
                .classBuilder("SubscribersRegister")
                .addModifiers(Modifier.FINAL)
                .build();
        final JavaFile javaFile = JavaFile.builder("com.cliqz.nove", binder).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
