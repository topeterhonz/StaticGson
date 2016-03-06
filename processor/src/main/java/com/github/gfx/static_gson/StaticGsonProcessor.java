package com.github.gfx.static_gson;

import com.github.gfx.static_gson.annotation.JsonSerializable;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.github.gfx.static_gson.*")
public class StaticGsonProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return true;
        }

        StaticGsonContext context = new StaticGsonContext(roundEnv, processingEnv);

        roundEnv.getElementsAnnotatedWith(JsonSerializable.class)
                .stream()
                .forEach(element -> {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                            "Processing " + element);

                    ModelDefinition model = new ModelDefinition(context, (TypeElement) element);

                    context.modelMap.put(model.modelType, model);
                });

        for (ModelDefinition model : context.modelMap.values()) {
            new TypeAdapterFactoryWriter(context, model).write();
        }

        return true;
    }
}
