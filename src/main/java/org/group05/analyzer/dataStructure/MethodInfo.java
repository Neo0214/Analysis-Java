package org.group05.analyzer.dataStructure;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MethodInfo {
    private String name;
    private ArrayList<String> parameters;
    private ArrayList<Index> callee;
    private ArrayList<Index> caller;

    public MethodInfo(String name, ArrayList<String> parameters){
        this.name= name;
        this.parameters = parameters;
        callee = new ArrayList<>();
        caller = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getParameters() {
        return parameters;
    }

    public boolean isSame(MethodInfo methodInfo){
        return this.name.equals(methodInfo.getName()) && this.parameters.equals(methodInfo.getParameters());
    }

    public void addCallee(Index index){
        callee.add(index);
    }

    public void addCaller(Index index){
        caller.add(index);
    }
}
