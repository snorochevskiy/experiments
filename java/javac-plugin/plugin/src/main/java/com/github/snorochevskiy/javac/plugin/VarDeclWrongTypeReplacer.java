package com.github.snorochevskiy.javac.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.ElementScanner8;

/**
 * This element scanner searches for variable declarations with type {@link wrongType}
 * and replaces the {@link wrongType} with {@link String}.
 */
public class VarDeclWrongTypeReplacer extends ElementScanner8<Object, Object> {

    // Allows to work with AST (part of compiler tree API)
    private final Trees mTrees;

    // Logger, allows to output messages during the compilation
    // Will abort compilation if ERROR message is sent
    private final Messager mLogger;

    // Allows to create new files in compiler sandbox during the compilation.
    // It automatically picks up correct directory in file system to place a file
    // e.g. in case of Gradle it's build/intermediates/classes
    private final Filer mFiler;

    private final Element mOriginElement;

    // Is used to manipulate AST
    // Is used by compiler for creating AST in the first compilation phase
    private final TreeMaker mTreeMaker;

    // Maps element names to AST constructions
    private final Names mNames;

    public VarDeclWrongTypeReplacer(ProcessingEnvironment env, Element element) {
        super();
        mTrees = Trees.instance(env);
        mLogger = env.getMessager();
        mFiler = env.getFiler();
        mOriginElement = element;
        final JavacProcessingEnvironment javacEnv = (JavacProcessingEnvironment) env;
        mTreeMaker = TreeMaker.instance(javacEnv.getContext());
        mNames = Names.instance(javacEnv.getContext());
    }

    @Override
    public Object visitExecutable(ExecutableElement e, Object p) {

        // Getting the body of a method
        BlockTree body = mTrees.getTree(e).getBody();
        // Iterating through statements
        for (StatementTree statement : body.getStatements()) {
            // If current statement is a varaible declaration
            if (statement instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl varDecl = (JCTree.JCVariableDecl) statement;
                // If the variable type is "wrongType", then change it to String
                if (varDecl.vartype.toString().equals("wrongType")) {
                    varDecl.accept(new TreeTranslator() {
                        @Override
                        public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
                            super.visitVarDef(jcVariableDecl);
                            jcVariableDecl.vartype = mTreeMaker.Ident(mNames.fromString("String"));
                        }
                    });
                }
            }
        }

        return scan(e.getParameters(), p);
    }

}
