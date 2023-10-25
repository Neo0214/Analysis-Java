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
     * This method is used to parse the java file and generate the AST.
     * @param javaFile the java file to be parsed
     */
    private static CompilationUnit parseJavaFile(File javaFile) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(javaFile);
            return cu;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method is used to load the project.
     * @param files the list of files in the project
     * @param filePath the path of the project
     */
    public static ArrayList<CompilationUnit> generateAST(ArrayList<String> files, String filePath) {
        //ASTGenerator analyzer = new ASTGenerator();
        //analyzer.loadProject(filePath);
        ArrayList<CompilationUnit> cus = new ArrayList<>();

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