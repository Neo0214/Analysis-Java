package org.group05.analyzer.dataStructure;

import java.util.ArrayList;
import org.group05.analyzer.dataStructure.Index;

public class TransmissionClass {
    //type of query
    private String queryName;
    private String methodName;

    //Information of callees
    private ArrayList<String> calleeName = new ArrayList<>();
    private ArrayList<String> calleeParam = new ArrayList<>();
    private ArrayList calleeDepth = new ArrayList<>();

    //Information of callers
    private ArrayList<String> callerName = new ArrayList<>();
    private ArrayList<String> callerParam = new ArrayList<>();
    private ArrayList callerDepth = new ArrayList<>();

    public TransmissionClass(String queryname){
        this.queryName=queryname;
    }
    public void setMethodName(String name){
        this.methodName=name;
    }
    public void setCallee(ArrayList<String> nameList, int depth){
        if(!nameList.isEmpty()){
            this.calleeName.addAll(nameList);
        }
        for(String i : nameList){
            this.calleeDepth.add(depth);
        }
    }
    public void setCaller(ArrayList<String> nameList, int depth){
        if(!nameList.isEmpty()){
            this.callerName.addAll(nameList);
        }
        for(String i : nameList){
            this.callerDepth.add(depth);
        }
    }
    public void print(){
        System.out.println("method:"+this.methodName);
        int d = 0;
        System.out.println("callee:");
        if(!this.calleeName.isEmpty()){
            for(String i : this.calleeName){
                System.out.println(i + "  depth:" + calleeDepth.get(d));
                d++;
            }
        }
        d=0;
        System.out.println("caller:");
        if(!this.callerName.isEmpty()){
            for(String i : this.callerName){
                System.out.println(i + "  depth:" + callerDepth.get(d));
                d++;
            }
        }
    }
}
