package com.github.gfx.static_gson;

import com.github.gfx.static_gson.annotation.JsonSerializable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;

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

        fields = new ArrayList<>();

        while (true) {
            fields.addAll(extractFields(annotation, element));
            if (element.getSuperclass().toString().equals(Object.class.getName())) {
                // reached the root
                break;
            }

            TypeElement superElement = context.getTypeElement(element.getSuperclass());

            if (superElement == null) {
                context.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        String.format("Unable create static gson for %s. Perhaps this is a generic type which is not supported yet", element.getSuperclass().toString()));
                break;
            }

            element = superElement;
        }
    }

    private static List<FieldDefinition> extractFields(
            JsonSerializable config,
            TypeElement typeElement) {

        return typeElement.getEnclosedElements().stream()
                .filter(element -> element instanceof VariableElement)
                .map(element -> (VariableElement) element)
                .filter(element -> !element.getModifiers().contains(Modifier.TRANSIENT))
                .filter(element -> !element.getModifiers().contains(Modifier.STATIC))
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
