package org.group05.analyzer;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.group05.analyzer.dataStructure.ClassNode;
import org.group05.analyzer.dataStructure.Index;
import org.group05.analyzer.dataStructure.MethodInfo;
import org.group05.analyzer.dataStructure.TransmissionClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is used for analysing the method call relationship
 */
public class MethodAnalyzer {
    private ArrayList<ClassNode> classes;
    private int myDepth;
    private TransmissionClass myResult=new TransmissionClass("method");

    public MethodAnalyzer(ArrayList<CompilationUnit> cus) {
        this.classes = new ArrayList<>();
        readCus(cus);
    }

    /**
     * This method is used to read all CompilationUnits and set class nodes and method nodes
     * @param cus
     */
    private void readCus(ArrayList<CompilationUnit> cus) {

        // first, we get class names and set class nodes
        for (CompilationUnit cu : cus) {
            ClassVisitor classVisitor = new ClassVisitor();
            classVisitor.visit(cu, classes);
        }

        // then, we get method names and set method nodes
        for (CompilationUnit cu : cus) {
            MethodVisitor methodVisitor = new MethodVisitor();
            methodVisitor.visit(cu, classes);
        }

        // at last, we check callee and caller and set index
        for (CompilationUnit cu : cus) {
            MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
            methodCallVisitor.visit(cu, classes);
        }
//        for (ClassNode classNode : classes) {
//            printClass(classNode);
//            System.out.println();
//        }
    }

//    public void printClass(ClassNode classNode) {
//        System.out.println("class name: " + classNode.getName());
//        ArrayList<MethodInfo> mds = classNode.getMethods();
//        for (MethodInfo md : mds) {
//            System.out.println("method name: " + md.getName());
//            ArrayList<String> parameters = md.getParameters();
//            System.out.print("parameters: ");
//            for (String parameter : parameters) {
//                System.out.print(parameter + " ");
//            }
//            System.out.println();
//            ArrayList<Index> callees = md.getCallees();
//            System.out.print("callees: ");
//            for (Index callee : callees) {
//                System.out.println(callee.getClassIndex() + " " + callee.getMethodIndex());
//            }
//            System.out.println();
//            ArrayList<Index> callers = md.getCallers();
//            System.out.print("callers: ");
//            for (Index caller : callers) {
//                System.out.println(caller.getClassIndex() + " " + caller.getMethodIndex());
//            }
//            System.out.println();
//        }
//
//
//    }

    /**
     * This class is used to get class nodes
     */
    private static class ClassVisitor extends VoidVisitorAdapter<ArrayList<ClassNode>> {
        /**
         * This method is used to get class nodes
         * @param cid class or interface declaration
         * @param classes class nodes list
         */
        @Override
        public void visit(ClassOrInterfaceDeclaration cid, ArrayList<ClassNode> classes) {
            if (!cid.isInterface() && cid.getParentNode().get() instanceof CompilationUnit) {  // only get class, not interface
                // now we got public class the same as file name
                String className = cid.getNameAsString();
                ClassNode cn = new ClassNode(className, className + ".java");
                classes.add(cn);
            }
            super.visit(cid, classes);
        }
    }

    /**
     * This class is used to get method nodes
     */
    private static class MethodVisitor extends VoidVisitorAdapter<ArrayList<ClassNode>> {
        /**
         * This method is used to get method nodes
         * @param md method declaration
         * @param classes class nodes list
         */
        @Override
        public void visit(MethodDeclaration md, ArrayList<ClassNode> classes) {

            // get name of class
            Optional<ClassOrInterfaceDeclaration> parent = md.findAncestor(ClassOrInterfaceDeclaration.class);
            if (parent.isPresent()) {
                ClassOrInterfaceDeclaration cid = parent.get();
                String className = cid.getNameAsString();
                // get name of method
                String methodName = md.getNameAsString();
                // get parameters of method
                NodeList<Parameter> parameters = md.getParameters();
                ArrayList<String> parameterList = new ArrayList<>();
                for (Parameter parameter : parameters) {
                    parameterList.add(parameter.getTypeAsString());
                }
                MethodInfo mi = new MethodInfo(methodName, className, parameterList);
                for (ClassNode cn : classes) {
                    if (cn.getName().equals(className)) {
                        cn.addMethod(mi);
                    }
                }
            }


            super.visit(md, classes);
        }
    }

