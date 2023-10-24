package org.group05.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.group05.analyzer.dataStructure.MethodNode;
import org.group05.service.MethodInterface;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MethodAnalyzer implements MethodInterface {

    private ArrayList<MethodNode> methodNodeList;

    //constructor
    public MethodAnalyzer(ArrayList<CompilationUnit> cus) {
        methodNodeList = new ArrayList<>();
        for(CompilationUnit cu : cus){
            new MethodCallVisitor().visit(cu,methodNodeList);
        }
        printAnalysis();

    }

    public ArrayList<MethodCallExpr> getCalls(String query) {
        return null;
    }

    public ArrayList<MethodCallExpr> getCalledBy(MethodCallExpr query) {
        return null;
    }

    public void printAnalysis(){
        System.out.println("\nMethodAnalyzer finished analyzing...");
        for(MethodNode method : methodNodeList){
            method.printMethodCalled();
        }
    }

    //内部类，重写访问者适配器，完成对调用方法表达式的遍历
    private static class MethodCallVisitor extends VoidVisitorAdapter<ArrayList<MethodNode>> {
        @Override
        public void visit(MethodCallExpr methodCallExpr, ArrayList<MethodNode> methodNodeList) {

            //获取被调用方法名
            String calledMethod = methodCallExpr.getNameAsString();
            //获取主动调用方法名
            MethodDeclaration callingMethod = methodCallExpr.findAncestor(MethodDeclaration.class).orElse(null);
            String callingMethodName = callingMethod != null ? callingMethod.getNameAsString() : "";

            //获取被调用方法的类名
            String calledClass = null;
            //获取主动调用类名
            Optional<ClassOrInterfaceDeclaration> classDeclaration = methodCallExpr.findAncestor(ClassOrInterfaceDeclaration.class);
            String callingMethodclass = classDeclaration.map(c -> c.getNameAsString()).orElse("");
            //获取主动调用类所属的包名
            Optional<PackageDeclaration> packageDeclaration = classDeclaration.get().findAncestor(PackageDeclaration.class);
            String callingPackage = packageDeclaration.map(p -> p.getNameAsString()).orElse("");
            //获取调用语句的参数
            List<Expression> arguments = methodCallExpr.getArguments();
            ArrayList<String> callArgs = new ArrayList<>();
            for (Expression argument : arguments) {
                callArgs.add(argument.toString());
            }

            boolean callingSameFlag = false;  //记录有无重复的主动调用Method
            boolean calledSameFlag = false;   //记录有无重复的被调用Method
            //创建两个新的methodNode对象并把被调用者加入调用者的CallRecord
            //创建新的主动调用method节点
            MethodNode callingMethodNode = new MethodNode(callingMethodName, callingMethodclass);
            MethodNode calledMethodNode = null;
            //根据method节点列表中已有节点的情况来确定是否新建被调用method节点
            for(MethodNode method : methodNodeList){
                if(method.getMethodName().equals(calledMethod)){
                    calledSameFlag = true;
                    calledMethodNode = method;
                }
            }
            if(calledSameFlag == false){
                calledMethodNode = new MethodNode(calledMethod, calledClass);
            }

            callingMethodNode.addCalledMethod(calledMethodNode, callArgs);

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


}
