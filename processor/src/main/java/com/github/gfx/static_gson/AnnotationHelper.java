package com.github.gfx.static_gson;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

final class AnnotationHelper {

    private AnnotationHelper() {
    }


    static boolean hasAnnotationWithName(Element element, String simpleName) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
            if (simpleName.equals(annotationName)) {
                return true;
            }
        }
        return false;
    }
}
