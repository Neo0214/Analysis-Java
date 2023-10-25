package org.group05.analyzer.dataStructure;

import org.group05.analyzer.dataStructure.MethodNode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is used to store the information of a call record.
 * A call record is a record of a method call, including the method called, the method that calls it, and the parameters passed.
 * The parameters are stored in an ArrayList of Strings.
 * The method called and the method that calls it are stored in MethodNode.
 * The boolean list is used to store whether the parameters are passed by reference.
 */
public class CallRecord {
    private MethodNode calleeMethod;
    private MethodNode callerMethod;
    private ArrayList<String> arguments;

    //to store whether this CallRecord is related to multi-layer calls
    private int[] transParam;

    /**
     * Constructor of CallRecord
     * @param calledMethod the method called
     * @param callerMethod the method that calls it
     * @param arguments the parameters passed
     */
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

    /**
     * This method is used to check whether the parameters are passed by reference.
     * @param order the order of the parameter
     * @return whether the parameter is passed by reference
     */
    public int isTransParam(int order) {
        if(transParam.length!=0) {
            return transParam[order];
        }
        else {
            return 0;
        }
    }

    /**
     * This method is used to set whether the parameters are passed by reference.
     * @param order the order of the parameter
     * @param transParam whether the parameter is passed by reference
     */
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
