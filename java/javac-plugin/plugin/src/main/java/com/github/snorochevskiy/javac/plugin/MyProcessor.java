package com.github.snorochevskiy.javac.plugin;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("*") // Applied to all classes
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MyProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (final Element element : roundEnv.getRootElements()) {
            inspectElement(element);
        }

        return false;
    }

    private void inspectElement(final Element element) {

        // interface javax.lang.model.element.Element is a high level abstraction for AST elements, that doesn't allow much.
        // For low-level AST manipulations, javac provides com.sun.tools.javac.tree.JCTree

        // If javax.lang.model.element.Element corresponds to common Main class, then:
        // element.getSimpleName().toString() -> Main
        // element.getClass().getName()       -> com.sun.tools.javac.code.Symbol$ClassSymbol (subclass of Element)
        // element.getKind().name()           -> CLASS

        // Element can be one of following types:
        // * PackageElement - package
        // * TypeElement - class
        // * VariableElement - class field
        // * Parameterizable - ???
        // * TypeParameterElement - ???
        // * QualifiedNameable - ???
        // * ExecutableElement - method
        // ExecutableElement - is what we need. We'll get JCTree from ExecutableElement and will parse the method AST
        // to find variable declaration statements.
        if (element instanceof ExecutableElement) {
            element.accept(new VarDeclWrongTypeReplacer(processingEnv, element), element);
        }

        element.getEnclosedElements().forEach(this::inspectElement);
    }
}
