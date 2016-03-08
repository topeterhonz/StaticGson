package com.github.gfx.static_gson;

import com.github.gfx.static_gson.annotation.StaticGsonGenerated;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;

public class Annotations {

    public static AnnotationSpec suppressWarnings(String... warnings) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(SuppressWarnings.class);
        CodeBlock.Builder names = CodeBlock.builder();
        boolean first = true;
        for (String warning : warnings) {
            if (first) {
                names.add("$S", warning);
                first = false;
            } else {
                names.add(", $S", warning);
            }
        }
        if (warnings.length == 1) {
            builder.addMember("value", names.build());
        } else {
            builder.addMember("value", "{$L}", names.build());
        }
        return builder.build();
    }

    public static AnnotationSpec staticGsonGenerated() {
        return AnnotationSpec.builder(StaticGsonGenerated.class)
                .addMember("value", "$S", StaticGsonProcessor.class.getCanonicalName())
                .build();
    }
}
