package com.github.gfx.static_gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.annotations.SerializedName;

import com.github.gfx.static_gson.annotation.JsonSerializable;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.lang.model.element.VariableElement;

public class FieldDefinition {

    private final JsonSerializable config;

    private final VariableElement element;

    private final TypeName type;

    private final String fieldName;

    private final String serializedName;

    private final List<String> serializedNameCandidates;

    public FieldDefinition(JsonSerializable config, VariableElement element) {
        this.config = config;
        this.element = element;
        type = TypeName.get(element.asType());
        fieldName = element.getSimpleName().toString();
        serializedNameCandidates = new ArrayList<>();

        SerializedName annotation = element.getAnnotation(SerializedName.class);
        if (annotation != null) {
            serializedName = annotation.value();
            serializedNameCandidates.add(serializedName);
            Collections.addAll(serializedNameCandidates, annotation.alternate());
        } else {
            serializedName = translateName(fieldName);
            serializedNameCandidates.add(serializedName);
        }
    }

    public VariableElement getElement() {
        return element;
    }

    public TypeName getType() {
        return type;
    }

    public List<String> getSerializedNameCandidates() {
        return serializedNameCandidates;
    }

    public boolean isSimpleType() {
        return type.equals(TypeName.BOOLEAN)
                || type.equals(TypeName.LONG)
                || type.equals(TypeName.INT)
                || type.equals(TypeName.BYTE)
                || type.equals(TypeName.SHORT)
                || type.equals(TypeName.FLOAT)
                || type.equals(TypeName.DOUBLE)
                || type.equals(Types.String);
    }

    /**
     *
     * @param typeRegistry A type registry for the model type
     * @param object A name of the target object
     * @param writer A {@link com.google.gson.stream.JsonWriter} instance
     * @return Statements to write the field
     */
    public CodeBlock buildWriteBlock(TypeRegistry typeRegistry, String object, String writer) {
        CodeBlock.Builder block = CodeBlock.builder();
        if (!type.isPrimitive() && !config.serializeNulls()) {
            block.beginControlFlow("if ($L.$L != null)", object, fieldName);
        }

        block.addStatement("$L.name($S)", writer, serializedName);

        if (!type.isPrimitive() && config.serializeNulls()) {
            block.beginControlFlow("if ($L.$L != null)", object, fieldName);
        }

        if (isSimpleType()) {
            block.addStatement("$L.value($L.$L)", writer, object, fieldName);
        } else {
            block.addStatement("$N.write($L, $L.$L)",
                    typeRegistry.getField(type), writer, object, fieldName);
        }

        if (!type.isPrimitive()) {
            block.endControlFlow();
            if (config.serializeNulls()) {
                block.beginControlFlow("else");
                block.addStatement("$L.nullValue()", writer);
                block.endControlFlow();
            }
        }
        return block.build();
    }

    /**
     *
     * @param typeRegistry A type registry for the model type
     * @param object A name of the target object
     * @param reader A {@link com.google.gson.stream.JsonReader} instance
     * @return An expression to read the field
     */
    public CodeBlock buildReadCodeBlock(TypeRegistry typeRegistry, String object, String reader) {
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
            block.addStatement("$L.$L = $N.read($L)",
                    object, fieldName, typeRegistry.getField(type), reader);
        }

        return block.build();
    }


    private String translateName(String name) {
        switch (config.fieldNamingPolicy()) {
            case UPPER_CAMEL_CASE:
                return upperCaseFirstLetter(name);
            case UPPER_CAMEL_CASE_WITH_SPACES:
                return upperCaseFirstLetter(separateCamelCase(name, " "));
            case LOWER_CASE_WITH_UNDERSCORES:
                return separateCamelCase(name, "_").toLowerCase(Locale.ENGLISH);
            case LOWER_CASE_WITH_DASHES:
                return separateCamelCase(name, "-").toLowerCase(Locale.ENGLISH);
            default: // IDENTITY
                return name;
        }
    }

    private static String separateCamelCase(String name, String separator) {
        StringBuilder translation = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);
            if (Character.isUpperCase(character) && translation.length() != 0) {
                translation.append(separator);
            }
            translation.append(character);
        }
        return translation.toString();
    }

    private static String upperCaseFirstLetter(String name) {
        StringBuilder fieldNameBuilder = new StringBuilder();
        int index = 0;
        char firstCharacter = name.charAt(index);

        while (index < name.length() - 1) {
            if (Character.isLetter(firstCharacter)) {
                break;
            }

            fieldNameBuilder.append(firstCharacter);
            firstCharacter = name.charAt(++index);
        }

        if (index == name.length()) {
            return fieldNameBuilder.toString();
        }

        if (!Character.isUpperCase(firstCharacter)) {
            String modifiedTarget = modifyString(Character.toUpperCase(firstCharacter), name, ++index);
            return fieldNameBuilder.append(modifiedTarget).toString();
        } else {
            return name;
        }
    }

    private static String modifyString(char firstCharacter, String srcString, int indexOfSubstring) {
        return (indexOfSubstring < srcString.length())
                ? firstCharacter + srcString.substring(indexOfSubstring)
                : String.valueOf(firstCharacter);
    }
}
