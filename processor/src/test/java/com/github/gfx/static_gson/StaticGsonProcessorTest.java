package com.github.gfx.static_gson;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

@RunWith(JUnit4.class)
public class StaticGsonProcessorTest {

    @Test
    public void process() throws Exception {
        JavaFileObject modelFile = JavaFileObjects.forResource("Book.java");

        assert_().about(javaSource())
                .that(modelFile)
                .processedWith(new StaticGsonProcessor())
                .compilesWithoutWarnings();
    }

}