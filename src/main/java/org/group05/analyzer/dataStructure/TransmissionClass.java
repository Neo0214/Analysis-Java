package org.group05.analyzer.dataStructure;

import java.util.ArrayList;
import org.group05.analyzer.dataStructure.Index;

public class TransmissionClass {
    //type of query
    private String queryName;

    //Information of callees
    private ArrayList<String> calleeName = new ArrayList<>();
    private ArrayList<String> calleeParam = new ArrayList<>();
    private ArrayList<String> calleeDepth = new ArrayList<>();

    //Information of callers
    private ArrayList<String> callerName = new ArrayList<>();
    private ArrayList<String> callerParam = new ArrayList<>();
    private ArrayList<String> callerDepth = new ArrayList<>();

    public TransmissionClass(String queryname){
        this.queryName=queryname;
    }
    public void setMethodInfo(){
        //todo...
    }
}
