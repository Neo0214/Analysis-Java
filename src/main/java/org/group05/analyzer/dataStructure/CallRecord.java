package org.group05.analyzer.dataStructure;

import org.group05.analyzer.dataStructure.MethodNode;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * This class is used to store the information of a call record.
 * A call record is a record of a method call in the code.
 * It contains the information of the callee method, the caller method and the arguments.
 * It also contains a boolean list to store whether this call record is related to multi-layer calls.
 */
public class CallRecord {
    private MethodNode calleeMethod;
    private MethodNode callerMethod;
    private ArrayList<String> arguments;

    //to store whether this CallRecord is related to multi-layer calls
    private int[] transParam;

    /**
     * Constructor of CallRecord
     * @param calledMethod the callee method
     * @param callerMethod the caller method
     * @param arguments the arguments of the callee method
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
     * This method is used to get the index of the argument in the argument list.
     * @param order the order of the argument in the argument list
     * @return the index of the argument in the argument list
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
     * This method is used to set the index of the argument in the argument list.
     * @param order the order of the argument in the argument list
     * @param transParam the index of the argument in the argument list
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
