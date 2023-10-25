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

    // 打印java代码源文件的所有内容（测试用）
    private void printJavaFile(File javaFile) {
        System.out.println(javaFile);
        try (BufferedReader reader = new BufferedReader(new FileReader(javaFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 解析Java源代码文件,将方法调用关系加入到方法调用图中
    private static CompilationUnit parseJavaFile(File javaFile) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(javaFile);
            return cu;
            // 创建一个访问者来查找方法调用关系
            //new MethodCallVisitor().visit(cu, methodNodeList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<CompilationUnit> generateAST(ArrayList<String> files, String filePath) {
        //ASTGenerator analyzer = new ASTGenerator();
        //analyzer.loadProject(filePath);
        //System.out.println("打印得到的方法调用关系如下：");
        ArrayList<CompilationUnit> cus = new ArrayList<>();
        // 创建类型解析器
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