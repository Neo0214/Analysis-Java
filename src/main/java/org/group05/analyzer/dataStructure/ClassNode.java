package org.group05.analyzer.dataStructure;

import java.util.ArrayList;

public class ClassNode {
    private String name;
    private ArrayList<MethodInfo> methods;
    private String fileName;

    public ClassNode(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
        methods = new ArrayList<>();
    }

    public void addMethod(MethodInfo method) {
        methods.add(method);
    }



}
