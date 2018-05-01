package com.github.gfx.static_gson

import com.github.gfx.static_gson.annotation.JsonSerializable
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil
import org.jetbrains.kotlin.serialization.deserialization.getName
import java.util.*
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

class ModelDefinition(private val context: StaticGsonContext, element: TypeElement) {

    private val METADATA_ANNOTATION_NAME = "Metadata"

    val element: TypeElement

    val modelType: ClassName

    val typeRegistry = TypeRegistry()

    private val fields: MutableList<FieldDefinition>

    val isKotlin: Boolean

    val complexTypes: Set<TypeName>
        get() = fields
                .filter { !it.isSimpleType }
                .map { it.type }
                .toSet()

    init {
        var element = element
        this.element = element

        modelType = ClassName.get(element)

        val annotation = element.getAnnotation(JsonSerializable::class.java)
        isKotlin = AnnotationHelper.hasAnnotationWithName(element, METADATA_ANNOTATION_NAME)

        if (isKotlin && !AnnotationHelper.hasPublicParameterlessConstructor(element)) {

            val metadata = kotlin.AnnotationHelper.getMetadata(element)!!

            val (nameResolver, classProto) = JvmProtoBufUtil.readClassDataFrom(metadata.d1, metadata.d2)

            if (classProto.constructorList.size > 1) {
                context.processingEnv.messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format(
                                "Kotlin classes %s must have only 1 constructor",
                                element.toString()))
            }

            val hasAnyDefaultParameterSet = classProto.constructorList[0].valueParameterList.any { it.flags and 0b00000010 > 0 }

            if (hasAnyDefaultParameterSet) {
                val hasAllDefaultParameterSet = classProto.constructorList[0].valueParameterList.all { it.flags and 0b00000010 > 0 }
                if (!hasAllDefaultParameterSet) {
                    context.processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "All default parameters on $element must be set if some are set")
                    classProto.constructorList[0].valueParameterList
                            .filter { it.flags and 0b00000010 == 0 }
                            .forEach {
                                context.processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Default parameters of \"$element.${nameResolver.getName(it.name).asString()}\" should be set.")
                            }

                }
            }
        }

        fields = ArrayList()

        while (true) {
            fields.addAll(extractFields(annotation, element, isKotlin, context))
            if (element.superclass.toString() == Any::class.java.name) {
                // reached the root
                break
            }

            val superElement = context.getTypeElement(element.superclass)

            if (superElement == null) {
                context.processingEnv.messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format(
                                "Unable create static gson for %s. Perhaps this is a generic type which is not supported yet",
                                element.superclass.toString()))
                break
            }

            element = superElement
        }
    }

    fun getFields(): List<FieldDefinition> {
        return fields
    }


    private fun extractFields(
            config: JsonSerializable,
            typeElement: TypeElement,
            isKotlin: Boolean,
            context: StaticGsonContext): List<FieldDefinition> {

        return typeElement.enclosedElements
                .filter { element -> element is VariableElement }
                .map { element -> element as VariableElement }
                .filter { element -> !element.modifiers.contains(Modifier.TRANSIENT) }
                .filter { element -> !element.modifiers.contains(Modifier.STATIC) }
                .map { element ->
                    FieldDefinition(config, element, isKotlin,
                            AnnotationHelper.hasDeclaredDefault(typeElement, element))
                }

    }
}
