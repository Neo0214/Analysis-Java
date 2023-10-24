package org.group05.analyzer;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.sun.source.tree.Scope;
import javassist.compiler.ast.Pair;
import org.group05.analyzer.dataStructure.ClassNode;
import org.group05.analyzer.dataStructure.Index;
import org.group05.analyzer.dataStructure.MethodInfo;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MethodAnalyzer {
    private ArrayList<ClassNode> classes;

    public MethodAnalyzer(ArrayList<CompilationUnit> cus) {
        this.classes = new ArrayList<>();
        readCus(cus);
    }

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

    }

    public void printClass(ClassNode classNode) {
        System.out.println(classNode.getName());
        ArrayList<MethodInfo> mds = classNode.getMethods();
        for (MethodInfo md : mds) {
            System.out.println(md.getName());
            ArrayList<String> parameters = md.getParameters();
            for (String parameter : parameters) {
                System.out.println(parameter);
            }
        }
    }

    private static class ClassVisitor extends VoidVisitorAdapter<ArrayList<ClassNode>> {
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

    private static class MethodVisitor extends VoidVisitorAdapter<ArrayList<ClassNode>> {
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
                MethodInfo mi = new MethodInfo(methodName, parameterList);
                for (ClassNode cn : classes) {
                    if (cn.getName().equals(className)) {
                        cn.addMethod(mi);
                    }
                }
            }


            super.visit(md, classes);
        }
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter<ArrayList<ClassNode>> {
        @Override
        public void visit(MethodDeclaration md, ArrayList<ClassNode> classes) {
            //super.visit(md, classes);
            // get md's class name
            Optional<ClassOrInterfaceDeclaration> parent = md.findAncestor(ClassOrInterfaceDeclaration.class);
            int mdClassIndex = -1;
            int mdMethodIndex = -1;
            if (parent.isPresent()){
                ClassOrInterfaceDeclaration cid = parent.get();
                String className = cid.getNameAsString();
                // get parameters
                List<Parameter> arguments = md.getParameters();
                ArrayList<String> parameterList = new ArrayList<>();
                for (Parameter parameter : arguments) {
                    parameterList.add(parameter.getTypeAsString());
                }
                mdClassIndex = Tools.getClassIndex(classes, className);
                mdMethodIndex = Tools.getMethodIndex(new MethodInfo(md.getNameAsString(),parameterList),classes.get(mdClassIndex));
            }
            // get md's method call
            List<MethodCallExpr> methodCallExprs = md.findAll(MethodCallExpr.class);
            for (MethodCallExpr methodCallExpr : methodCallExprs) {
                // get callee name and parameters and class name
                // 获取被调用方法的类名,而不是实例名
                String calledClass = methodCallExpr.getScope().orElse(null).calculateResolvedType().asReferenceType().getTypeDeclaration().get().getClassName();
                String calledMethod = methodCallExpr.getNameAsString();
                List<Expression> arguments = methodCallExpr.getArguments();
                ArrayList<String> callArgs = new ArrayList<>();
                for (Expression argument : arguments) {
                    callArgs.add(argument.calculateResolvedType().describe());
                    System.out.println(argument.calculateResolvedType().describe());
                }
                // get callee class's index
                int classIndex = Tools.getClassIndex(classes, calledClass);
                int methodIndex=Tools.getMethodIndex(new MethodInfo(calledMethod,callArgs),classes.get(classIndex));
                Index calleeIndex = new Index(classIndex, methodIndex);  // set callee index
                classes.get(mdClassIndex).getMethods().get(mdMethodIndex).addCallee(calleeIndex);  // set caller index
                classes.get(classIndex).getMethods().get(methodIndex).addCaller(new Index(mdClassIndex, mdMethodIndex));
            }
        }
    }

    private static class Tools{
        public static int getClassIndex(ArrayList<ClassNode> classes, String className){
            for (int i = 0; i < classes.size(); i++) {
                if (classes.get(i).getName().equals(className)) {
                    return i;
                }
            }
            return -1;
        }
        public static int getMethodIndex(MethodInfo mi, ClassNode cv){
            ArrayList<MethodInfo> methods = cv.getMethods();
            for (int i = 0; i < methods.size(); i++) {
                if (methods.get(i).isSame(mi)) {
                    return i;
                }
            }
            return -1;
        }
    }
}
