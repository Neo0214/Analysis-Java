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

    public void setMethods(ArrayList<MethodInfo> methods) {
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public ArrayList<MethodInfo> getMethods() {
        return methods;
    }

    public String getFileName() {
        return fileName;
    }
}
