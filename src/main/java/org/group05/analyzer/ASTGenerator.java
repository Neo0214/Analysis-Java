package org.group05.analyzer;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.BufferedReader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import org.group05.analyzer.dataStructure.MethodNode;

public class ASTGenerator {
    private ArrayList<MethodNode> methodNodeList;

    public ASTGenerator() {
        methodNodeList = new ArrayList<>();
    }


    /**
     * This method is used to load the AST
     * @param javaFile the file
     */
    private static CompilationUnit parseJavaFile(File javaFile) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(javaFile);
            return cu;

            //new MethodCallVisitor().visit(cu, methodNodeList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method is used to load the project's AST
     * @param files the list of files
     * @param filePath the path of the project
     * @return the AST
     */
    public static ArrayList<CompilationUnit> generateAST(ArrayList<String> files, String filePath) {
        //ASTGenerator analyzer = new ASTGenerator();
        //analyzer.loadProject(filePath);
        //System.out.println("打印得到的方法调用关系如下：");
        ArrayList<CompilationUnit> cus = new ArrayList<>();
        //
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        String srcPath = filePath + "/src/main/java";
        combinedTypeSolver.add(new JavaParserTypeSolver(srcPath));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);

        for (String file : files) {
            CompilationUnit cu=parseJavaFile(new File(file));
            cus.add(cu);
        }
        return cus;
    }
}