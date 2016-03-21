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

    public TypeRegistry() {
    }

    private static CodeBlock toInitializer(TypeName type) {
        return CodeBlock.of("gson.getAdapter($T.get($L))", Types.TypeToken, toTypeExpr(type));
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
            return CodeBlock.of("$T.newParameterizedTypeWithOwner(null, $T.class, $L)",
                            Types.$Gson$Types, parameterizedType.rawType, typeArgsExpr.build());
        } else {
            return CodeBlock.of("$T.class", type);
        }
    }

    private static String toName(TypeName type) {
        return "$" + type.toString().replaceAll("[^a-zA-Z0-9?]+", "\\$");
    }

    public FieldSpec getField(TypeName type) {
        return registry.compute(type, (typeName, fieldSpec) -> {
            if (fieldSpec == null) {
                return FieldSpec.builder(Types.getTypeAdapter(typeName), toName(typeName))
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build();
            } else {
                return fieldSpec;
            }
        });
    }

    public Collection<FieldSpec> getFields() {
        return registry.values();
    }

    public CodeBlock getFieldInitialization() {
        CodeBlock.Builder block = CodeBlock.builder();
        registry.forEach((type, field) -> {
            if (type instanceof ParameterizedTypeName) {
                block.addStatement("$N = ($T) $L",
                        field, Types.getTypeAdapter(type), toInitializer(type));
            } else {
                block.addStatement("$N = $L",
                        field, toInitializer(type));
            }
        });
        return block.build();
    }
}
