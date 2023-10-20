package org.group05.analyzer;

import java.io.File;

public class MainAnalyzer {
    private String filePath;

    public MainAnalyzer(String filePath) {
        setRootPath(filePath);
    }

    public boolean setRootPath(String filePath){
        File file=new File(filePath);
        if (file.isDirectory()) {
            this.filePath = filePath;
            return true;
        }
        else{
            return false;
        }
    }
}
