package org.group05.service;

import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;

public interface MethodInterface {
    public ArrayList<MethodCallExpr> getCalls(String query); // name of methods which is called in the code
    public ArrayList<MethodCallExpr> getCalledBy(MethodCallExpr query); // name of methods which call the query method
}
