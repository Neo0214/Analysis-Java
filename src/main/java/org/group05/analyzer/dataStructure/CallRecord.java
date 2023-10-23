package org.group05.analyzer.dataStructure;

import org.group05.analyzer.dataStructure.MethodNode;

import java.util.ArrayList;

public class CallRecord {
    private MethodNode calleeMethod;
    private ArrayList<String> arguments;

    //to store whether this CallRecord is related to multi-layer calls
    private boolean transParam;

    public CallRecord(MethodNode calledMethod,ArrayList<String> arguments)
    {
        this.calleeMethod = calledMethod;
        this.arguments = arguments;
        this.transParam = false;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public MethodNode getCalleeMethod() {
        return calleeMethod;
    }

    public boolean isTransParam() {
        return transParam;
    }

    public void setTransParam(boolean transParam) {
        this.transParam = transParam;
    }

    public void setArguments(ArrayList<String> arguments) {
        this.arguments = arguments;
    }

    public void setCalleeMethod(MethodNode calledMethod) {
        this.calleeMethod = calledMethod;
    }
}
