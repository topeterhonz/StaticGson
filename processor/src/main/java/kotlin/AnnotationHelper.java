package kotlin;

import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.Element;

// Hack - deliberately placed in the Kotlin namespace in Java so Metadata class can be accessed
public class AnnotationHelper {

    @Nullable
    public static com.github.gfx.static_gson.Metadata getMetadata(Element element) {
        Metadata metadata = element.getAnnotation(Metadata.class);
        if (metadata != null) {
            return new com.github.gfx.static_gson.Metadata(metadata.k(), metadata.mv(), metadata.bv(), metadata.d1(),
                    metadata.d2(), metadata.xs(), metadata.xi());
        }
        return null;
    }
}
