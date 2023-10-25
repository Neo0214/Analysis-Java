package org.group05.analyzer.dataStructure;

import org.group05.analyzer.dataStructure.MethodNode;

import java.util.ArrayList;
import java.util.Arrays;

public class CallRecord {
    private MethodNode calleeMethod;
    private MethodNode callerMethod;
    private ArrayList<String> arguments;

    //to store whether this CallRecord is related to multi-layer calls
    private int[] transParam;

    public CallRecord(MethodNode calledMethod,MethodNode callerMethod,ArrayList<String> arguments)
    {
        this.calleeMethod = calledMethod;
        this.callerMethod = callerMethod;
        this.arguments = arguments;
        // set boolean list to false
        int argNum = arguments.size();
        transParam = new int[argNum];
        for(int tran : transParam){
            tran = -1;
        }
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }
    public MethodNode getCalleeMethod() {
        return calleeMethod;
    }

    public MethodNode getCallerMethod() {
        return callerMethod;
    }
    public int isTransParam(int order) {
        return transParam[order];
    }
    public void setTrans(int order,int transParam){
        if(order<this.transParam.length) {
            this.transParam[order] = transParam;
        }
    }
    public  void changeCalleeMethod(MethodNode calleeMethod){
        this.calleeMethod = calleeMethod;
    }

    public void setArguments(ArrayList<String> arguments) {
        this.arguments = arguments;
    }
    public void setCalleeMethod(MethodNode calledMethod) {
        this.calleeMethod = calledMethod;
    }
}
