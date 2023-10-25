package org.group05.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.checkerframework.checker.units.qual.C;
import org.group05.analyzer.dataStructure.CallRecord;
import org.group05.analyzer.dataStructure.MethodNode;
import org.group05.service.ParameterInterface;

import javax.tools.Tool;
import java.util.*;

public class ParameterAnalyzer implements ParameterInterface {

    private ArrayList<MethodNode> methodNodeList;
    private HashMap<MethodNode, ArrayList<CallRecord>> incomingParamEdges;

    public ArrayList<MethodCallExpr> getCalls(MethodCallExpr query) {
        return null;
    }

    ; // name of methods which is called in the code

    public ArrayList<MethodCallExpr> getCalledBy(MethodCallExpr query) {
        return null;
    }

    ; // name of methods which call the query method

    //constructor
    public ParameterAnalyzer(ArrayList<CompilationUnit> cus) {
        methodNodeList = new ArrayList<>();
        incomingParamEdges = new HashMap<>();
        //先visit所有方法声明，将所有已声明方法填入方法节点列表
        for (CompilationUnit cu : cus) {
            new MethodVisitor().visit(cu, methodNodeList);
        }
        //再visit所有方法调用，将调用关系加入方法节点列表
        for (CompilationUnit cu : cus) {
            new MethodCallVisitor().visit(cu, methodNodeList);
        }
        Tools.nodeChecker(methodNodeList);   //重整节点指针关系
        Tools.transMarker(methodNodeList);   //标记涉及间接调用的调用
        AllIncomingEdges();                  //获取所有入边集合
        Tools.printNodeList(methodNodeList); //输出所有探明方法节点
        printAnalysis();                     //分析所有探明方法的形参来源
    }

    public void printAnalysis() {
        System.out.println("\nParameterAnalyzer finished analyzing...");
        for (MethodNode method : methodNodeList) {
            //method.printMethodCalled();
        }
        System.out.println("query every MethodNode (for test)");
        for (MethodNode method : methodNodeList) {
            queryParam(method.getClassName(), method.getMethodName());
        }
    }

