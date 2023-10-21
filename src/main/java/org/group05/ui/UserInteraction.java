package org.group05.ui;

import org.group05.Main;
import org.group05.analyzer.MainAnalyzer;

import java.util.Scanner;

/**
 * This class is responsible for handling user interaction.
 */
public class UserInteraction {
    private Mode mode;
    private final Scanner scanner;
    private MainAnalyzer mainAnalyzer;
    /**
     * Constructor for UserInteraction class.
     * default mode is output
     */
    public UserInteraction() {
        putPrompt();
        setMode(Mode.COMMAND);
        scanner=new Scanner(System.in);
    }

    /**
     * This method is responsible for running the whole.
     *
     */
    public void run() {
        mainAnalyzer=new MainAnalyzer("");
        while (true){
            if (mode==Mode.EXEC){
                putMessage("Enter your command('method method_name class_name depth_of_search' or 'parameter method_name class_name'):\n");
                String command=getInput();
                execInstruction(command);
            }
            else if (mode==Mode.COMMAND){
                // command mode can change project root path or quit the system
                putMessage("Enter your command('project root path' for new project or 'q' for quit):\n");
                String command=getInput();
                execCommand(command);
            }
            else if (mode==Mode.QUIT){
                cleanWork();
                break;
            }
        }
    }

    /**
     * isMethodQuery
     * check if the command is method query
     * @param command the command to be checked and exec by analyzer
     * @return true if it is method query
     */
    private void execInstruction(String command){
        if (command.equals("cmd")){
            setMode(Mode.COMMAND);
        }
        else if (isMethodQuery(command)){
            // do function query
        }
        else if (isParameterQuery(command)){
            // do parameter query
        }
        else{
            putMessage("Wrong instruction,correct it:'method method_name class_name depth_of_search' or 'parameter method_name class_name'\n");
        }
    }

    /**
     * isMethodQuery
     * check if the command is method query
     * @param command the command to be checked
     * @return true if it is method query
     */
    private boolean isMethodQuery(String command){
        String[] commandList=command.split(" ");
        return commandList.length == 4 && commandList[0].equals("method");
    }
    /**
     * isParameterQuery
     * check if the command is parameter query
     * @param command the command to be checked
     * @return true if it is parameter query
     */
    private boolean isParameterQuery(String command){
        String[] commandList=command.split(" ");
        return commandList.length == 3 && commandList[0].equals("parameter");
    }


    /**
     * cleanWork
     * clean work before quit
     */
    private void cleanWork(){

    }
    private void execCommand(String command){
        if (command.equals("q")){
            setMode(Mode.QUIT);
        }
        else{
            // maybe this is path, check it
            if(this.mainAnalyzer.setRootPath(command)){
                System.out.print("cmd>set root path to "+command+"\n");
                setMode(Mode.EXEC);
            }
            else{
                putMessage("Wrong root path\n");
            }
        }
    }

    /**
     * getInput
     * block when no input
     * @return java.lang.String
     **/
    private String getInput(){
        while (!scanner.hasNextLine()){
        }
        return scanner.nextLine();
    }


    /**
     * This method is responsible for printing a message to the user.
     * also, default message for current mode
     * @param str the message to be printed
     */
    private void putMessage(String str) {
        if (mode==Mode.COMMAND){
            System.out.print("cmd>");
            System.out.print(str);
            System.out.print("cmd>");
        }
        else if (mode==Mode.EXEC){
            System.out.print(">>");
            System.out.print(str);
            System.out.print(">>");
        }
        else if (mode==Mode.QUIT){
            System.out.print("quiting......");
        }

    }
    /**
     * This method is responsible for printing a prompt to the user.
     * after prompt, it should change to input mode
     */
    private void putPrompt() {
        System.out.print("Welcome to the Java Project Analyzer!\n");
        System.out.print("cmd>press ESC at any time to command mode to change project path, etc.\n");
        System.out.print("cmd>Enter your java project path:\n");
    }

    private void setMode(Mode mode){
        this.mode=mode;
        putMessage("now in "+mode.toString()+" mode\n");
    }
}
