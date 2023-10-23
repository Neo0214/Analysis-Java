package org.group05.analyzer.dataStructure;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MethodInfo {
    private String name;
    private ArrayList<String> parameters;
    private ArrayList<Index> callee;
    private ArrayList<Index> caller;
}
