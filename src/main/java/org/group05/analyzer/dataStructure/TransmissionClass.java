package org.group05.analyzer.dataStructure;

import java.util.ArrayList;
import org.group05.analyzer.dataStructure.MethodInfo;
import org.group05.analyzer.dataStructure.Index;

/**
 * This class is used to transmission information of a method
 */
public class TransmissionClass {
    //type of query
    private String queryName;
    private String methodName;

    //Information of callees
    private ArrayList<MethodInfo> callee = new ArrayList<>();
    private ArrayList<Integer> calleeDepth = new ArrayList<>();

    //Information of callers
    private ArrayList<MethodInfo> caller = new ArrayList<>();
    private ArrayList<Integer> callerDepth = new ArrayList<>();

    public TransmissionClass(String queryname){
        this.queryName=queryname;
    }
    public void setMethodName(String name){
        this.methodName=name;
    }
    public void addCallee(MethodInfo m, int depth){
        if(this.callee.indexOf(m)==-1 || this.calleeDepth.get(this.callee.indexOf(m)) != depth){
            this.callee.add(m);
            this.calleeDepth.add(depth);
        }
    }
    public void addCaller(MethodInfo m, int depth){
        if(this.caller.indexOf(m)==-1 || this.callerDepth.get(this.caller.indexOf(m)) != depth){
            this.caller.add(m);
            this.callerDepth.add(depth);
        }
    }
    public void print(){
        System.out.println("Method:\n    "+this.methodName);
        int d = 0;
        System.out.println("Callee:");
        if(!this.callee.isEmpty()){
            for(MethodInfo i : this.callee){
                System.out.println("    method:" + i.getName() + "  class:" + i.getClassName() + "  parameters:" + i.getParameters() + "  depth:" + calleeDepth.get(d));
                d++;
            }
        }
        else{
            System.out.println("    [NONE]");
        }
        d=0;
        System.out.println("Caller:");
        if(!this.caller.isEmpty()){
            for(MethodInfo i : this.caller){
                System.out.println("    method:" + i.getName() + "  class:" + i.getClassName() + "  parameters:" + i.getParameters() + "  depth:" + callerDepth.get(d));
                d++;
            }
        }
        else{
            System.out.println("    [NONE]");
        }
    }
}
