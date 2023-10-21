package org.group05.analyzer;

import com.github.javaparser.ast.expr.MethodCallExpr;
import org.group05.service.ParameterInterface;

import java.util.ArrayList;

public class ParameterAnalyzer implements ParameterInterface {
    public ArrayList<MethodCallExpr> getCalls(MethodCallExpr query) {
        return null;
    }

    public ArrayList<MethodCallExpr> getCalledBy(MethodCallExpr query) {
        return null;
    }
}
