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
    private ArrayList<String> CallerArgs;

    public  MethodNode(String MethodName,String ClassName)
    {
        setMethodName(MethodName);
        setClassName(ClassName);
        CallRecords = new ArrayList<>();
        CallerArgs = new ArrayList<>();
    }

    //getters and setters
    public void addCalledMethod(MethodNode calledMethod,MethodNode callerMethod,ArrayList<String>args) {
       CallRecords.add(new CallRecord(calledMethod,callerMethod,args));
    }

    public void removeCalledMethod(CallRecord call){
        // System.out.println("删掉至"+call.getCalleeMethod().getMethodName()+"的边");
        CallRecords.remove(call);
    }
    private void setClassName(String className) {
        ClassName = className;
    }
    private void setMethodName(String methodName) {
        MethodName = methodName;
    }
    public void setCallerArgs(ArrayList<String> callerArgs) {
        CallerArgs = callerArgs;
    }
    public ArrayList<String> getCallerArgs() {
        return CallerArgs;
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
        System.out.print("ClassName : "+ClassName+", MethodName : "+MethodName);
        System.out.print(", CallerArgs : ");
        for(String arg : CallerArgs){
            System.out.print(arg);
        }
        if(CallerArgs.isEmpty()){
            System.out.print("Null(not needed here)");
        }
        System.out.print('\n');

        for(CallRecord call : CallRecords){
            MethodNode callerMethod = call.getCallerMethod();
            MethodNode calledMethod = call.getCalleeMethod();
            ArrayList<String> calledArgs = call.getArguments();
            System.out.print("  "+callerMethod.getMethodName());
            //被调用的方法
            System.out.print(" calls : "+calledMethod.getMethodName());
            //被调用方法的所属类
            if(calledMethod.getMethodName()!=null){
                System.out.print("(Class:"+calledMethod.getClassName()+')');
            }
            else{

            }
            System.out.print(" with "+ calledArgs.size() + " args : ");
            for(String arg : calledArgs)
                System.out.print(arg+' ');
            if(calledArgs.isEmpty()){
                System.out.print("none");
            }
            System.out.print('\n');
        }
        if(CallRecords.isEmpty())
            System.out.println("  no call expression found");
    }


}