    /**
     * This class is used to get callee and caller
     */
    private static class MethodCallVisitor extends VoidVisitorAdapter<ArrayList<ClassNode>> {
        /**
         * This method is used to get callee and caller
         * @param md method declaration
         * @param classes class nodes list
         */
        @Override
        public void visit(MethodDeclaration md, ArrayList<ClassNode> classes) {
            super.visit(md, classes);
            // get md's class name
            Optional<ClassOrInterfaceDeclaration> parent = md.findAncestor(ClassOrInterfaceDeclaration.class);
            int mdClassIndex = -1;
            int mdMethodIndex = -1;
            if (parent.isPresent()) {
                ClassOrInterfaceDeclaration cid = parent.get();
                String className = cid.getNameAsString();
                // get parameters
                List<Parameter> arguments = md.getParameters();
                ArrayList<String> parameterList = new ArrayList<>();
                for (Parameter parameter : arguments) {
                    parameterList.add(parameter.getTypeAsString());
                }

                mdClassIndex = Tools.getClassIndex(classes, className);
                if (mdClassIndex == -1) {
                    return;
                }
                mdMethodIndex = Tools.getMethodIndex(new MethodInfo(md.getNameAsString(),"", parameterList), classes.get(mdClassIndex));
            }
            // get md's method call
            List<MethodCallExpr> methodCallExprs = md.findAll(MethodCallExpr.class);
            for (MethodCallExpr methodCallExpr : methodCallExprs) {
                // forbid system library call
                // get callee name and parameters and class name
                // 获取被调用方法的类名,而不是实例名
                boolean flag = false;
                Optional<Expression> scope = methodCallExpr.getScope();
                if (scope.isEmpty()) {
                    flag = true;
                }
                String calledClass;
                try {
                    if (!flag) {
                        calledClass = scope.orElse(null).calculateResolvedType().asReferenceType().getTypeDeclaration().get().getClassName();
                    }
                    else{
                        calledClass=parent.get().getNameAsString();
                    }
                } catch (Exception ex) {
                    continue;
                }
                String calledMethod = methodCallExpr.getNameAsString();

                List<Expression> arguments = methodCallExpr.getArguments();
                ArrayList<String> callArgs = new ArrayList<>();
                for (Expression argument : arguments) {
                    try {
                        callArgs.add(argument.calculateResolvedType().asTypeParameter().getName());
                    } catch (Exception ex) {
                        callArgs.add(argument.toString());
                    }
                }
                // get callee class's index
                int classIndex = Tools.getClassIndex(classes, calledClass);
                if (classIndex == -1) {
                    continue;
                }
                int methodIndex = Tools.getMethodIndex(new MethodInfo(calledMethod,"", callArgs), classes.get(classIndex));
                Index calleeIndex = new Index(classIndex, methodIndex);  // set callee index
                if (mdClassIndex == -1 || mdMethodIndex == -1 || classIndex == -1 || methodIndex == -1) {
                    continue;
                }
                classes.get(mdClassIndex).getMethods().get(mdMethodIndex).addCallee(calleeIndex);  // set caller index
                classes.get(classIndex).getMethods().get(methodIndex).addCaller(new Index(mdClassIndex, mdMethodIndex));
            }
        }
    }

    private static class Tools {
        /**
         * This method is used to get class index
         * @param classes class nodes list
         * @param className class name
         * @return class index
         */
        public static int getClassIndex(ArrayList<ClassNode> classes, String className) {
            for (int i = 0; i < classes.size(); i++) {
                if (classes.get(i).getName().equals(className)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * This method is used to get method index
         * @param mi method info
         * @param cv class node
         * @return method index
         */
        public static int getMethodIndex(MethodInfo mi, ClassNode cv) {
            ArrayList<MethodInfo> methods = cv.getMethods();
            for (int i = 0; i < methods.size(); i++) {
                if (methods.get(i).isSame(mi)) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * This method is used to analyze the method call relationship
     * @param methodName the name of the method
     * @param className the name of the class
     * @param depth the depth of the method call relationship
     * @param paramList the parameters of the method
     */
    public void analyze(String methodName, String className, int depth,ArrayList<String> paramList){
        boolean flag=false;
        MethodInfo tempMethod=null;
        ArrayList<Index> tempCallee=new ArrayList<Index>();
        ArrayList<Index> tempCaller=new ArrayList<Index>();
        this.myDepth = depth;
        this.myResult=new TransmissionClass("method");
        System.out.println("============================================================");
        //First, find method according to the given command
        for (ClassNode myclass : this.classes) {
            if(myclass.getName().equals(className)){
                //find method
                tempMethod = myclass.getMethodByName(methodName,paramList);
                if(tempMethod!=null){
                    flag=true;
                    myResult.setMethodName(tempMethod.getName());
                    break;
                }
            }
        }
        if(flag){
            System.out.println("successfully find this method,querying...");
            tempCallee = tempMethod.getCallees();
            if(!tempCallee.isEmpty()){
                for(Index i : tempCallee){
                    DFSGetCallee(i,1);
                }
            }
            tempCaller = tempMethod.getCallers();
            if(!tempCaller.isEmpty()){
                for(Index i : tempCaller){
                    DFSGetCaller(i,1);
                }
            }
            myResult.print();
        }
        else{
            System.out.println("fail to find this method,please check your command");
        }
        System.out.println("============================================================");
    }

    /**
     * This method is used to get method by index
     * @param methodIndex the index of the method
     * @return the method
     */
    public MethodInfo getMethodByIndex(Index methodIndex){
        ClassNode tempclass=this.classes.get(methodIndex.getClassIndex());
        return tempclass.getMethodByIndex(methodIndex.getMethodIndex());
    }

    /**
     * This method is used to get callee
     * @param methodIndex the index of the method
     * @param depth the depth of the method call relationship
     */
    public void DFSGetCallee(Index methodIndex, int depth){
        MethodInfo m = getMethodByIndex(methodIndex);
        ArrayList<Index> tempCallee = m.getCallees();
        if(depth < this.myDepth && !tempCallee.isEmpty()){
            //recursion
            for(Index i : tempCallee){
                myResult.addCallee(getMethodByIndex(methodIndex),depth);
                DFSGetCallee(i,depth+1);
            }
        }
        else{
            myResult.addCallee(getMethodByIndex(methodIndex),depth);
        }
    }

    /**
     * This method is used to get caller
     * @param methodIndex the index of the method
     * @param depth the depth of the method call relationship
     */
    public void DFSGetCaller(Index methodIndex, int depth){
        MethodInfo m = getMethodByIndex(methodIndex);
        ArrayList<Index> tempCaller = m.getCallers();
        if(depth < this.myDepth && !tempCaller.isEmpty()){
            //recursion
            for(Index i : tempCaller){
                myResult.addCaller(getMethodByIndex(methodIndex),depth);
                DFSGetCaller(i,depth+1);
            }
        }
        else{
            myResult.addCaller(getMethodByIndex(methodIndex),depth);
        }
    }
}