    public void queryParam(String ClassName, String MethodName) {
        MethodNode m = null;    //要查询的方法
        boolean findFlag = false;
        ArrayList<CallRecord> result = new ArrayList<>();
        for(int i=0;i<60;i++){
            System.out.print('=');
        }
        System.out.print('\n');
        //遍历列表找到待查询的方法
        for (MethodNode method : methodNodeList) {
            if (method.getClassName().equals(ClassName) && method.getMethodName().equals(MethodName)) {
                m = method;
                findFlag = true;
                System.out.println("successfully find ["+m.getClassName()+'.'+m.getMethodName()+"],querying...");
            }
        }
        if (!findFlag) {
            System.out.println("fail to find this method,please check your command");
        }
        else {
            //遍历所有的method节点，找到所有 有出边指向m 的，把这些出边提取到result里
            for (MethodNode method : methodNodeList) {
                ArrayList<CallRecord> callRecords = method.getMethodCalled();
                for (CallRecord call : callRecords) {
                    if (call.getCalleeMethod().equals(m)) {
                        result.add(call);
                    }
                }
            }
            //按照规定好的输出方式，逐个输出这些出边（发出者及其权重）
            System.out.println(m.getClassName() + ':');
            ArrayList<String> CallerArgs = m.getCallerArgs();
            //打印正在查询的方法名及其形参
            for (int i = 0; i < 4; i++) {
                System.out.print(' ');
            }
            System.out.print(m.getMethodName() + '(');
            for (int i = 0; i < CallerArgs.size(); i++) {
                System.out.print(CallerArgs.get(i));
                if (i < CallerArgs.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.print("):\n");
            //打印数个形参分别的可能来源
            for (int i = 0; i < CallerArgs.size(); i++) {
                //缩进8格
                for (int j = 0; j < 8; j++) {
                    System.out.print(' ');
                }
                System.out.println(CallerArgs.get(i) + ':');

                if (!result.isEmpty()) {
                    //打印result中已获取的所有可能来源
                    for (int n=0;n<result.size();n++) {
                        CallRecord call = result.get(n);
                        String CallerMethod = call.getCallerMethod().getClassName() + '.' + call.getCallerMethod().getMethodName();
                        ArrayList<String> args = call.getArguments();
                        if (i < args.size()) {
                            for (int j = 0; j < 12; j++) {
                                System.out.print(' ');
                            }

                            System.out.print('[');
                            Tools.printArgSource(args.get(i), CallerMethod);
                            for (int j = 0; j < call.getCallerMethod().getCallerArgs().size(); j++) {
                                String arg = call.getCallerMethod().getCallerArgs().get(j);
                                int size = arg.split(" ").length;  //用于获取形参的name，把type去掉
                                //如果调用者的形参中有和调用传参的第i位相同的，就继续追溯下去，寻找间接调用
                                if (args.get(i).equals(arg.split(" ")[size - 1])) {
                                    dfsFindSource(1, call.getCallerMethod(), j);
                                }
                            }
                            System.out.print("]");
                            if(n< result.size()-1){
                                System.out.print('\n');
                            }
                        }

                    }
                }
                else {
                    for (int j = 0; j < 12; j++) {
                        System.out.print(' ');
                    }
                    System.out.print("no call records found");
                }
                System.out.print('\n');
            }
        }
        for(int i=0;i<60;i++){
            System.out.print('=');
        }
        System.out.println('\n');
    }

    // 初始化每个节点的所有入边并存储在incomingParamEdges中
    public void AllIncomingEdges() {

        for (MethodNode methodNode : methodNodeList) {
            ArrayList<CallRecord> incomingEdgeList = new ArrayList<>();

            // 遍历 methodNodeList，找出所有指向 methodNode 的入边
            for (MethodNode caller : methodNodeList) {
                if (caller != methodNode) {
                    ArrayList<CallRecord> callRecords = caller.getMethodCalled();
                    for (CallRecord callRecord : callRecords) {
                        if (callRecord.getCalleeMethod().equals(methodNode)) {
                            incomingEdgeList.add(callRecord);
                        }
                    }
                }
                for (CallRecord callRecord : incomingEdgeList) {
                    //System.out.println(callRecord.getCalleeMethod().getMethodName());
                }
            }
            // 将 methodNode 及其对应的入边列表存储在 HashMap 中
            incomingParamEdges.put(methodNode, incomingEdgeList);
        }

    }

    // 获取特定节点的所有入边并返回
    public ArrayList<CallRecord> getIncomingEdges(String methodName) {

        for (MethodNode methodNode : methodNodeList) {
            if (methodNode.getMethodName().equals(methodName)) {
                ArrayList<CallRecord> incomingEdges = incomingParamEdges.get(methodNode);
                return incomingEdges;
            }
        }
        return null;
    }

    //order表示间接调用的参数所在位置，取值：0 ~ n-1
    public void dfsFindSource(int depth,MethodNode method,int order) {

        int paramNum = method.getCallerArgs().size();   //method的形参个数
        ArrayList<CallRecord> inEdges = getIncomingEdges(method.getMethodName());

        for (int i=0;i<inEdges.size();i++) {
            CallRecord call = inEdges.get(i);
            MethodNode caller = call.getCallerMethod();
            ArrayList<String> callerArgs = caller.getCallerArgs();
            if(order<call.getArguments().size()) {
                if (call.isTransParam(order) != -1) {
                    String arg = call.getArguments().get(order);
                    String methodName = call.getCallerMethod().getClassName() + '.' + call.getCallerMethod().getMethodName();
                    System.out.print(" <-- ");
                    Tools.printArgSource(arg, methodName);

                    for (int j = 0; j < callerArgs.size(); j++) {
                        String callerArg = callerArgs.get(j);
                        if (arg.equals(callerArg)) {
                            dfsFindSource(depth + 1, caller, j);
                        }
                    }

                    break;
                }
            }

        }
    }

    //内部类，重写访问者适配器，完成对调用方法表达式的遍历
    private static class MethodCallVisitor extends VoidVisitorAdapter<ArrayList<MethodNode>> {
        @Override
        public void visit(MethodCallExpr methodCallExpr, ArrayList<MethodNode> methodNodeList) {
            // 获取被调用方法的类名和方法名

            //获取被调用方法的类名(不强制获取)
            String calledClass = null;
            //获取被调用方法名
            String calledMethod = methodCallExpr.getNameAsString();

            //获取主动调用方法的类名和方法名

            //获取主动调用类名
            Optional<ClassOrInterfaceDeclaration> classDeclaration = methodCallExpr.findAncestor(ClassOrInterfaceDeclaration.class);
            String callingMethodclass = classDeclaration.map(c -> c.getNameAsString()).orElse("");
            //获取主动调用方法名
            MethodDeclaration callingMethod = methodCallExpr.findAncestor(MethodDeclaration.class).orElse(null);
            String callingMethodName = callingMethod != null ? callingMethod.getNameAsString() : callingMethodclass;

            //获取调用语句的参数
            List<Expression> arguments = methodCallExpr.getArguments();
            ArrayList<String> callArgs = new ArrayList<>();
            for (Expression argument : arguments) {
                callArgs.add(argument.toString());
            }

            //获取被调用方法的形参

            // 获取调用语句的发起调用者方法的形参列表
            ArrayList<String> callerMethodParams = new ArrayList<>();
            if (callingMethod != null) {
                NodeList<Parameter> parameters = callingMethod.getParameters();
                for (Parameter parameter : parameters) {
                    String type = parameter.getTypeAsString();
                    if(type==null){
                        type = "type";
                    }
                    callerMethodParams.add(type + ' ' + parameter.getNameAsString());
                }
            }

            boolean callingSameFlag = false;  //记录有无重复的主动调用Method
            boolean calledSameFlag = false;   //记录有无重复的被调用Method
            //创建两个新的methodNode对象并把被调用者加入调用者的CallRecord
            //创建新的主动调用method节点
            MethodNode callingMethodNode = new MethodNode(callingMethodName, callingMethodclass);
            MethodNode calledMethodNode = null;
            //根据method节点列表中已有节点的情况来确定是否新建被调用method节点
            for (MethodNode method : methodNodeList) {
                if (method.getMethodName().equals(calledMethod)) {
                    calledSameFlag = true;
                    calledMethodNode = method;
                }
            }
            if (calledSameFlag == false) {
                calledMethodNode = new MethodNode(calledMethod, calledClass);
            }

            callingMethodNode.addCalledMethod(calledMethodNode, callingMethodNode, callArgs);
            callingMethodNode.setCallerArgs(callerMethodParams);
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

    private static class MethodVisitor extends VoidVisitorAdapter<ArrayList<MethodNode>> {
        @Override
        public void visit(MethodDeclaration methodDeclaration, ArrayList<MethodNode> methodNodeList) {
            //获取方法名
            String methodName = methodDeclaration.getNameAsString();
            //获取方法所属调用类名
            Optional<ClassOrInterfaceDeclaration> classDeclaration = methodDeclaration.findAncestor(ClassOrInterfaceDeclaration.class);
            String className = classDeclaration.map(c -> c.getNameAsString()).orElse("");
            MethodNode method = new MethodNode(methodName,className);
            //获取方法的形参列表
            ArrayList<String> methodParams = new ArrayList<>();
            if (methodDeclaration != null) {
                NodeList<Parameter> parameters = methodDeclaration.getParameters();
                for (Parameter parameter : parameters) {
                    String type = parameter.getTypeAsString();
                    if(type==null){
                        type = "type";
                    }
                    methodParams.add(type + ' ' + parameter.getNameAsString());
                }
            }
            method.setCallerArgs(methodParams);
            //将该方法加入列表
            methodNodeList.add(method);

            // 更新方法调用图
            super.visit(methodDeclaration, methodNodeList);
        }
    }

    private static class Tools {

        //重新排查一遍已提取信息的列表，将节点边指针指向真正要指向的节点
        //并将系统库中的类的方法调用删掉
        private static void nodeChecker(ArrayList<MethodNode> methodList) {
            for (MethodNode method : methodList) {
                ArrayList<CallRecord> callRecords = method.getMethodCalled();
                for (int i = 0; i < callRecords.size(); i++) {
                    CallRecord call = callRecords.get(i);
                    MethodNode md = call.getCalleeMethod();
                    if (md.getClassName() == null) {
                        boolean sameFlag = false;
                        for (MethodNode m : methodList) {
                            //System.out.println("比较"+md.getMethodName()+"与"+m.getMethodName());
                            if (md.getMethodName().equals(m.getMethodName())) {
                                sameFlag = true;
                                //System.out.println("发现一致，改变边的终点");
                                call.changeCalleeMethod(m);
                            }
                        }
                        if (sameFlag == false) {
                            //System.out.println("列表方法无此项，删掉此边");
                            method.removeCalledMethod(callRecords.get(i));
                            i--;
                        }
                    }

                }
            }
        }

        //遍历所有的callRecord，将其中涉及到间接调用的做特殊标记（更改其参数）
        //trans--间接调用   Marker--标记者
        private static void transMarker(ArrayList<MethodNode> methodList) {
            for (MethodNode method : methodList) {
                ArrayList<String> formalArgs = method.getCallerArgs();

                ArrayList<CallRecord> callRecords = method.getMethodCalled();
                for (CallRecord call : callRecords) {
                    ArrayList<String> callArg = call.getArguments();
                    //如果某一条出边的第i个参数和本节点的第j个形参相同，就将该出边的该参数trans标号记为j（表示其涉及间接调用）
                    for (int i = 0; i < callArg.size(); i++) {
                        for (int j = 0; j < formalArgs.size(); j++)
                            //分离变量类型
                            if (callArg.get(i).equals(formalArgs.get(j).split(" ")[1])) {
                                call.setTrans(i, j);
                            }
                    }

                }
            }
        }

        public static void printNodeList(ArrayList<MethodNode> methodList){
            for(MethodNode method : methodList){
                method.printMethodCalled();
            }
        }

        public static void printArgSource(String arg, String method) {
            System.out.print(arg + " : " + method);
        }

    }
}
