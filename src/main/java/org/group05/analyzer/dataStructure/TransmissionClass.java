package org.group05.analyzer.dataStructure;

import java.util.ArrayList;
import org.group05.analyzer.dataStructure.MethodInfo;
import org.group05.analyzer.dataStructure.Index;

public class TransmissionClass {
    //type of query
    private String queryName;
    private String methodName;

    //Information of callees
    private ArrayList<MethodInfo> callee = new ArrayList<>();
    private ArrayList calleeDepth = new ArrayList<>();

    //Information of callers
    private ArrayList<MethodInfo> caller = new ArrayList<>();
    private ArrayList callerDepth = new ArrayList<>();

    public TransmissionClass(String queryname){
        this.queryName=queryname;
    }
    public void setMethodName(String name){
        this.methodName=name;
    }
    public void addCallee(MethodInfo m, int depth){
        this.callee.add(m);
        this.calleeDepth.add(depth);
    }
    public void addCaller(MethodInfo m, int depth){
        this.caller.add(m);
        this.callerDepth.add(depth);
    }
    public void print(){
        System.out.println("method:"+this.methodName);
        int d = 0;
        System.out.println("callee:");
        if(!this.callee.isEmpty()){
            for(MethodInfo i : this.callee){
                System.out.println(i.getName() + "  depth:" + calleeDepth.get(d));
                d++;
            }
        }
        else{
            System.out.println(" [NONE]");
        }
        d=0;
        System.out.println("caller:");
        if(!this.caller.isEmpty()){
            for(MethodInfo i : this.caller){
                System.out.println(i.getName() + "  depth:" + callerDepth.get(d));
                d++;
            }
        }
        else{
            System.out.println(" [NONE]");
        }
    }
}
