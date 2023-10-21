package org.group05.analyzer;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.BufferedReader;

import java.util.Scanner;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


public class ASTGenerator {
    //存储调用关系的HashMap数据结构: 调用类 -> [ 多个 ( 被调用方法 -> 被调用次数) ]
    private Map<String, Map<String, Integer>> methodCallGraph; // 存储方法调用关系

    //无参构造函数，新建一个空的哈希表分配给私有成员methodCallGraph，用以存储方法调用关系
    public ASTGenerator() {
        methodCallGraph = new HashMap<>();
    }

    //内部类，重写访问者适配器，完成对调用方法表达式的遍历
    private static class MethodCallVisitor extends VoidVisitorAdapter<Map<String, Map<String, Integer>>> {
        @Override
        public void visit(MethodCallExpr methodCallExpr, Map<String, Map<String, Integer>> methodCallGraph) {
            // 获取调用方法的类名和方法名

            //getScope() 方法可以获取调用者对象，但得到的是一个Optional<Expression>对象，
            //需要用一个Lamda表达式scope->scope.toString()来将其转换为字符串，如果为空就转换为空字符串
            String callerClass = methodCallExpr.getScope().map(scope -> scope.toString()).orElse("");
            //getNameAsString() 方法直接将 方法调用表达式 本身转化成字符串
            String calledMethod = methodCallExpr.getNameAsString();

            // 更新方法调用图
            methodCallGraph.computeIfAbsent(callerClass, k -> new HashMap<>())
                    .merge(calledMethod, 1, Integer::sum);

            super.visit(methodCallExpr, methodCallGraph);
        }
    }

    // 加载Java项目文件并解析源代码
    public void loadProject(String projectPath) {
        File projectDirectory = new File(projectPath);
        if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
            System.err.println("指定的路径不是一个有效的目录。");
            return;
        }
        File[] javaFiles = projectDirectory.listFiles((dir, name) -> name.endsWith(".java"));
        if (javaFiles == null || javaFiles.length == 0) {
            System.err.println("在指定的目录中找不到Java源代码文件。");
            return;
        }
        for (File javaFile : javaFiles) {
            printJavaFile(javaFile);   //用于测试有没有得到javaFile, {*可注释*}
            parseJavaFile(javaFile);   //用于解析所有的javaFile并将解析结果填入方法调用图中
            printCallRelation(methodCallGraph);  //用于打印出方法调用图中显示的调用关系, {*可注释*}
        }
    }

    // 打印java代码源文件的所有内容（测试用）
    private void printJavaFile(File javaFile){
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
    private void parseJavaFile(File javaFile) {
        try{
            CompilationUnit cu = StaticJavaParser.parse(javaFile);
            // 创建一个访问者来查找方法调用关系
            new MethodCallVisitor().visit(cu, methodCallGraph);
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    // 打印全部方法调用关系（测试用）
    private void printCallRelation(Map<String, Map<String, Integer>> methodCallGraph ){


        //遍历HashMap的所有键(key)，即遍历记录下来的所有调用者类
        for (String callerClass : methodCallGraph.keySet()) {
            System.out.println("调用类 " + callerClass);
            //获得该被调用类调用的方法
            Map<String, Integer> calledMethods = methodCallGraph.get(callerClass);
            for (String calledMethod : calledMethods.keySet()) {
                System.out.println("  调用了 " + calledMethod + "方法" + calledMethods.get(calledMethod) + " 次");
            }
        }
    }

    public void checkInvokeRelation(String className ,String methodName){

    }

    public static void generateAST(String filePath) {
        ASTGenerator analyzer = new ASTGenerator();
        Scanner scanner = new Scanner(System.in);
        String inputMethodName = "";
        String inputClassName = "";
        int flag = 1;
        // 将需要分析的项目放入D盘的ProjectToAnalyze文件夹中（包含多个java文件）
        analyzer.loadProject(filePath);

        while(flag == 1){
            System.out.println("请输入要查询的类名：");
            inputClassName = scanner.next();
            System.out.println("请输入要查询的方法名：");
            inputMethodName = scanner.next();

            // 在分析后，调用分析类的查询方法来获取用户想要查询的调用关系
            analyzer.checkInvokeRelation(inputClassName, inputMethodName);
            // 根据用户输入决定循环是否继续
            System.out.println("输入0以终止查询，输入1以继续查询");
            flag = scanner.nextInt();
            if(flag == 1)
                continue;
            else
                break;
        }
        scanner.close();
    }
}