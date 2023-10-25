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


import javax.tools.Tool;
import java.util.*;

public class ParameterAnalyzer {

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

        for (CompilationUnit cu : cus) {
            new MethodVisitor().visit(cu, methodNodeList);
        }
        for (CompilationUnit cu : cus) {
            new MethodCallVisitor().visit(cu, methodNodeList);
        }
        Tools.nodeChecker(methodNodeList);
        Tools.transMarker(methodNodeList);
        AllIncomingEdges();
    }


    /**
     * This method is used to query the parameter of a method.
     * @param ClassName the name of the class
     * @param MethodName the name of the method
     */
    public void queryParam(String ClassName, String MethodName) {
        MethodNode m = null;
        boolean findFlag = false;
        ArrayList<CallRecord> result = new ArrayList<>();
        for(int i=0;i<60;i++){
            System.out.print('=');
        }
        System.out.print('\n');

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

            for (MethodNode method : methodNodeList) {
                ArrayList<CallRecord> callRecords = method.getMethodCalled();
                for (CallRecord call : callRecords) {
                    if (call.getCalleeMethod().equals(m)) {
                        result.add(call);
                    }
                }
            }

            System.out.println(m.getClassName() + ':');
            ArrayList<String> CallerArgs = m.getCallerArgs();

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

            for (int i = 0; i < CallerArgs.size(); i++) {

                for (int j = 0; j < 8; j++) {
                    System.out.print(' ');
                }
                System.out.println(CallerArgs.get(i) + ':');

                if (!result.isEmpty()) {

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
                                int size = arg.split(" ").length;

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


    /**
     * a method to get all the incoming edges of a method
     */
    public void AllIncomingEdges() {

        for (MethodNode methodNode : methodNodeList) {
            ArrayList<CallRecord> incomingEdgeList = new ArrayList<>();


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

            incomingParamEdges.put(methodNode, incomingEdgeList);
        }

    }


    /**
     * This method is used to get all the incoming edges of a method.
     * @param methodName the name of the method
     * @return the list of incoming edges
     */
    public ArrayList<CallRecord> getIncomingEdges(String methodName) {

        for (MethodNode methodNode : methodNodeList) {
            if (methodNode.getMethodName().equals(methodName)) {
                ArrayList<CallRecord> incomingEdges = incomingParamEdges.get(methodNode);
                return incomingEdges;
            }
        }
        return null;
    }


    /**
     * This method is used to get the source of a parameter.
     * @param depth the depth of the parameter
     * @param method the method to be searched
     * @param order the order of the parameter in the argument list
     */
    public void dfsFindSource(int depth,MethodNode method,int order) {

        int paramNum = method.getCallerArgs().size();
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


    private static class MethodCallVisitor extends VoidVisitorAdapter<ArrayList<MethodNode>> {
        /**
         * This method is used to visit the method call expression.
         * @param methodCallExpr the method call expression to be visited
         * @param methodNodeList the list of method nodes
         */
        @Override
        public void visit(MethodCallExpr methodCallExpr, ArrayList<MethodNode> methodNodeList) {



            String calledClass = null;

            String calledMethod = methodCallExpr.getNameAsString();




            Optional<ClassOrInterfaceDeclaration> classDeclaration = methodCallExpr.findAncestor(ClassOrInterfaceDeclaration.class);
            String callingMethodclass = classDeclaration.map(c -> c.getNameAsString()).orElse("");

            MethodDeclaration callingMethod = methodCallExpr.findAncestor(MethodDeclaration.class).orElse(null);
            String callingMethodName = callingMethod != null ? callingMethod.getNameAsString() : callingMethodclass;


            List<Expression> arguments = methodCallExpr.getArguments();
            ArrayList<String> callArgs = new ArrayList<>();
            for (Expression argument : arguments) {
                callArgs.add(argument.toString());
            }




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

            boolean callingSameFlag = false;
            boolean calledSameFlag = false;

            MethodNode callingMethodNode = new MethodNode(callingMethodName, callingMethodclass);
            MethodNode calledMethodNode = null;

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



            super.visit(methodCallExpr, methodNodeList);
        }
    }

    private static class MethodVisitor extends VoidVisitorAdapter<ArrayList<MethodNode>> {
        /**
         * This method is used to visit the method declaration.
         * @param methodDeclaration the method declaration to be visited
         * @param methodNodeList the list of method nodes
         */
        @Override
        public void visit(MethodDeclaration methodDeclaration, ArrayList<MethodNode> methodNodeList) {

            String methodName = methodDeclaration.getNameAsString();

            Optional<ClassOrInterfaceDeclaration> classDeclaration = methodDeclaration.findAncestor(ClassOrInterfaceDeclaration.class);
            String className = classDeclaration.map(c -> c.getNameAsString()).orElse("");
            MethodNode method = new MethodNode(methodName,className);

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

            methodNodeList.add(method);


            super.visit(methodDeclaration, methodNodeList);
        }
    }

    private static class Tools {

        /**
         * This method is used to check whether the method nodes are the same.
         * @param methodList the list of method nodes
         */
        private static void nodeChecker(ArrayList<MethodNode> methodList) {
            for (MethodNode method : methodList) {
                ArrayList<CallRecord> callRecords = method.getMethodCalled();
                for (int i = 0; i < callRecords.size(); i++) {
                    CallRecord call = callRecords.get(i);
                    MethodNode md = call.getCalleeMethod();
                    if (md.getClassName() == null) {
                        boolean sameFlag = false;
                        for (MethodNode m : methodList) {

                            if (md.getMethodName().equals(m.getMethodName())) {
                                sameFlag = true;

                                call.changeCalleeMethod(m);
                            }
                        }
                        if (sameFlag == false) {

                            method.removeCalledMethod(callRecords.get(i));
                            i--;
                        }
                    }

                }
            }
        }


        /**
         * This method is used to mark the index of the argument in the argument list.
         * @param methodList the list of method nodes
         */
        private static void transMarker(ArrayList<MethodNode> methodList) {
            for (MethodNode method : methodList) {
                ArrayList<String> formalArgs = method.getCallerArgs();

                ArrayList<CallRecord> callRecords = method.getMethodCalled();
                for (CallRecord call : callRecords) {
                    ArrayList<String> callArg = call.getArguments();

                    for (int i = 0; i < callArg.size(); i++) {
                        for (int j = 0; j < formalArgs.size(); j++)

                            if (callArg.get(i).equals(formalArgs.get(j).split(" ")[1])) {
                                call.setTrans(i, j);
                            }
                    }

                }
            }
        }



        public static void printArgSource(String arg, String method) {
            System.out.print(arg + " : " + method);
        }

    }
}
