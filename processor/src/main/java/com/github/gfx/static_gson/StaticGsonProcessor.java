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
        long t0 = System.currentTimeMillis();

        StaticGsonContext context = new StaticGsonContext(roundEnv, processingEnv);

        roundEnv.getElementsAnnotatedWith(JsonSerializable.class)
                .forEach(element -> {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                            "[StaticGson] processing " + element);

                    ModelDefinition model = new ModelDefinition(context, (TypeElement) element);

                    context.modelMap.put(model.modelType, model);
                });

        for (ModelDefinition model : context.modelMap.values()) {
            new TypeAdapterFactoryWriter(context, model).write();
        }

        if (!context.modelMap.isEmpty()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "[StaticGson] finished in " + (System.currentTimeMillis() - t0) + "ms");
        }

        return true;
    }
}
