package org.group05.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.group05.service.MethodInterface;

import java.util.ArrayList;

public class MethodAnalyzer implements MethodInterface {
    public ArrayList<MethodCallExpr> getCalls(String query) {
        return null;
    }

    public ArrayList<MethodCallExpr> getCalledBy(MethodCallExpr query) {
        return null;
    }

    public MethodAnalyzer(ArrayList<CompilationUnit> cus) {
        calc_data_structure(cus);
    }

    private void calc_data_structure(ArrayList<CompilationUnit> cus) {
        // to do
    }
}
