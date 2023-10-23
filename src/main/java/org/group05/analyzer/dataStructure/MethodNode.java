package org.group05.analyzer.dataStructure;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class MethodNode {
    //the name of this method
    private String MethodName;
    //the class this method belongs to
    private String ClassName;
    //methods called by this method (stored in map)
    private ArrayList<CallRecord> CallRecords;


    public  MethodNode(String MethodName,String ClassName)
    {
        setMethodName(MethodName);
        setClassName(ClassName);
        CallRecords = new ArrayList<>();
    }

    //getters and setters
    public void addCalledMethod(MethodNode calledMethod,ArrayList<String>args) {
       CallRecords.add(new CallRecord(calledMethod,args));
    }
    private void setClassName(String className) {
        ClassName = className;
    }
    private void setMethodName(String methodName) {
        MethodName = methodName;
    }
    public ArrayList<CallRecord> getMethodCalled() {
        return CallRecords;
    }
    public String getClassName() {
        return ClassName;
    }
    public String getMethodName() {
        return MethodName;
    }

    //judges if another MethodNode has the same MethodName and ClassName with this one
    public boolean euqalsto(MethodNode method){
        return this.getMethodName().equals(method.getMethodName()) && this.getClassName().equals(method.getClassName());
    }


    //merges another MethodNode which has the same ClassName and MethodName with this MethodNode
    public void mergeCall(MethodNode method){
        for(CallRecord call : method.CallRecords){
            this.CallRecords.add(call);
        }
    }


    //print the calling relation according to the saved information
    public void printMethodCalled(){
        System.out.println("ClassName : "+ClassName+", MethodName : "+MethodName);
        for(CallRecord call : CallRecords){
            MethodNode calledMethod = call.getCalleeMethod();
            ArrayList<String> calledArgs = call.getArguments();
            System.out.print("  calls : "+calledMethod.getMethodName()+" with args : ");
            for(String arg : calledArgs)
                System.out.print(arg+' ');
            System.out.print('\n');
        }
        if(CallRecords.isEmpty())
            System.out.println("  no call expression found");
    }


}
