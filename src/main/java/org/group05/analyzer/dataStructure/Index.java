package org.group05.analyzer.dataStructure;

public class Index {
    private int classIndex;
    private int methodIndex;

    public Index(int classIndex, int methodIndex){
        this.classIndex = classIndex;
        this.methodIndex = methodIndex;
    }

    public int getClassIndex(){
        return this.classIndex;
    }

    public int getMethodIndex(){
        return this.methodIndex;
    }
}
