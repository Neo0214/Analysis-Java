package org.group05.analyzer.dataStructure;

import org.group05.analyzer.dataStructure.MethodNode;

import java.util.ArrayList;

public class CallRecord {
    private MethodNode calledMethod;
    private ArrayList<String> arguments;

    public CallRecord(MethodNode calledMethod,ArrayList<String> arguments)
    {
        this.calledMethod = calledMethod;
        this.arguments = arguments;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public MethodNode getCalledMethod() {
        return calledMethod;
    }

    public void setArguments(ArrayList<String> arguments) {
        this.arguments = arguments;
    }

    public void setCalledMethod(MethodNode calledMethod) {
        this.calledMethod = calledMethod;
    }
}
