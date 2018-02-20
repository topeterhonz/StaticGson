package com.github.gfx.static_gson

import org.jetbrains.kotlin.serialization.jvm.JvmProtoBufUtil
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier


object AnnotationHelper {
    @JvmStatic
    fun hasAnnotationWithName(element: Element, simpleName: String): Boolean {
        return element.annotationMirrors.any {
            simpleName == it.annotationType.asElement().simpleName.toString()
        }
    }

    @JvmStatic
    fun hasDeclaredDefault(element: Element, enclosedElement: Element): Boolean {
        val metadata = kotlin.AnnotationHelper.getMetadata(element)

        if (metadata != null) {
            val (nameResolver, classProto) = JvmProtoBufUtil.readClassDataFrom(metadata.d1, metadata.d2)
            val constructor = classProto.getConstructor(0)

            return constructor.valueParameterList
                    .firstOrNull { nameResolver.getName(it.name).asString() == enclosedElement.simpleName.toString() }
                    ?.let { it.flags and 0b00000010 > 0 } ?: false // the second bit signifies hasDeclaredDefault
        }
        return false

    }

    @JvmStatic
    fun hasPublicParameterlessConstructor(element: Element): Boolean {
        for (enclosed in element.enclosedElements) {
            if (enclosed.kind == ElementKind.CONSTRUCTOR) {
                val constructorElement = enclosed as ExecutableElement
                if (constructorElement.parameters.size == 0 && constructorElement.modifiers
                                .contains(Modifier.PUBLIC)) {
                    return true
                }
            }
        }
        return false
    }
}
