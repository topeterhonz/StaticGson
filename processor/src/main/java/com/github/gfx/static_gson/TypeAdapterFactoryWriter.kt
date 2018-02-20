package com.github.gfx.static_gson

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec

import java.io.IOException
import java.util.stream.Collectors

import javax.lang.model.element.Modifier

class TypeAdapterFactoryWriter(private val context: StaticGsonContext, private val model: ModelDefinition) {

    private val typeAdapterClassName: String

    private val typeToken: ParameterizedTypeName

    private val typeAdapter: ParameterizedTypeName

    private val objectConstructor: ParameterizedTypeName

    internal val packageName: String
        get() = model.modelType.packageName()

    init {
        typeAdapter = Types.getTypeAdapter(model.modelType)
        typeToken = ParameterizedTypeName.get(Types.TypeToken, model.modelType)
        typeAdapterClassName = createTypeAdapterClassName(model.modelType)
        objectConstructor = ParameterizedTypeName.get(ClassName.get("com.google.gson.internal", "ObjectConstructor"), model.modelType)
    }

    internal fun buildTypeSpec(): TypeSpec {
        val typeAdapterClass = TypeSpec.classBuilder(typeAdapterClassName)
        typeAdapterClass.addJavadoc("This class is dynamically loaded by {@link \$T}.\n",
                Types.StaticGsonTypeAdapterFactory)
        typeAdapterClass.addAnnotation(Annotations.suppressWarnings("unused"))
        typeAdapterClass.addAnnotation(Annotations.staticGsonGenerated())
        typeAdapterClass.addModifiers(Modifier.PUBLIC)
        typeAdapterClass.superclass(typeAdapter)

        for (type in model.complexTypes) {
            typeAdapterClass.addField(model.typeRegistry.getField(type))
        }

        typeAdapterClass.addField(objectConstructor, "objectConstructor", Modifier.FINAL, Modifier.PRIVATE)

        typeAdapterClass.addMethod(MethodSpec.constructorBuilder()
                .addAnnotation(Annotations.suppressWarnings("unchecked"))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Gson::class.java, "gson")
                .addParameter(typeToken, "typeToken")
                .addParameter(objectConstructor, "objectConstructor")
                .addCode(model.typeRegistry.fieldInitialization)
                .addStatement("this.objectConstructor = objectConstructor")
                .build())

        typeAdapterClass.addMethod(buildWriteMethod())
        typeAdapterClass.addMethod(buildReadMethod())

        return typeAdapterClass.build()
    }

    /**
     * @return `public void write(JsonWriter out, T value) throws IOException`
     */
    private fun buildWriteMethod(): MethodSpec {
        val method = MethodSpec.methodBuilder("write")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addException(IOException::class.java)
                .addParameter(JsonWriter::class.java, "writer")
                .addParameter(model.modelType, "value")

        method.addStatement("writer.beginObject()")
        for (field in model.getFields()) {
            method.addCode(field.buildWriteBlock(model.typeRegistry, "value", "writer"))
        }
        method.addStatement("writer.endObject()")

        return method.build()
    }

    /**
     * @return `public T read(JsonReader in) throws IOException `
     */
    private fun buildReadMethod(): MethodSpec {
        val method = MethodSpec.methodBuilder("read")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .returns(model.modelType)
                .addException(IOException::class.java)
                .addParameter(JsonReader::class.java, "reader")

        // allow null
        method.beginControlFlow("if (reader.peek() == \$T.\$L)", JsonToken::class.java, JsonToken.NULL)
        method.addStatement("reader.nextNull()")
        method.addStatement("return null")
        method.endControlFlow() // if

        //        // check type
        //        method.beginControlFlow("if (reader.peek() != $T.$L)", JsonToken.class, JsonToken.BEGIN_OBJECT);
        //        method.addStatement("reader.skipValue()");
        //        method.addStatement("return null");
        //        method.endControlFlow(); // if

        method.addStatement("\$T object = objectConstructor.construct()", model.modelType)

        // NonNull checks
        for (field in model.getFields()) {
            method.addCode(field.buildMustDeclareFlagCodeBlock())
        }


        method.addStatement("reader.beginObject()")
        method.beginControlFlow("while (reader.hasNext())")
        method.beginControlFlow("switch (reader.nextName())")
        val objectName = "object"
        for (field in model.getFields()) {
            for (name in field.serializedNameCandidates) {
                method.addCode("case \$S:\n", name)
            }
            method.addCode(field.buildReadCodeBlock(model.typeRegistry, objectName, "reader", model.modelType.reflectionName(), context))
            method.addStatement("break")
        }
        method.addCode("default:\n")
        method.addStatement("reader.skipValue()")
        method.addStatement("break")
        method.endControlFlow() // switch
        method.endControlFlow() // while
        method.addStatement("reader.endObject()")

        // NonNull checks
        for (field in model.getFields()) {
            method.addCode(field.buildNullCheckCodeBlock(model.modelType.simpleName(), objectName))
            method.addCode(field.buildMustSetCheckFlagCodeBlock(model.modelType.simpleName()))
        }

        method.addStatement("return object", model.modelType)

        return method.build()
    }

    private fun buildJavaFile(): JavaFile {
        return JavaFile.builder(packageName, buildTypeSpec())
                .skipJavaLangImports(true)
                .build()
    }

    fun write() {
        try {
            buildJavaFile().writeTo(context.synchronizedFiler)
        } catch (e: IOException) {
            throw ProcessingException(e)
        }

    }

    companion object {

        internal fun createTypeAdapterClassName(modelType: ClassName): String {
            val modelClassName = modelType.simpleNames().joinToString("$")
            return StaticGsonTypeAdapterFactory.getTypeAdapterFactoryName(modelClassName)
        }
    }
}
