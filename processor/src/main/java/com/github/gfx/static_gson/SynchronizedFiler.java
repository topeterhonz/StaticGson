package com.github.gfx.static_gson;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class SynchronizedFiler implements Filer {

    private final Filer parent;

    public SynchronizedFiler(Filer parent) {
        this.parent = parent;
    }

    @Override
    public synchronized JavaFileObject createSourceFile(CharSequence name,
            Element... originatingElements)
            throws IOException {
        return parent.createSourceFile(name, originatingElements);
    }

    @Override
    public synchronized JavaFileObject createClassFile(CharSequence name,
            Element... originatingElements)
            throws IOException {
        return parent.createClassFile(name, originatingElements);
    }

    @Override
    public synchronized FileObject createResource(JavaFileManager.Location location,
            CharSequence pkg,
            CharSequence relativeName,
            Element... originatingElements)
            throws IOException {
        return parent.createResource(location, pkg, relativeName, originatingElements);
    }

    @Override
    public synchronized FileObject getResource(JavaFileManager.Location location,
            CharSequence pkg,
            CharSequence relativeName) throws IOException {
        return parent.getResource(location, pkg, relativeName);
    }
}
