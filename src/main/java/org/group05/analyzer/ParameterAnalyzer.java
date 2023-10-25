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

/**
 * This class is used to analyze the parameters of the methods
 */
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
    /**
     * This method is used to analyze the parameters of the methods
     * @param cus the AST of the project
     */
    public ParameterAnalyzer(ArrayList<CompilationUnit> cus) {
        methodNodeList = new ArrayList<>();
        incomingParamEdges = new HashMap<>();
        for (CompilationUnit cu : cus) {
            new MethodCallVisitor().visit(cu, methodNodeList);
        }
        Tools.nodeChecker(methodNodeList);
        Tools.transMarker(methodNodeList);
        AllIncomingEdges();
        printAnalysis();
    }

    public void printAnalysis() {
        System.out.println("\nParameterAnalyzer finished analyzing...");
        for (MethodNode method : methodNodeList) {
            method.printMethodCalled();
        }
        System.out.println("query every MethodNode (for test)");
        for (MethodNode method : methodNodeList) {
            queryParam(method.getClassName(), method.getMethodName());
        }
    }

    /**
     * This method is used to do parameter query
     * @param ClassName the name of the class
     * @param MethodName the name of the method
     */
    public void queryParam(String ClassName, String MethodName) {
        MethodNode m = null;    //要查询的方法
        boolean findFlag = false;
        ArrayList<CallRecord> result = new ArrayList<>();
        //遍历列表找到待查询的方法
        for (MethodNode method : methodNodeList) {
            if (method.getClassName().equals(ClassName) && method.getMethodName().equals(MethodName)) {
                m = method;
                findFlag = true;
                System.out.println("successfully find this method,querying...");
            }
        }
        if (!findFlag) {
            System.out.println("fail to find this method,please check your command");
        }
        //
        for (MethodNode method : methodNodeList) {
            ArrayList<CallRecord> callRecords = method.getMethodCalled();
            for (CallRecord call : callRecords) {
                if (call.getCalleeMethod().equals(m)) {
                    result.add(call);
                }
            }
        }
        //
        System.out.println(m.getClassName() + ':');
        ArrayList<String> CallerArgs = m.getCallerArgs();
        //
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
        //
        for (int i = 0; i < CallerArgs.size(); i++) {
            //
            for (int j = 0; j < 8; j++) {
                System.out.print(' ');
            }
            System.out.println(CallerArgs.get(i) + ':');

            //
            for (CallRecord call : result) {
                String CallerMethod = call.getCallerMethod().getClassName() + '.' + call.getCallerMethod().getMethodName();
                ArrayList<String> args = call.getArguments();
                if(i<args.size()) {
                    for (int j = 0; j < 12; j++) {
                        System.out.print(' ');
                    }
                    System.out.print('[');
                    Tools.printArgSource(args.get(i), CallerMethod);
                    for (int j = 0; j < call.getCallerMethod().getCallerArgs().size(); j++) {
                        String arg = call.getCallerMethod().getCallerArgs().get(j);
                        int size = arg.split(" ").length;  //
                        //
                        if (args.get(i).equals(arg.split(" ")[size - 1])) {
                            dfsFindSource(1, call.getCallerMethod(), j);
                        }
                    }
                    System.out.print("]\n");
                }
            }
            System.out.println();
        }

    }

    //
    /**
     * This method is used to initialize all the incoming edges of each node and store them in incomingParamEdges
     */
    public void AllIncomingEdges() {

        for (MethodNode methodNode : methodNodeList) {
            ArrayList<CallRecord> incomingEdgeList = new ArrayList<>();

            //
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
                    System.out.println(callRecord.getCalleeMethod().getMethodName());
                }
            }
            //
            incomingParamEdges.put(methodNode, incomingEdgeList);
        }

    }

    //
    /**
     * This method is used to get all the incoming edges of a specific node and return them
     * @param methodName the name of the method
     * @return all the incoming edges of a specific node
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

    //
    /**
     * This method is used to find the source of the parameter
     * @param depth the depth of the method
     * @param method the method
     * @param order the order of the parameter
     */
    public void dfsFindSource(int depth,MethodNode method,int order) {

        /*
        if(depth>1) {
            System.out.print('\n');
            for (int i = 0; i < 12; i++) {
                System.out.print(' ');
            }
        }
         */

        int paramNum = method.getCallerArgs().size();   //
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

    //
    /**
     * This class is used to override the visitor adapter to complete the traversal of the call method expression
     */
    private static class MethodCallVisitor extends VoidVisitorAdapter<ArrayList<MethodNode>> {
        /**
         * This method is used to visit the method call expression
         * @param methodCallExpr the method call expression
         * @param methodNodeList the list of the method node
         */
        @Override
        public void visit(MethodCallExpr methodCallExpr, ArrayList<MethodNode> methodNodeList) {
            //

            //
            String calledClass = null;
            //
            String calledMethod = methodCallExpr.getNameAsString();

            //

            //
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
            //
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


            //
            super.visit(methodCallExpr, methodNodeList);
        }
    }


    private static class Tools {


        /**
         * This method is used to re-examine the list of extracted information, point the node edge pointer to the node that really needs to be pointed to
         * and delete the method call of the system library
         * @param methodList the list of the method
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
                            //System.out.println("compare "+md.getMethodName()+" and "+m.getMethodName());
                            if (md.getMethodName().equals(m.getMethodName())) {
                                sameFlag = true;
                                //System.out.println("the same change ");
                                call.changeCalleeMethod(m);
                            }
                        }
                        if (sameFlag == false) {
                            //System.out.println("no existence in list, delete");
                            method.removeCalledMethod(callRecords.get(i));
                            i--;
                        }
                    }

                }
            }
        }

        //all callRecord
        //trans   Marker
        /**
         * This method is used to traverse all callRecords and make special marks on those involving indirect calls (change their parameters)
         * @param methodList the list of the method
         */
        private static void transMarker(ArrayList<MethodNode> methodList) {
            for (MethodNode method : methodList) {
                ArrayList<String> formalArgs = method.getCallerArgs();

                ArrayList<CallRecord> callRecords = method.getMethodCalled();
                for (CallRecord call : callRecords) {
                    ArrayList<String> callArg = call.getArguments();
                    //
                    for (int i = 0; i < callArg.size(); i++) {
                        for (int j = 0; j < formalArgs.size(); j++)
                            //
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
