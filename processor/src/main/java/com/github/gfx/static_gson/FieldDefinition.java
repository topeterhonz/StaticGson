package com.github.gfx.static_gson;

import com.google.gson.annotations.SerializedName;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.VariableElement;

public class FieldDefinition {

    private final StaticGsonContext context;

    private final VariableElement element;

    private final TypeName type;

    private final String fieldName;

    private final String serializedName;

    private final List<String> serializedNameCandidates;

    public FieldDefinition(StaticGsonContext context, VariableElement element) {
        this.context = context;
        this.element = element;
        type = TypeName.get(element.asType());
        fieldName = element.getSimpleName().toString();
        serializedNameCandidates = new ArrayList<>();

        SerializedName annotation = element.getAnnotation(SerializedName.class);
        if (annotation != null) {
            serializedName = annotation.value();
            Collections.addAll(serializedNameCandidates, annotation.alternate());
        } else {
            serializedName = fieldName;
            serializedNameCandidates.add(fieldName);
        }
    }

    private static CodeBlock getTypeExpr(TypeName type) {
        if (type instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedType = (ParameterizedTypeName) type;
            CodeBlock.Builder typeArgsExpr = CodeBlock.builder();
            for (int i = 0; i < parameterizedType.typeArguments.size(); i++) {
                TypeName t = parameterizedType.typeArguments.get(i);
                if (i != 0) {
                    typeArgsExpr.add(", $L", getTypeExpr(t));
                } else {
                    typeArgsExpr.add("$L", getTypeExpr(t));
                }
            }

            return CodeBlock.builder()
                    .add("$T.newParameterizedTypeWithOwner(null, $T.class, $L)",
                            Types.$Gson$Types, parameterizedType.rawType, typeArgsExpr.build())
                    .build();
        } else {
            return CodeBlock.builder()
                    .add("$T.class", type)
                    .build();
        }
    }

    public VariableElement getElement() {
        return element;
    }

    public List<String> getSerializedNameCandidates() {
        return serializedNameCandidates;
    }

    /**
     * @param object A name of the target object
     * @param writer A {@link com.google.gson.stream.JsonWriter} instance
     * @return Statements to write the field
     */
    public CodeBlock buildWriteBlock(String object, String writer) {
        CodeBlock.Builder block = CodeBlock.builder();
        block.addStatement("$L.name($S)", writer, serializedName);
        if (type.equals(TypeName.BOOLEAN)
                || type.equals(TypeName.LONG)
                || type.equals(TypeName.INT)
                || type.equals(TypeName.BYTE)
                || type.equals(TypeName.SHORT)
                || type.equals(TypeName.FLOAT)
                || type.equals(TypeName.DOUBLE)
                || type.equals(Types.String)
                ) {
            block.addStatement("$L.value($L.$L)", writer, object, fieldName);
        } else {
            block.addStatement("$L.jsonValue(gson.toJson($L.$L))", writer, object, fieldName);
        }
        return block.build();
    }

    /**
     * @param object A name of the target object
     * @param reader A {@link com.google.gson.stream.JsonReader} instance
     * @return An expression to read the field
     */
    public CodeBlock buildReadCodeBlock(String object, String reader) {
        CodeBlock.Builder block = CodeBlock.builder();
        if (type.equals(TypeName.BOOLEAN)) {
            block.addStatement("$L.$L = $L.nextBoolean()", object, fieldName, reader);
        } else if (type.equals(TypeName.LONG)) {
            block.addStatement("$L.$L = $L.nextLong()", object, fieldName, reader);

        } else if (type.equals(TypeName.INT)
                || type.equals(TypeName.BYTE) || type.equals(TypeName.SHORT)) {
            block.addStatement("$L.$L = ($T) $L.nextLong()", object, fieldName, type, reader);
        } else if (type.equals(TypeName.DOUBLE)) {
            block.addStatement("$L.$L = $L.nextDouble()", object, fieldName, reader);
        } else if (type.equals(TypeName.FLOAT)) {
            block.addStatement("$L.$L = ($T) $L.nextDouble()", object, fieldName, type, reader);
        } else if (type.equals(Types.String)) {
            block.addStatement("$L.$L = $L.nextString()", object, fieldName, reader);
        } else {
            block.addStatement("$L.$L = gson.fromJson($L, $L)", object, fieldName, reader,
                    getTypeExpr(type));
        }

        return block.build();
    }
}
