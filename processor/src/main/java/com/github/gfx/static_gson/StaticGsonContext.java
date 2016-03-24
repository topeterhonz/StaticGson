package com.github.gfx.static_gson;

import com.squareup.javapoet.TypeName;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class StaticGsonContext {

    public final RoundEnvironment roundEnv;

    public final ProcessingEnvironment processingEnv;

    public final Map<TypeName, ModelDefinition> modelMap = new HashMap<>();

    public StaticGsonContext(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
        this.roundEnv = roundEnv;
        this.processingEnv = processingEnv;
    }

    public TypeElement getTypeElement(String type) {
        return processingEnv.getElementUtils().getTypeElement(type);
    }

    public TypeElement getTypeElement(TypeMirror typeMirror) {
        return getTypeElement(typeMirror.toString());
    }


    public Filer getSynchronizedFiler() {
        return new SynchronizedFiler(processingEnv.getFiler());
    }

    public void addModel(ModelDefinition model) {
        modelMap.put(model.modelType, model);
    }
}
