package org.group05.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.group05.analyzer.dataStructure.MethodNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is used as a medium for the front end and the back end
 */
public class MainAnalyzer {
    private File project;
    private ArrayList<String> fileList;
    private ArrayList<CompilationUnit> compilationUnits;
    private MethodAnalyzer methodAnalyzer;
    private ParameterAnalyzer parameterAnalyzer;
    public MainAnalyzer(String filePath) {
        this.fileList = new ArrayList<>();
        setRootPath(filePath);
    }

    /**
     * setRootPath
     * set the root path of the project
     * @param filePath the path of the project
     * @return true if the path is valid
     */
    public boolean setRootPath(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory() && hasJavaFile(file)) {
            this.project = file;

            compilationUnits = ASTGenerator.generateAST(fileList,filePath);
            //this.methodAnalyzer = new MethodAnalyzer(compilationUnits);
            this.parameterAnalyzer = new ParameterAnalyzer(compilationUnits);
            return true;
        } else {
            return false;
        }
    }



    /**
     * methodQuery
     * do method query
     * @param command the command of method query
     */
    public void methodQuery(String command) {
        // use MethodAnalyzer to do method query

        String[] commandList=command.split(" ");
        String methodName=commandList[1];
        String className=commandList[2];
        int depth=Integer.parseInt(commandList[3]);
        ArrayList<String> paramList=new ArrayList<>();

        for(int i=4;i<commandList.length; i++){
            paramList.add(commandList[i]);
        }
        this.methodAnalyzer=new MethodAnalyzer(this.compilationUnits);
        this.methodAnalyzer.analyze(methodName, className, depth, paramList);
    }



    /**
     * hasJavaFile
     * check if the project has java file and set fileList as all java file
     *
     * @return true if the project has java file
     */
    private boolean hasJavaFile(File file) {
        accessFile(file);
        return !this.fileList.isEmpty();
    }

    /**
     * accessFile
     * access all java file in the project
     * @param file the file to be accessed
     */
    private void accessFile(File file) {
        if (file.isDirectory()) {
            // if is directory, access all child files
            File[] files = file.listFiles();
            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                accessFile(files[i]);
            }
        } else if (file.isFile()) {
            // if is file, check if it is java file
            if (file.getName().endsWith(".java")) {
                this.fileList.add(file.getAbsolutePath());
            }
        }
    }
}
