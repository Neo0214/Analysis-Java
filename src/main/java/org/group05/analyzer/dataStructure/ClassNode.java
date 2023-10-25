package org.group05.analyzer.dataStructure;

import java.util.ArrayList;

/**
 * This class is used to store the information of a class
 */
public class ClassNode {
    private String name;
    private ArrayList<MethodInfo> methods;
    private String fileName;

    public ClassNode(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
        methods = new ArrayList<>();
    }

    public void addMethod(MethodInfo method) {
        methods.add(method);
    }

    public void setMethods(ArrayList<MethodInfo> methods) {
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public ArrayList<MethodInfo> getMethods() {
        return methods;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * This method is used to get the method by its name and parameters
     * @param methodName the name of the method
     * @param paramList the parameters of the method
     * @return the method
     */
    public MethodInfo getMethodByName(String methodName, ArrayList<String> paramList){
        for(MethodInfo mymethod :this.methods){
            if(methodName.equals(mymethod.getName()) && paramList.equals(mymethod.getParameters())){
                return mymethod;
            }
        }
        return null;
    }
    public MethodInfo getMethodByIndex(int methodIndex){
        return this.methods.get(methodIndex);
    }
}
