package org.group05.analyzer;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.group05.analyzer.dataStructure.ClassNode;
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
        for (CompilationUnit cu : cus) {
            ClassVisitor classVisitor = new ClassVisitor();
            classVisitor.visit(cu, null);
        }
    }

    public static class ClassVisitor extends VoidVisitorAdapter<Void>{
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);

        }

    }

}
