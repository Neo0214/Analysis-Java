package org.group05.analyzer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.util.ArrayList;

public class ASTGenerator {
    private ArrayList<CompilationUnit> cus;
    public ASTGenerator() {
        this.cus = new ArrayList<>();
    }

    public void generateAST(ArrayList<String> files) {
        for (String file : files) {
            try {
                CompilationUnit cu = StaticJavaParser.parse(new File(file));
                cus.add(cu);  // store all CompilationUnit into cus
            } catch (Exception e) {
                System.out.println("Error in parsing file: " + file);
            }
        }
    }
}
