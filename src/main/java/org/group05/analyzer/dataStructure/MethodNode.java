package org.group05.analyzer.dataStructure;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class is used to store the information of a method(parameter analysis)
 */
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
    /**
     * This method is used to add a method called by this method
     * @param calledMethod the method called
     * @param callerMethod the method that calls it
     * @param args the parameters passed
     */
    public void addCalledMethod(MethodNode calledMethod,MethodNode callerMethod,ArrayList<String>args) {
       CallRecords.add(new CallRecord(calledMethod,callerMethod,args));
    }

    /**
     * This method is used to add a method called by this method
     * @param call the call record
     */
    public void removeCalledMethod(CallRecord call){
        System.out.println("delete to "+call.getCalleeMethod().getMethodName()+" 's edge");
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
    /**
     * This method is used to check whether the method is the same as another method
     * @param method the method to be compared
     * @return whether the method is the same as another method
     */
    public boolean euqalsto(MethodNode method){
        return this.getMethodName().equals(method.getMethodName()) && this.getClassName().equals(method.getClassName());
    }


    //merges another MethodNode which has the same ClassName and MethodName with this MethodNode
    /**
     * This method is used to merge another MethodNode which has the same ClassName and MethodName with this MethodNode
     * @param method the method to be merged
     */
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

            System.out.print(" calls : "+calledMethod.getMethodName());

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
