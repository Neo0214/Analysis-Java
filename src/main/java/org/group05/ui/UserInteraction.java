package org.group05.ui;

import org.group05.Main;
import org.group05.analyzer.MainAnalyzer;

import java.util.Scanner;

/**
 * This class is responsible for handling user interaction.
 */
public class UserInteraction {
    private Mode mode;   //current mode
    private final Scanner scanner;
    private MainAnalyzer mainAnalyzer;
    /**
     * Constructor for UserInteraction class.
     * default mode is output
     */
    public UserInteraction() {
        System.out.print("Welcome to the Java Project Analyzer!\n");
        setMode(Mode.COMMAND);
        putPrompt();
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
                //putMessage("Enter your command('method method_name class_name depth_of_search' or 'parameter method_name class_name'):\n");
                String command=getInput();
                execInstruction(command);
            }
            else if (mode==Mode.COMMAND){
                // command mode can change project root path or quit the system
                //putMessage("Enter your command('project root path' for new project or 'q' for quit):\n");
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
     * execCommand
     * This method is used to check the input command and choose to switch to a particular mode
     * @param command the command to be checked by UI to change its current mode
     */
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
     * execInstruction
     * This method is used in EXEC mode to send analyzer request
     * @param command the command to be checked and exec by analyzer
     */
    private void execInstruction(String command){
        if (command.equals("cmd")){
            setMode(Mode.COMMAND);
        }
        else if (isMethodQuery(command)){
            // do function query
            mainAnalyzer.methodQuery(command);
        }
        else if (isParameterQuery(command)){
            // do parameter query
            mainAnalyzer.parameterQuery(command);
        }
        else{
            putMessage("Wrong instruction,correct it:'method method_name class_name depth_of_search parameter[0] parameter[1]...' or 'parameter method_name class_name'\n");
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
        //return commandList.length == 4 && commandList[0].equals("method");
        if(commandList.length < 4){
            return false;
        }
        try {
            int depth = Integer.parseInt(commandList[3]);
            return commandList[0].equals("method");
        } catch (NumberFormatException e) {
            return false;
        }
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

    /**
     * getInput
     * block when no input
     * @return java.lang.String
     **/
    private String getInput(){
        putMessage("");
        while (!scanner.hasNextLine()){
        } // block when no input
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
        }
        else if (mode==Mode.EXEC){
            System.out.print("query>>");
            System.out.print(str);
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
        putMessage("press q to go to command mode to change project path, etc.\n");
        putMessage("Enter your java project path:\n");
    }

    /**
     * This method is used to set current mode
     * @param mode the mode to set
     */
    private void setMode(Mode mode){
        this.mode=mode;
    }
}
