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

    /**
     * This method is used to check whether two methods are the same.
     * @param methodInfo the method to be compared
     * @return true if the two methods are the same, false otherwise
     */
    public boolean isSame(MethodInfo methodInfo){
        return this.name.equals(methodInfo.getName()) && checkParameters(this.parameters,methodInfo.getParameters());
    }

    /**
     * This method is used to check whether two methods have the same parameters.
     * @param parameters1 the list of parameters of the first method
     * @param parameters2 the list of parameters of the second method
     * @return true if the two methods have the same parameters, false otherwise
     */
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
