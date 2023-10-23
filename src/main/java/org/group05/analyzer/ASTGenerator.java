package org.group05.analyzer;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.BufferedReader;

import java.util.*;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import org.group05.analyzer.dataStructure.MethodNode;

public class ASTGenerator {
    private ArrayList<MethodNode> methodNodeList;

    //无参构造函数，新建一个空的哈希表分配给私有成员methodCallGraph，用以存储方法调用关系
    public ASTGenerator() {
        methodNodeList = new ArrayList<>();
    }

    //内部类，重写访问者适配器，完成对调用方法表达式的遍历
    private static class MethodCallVisitor extends VoidVisitorAdapter<ArrayList<MethodNode>> {
        @Override
        public void visit(MethodCallExpr methodCallExpr, ArrayList<MethodNode> methodNodeList) {
            // 获取被调用方法的类名和方法名

            //获取被调用方法的类名
            String calledClass = methodCallExpr.getScope().map(scope -> scope.toString()).orElse("");
            //获取被调用方法名
            String calledMethod = methodCallExpr.getNameAsString();

            //获取主动调用方法的类名和方法名

            //获取主动调用方法名
            MethodDeclaration callingMethod = methodCallExpr.findAncestor(MethodDeclaration.class).orElse(null);
            String callingMethodName = callingMethod != null ? callingMethod.getNameAsString() : "";
            //获取主动调用类名
            Optional<ClassOrInterfaceDeclaration> classDeclaration = methodCallExpr.findAncestor(ClassOrInterfaceDeclaration.class);
            String callingMethodclass = classDeclaration.map(c -> c.getNameAsString()).orElse("");

            //获取调用语句的参数
            List<Expression> arguments = methodCallExpr.getArguments();
            ArrayList<String> callArgs = new ArrayList<>();
            for (Expression argument : arguments) {
                callArgs.add(argument.toString());
            }

            //创建两个新的methodNode对象并把被调用者加入调用者的CallRecord
            MethodNode callingMethodNode = new MethodNode(callingMethodName, callingMethodclass);
            MethodNode calledMethodNode = new MethodNode(calledMethod, calledClass);
            callingMethodNode.addCalledMethod(calledMethodNode, callArgs);
            boolean callingSameFlag = false;
            //merges two MethodNodes with the same MethodName and ClassName
            for (MethodNode method : methodNodeList) {
                if (method.euqalsto(callingMethodNode)) {
                    method.mergeCall(callingMethodNode);
                    callingSameFlag = true;
                }
            }
            //if this node has never been added before,then it should be added this time
            if (callingSameFlag == false)
                methodNodeList.add(callingMethodNode);

            // 更新方法调用图
            super.visit(methodCallExpr, methodNodeList);
        }
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

    public static ArrayList<CompilationUnit> generateAST(ArrayList<String> files) {
        //ASTGenerator analyzer = new ASTGenerator();
        //analyzer.loadProject(filePath);
        //System.out.println("打印得到的方法调用关系如下：");
        ArrayList<CompilationUnit> cus = new ArrayList<>();
        for (String file : files) {
            CompilationUnit cu=parseJavaFile(new File(file));
            cus.add(cu);
        }
        return cus;
    }
}