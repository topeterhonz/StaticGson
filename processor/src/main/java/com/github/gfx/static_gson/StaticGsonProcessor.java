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
@SupportedAnnotationTypes("com.github.gfx.static_gson.annotation.*")
public class StaticGsonProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return true;
        }

        long t0 = System.currentTimeMillis();

        StaticGsonContext context = new StaticGsonContext(roundEnv, processingEnv);

        roundEnv.getElementsAnnotatedWith(JsonSerializable.class)
                .stream()
                .map(element -> new ModelDefinition(context, (TypeElement) element))
                .forEach(context::addModel);

        context.modelMap.values()
                .parallelStream()
                .map(model -> new TypeAdapterFactoryWriter(context, model))
                .forEach(TypeAdapterFactoryWriter::write);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                "[StaticGson] processed " + context.modelMap.size() + " of models in "
                        + (System.currentTimeMillis() - t0) + "ms");

        return true;
    }
}
