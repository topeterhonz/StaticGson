package com.github.gfx.static_gson;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;

public class TypeRegistry {

    private final Map<TypeName, FieldSpec> registry = new HashMap<>();

    public FieldSpec getField(TypeName type) {
        FieldSpec field = registry.get(type);
        if (field == null) {
            field = createField(type);
            registry.put(type, field);
        }
        return field;
    }

    public Collection<FieldSpec> getFields() {
        return registry.values();
    }

    public TypeRegistry() {
    }

    public CodeBlock getFieldInitialization() {
        CodeBlock.Builder block = CodeBlock.builder();
        registry.forEach((type, field) -> block.addStatement("$N = ($T) $L",
                field, ParameterizedTypeName.get(Types.TypeAdapter, type), toInitializer(type)));
        return block.build();
    }

    private static FieldSpec createField(TypeName type) {
        return FieldSpec.builder(ParameterizedTypeName.get(Types.TypeAdapter, type), toName(type))
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
    }

    private static CodeBlock toInitializer(TypeName type) {
        return CodeBlock.builder()
                .add("gson.getAdapter($T.get($L))", Types.TypeToken, toTypeExpr(type))
                .build();
    }

    private static CodeBlock toTypeExpr(TypeName type) {
        if (type instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedType = (ParameterizedTypeName) type;
            CodeBlock.Builder typeArgsExpr = CodeBlock.builder();
            for (int i = 0; i < parameterizedType.typeArguments.size(); i++) {
                TypeName t = parameterizedType.typeArguments.get(i);
                if (i != 0) {
                    typeArgsExpr.add(", $L", toTypeExpr(t));
                } else {
                    typeArgsExpr.add("$L", toTypeExpr(t));
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


    private static String toName(TypeName type) {
        return type.toString().replaceAll("[^a-zA-Z0-9?]+", "\\$");
    }
}
