package com.github.gfx.static_gson

import com.squareup.javapoet.TypeName

import java.util.HashMap

import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

class StaticGsonContext(val roundEnv: RoundEnvironment, val processingEnv: ProcessingEnvironment) {

    val modelMap: MutableMap<TypeName, ModelDefinition> = HashMap()

    val synchronizedFiler: Filer
        get() = SynchronizedFiler(processingEnv.filer)

    fun getTypeElement(type: String): TypeElement? {
        return processingEnv.elementUtils.getTypeElement(type)
    }

    fun getTypeElement(typeMirror: TypeMirror): TypeElement? {
        return getTypeElement(typeMirror.toString())
    }

    fun addModel(model: ModelDefinition) {
        modelMap[model.modelType] = model
    }
}
