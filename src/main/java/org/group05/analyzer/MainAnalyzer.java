package org.group05.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// MainAnalyzer should make AST for files, I recommend to create thread for efficiency.
// This part hasn't been implemented yet
public class MainAnalyzer {
    private File project;
    private ArrayList<String> fileList;

    public MainAnalyzer(String filePath) {
        this.fileList= new ArrayList<>();
        setRootPath(filePath);
    }

    public boolean setRootPath(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory() && hasJavaFile(file)) {
            this.project = file;
            return true;
        } else {
            return false;
        }
    }

    public void methodQuery(String methodName, String className, String depth) {
        // use MethodAnalyzer to do method query
    }

    public void parameterQuery(String parameterName, String className) {
        // use ParameterAnalyzer to do parameter query
    }

    /**
     * hasJavaFile
     * check if the project has java file and set fileList as all java file
     *
     * @return true if has java file
     */
    private boolean hasJavaFile(File file) {
        accessFile(file);
        return !this.fileList.isEmpty();
    }

    private void accessFile(File file) {
        if (file.isDirectory()){
            // if is directory, access all child files
            File[] files=file.listFiles();
            for (int i = 0; i< Objects.requireNonNull(files).length; i++){
                accessFile(files[i]);
            }
        }
        else if (file.isFile()){
            // if is file, check if it is java file
            if (file.getName().endsWith(".java")){
                this.fileList.add(file.getAbsolutePath());
            }
        }
    }
}
