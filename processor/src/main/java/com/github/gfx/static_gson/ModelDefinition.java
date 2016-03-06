package com.github.gfx.static_gson;

import com.squareup.javapoet.ClassName;

import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class ModelDefinition {

    public final TypeElement element;

    public final ClassName modelType;

    private final StaticGsonContext context;

    private final List<FieldDefinition> fields;

    public ModelDefinition(StaticGsonContext context, TypeElement element) {
        this.context = context;
        this.element = element;

        modelType = ClassName.get(element);

        fields = extractFields(context, element);
    }

    private static List<FieldDefinition> extractFields(StaticGsonContext context,
            TypeElement typeElement) {
        return typeElement.getEnclosedElements().stream()
                .filter(element -> element instanceof VariableElement)
                .map(element -> (VariableElement) element)
                .filter(element -> !element.getModifiers().contains(Modifier.TRANSIENT))
                .map(element -> new FieldDefinition(context, element))
                .collect(Collectors.toList());
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }
}
