package input.options;

import input.task.TaskInputDto;
import input.task.impl.CompilationTaskInputDto;
import input.task.impl.SimulationTaskInputDto;
import main.PrimitiveWrapper;
import output.CirclePathForTargetDto;
import output.GraphInfoDto;
import output.TargetInfoDto;
import manager.Mediator;
import xml.DependenyConflict;
import xml.TargetAlreadyExist;
import xml.TargetNotExist;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import static java.lang.System.in;

public class ConsoleFromUser {
    private Mediator med;
    final private Scanner scanner;
    final private PrimitiveWrapper<Boolean> exitToMainMenu;
    private List<Consumer<String>> consumersListOfStr;

    public ConsoleFromUser() {
        med = new Mediator();
        scanner = new Scanner(in);
        exitToMainMenu = new PrimitiveWrapper<>(false);
        consumersListOfStr = new ArrayList<>();
        consumersListOfStr.add(System.out::print);
    }

    private Boolean isFileLoaded() {
        if (med.isFileLoaded())
            return true;
        else {
            System.out.println("File has not opened yet.\nPlease enter the file first");
            return false;
        }
    }

    public void run() {
        boolean exitProgram = false;
        String option;
        while (!exitProgram) {
            printMenu();
            option = scanner.nextLine().trim();
            switch (option) {

                case "1": {//Load system information from a file
                    case1();
                    break;
                }
                case "2": {
                    if (isFileLoaded()) {
                        case2();
                    }
                    break;
                }
                case "3": {//Show information about a target
                    if (isFileLoaded()) {
                        case3();
                    }
                    break;
                }
                case "4": {
                    if (isFileLoaded()) {
                        case4();
                    }
                    break;
                }
                case "5": {//Run a task
                    if (isFileLoaded()) {
                        case5();
                    }
                    break;
                }
                case "6": {
                    if (isFileLoaded()) {
                        case6();
                    }
                    break;
                }
                case "7": {
                    if (isFileLoaded()) {
                        case7();
                    }
                    break;
                }
                case "8": {
                    case8();
                    break;
                }
                case "9": {//Exit system
                    exitProgram = true;
                    System.out.println("Bye Bye Amigo\n");
                    break;
                }
                default: {
                    System.out.println("This is not an option,please enter a valid number between 1 to 9\n");
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("\nPlease enter a number representing an option from the following:\n");
        System.out.println("#1 Load system information from a file\n");
        System.out.println("#2 Show general information about the targets' Graph\n");
        System.out.println("#3 Show information about a target\n");
        System.out.println("#4 Find a bind between two targets\n");
        System.out.println("#5 Run a task\n");
        System.out.println("#6 Check if a target participates in a circle\n");
        System.out.println("#7 Save system to a file\n");
        System.out.println("#8 Load system from a file\n");
        System.out.println("#9 Exit system\n");
    }

    private void case1() {
        boolean succeed = false;
        while (!succeed) {
            System.out.println("Please enter the full file name\nPress 0 for return to the main menu \n");
            String fileName = scanner.nextLine().trim();
            if (fileName.equals("0"))//return to main menu
                return;
            if (isFileNameValid(fileName)) {
                File f = new File(fileName);
                if (!f.exists()) {
                    System.out.println("File does not exist! please try again");
                } else {
                    Mediator temp = null;
                    try {
                        temp = new Mediator();
                        succeed = temp.loadFile(fileName);
                    } catch (IOException | JAXBException | TargetAlreadyExist | TargetNotExist | DependenyConflict e1) {
                        System.out.println("Error in loading because:\n" + e1.getMessage());
                    }
                    if (succeed) {
                        med = temp;
                        System.out.println("Load successfully\n");
                    }
                }
            }
        }
    }

    private Boolean isFileNameValid(final String fileName) {
        try {
            if (med.isFileNameValid(fileName)) {
                return true;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void case2() {
        final GraphInfoDto graphDto = med.getGraphInfoDto();
        System.out.println("The amount of Targets in the Graph are: " + graphDto.getTargetsAmount());
        System.out.println("The amount of Roots in the Graph are: " + graphDto.getRootsAmount());
        System.out.println("The amount of Middles in the Graph are: " + graphDto.getMiddlesAmount());
        System.out.println("The amount of Leaves in the Graph are: " + graphDto.getLeavesAmount());
        System.out.println("The amount of Independents in the Graph are: " + graphDto.getIndependentAmount());
    }

    private void case3() {
        boolean succeed = false;
        String str;
        TargetInfoDto t = null;
        while (!succeed) {
            System.out.println("Please enter the target's name or press 0 for return to the main menu \n");
            str = scanner.nextLine().trim();
            if (str.equals("0"))//return to main menu
                return;
            t = med.getTargetInfoDto(str);
            if (t == null) {
                System.out.println("The target '" + str + "' doesn't exist\n");
            } else
                succeed = true;
        }
        showInfoAboutTarget(t);
    }

    private void showInfoAboutTarget(final TargetInfoDto t) {
        System.out.println("The target name is: " + t.getTargetName());
        System.out.println("The position of the target is: " + t.getTargetStatus());
        List<String> tInListNames = t.getInTargetListNames();
        List<String> tOutListNames = t.getOutTargetListNames();
        if (tInListNames.isEmpty()) {
            System.out.println("There aren't any targets that " + t.getTargetName() + " required directly for them");
        } else {
            System.out.println("The targets that " + t.getTargetName() + " required directly for them are:");
            for (String currTargetName : tInListNames) {
                System.out.println(currTargetName);
            }
            System.out.println("\n\n");
        }
        if (tOutListNames.isEmpty()) {
            System.out.println("There aren't any targets that " + t.getTargetName() + " directly depends on them\n");
        } else {
            System.out.println("The targets that are " + t.getTargetName() + " directly depends on them are:");
            for (String currTargetName : tOutListNames) {
                System.out.println(currTargetName);
            }
            System.out.println("\n\n");
        }
        if (t.getTargetExtraInfo() == null) {
            System.out.println("There is no extra data about the target");
        } else {
            System.out.println("The extra data on the target is: " + t.getTargetExtraInfo() + "\n");
        }
    }


    private TargetInfoDto getValidTargetForCase4(final String targetRole) {
        boolean nameSucceed = false;
        TargetInfoDto targetDto = null;
        String str;
        while (!nameSucceed && !exitToMainMenu.get()) {
            System.out.format("Enter the %s target name\nPress 0 for return to the main menu:\n", targetRole);
            str = scanner.nextLine().trim();
            if (str.equals("0")) {
                exitToMainMenu.set(true);
            } else {
                targetDto = med.getTargetInfoDto(str);
                if (targetDto == null) {
                    System.out.println("target has not found! please try again\n");
                } else {
                    nameSucceed = true;
                }
            }
        }
        return targetDto;
    }

    private String getValidRelationForCase4() {
        String relation = null;
        boolean relationSuccess = false;
        System.out.println("What is the relation between the source and the destination targets?\n");
        while (!relationSuccess && !exitToMainMenu.get()) {
            System.out.println("Please enter 1 for 'Depends on' or 2 for 'Required for'\n" +
                    "Press 0 for return to the main menu");
            relation = scanner.nextLine().trim();
            if (relation.equals("0")) {
                exitToMainMenu.set(true);
            } else if (relation.equals("1") || relation.equals("2"))
                relationSuccess = true;
            else
                System.out.println("Wrong input\n");
        }
        return relation;
    }

    private void case4() {
        TargetInfoDto t1, t2;
        String str1, str2, relation;
        System.out.println("Please enter two targets name, the first will be the source and the second will be the destination \n");
        t1 = getValidTargetForCase4("source");
        if (!exitToMainMenu.get()) {
            t2 = getValidTargetForCase4("destination");
        } else {
            exitToMainMenu.set(false);
            return;//if t1 "wants" to go to the main menu
        }

        if (!exitToMainMenu.get()) {//if t2 "does not want" to go to the main menu
            str1 = t1.getTargetName();
            str2 = t2.getTargetName();
            relation = getValidRelationForCase4();
            if (exitToMainMenu.get()) {
                exitToMainMenu.set(false);
                return;
            }
            System.out.format("Source: %s \nDestination: %s\n relation: ", str1, str2);
            System.out.println(relation.equals("1") ? "Depends on\n" : "Required on\n");
            System.out.println("Path:\n");
            if (relation.equals("1")) {
                findBindBetweenTwoTargetsAndPrintIfExist(str1, str2, false);
            } else {
                findBindBetweenTwoTargetsAndPrintIfExist(str2, str1, true);
            }
        } else {
            exitToMainMenu.set(false);
            //if t2 "wants" to go to the main menu
        }
    }

    private void case6() {
        CirclePathForTargetDto res = null;
        TargetInfoDto t = getValidTargetForCase4("\b");
        if (exitToMainMenu.get()) {
            exitToMainMenu.set(false);
            return;
        }
        try {
            res = med.checkIfTargetIsInCircle(t.getTargetName());
        } catch (TargetNotExist e) {
            System.out.println(e.getMessage());
            return;
        }
        if (res.getResult().isEmpty()) {
            System.out.println("There isn't any circle that includes '" + t.getTargetName() + "'\n");
            return;
        }
        LinkedList<String> circle = res.getResult();
        int len = circle.size();
        for (int i = 0; i < len - 1; i++) {
            System.out.format("%s-> ", circle.get(i));
        }
        System.out.println(circle.get(len - 1));
        System.out.println("\n");
    }

    private void findBindBetweenTwoTargetsAndPrintIfExist(String name1, String name2, boolean isSwitched) {
        final List<List<String>> pathsList = med.findBindBetweenTwoTargets(name1, name2).getAllPaths();
        if (pathsList.isEmpty())
            System.out.println("There isn't any path between the two of the targets\n");
        else if (!isSwitched) {
            for (List<String> currList : pathsList) {
                for (String s : currList) {
                    if (s.equals(currList.get(currList.size() - 1)))//if we got the "tail"
                        System.out.format("%s", s);
                    else//if we don't hold the "tail"
                        System.out.format("%s-> ", s);
                }
                System.out.println("\n");
            }
        } else//if it's switched
        {
            String helper = "";
            for (List<String> currList : pathsList) {
                for (String s : currList) {
                    if (s.equals(currList.get(0)))//if we got the "head"
                        helper = s;
                    else//if we don't hold the "head"
                        helper = s + "-> " + helper;
                }
                System.out.println(helper + '\n');
            }
        }
        System.out.println("\n");
    }

    private void case5() {
        Mediator.TaskType taskType = getTaskType();
        TaskInputDto data = null;
        if (exitToMainMenu.get()) {
            exitToMainMenu.set(false);
            return;
        }
        Boolean isScratch = getIsScratch();
        if (exitToMainMenu.get()) {
            exitToMainMenu.set(false);
            return;
        }
        Integer amountOfThreads = getAmountOfThreads();
        if (exitToMainMenu.get()) {
            exitToMainMenu.set(false);
            return;
        }
        if (taskType.equals(Mediator.TaskType.SIMULATION)) {
            data = handleSimulationFromUser(isScratch, amountOfThreads);
        } else if (taskType.equals(Mediator.TaskType.COMPILATION)) {
            data = handleCompilationFromUser(isScratch, amountOfThreads);
        }
        if (data == null) {
            exitToMainMenu.set(false);
            return;
        }
        try {
            med.runTask(Mediator.TaskType.SIMULATION, data, consumersListOfStr);//todo make it generic
        } catch (SecurityException e1) {
            System.out.println(e1.getMessage());
        }
    }
    private Integer getAmountOfThreads() {
        boolean correctInput = false;
        String threadsAmountStr;
        Integer threadsAmount = null;
        while (!correctInput && !exitToMainMenu.get()) {
            System.out.println("Please enter the threads amount (an integer number between 1 to "
                    + med.getMaxThreadsAmount() + ")\nType 'exit' for return to the main menu");
            threadsAmountStr = scanner.nextLine().trim();
            if (threadsAmountStr.equals("exit")) {
                exitToMainMenu.set(true);
            } else {
                try {
                    threadsAmount = Integer.parseInt(threadsAmountStr);
                    if(threadsAmount>=1&&threadsAmount<=med.getMaxThreadsAmount()) {
                        correctInput = true;
                    }
                    else{
                        System.out.println("Wrong number,you should enter an integer number between 1 to " +
                                med.getMaxThreadsAmount() +"\nPlease try again");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Wrong input,you should enter an integer number between 1 to " +
                           med.getMaxThreadsAmount() +"\nPlease try again");
                }
            }
        }
        return threadsAmount;
    }

    private Mediator.TaskType getTaskType() {
        boolean correctInput = false;
        Mediator.TaskType taskType = null;
        String taskTypeStr;
        while (!correctInput && !exitToMainMenu.get()) {
            System.out.println("Please enter the task type:)\n" +
                    "1.Simulation Task\n2.Compilation\nType 'exit' for return to the main menu");
            taskTypeStr = scanner.nextLine().trim();
            if (taskTypeStr.equals("exit")) {
                exitToMainMenu.set(true);
            } else if (taskTypeStr.equals("1")) {
                taskType = Mediator.TaskType.SIMULATION;
                correctInput = true;
            } else if (taskTypeStr.equals("2")) {
                taskType = Mediator.TaskType.COMPILATION;
                correctInput = true;
            } else {
                System.out.println("Wrong input, please try again\n");
            }
        }
        return taskType;
    }

    private Long getProcessTime() {
        boolean correctInput = false;
        String processTimeString;
        Long processTime = null;
        while (!correctInput && !exitToMainMenu.get()) {
            System.out.println("Please enter the process time for all the targets in task(an integer in milliSeconds)\n" +
                    "Type 'exit' for return to the main menu");
            processTimeString = scanner.nextLine().trim();
            if (processTimeString.equals("exit")) {
                exitToMainMenu.set(true);
            } else {
                try {
                    processTime = Long.parseLong(processTimeString);
                    correctInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Wrong input, please try again");
                }
            }
        }
        return processTime;
    }

    private Boolean getProcessTimeType(Long processTime) {
        Boolean correctInput = false, isRandom = null;
        String randomOrFixedStr;
        while (!correctInput && !exitToMainMenu.get()) {
            System.out.format("Please enter 1 for a random process time (that wil be blocked from 0 to %d)\n" +
                    "Enter 2 for not a random process time.\nType 'exit' for return to the main menu. \n", processTime);
            randomOrFixedStr = scanner.nextLine().trim();
            if (randomOrFixedStr.equals("exit")) {
                exitToMainMenu.set(true);
            } else if (randomOrFixedStr.equals("1")) {
                isRandom = true;
                correctInput = true;
            } else if (randomOrFixedStr.equals("2")) {
                isRandom = false;
                correctInput = true;
            } else {
                System.out.println("Wrong input, please try again\n");
            }
        }
        return isRandom;
    }

    private Double getProbabilityOfSuccess() {
        Boolean correctInput = false;
        String probabilitySuccessStr = null;
        Double probabilitySuccess = null;
        while (!correctInput && !exitToMainMenu.get()) {
            System.out.println("Please enter the probability of success of a task(a decimal number between 0 to 1).\n" +
                    "Type 'exit' for return to the main menu\n");
            probabilitySuccessStr = scanner.nextLine().trim();//check it
            if (probabilitySuccessStr.equals("exit")) {
                exitToMainMenu.set(true);
            } else try {
                probabilitySuccess = Double.parseDouble(probabilitySuccessStr);
                if (!(probabilitySuccess >= 0 && probabilitySuccess <= 1)) {
                    System.out.println("Wrong input enter a decimal number between 0 to 1 ");
                } else {
                    correctInput = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, please try again");
            }
        }
        return probabilitySuccess;
    }

    private Double getProbabilityOfWarning() {
        Boolean correctInput = false;
        String probabilityWarningStr = null;
        Double probabilityWarning = null;
        while (!correctInput && !exitToMainMenu.get()) {
            System.out.println("In case a task will succeed,what is the probability it will succeed with warning?\n" +
                    "(enter a decimal number between 0 to 1)\n" +
                    "Type 'exit' for return to the main menu.\n");
            probabilityWarningStr = scanner.nextLine().trim();
            if (probabilityWarningStr.equals("exit")) {
                exitToMainMenu.set(true);
            } else try {
                probabilityWarning = Double.parseDouble(probabilityWarningStr);
                if (!(probabilityWarning >= 0 && probabilityWarning <= 1)) {
                    System.out.println("Wrong input enter a decimal number between 0 to 1 ");
                } else {
                    correctInput = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, please try again");
            }
        }
        return probabilityWarning;
    }

    private Boolean getIsScratch() {
        Boolean correctInput = false, isScratch = null;
        while (!correctInput && !exitToMainMenu.get()) {
            System.out.println("Enter 1 for running a task from scratch\nEnter 2 for incremental\n" +
                    "Enter 0 for return to the main menu\n" +
                    "Pay attention: in case you enter 2 and there isn't previous data the running will be from scratch automatically\n");
            String isScratchStr = scanner.nextLine().trim();
            if (isScratchStr.equals("0")) {
                exitToMainMenu.set(true);
            } else if (isScratchStr.equals("1")) {
                isScratch = true;
                correctInput = true;
            } else if (isScratchStr.equals("2")) {
                isScratch = false;
                correctInput = true;
            } else {
                System.out.println("Wrong input, please try again\n");
            }
        }
        return isScratch;
    }

    private void case7() {
        System.out.println("Please enter the full path name of the file without the suffix\nEnter 0 for return to the main menu");
        String fileName = scanner.nextLine().trim();
        if (fileName.equals("0")) {
            return;
        }
        fileName += ".txt";
        try {
            med.writeToFile(fileName);
        } catch (IOException ignore) {
            System.out.println("Error! can not write to the file\n");
            return;
        }
        System.out.println("Saved successfully!\n");
    }

    private void case8() {
        System.out.println("Please enter the full path name of the file without the suffix\n" +
                "Enter 0 for return to the main menu");
        String fileName = scanner.nextLine().trim();
        if (fileName.equals("0")) {
            return;
        }
        fileName += ".txt";
        File f = new File(fileName);
        if (!f.exists()) {
            System.out.println("\nthe file full path name does not exist!\nPlease try again\n");
            return;
        }
        try {
            med.readFromFile(fileName);
        } catch (IOException | ClassNotFoundException ignore) {
            System.out.println("Error! can not load from a file\n");
            return;
        }
        System.out.println("Load successfully!\n");
    }

    private TaskInputDto handleSimulationFromUser(Boolean isScratch, Integer amountOfThreads) {
        Long processTime = getProcessTime();
        if (exitToMainMenu.get()) {
            exitToMainMenu.set(false);
            return null;
        }
        Boolean isRandom = getProcessTimeType(processTime);
        if (exitToMainMenu.get()) {
            exitToMainMenu.set(false);
            return null;
        }
        Double probabilitySuccess = getProbabilityOfSuccess();
        if (exitToMainMenu.get()) {
            exitToMainMenu.set(false);
            return null;
        }
        Double probabilityWarning = getProbabilityOfWarning();
        if (exitToMainMenu.get()) {
            exitToMainMenu.set(false);
            return null;
        }
        return new SimulationTaskInputDto(processTime, isRandom, probabilitySuccess,
                probabilityWarning, isScratch, amountOfThreads);
    }

    private String getCompilationSourceFolderPath() {
        String compilationSourceFolderPath = null;
        Boolean correctInput = false;
        while (!correctInput && !exitToMainMenu.get()) {
            System.out.println("Please enter the full path to the main directory which contains all the source code for for the compilation\n" +
                    "The directory must be exist in advance\nEnter 0 for return to the main menu\n");
            compilationSourceFolderPath = scanner.nextLine().trim();
            if (compilationSourceFolderPath.equals("0")) {
                exitToMainMenu.set(true);
                break;
            }
            File f = new File(compilationSourceFolderPath);
            if (!f.exists()) {
                System.out.println("\nthe full path to the main directory does not exist!\nPlease try again\n");
            } else if (!f.isDirectory()) {
                System.out.println("\nthe full path you've entered belongs to a file and not to a directory\nPlease try again\n");
            } else {
                correctInput = true;
            }
        }
        return compilationSourceFolderPath;
    }

    private TaskInputDto handleCompilationFromUser(Boolean isScratch, Integer amountOfThreads) {
        String compilationSourceFolderPath = getCompilationSourceFolderPath();
        if (compilationSourceFolderPath == null) {
            return null;//return to the main menu
        }
        String compilationDestFolderPath = getCompilationDestFolderPath();//todo is it from the xml or user?
        if (compilationDestFolderPath == null) {
            return null;//return to the main menu
        }
        return new CompilationTaskInputDto(isScratch, amountOfThreads,
                compilationSourceFolderPath, compilationDestFolderPath);
    }
    private String getCompilationDestFolderPath() {
        String compilationDestFolderPath = null;
        Boolean correctInput = false;
        while (!correctInput && !exitToMainMenu.get()) {
            System.out.println("Please enter the full path to the directory which the compilation products will be created into " +
                    "\nIf the directory does not exist it will be created.\nEnter 0 for return to the main menu\n");
            compilationDestFolderPath = scanner.nextLine().trim();
            if (compilationDestFolderPath.equals("0")) {
                exitToMainMenu.set(true);
                break;
            }
            File f = new File(compilationDestFolderPath);
            if (!f.exists()) {
                boolean isDirectoryCreated = f.mkdir();
                if (!isDirectoryCreated) {
                    throw new SecurityException("The computer had a problem with creating the directory in the current path:\n");
                }
            }
            else if (!f.isDirectory()) {//todo check how to handle it, if the directory doesn't exist and it is a file
                System.out.println("\nthe full path you've entered belongs to a file and not to a directory\nPlease try again\n");
            } else {
                correctInput = true;
            }
        }
        return compilationDestFolderPath;
    }
}




