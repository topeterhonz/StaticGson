package com.github.gfx.static_gson;

import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonToken;

import com.github.gfx.static_gson.annotation.JsonMustSet;
import com.github.gfx.static_gson.annotation.JsonSerializable;
import com.github.gfx.static_gson.annotation.JsonStrict;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

public class FieldDefinition {

    private static final String NONNULL_ANNOTATION_NAME = "NonNull";

    private static final String NOTNULL_ANNOTATION_NAME = "NotNull";

    private final JsonSerializable config;

    private final VariableElement element;

    private final TypeName type;

    private final String fieldName;

    private final String serializedName;

    private final List<String> serializedNameCandidates;

    private final boolean strict;

    private final boolean nonNull;

    private final boolean nullable;

    private final boolean mustSet;

    private final boolean isKotlin;

    private final boolean hasDeclaredDefault;


    public FieldDefinition(JsonSerializable config, VariableElement element, boolean isKotlin, boolean hasDeclaredDefault) {
        this.config = config;
        this.element = element;
        this.isKotlin = isKotlin;
        this.hasDeclaredDefault = hasDeclaredDefault;
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
        strict = element.getAnnotation(JsonStrict.class) != null;
        mustSet = element.getAnnotation(JsonMustSet.class) != null;
        nonNull = AnnotationHelper.hasAnnotationWithName(element, NONNULL_ANNOTATION_NAME)
                || AnnotationHelper.hasAnnotationWithName(element, NOTNULL_ANNOTATION_NAME);
        nullable = element.getAnnotation(Nullable.class) != null;
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

    public VariableElement getElement() {
        return element;
    }

    public TypeName getType() {
        return type;
    }

    public List<String> getSerializedNameCandidates() {
        return serializedNameCandidates;
    }

    /**
     * @return {@code true} if the type doesn't require a specific type adapter
     */
    public boolean isSimpleType() {
        // long and Long are not a simple type because of LongSerializationPolicy
        return type.equals(TypeName.BOOLEAN)
                || type.equals(TypeName.INT)
                || type.equals(TypeName.BYTE)
                || type.equals(TypeName.SHORT)
                || type.equals(TypeName.FLOAT)
                || type.equals(TypeName.DOUBLE)
                || type.equals(TypeName.BOOLEAN.box())
                || type.equals(TypeName.INT.box())
                || type.equals(TypeName.BYTE.box())
                || type.equals(TypeName.SHORT.box())
                || type.equals(TypeName.FLOAT.box())
                || type.equals(TypeName.DOUBLE.box())
                || type.equals(Types.String);
    }

    /**
     * @param typeRegistry A type registry for the model type
     * @param object       A name of the target object
     * @param writer       A {@link com.google.gson.stream.JsonWriter} instance
     * @return Statements to write the field
     */
    public CodeBlock buildWriteBlock(TypeRegistry typeRegistry, String object, String writer) {
        CodeBlock.Builder block = CodeBlock.builder();

        String field = "field$" + fieldName;

        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            block.beginControlFlow("try");
            block.addStatement("$T f = $L.getClass().getDeclaredField($S)", Field.class, object, fieldName);
            block.addStatement("f.setAccessible(true)");
            block.addStatement("$T $L = ($T)f.get(value)", element.asType(), field, element.asType());
        } else {
            block.addStatement("$T $L = $L.$L", element.asType(), field, object, fieldName);
        }

        if (!type.isPrimitive() && !config.serializeNulls()) {
            block.beginControlFlow("if ($L != null)", field);
        }

        block.addStatement("$L.name($S)", writer, serializedName);

        if (!type.isPrimitive() && config.serializeNulls()) {
            block.beginControlFlow("if ($L != null)", field);
        }

        if (isSimpleType()) {
            block.addStatement("$L.value($L)", writer, field);
        } else {
            block.addStatement("$N.write($L, $L)", typeRegistry.getField(type), writer, field);
        }

        if (!type.isPrimitive()) {
            block.endControlFlow();
            if (config.serializeNulls()) {
                block.beginControlFlow("else");
                block.addStatement("$L.nullValue()", writer);
                block.endControlFlow();
            }
        }

        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            block.nextControlFlow("catch ($T|$T ex)", NoSuchFieldException.class, IllegalAccessException.class);
            block.endControlFlow();
        }
        return block.build();
    }

    /**
     * @param typeRegistry A type registry for the model type
     * @param object       A name of the target object
     * @param reader       A {@link com.google.gson.stream.JsonReader} instance
     * @return An expression to read the field
     */
    public CodeBlock buildReadCodeBlock(TypeRegistry typeRegistry, String object, String reader, String className,
            StaticGsonContext context) {

        // check private
        CodeBlock.Builder block = CodeBlock.builder();

        TypeName unboxType;
        try {
            unboxType = type.unbox();
        } catch (UnsupportedOperationException e) {
            unboxType = type;
        }

        block.add(buildReadNullValueBlock(reader));

        block.beginControlFlow("try");

        if (element.getModifiers().contains(Modifier.PRIVATE)) {

            block.addStatement("$T f = $L.getClass().getDeclaredField($S)", Field.class, object, fieldName);
            block.addStatement("f.setAccessible(true)");

            if (unboxType.equals(TypeName.BOOLEAN)) {
                block.addStatement(
                        "f.set($L, com.github.gfx.static_gson.ParserHelper.nextRelaxedBoolean($L, $L, $L, (Boolean)f.get($L)))", object,
                        reader, nullable, strict || nonNull || mustSet || (isKotlin && !nullable && !hasDeclaredDefault), object);
            } else if (unboxType.equals(TypeName.LONG)) {
                block.addStatement("f.set($L, $L.nextLong())", object, reader);
            } else if (unboxType.equals(TypeName.INT)
                    || unboxType.equals(TypeName.BYTE)
                    || unboxType.equals(TypeName.SHORT)) {
                block.addStatement("f.set($L, ($T) $L.nextLong())", object, unboxType, reader);
            } else if (unboxType.equals(TypeName.DOUBLE)) {
                block.addStatement("f.set($L, $L.nextDouble())", object, reader);
            } else if (unboxType.equals(TypeName.FLOAT)) {
                block.addStatement("f.set($L, ($T) $L.nextDouble())", object, unboxType, reader);
            } else if (unboxType.equals(Types.String)) {
                block.addStatement("f.set($L, $L.nextString())", object, reader);
            } else {
                checkFieldTypeJsonSerializable(className, context);
                block.addStatement("f.set($L, $N.read($L))",
                        object, typeRegistry.getField(type), reader);
            }

        } else {
            if (unboxType.equals(TypeName.BOOLEAN)) {
                block.addStatement("$L.$L = com.github.gfx.static_gson.ParserHelper.nextRelaxedBoolean($L, $L, $L, $L.$L)",
                        object, fieldName, reader, nullable, strict || nonNull || mustSet || (isKotlin && !nullable && !hasDeclaredDefault), object, fieldName);
            } else if (unboxType.equals(TypeName.LONG)) {
                block.addStatement("$L.$L = $L.nextLong()", object, fieldName, reader);
            } else if (unboxType.equals(TypeName.INT)
                    || unboxType.equals(TypeName.BYTE)
                    || unboxType.equals(TypeName.SHORT)) {
                block.addStatement("$L.$L = ($T) $L.nextLong()", object, fieldName, unboxType, reader);
            } else if (unboxType.equals(TypeName.DOUBLE)) {
                block.addStatement("$L.$L = $L.nextDouble()", object, fieldName, reader);
            } else if (unboxType.equals(TypeName.FLOAT)) {
                block.addStatement("$L.$L = ($T) $L.nextDouble()", object, fieldName, unboxType, reader);
            } else if (unboxType.equals(Types.String)) {
                block.addStatement("$L.$L = $L.nextString()", object, fieldName, reader);
            } else {
                checkFieldTypeJsonSerializable(className, context);
                block.addStatement("$L.$L = $N.read($L)",
                        object, fieldName, typeRegistry.getField(type), reader);
            }
        }

        block.add(buildMustSetFlagCodeBlock());

        block.nextControlFlow("catch ($T ex)", JsonUngracefulException.class);
        block.addStatement("throw ex");

        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            block.nextControlFlow("catch ($T|$T ex)", NoSuchFieldException.class, IllegalAccessException.class);
        }
        block.nextControlFlow("catch ($T ex)", Exception.class);

        block.beginControlFlow("if ($L.peek() != $T.$L)", reader, JsonToken.class,
                JsonToken.END_OBJECT);
        block.addStatement("$L.skipValue()", reader);
        block.endControlFlow();

        if (strict || nonNull || mustSet || (isKotlin && !nullable && !hasDeclaredDefault)) {
            // skip all other values
            block.beginControlFlow("while ($L.peek() != $T.$L)", reader, JsonToken.class,
                    JsonToken.END_OBJECT);
            block.addStatement("$L.skipValue()", reader);
            block.endControlFlow();

            block.addStatement("reader.endObject()");
            block.addStatement("throw new $T(ex)", JsonGracefulException.class);
        } else {
            ClassName log = ClassName.get("com.github.gfx.static_gson", "Logger");
            block.addStatement("$T.log(ex)", log);
        }
        block.endControlFlow();

        return block.build();
    }

    private void checkFieldTypeJsonSerializable(String className, StaticGsonContext context) {
        TypeName checkType;
        if (type instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) type;
            if (parameterizedTypeName.rawType.simpleName().equals(List.class.getSimpleName())) {
                checkType = parameterizedTypeName.typeArguments.get(0);
            } else if (parameterizedTypeName.rawType.simpleName().equals(Map.class.getSimpleName())) {
                checkType = parameterizedTypeName.typeArguments.get(1);
            } else {
                checkType = type;
            }
        } else {
            checkType = type;
        }

        if (checkType.isPrimitive()
                || checkType.isBoxedPrimitive()
                || checkType.equals(Types.String)
                || checkType.equals(Types.Date)
                || checkType.equals(Types.Calendar)
                || checkType.equals(Types.Object)) {
            // ignore primitive and strings
            return;
        }

        TypeElement typeElement = context.getTypeElement(checkType.toString());

        if (typeElement == null) {
            return;
        }

        // ignore enum
        if (typeElement.getKind() == ElementKind.ENUM) {
            return;
        }

        boolean jsonSerializable = typeElement.getAnnotation(JsonSerializable.class) != null;

        if (!jsonSerializable) {
            context.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s at %s.%s must be annotated with @JsonSerializable", type.toString(), className,
                            fieldName));
        }
    }

    public CodeBlock buildNullCheckCodeBlock(String className, String object) {
        CodeBlock.Builder block = CodeBlock.builder();
        if ((nonNull || isKotlin && !nullable && !hasDeclaredDefault) && !type.isPrimitive()) {
            if (element.getModifiers().contains(Modifier.PRIVATE)) {
                block.beginControlFlow("try");
                String field = "field$" + fieldName;
                block.addStatement("$T $L = $L.getClass().getDeclaredField($S)", Field.class, field, object, fieldName);
                block.addStatement("$L.setAccessible(true)", field);
                block.beginControlFlow("if ($L.get($L) == null)", field, object);

                block.addStatement("throw new $T(\"$L.$L must not be null\")", JsonGracefulException.class, className,
                        fieldName);
                block.endControlFlow();
                block.nextControlFlow("catch ($T|$T ex)", NoSuchFieldException.class, IllegalAccessException.class);
                block.endControlFlow();

            } else {
                block.beginControlFlow("if ($L.$L == null)", object, fieldName);
                block.addStatement("throw new $T(\"$L.$L must not be null\")", JsonGracefulException.class, className,
                        fieldName);
                block.endControlFlow();
            }
        }
        return block.build();
    }

    public CodeBlock buildMustDeclareFlagCodeBlock() {
        CodeBlock.Builder block = CodeBlock.builder();
        if ((mustSet || isKotlin && !nullable && !hasDeclaredDefault) && type.isPrimitive()) {
            block.addStatement("boolean $LSet = false", fieldName);
        }
        return block.build();
    }

    public CodeBlock buildMustSetFlagCodeBlock() {
        CodeBlock.Builder block = CodeBlock.builder();
        if ((mustSet || isKotlin && !nullable && !hasDeclaredDefault) && type.isPrimitive()) {
            block.addStatement("$LSet = true", fieldName);
        }
        return block.build();
    }

    public CodeBlock buildMustSetCheckFlagCodeBlock(String className) {
        CodeBlock.Builder block = CodeBlock.builder();
        if ((mustSet || isKotlin && !nullable && !hasDeclaredDefault) && type.isPrimitive()) {
            block.beginControlFlow("if (!$LSet)", fieldName);
            block.addStatement("throw new $T(\"$L.$L must be set\")", JsonGracefulException.class, className, fieldName);
            block.endControlFlow();
        }
        return block.build();
    }

    private CodeBlock buildReadNullValueBlock(String reader) {
        CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow("if ($L.peek() == $T.$L)", reader, JsonToken.class, JsonToken.NULL);
        block.addStatement("$L.nextNull()", reader);
        block.addStatement("break");
        block.endControlFlow();
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

}
