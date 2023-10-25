package org.group05.analyzer.dataStructure;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MethodInfo {
    private String name;
    private String className;
    private ArrayList<String> parameters;
    private ArrayList<Index> callee;
    private ArrayList<Index> caller;

    public MethodInfo(String name, String classname, ArrayList<String> parameters){
        this.name= name;
        this.className=classname;
        this.parameters = parameters;
        callee = new ArrayList<>();
        caller = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public String getClassName() {
        return className;
    }
    public ArrayList<String> getParameters() {
        return parameters;
    }

    public boolean isSame(MethodInfo methodInfo){
        return this.name.equals(methodInfo.getName()) && checkParameters(this.parameters,methodInfo.getParameters());
    }
    private boolean checkParameters(ArrayList<String> parameters1, ArrayList<String> parameters2){
        if(parameters1.size()!=parameters2.size()){
            return false;
        }
        for(int i=0;i<parameters1.size();i++){
            if(!parameters1.get(i).equals(parameters2.get(i))){
                //return false;
            }
        }
        return true;
    }
    public void addCallee(Index index){
        callee.add(index);
    }

    public void addCaller(Index index){
        caller.add(index);
    }

    public ArrayList<Index> getCallees(){
        return callee;
    }

    public ArrayList<Index> getCallers(){
        return caller;
    }
}
