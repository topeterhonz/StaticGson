package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

public class TypeAdapterFactoryWriter {

    private final StaticGsonContext context;

    private final ModelDefinition model;

    private final String typeAdapterClassName;

    private final ParameterizedTypeName typeToken;

    private final ParameterizedTypeName typeAdapter;

    private final ParameterizedTypeName objectConstructor;

    public TypeAdapterFactoryWriter(StaticGsonContext context, ModelDefinition model) {
        this.context = context;
        this.model = model;
        typeAdapter = Types.getTypeAdapter(model.modelType);
        typeToken = ParameterizedTypeName.get(Types.TypeToken, model.modelType);
        typeAdapterClassName = createTypeAdapterClassName(model.modelType);
        objectConstructor = ParameterizedTypeName.get(ClassName.get("com.google.gson.internal", "ObjectConstructor"), model.modelType);
    }

    static String createTypeAdapterClassName(ClassName modelType) {
        String modelClassName = modelType.simpleNames().stream().collect(Collectors.joining("$"));
        return StaticGsonTypeAdapterFactory.getTypeAdapterFactoryName(modelClassName);
    }

    String getPackageName() {
        return model.modelType.packageName();
    }

    TypeSpec buildTypeSpec() {
        TypeSpec.Builder typeAdapterClass = TypeSpec.classBuilder(typeAdapterClassName);
        typeAdapterClass.addJavadoc("This class is dynamically loaded by {@link $T}.\n",
                Types.StaticGsonTypeAdapterFactory);
        typeAdapterClass.addAnnotation(Annotations.suppressWarnings("unused"));
        typeAdapterClass.addAnnotation(Annotations.staticGsonGenerated());
        typeAdapterClass.addModifiers(Modifier.PUBLIC);
        typeAdapterClass.superclass(typeAdapter);

        for (TypeName type : model.getComplexTypes()) {
            typeAdapterClass.addField(model.typeRegistry.getField(type));
        }

        typeAdapterClass.addField(objectConstructor, "objectConstructor", Modifier.FINAL, Modifier.PRIVATE);

        typeAdapterClass.addMethod(MethodSpec.constructorBuilder()
                .addAnnotation(Annotations.suppressWarnings("unchecked"))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Gson.class, "gson")
                .addParameter(typeToken, "typeToken")
                .addParameter(objectConstructor, "objectConstructor")
                .addCode(model.typeRegistry.getFieldInitialization())
                .addStatement("this.objectConstructor = objectConstructor")
                .build());

        typeAdapterClass.addMethod(buildWriteMethod());
        typeAdapterClass.addMethod(buildReadMethod());

        return typeAdapterClass.build();
    }

    /**
     * @return {@code public void write(JsonWriter out, T value) throws IOException}
     */
    private MethodSpec buildWriteMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("write")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addException(IOException.class)
                .addParameter(JsonWriter.class, "writer")
                .addParameter(model.modelType, "value");

        method.addStatement("writer.beginObject()");
        for (FieldDefinition field : model.getFields()) {
            method.addCode(field.buildWriteBlock(model.typeRegistry, "value", "writer"));
        }
        method.addStatement("writer.endObject()");

        return method.build();
    }

    /**
     * @return {@code public T read(JsonReader in) throws IOException }
     */
    private MethodSpec buildReadMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("read")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(model.modelType)
                .addException(IOException.class)
                .addParameter(JsonReader.class, "reader");

        // allow null
        method.beginControlFlow("if (reader.peek() == $T.$L)", JsonToken.class, JsonToken.NULL);
        method.addStatement("reader.nextNull()");
        method.addStatement("return null");
        method.endControlFlow(); // if

//        // check type
//        method.beginControlFlow("if (reader.peek() != $T.$L)", JsonToken.class, JsonToken.BEGIN_OBJECT);
//        method.addStatement("reader.skipValue()");
//        method.addStatement("return null");
//        method.endControlFlow(); // if

        method.addStatement("$T object = objectConstructor.construct()", model.modelType);

        // NonNull checks
        for (FieldDefinition field : model.getFields()) {
            method.addCode(field.buildMustDeclareFlagCodeBlock());
        }


        method.addStatement("reader.beginObject()");
        method.beginControlFlow("while (reader.hasNext())");
        method.beginControlFlow("switch (reader.nextName())");
        String objectName = "object";
        for (FieldDefinition field : model.getFields()) {
            for (String name : field.getSerializedNameCandidates()) {
                method.addCode("case $S:\n", name);
            }
            method.addCode(field.buildReadCodeBlock(model.typeRegistry, objectName, "reader", model.modelType.reflectionName(), context));
            method.addStatement("break");
        }
        method.addCode("default:\n");
        method.addStatement("reader.skipValue()");
        method.addStatement("break");
        method.endControlFlow(); // switch
        method.endControlFlow(); // while
        method.addStatement("reader.endObject()");

        // NonNull checks
        for (FieldDefinition field : model.getFields()) {
            method.addCode(field.buildNullCheckCodeBlock(model.modelType.simpleName(), objectName));
            method.addCode(field.buildMustSetCheckFlagCodeBlock(model.modelType.simpleName()));
        }

        method.addStatement("return object", model.modelType);

        return method.build();
    }

    private JavaFile buildJavaFile() {
        return JavaFile.builder(getPackageName(), buildTypeSpec())
                .skipJavaLangImports(true)
                .build();
    }

    public void write() {
        try {
            buildJavaFile().writeTo(context.getSynchronizedFiler());
        } catch (IOException e) {
            throw new ProcessingException(e);
        }
    }
}
