package com.github.gfx.static_gson;

import com.google.gson.FieldNamingPolicy;

import com.github.gfx.static_gson.annotation.JsonSerializable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class ModelDefinition {

    public final TypeElement element;

    public final ClassName modelType;

    public final TypeRegistry typeRegistry = new TypeRegistry();

    private final StaticGsonContext context;

    private final List<FieldDefinition> fields;

    public ModelDefinition(StaticGsonContext context, TypeElement element) {
        this.context = context;
        this.element = element;

        modelType = ClassName.get(element);

        JsonSerializable annotation = element.getAnnotation(JsonSerializable.class);
        fields = extractFields(annotation, element);
    }

    private static List<FieldDefinition> extractFields(
            JsonSerializable config,
            TypeElement typeElement) {
        return typeElement.getEnclosedElements().stream()
                .filter(element -> element instanceof VariableElement)
                .map(element -> (VariableElement) element)
                .filter(element -> !element.getModifiers().contains(Modifier.TRANSIENT))
                .map(element -> new FieldDefinition(config, element))
                .collect(Collectors.toList());
    }

    public Set<TypeName> getComplexTypes() {
        return fields.stream()
                .filter(field -> !field.isSimpleType())
                .map(FieldDefinition::getType)
                .collect(Collectors.toSet());
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }
}
