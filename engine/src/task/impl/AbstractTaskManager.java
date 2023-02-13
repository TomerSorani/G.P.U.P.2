package task.impl;

import data.structure.Graph;
import data.structure.Target;
import data.structure.TargetsSerialSet;
import input.task.TaskInputDto;
import javafx.beans.property.SimpleBooleanProperty;
import manager.Mediator;
import output.UpdateTargetStatusDuringTaskDto;
import task.Task;
import task.TaskTargetRun;
import task.misc.TargetListViewDetails;
import task.misc.UiAdapterInterface;
import task.thread.ThreadManager;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractTaskManager implements Serializable, Task {
    private boolean isFirstTime;
    private LinkedList<Target> sortedTargetList;
    protected Map<Target, RunningState> targetRunningStateMap;
    protected Map<Target, RunningResult> targetRunningResultMap;
    private Map<Target, String> targetsRunningTimeMap;
    protected Set<Target> runningTargetsThatAlreadyFinishedOrSkippedMap;

   private Map<Target, Instant> targetsWaitingTimeMap;
   private Map<Target, Instant> targetsInProcessTimeMap;

    protected UiAdapterInterface uiAdapter;
    protected int amountOfRunningTargets;

    protected Graph g;
    private String taskPath;
    public enum RunningResult {
        SUCCESS, WARNING, FAILURE, FROZEN, SKIPPED,DONT_CHECK;
    }

    public enum RunningState {
        FROZEN, SKIPPED, WAITING, IN_PROCESS, FINISHED;
    }

    public AbstractTaskManager(final Graph gr,UiAdapterInterface UiAdapterIn) {
        amountOfRunningTargets=gr.getTargetsAmount();
        runningTargetsThatAlreadyFinishedOrSkippedMap= new HashSet<>();
        uiAdapter=UiAdapterIn;
        taskPath = null;
        g = gr;
        isFirstTime = true;
        sortedTargetList = g.bottomUpSort();
        targetRunningResultMap = new HashMap<>();
        targetRunningStateMap = new HashMap<>();
        targetsRunningTimeMap = new HashMap<>();
        targetsWaitingTimeMap=new HashMap<>();
        targetsInProcessTimeMap=new HashMap<>();

        for (Target t : sortedTargetList) {
            targetRunningResultMap.put(t, RunningResult.FROZEN);
            if (t.getStatus().equals(Target.TargetStatus.LEAF.toString()) ||
                    t.getStatus().equals(Target.TargetStatus.INDEPENDENT.toString())) {
                uiAdapter.updateTargetStatusDuringTask(
                        new UpdateTargetStatusDuringTaskDto(t.getName(),null,"WAITING"));
                targetRunningStateMap.put(t, RunningState.WAITING);
                targetsWaitingTimeMap.put(t, Instant.now());
            }
            else {
                uiAdapter.updateTargetStatusDuringTask(
                        new UpdateTargetStatusDuringTaskDto(t.getName(),null,"FROZEN"));
                targetRunningStateMap.put(t, RunningState.FROZEN);
            }
        }
    }
   public synchronized TargetListViewDetails getTargetListViewDetails(String targetName){
       Target t=g.findTargetByName(targetName);
        String level,finishRunningResultType=null,targetRunningState,MsWaitingTimeInQueueAlready=null,MsInProcessTimeTookAlready=null;
        Set<String> serialSetsName=null,directDependsList=null,directAndIndirectDependsListThatFailed=null;

        level=t.getStatus();    //level

       Set<TargetsSerialSet> serialSets=t.getTheSetsThatTheTargetBelongsTo();   //serial sets
       if(!serialSets.isEmpty()) {
           serialSetsName=new HashSet<>();//todo fix it
           for (TargetsSerialSet serial : serialSets) {
               serialSetsName.add(serial.getSerialSetName());
           }
       }
            targetRunningState = targetRunningStateMap.get(t).toString();
       switch (targetRunningState) {
           case "FINISHED": {
               finishRunningResultType = targetRunningResultMap.get(t).toString();
               break;
           }
           case "WAITING" : {
               Instant endTime = Instant.now();
               Duration res = Duration.between(targetsWaitingTimeMap.get(t), endTime);
               MsWaitingTimeInQueueAlready = getFullTimeFromDuration(res);
               break;
           }
           case "IN_PROCESS" : {
               Instant endTime = Instant.now();
               Duration res = Duration.between(targetsInProcessTimeMap.get(t), endTime);
               MsInProcessTimeTookAlready = getFullTimeFromDuration(res);
               break;
           }
           case "FROZEN" : {
               List<Target> directDependOnList = t.getOutTargetList();
               if (!directDependOnList.isEmpty()) {
                   directDependsList = new HashSet<>();
                   for (Target tar : directDependOnList) {//todo check if it makes problems in sycnchronze when one target is skipped(the one we depand at and we are frozen it the same time)
                       RunningState TarRunningStateMapStr = targetRunningStateMap.get(tar);
                       if (TarRunningStateMapStr.equals(RunningState.FROZEN) ||
                               TarRunningStateMapStr.equals(RunningState.WAITING) ||
                               TarRunningStateMapStr.equals(RunningState.IN_PROCESS)) {
                           directDependsList.add(tar.getName());
                       }
                   }
               }
               break;
           }
           case "SKIPPED" : {
               Set<Target> accessibleTargets = g.getAccessibleTargets(false, t);
               if (!accessibleTargets.isEmpty()) {
                   directAndIndirectDependsListThatFailed = new HashSet<>();
                   for (Target tar : accessibleTargets) {
                       RunningState TarRunningStateMapStr = targetRunningStateMap.get(tar);
                       if (TarRunningStateMapStr.equals(RunningState.FINISHED) &&
                               targetRunningResultMap.get(tar).equals(RunningResult.FAILURE)) {
                           directAndIndirectDependsListThatFailed.add(tar.getName());
                       }
                   }
               }
               break;
           }
       }

        return new TargetListViewDetails(targetName,level,serialSetsName,
                 directDependsList, MsWaitingTimeInQueueAlready,
                 directAndIndirectDependsListThatFailed,
                 MsInProcessTimeTookAlready, finishRunningResultType, targetRunningState);
    }
    public String createTaskFolderAndGetPath(Mediator.TaskType type) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        dateStr = dateStr.replaceAll(":", ".");

        String typeNameCapitalOnlyFirstLetter = type.toString();
        typeNameCapitalOnlyFirstLetter = typeNameCapitalOnlyFirstLetter.charAt(0) + typeNameCapitalOnlyFirstLetter.substring(1).toLowerCase();
        taskPath = g.getWorkingDirectory() + "\\" + typeNameCapitalOnlyFirstLetter + " " + dateStr;
        File currentTaskFolder = null;
        try {
            currentTaskFolder = new File(taskPath);
        } catch (Exception ignore) {
        }

        if (currentTaskFolder != null) {
            boolean isFolderCreated = currentTaskFolder.mkdir();
            if (!isFolderCreated)
                throw new SecurityException("The computer had a problem with creating a folder in the current path:\n");
        }
        return taskPath;
    }

    protected void setFirstTime(boolean input) {
        isFirstTime = input;
    }

    final public boolean areThereAnyLeftOvers() {
        if (isFirstTime) {
            return true;
        } else {
            for (Target t : sortedTargetList) {
                if (targetRunningResultMap.get(t).equals(RunningResult.FAILURE)
                        || targetRunningResultMap.get(t).equals(RunningResult.FROZEN))
                    return true;
            }
            return false;
        }
    }
    /*
        public enum RunningResult {
        SUCCESS, WARNING, FAILURE, FROZEN, SKIPPED,DONT_CHECK;
    }

    public enum RunningState {
        FROZEN, SKIPPED, WAITING, IN_PROCESS, FINISHED;
    }
     */
    public void incrementor() {
        for (Target t : sortedTargetList) {
            if (targetRunningResultMap.get(t).equals(RunningResult.FAILURE) ||
                    targetRunningResultMap.get(t).equals(RunningResult.SKIPPED)) {
                if (t.getStatus().equals(Target.TargetStatus.LEAF.toString()) || t.getStatus().equals(Target.TargetStatus.INDEPENDENT.toString())) {
                    uiAdapter.updateTargetStatusDuringTask(
                            new UpdateTargetStatusDuringTaskDto(t.getName(),null,"WAITING"));
                    targetRunningStateMap.put(t, RunningState.WAITING);
                    targetsWaitingTimeMap.put(t, Instant.now());
                }
                else {
                    uiAdapter.updateTargetStatusDuringTask(
                            new UpdateTargetStatusDuringTaskDto(t.getName(),null,"FROZEN"));
                    targetRunningStateMap.put(t, RunningState.FROZEN);
                }
                targetRunningResultMap.put(t, RunningResult.FROZEN);
            }
            else {//if we do two times increment maybe the previous "real state" is dont check as well-> other radical case to check
                uiAdapter.updateTargetStatusDuringTask(
                        new UpdateTargetStatusDuringTaskDto(t.getName(),null,"DONT_CHECK"));
                targetRunningResultMap.put(t,RunningResult.DONT_CHECK);
            }
        }
        for (Target u : sortedTargetList) {
            if (!u.getInTargetList().isEmpty()) {
                for (Target v : u.getInTargetList()) {
                    boolean toAdd = true;
                    for (Target t : v.getOutTargetList()) {
                        if ((targetRunningResultMap.get(t).equals(RunningResult.FAILURE)) ||
                                (targetRunningResultMap.get(t).equals(RunningResult.SKIPPED)) ||
                                targetRunningResultMap.get(t).equals(RunningResult.FROZEN)) {
                            toAdd = false;
                            break;
                        }
                    }
                    if (toAdd &&(!targetRunningStateMap.get(v).equals(RunningState.FINISHED))) {
                        uiAdapter.updateTargetStatusDuringTask(
                                new UpdateTargetStatusDuringTaskDto(v.getName(),targetRunningStateMap.get(v).toString(),"WAITING"));
                        targetRunningStateMap.put(v, RunningState.WAITING);
                        targetsWaitingTimeMap.put(v, Instant.now());
                    }
                }
            }
        }
        amountOfRunningTargets=getAmountOfRunningTargets();
        runningTargetsThatAlreadyFinishedOrSkippedMap.clear();
    }

    final public Map<Target, String> getTargetsRunningTimeMap() {
        return targetsRunningTimeMap;
    }

    final public Map<Target, RunningResult> getTargetRunningResultMap() {
        return targetRunningResultMap;
    }

    final public String getTaskPath() {
        return taskPath;
    }

    abstract protected void RunTaskOnTarget(Target t, FileWriter targetFile,ThreadManager theThreadManager);
    abstract public void updateMembers(TaskInputDto taskInput);

    public void executeTaskOnTarget(final String taskFolder, Target u, ThreadManager theThreadManager,Mediator.TaskType taskType) {
        while(theThreadManager.checkIfPaused()){
            synchronized (theThreadManager.getPauseLock()){
                try{
                    theThreadManager.getPauseLock().wait();
                }
                catch (InterruptedException ignore){}
            }
        }
        FileWriter targetFile = null;
        String fileName=null;
        System.out.println("Thread " + Thread.currentThread().getName() + " start running on " + u.getName() + ". id:" + Thread.currentThread().getId() + "\n");
        try {
             fileName = taskFolder + "\\" + u.getName() + ".log";
            targetFile = new FileWriter(fileName);
            printStartingAndExtraInfoAboutTarget(u, uiAdapter, targetFile);
            synchronized (this) {
                uiAdapter.updateTargetStatusDuringTask(
                        new UpdateTargetStatusDuringTaskDto(u.getName(),targetRunningStateMap.get(u).toString(),"IN_PROCESS"));
                targetRunningStateMap.put(u, RunningState.IN_PROCESS);
                targetsInProcessTimeMap.put(u, Instant.now());
            }

            //targetsWaitingTimeMap.remove(u);//todo we need to "init" the  target waiting time and process time after finishing task
            runTaskOnTargetWithTime(u, targetFile,theThreadManager);
            String taskResStr = "The task on target: " + u.getName() + " has been finished with " + targetRunningResultMap.get(u) + "\n\n";
            targetFile.write(taskResStr);
            if(taskType.equals(Mediator.TaskType.COMPILATION)){
                String compilerWorkingTimeStr;
                synchronized (this) {
                     compilerWorkingTimeStr = targetsRunningTimeMap.get(u);
                }
                String[] fullTime= compilerWorkingTimeStr.split(":");
                String compilingTimeInMs=fullTime[fullTime.length-1];
                String compilerWorkingTime = "The compiler working time in milliseconds was: " + compilingTimeInMs;
                targetFile.write(compilerWorkingTime);
            }
              synchronized (this) {
                  if (targetRunningResultMap.get(u).equals(RunningResult.FAILURE)) { //if U has failed
                      targetFailureCase(u, targetFile);
                  } else { //else- U has succeeded or succeeded with warning
                      targetNotFailedCase(u, uiAdapter, targetFile, taskFolder, theThreadManager, taskType);
                  }
              }
            targetFile.write("\n");
            for (TargetsSerialSet set : u.getTheSetsThatTheTargetBelongsTo()) {
                for (Target t : set.getTargetsSerialSet()) {
                    boolean threadCondition = false;
                    synchronized (this) {
                        threadCondition = (targetRunningStateMap.get(t).equals(RunningState.WAITING) && !targetRunningResultMap.get(t).equals(RunningResult.DONT_CHECK)
                                && !theThreadManager.isTargetLockedFromSet(t));
                        if (threadCondition) {
                            theThreadManager.lockAllSetsOfTheTarget(t);
                        }
                    }
                    if (threadCondition) {
                        TaskTargetRun runnableT = new TaskTargetRun(taskFolder, uiAdapter, t, this, theThreadManager,taskType);
                        try {
                            synchronized (this) {
                                targetsWaitingTimeMap.put(t, Instant.now());
                            }
                                theThreadManager.getThreadPool().execute(runnableT);
                        } catch (RejectedExecutionException exception) {
                            exception.printStackTrace();//todo delete it
                        }
                    }
                }
            }

        } catch (IOException ignore) {
        } finally {
            if (targetFile != null) {
                try {
                    targetFile.close();
                    try (Stream<String> lines = Files.lines(Paths.get(fileName))){
                        String content = lines.collect(Collectors.joining(System.lineSeparator()));
                        uiAdapter.updateTaskOnDisplay(content);
                    }
                } catch (IOException ignore) {
                }
            }
        }
        setFirstTime(false);
        theThreadManager.handleIfDoneAllTasks();
    }

    public int getAmountOfRunningTargets(){
        int counter=0;
       for(Target t: sortedTargetList){
           if(!targetRunningResultMap.get(t).equals(RunningResult.DONT_CHECK)){
               counter++;
           }
       }
       return counter;
    }

    public void runTaskOnTargetList(final String taskFolder, Integer maxThreadsAmount, Mediator.TaskType taskType,SimpleBooleanProperty pausePropertyIn) {
        if(amountOfRunningTargets==0){
            return;
        }
        Double currentProgress=((double)runningTargetsThatAlreadyFinishedOrSkippedMap.size())/amountOfRunningTargets;
        uiAdapter.updateProgressBarDuringTask(currentProgress);
        ThreadManager theThreadManager = new ThreadManager(targetRunningStateMap, sortedTargetList, maxThreadsAmount,pausePropertyIn);
        ExecutorService threadExecutor = theThreadManager.getThreadPool();
        for (Target u : sortedTargetList) {
            boolean threadCondition = false;
            synchronized (this) {
                threadCondition = (targetRunningStateMap.get(u).equals(RunningState.WAITING) && !targetRunningResultMap.get(u).equals(RunningResult.DONT_CHECK)
                        && !theThreadManager.isTargetLockedFromSet(u));
                if (threadCondition)
                    theThreadManager.lockAllSetsOfTheTarget(u);
            }
            if (threadCondition) {
                TaskTargetRun runnableU = new TaskTargetRun(taskFolder, uiAdapter, u, this, theThreadManager,taskType);
                try {
                    threadExecutor.execute(runnableU);
                } catch (RejectedExecutionException exception) {
                    exception.printStackTrace();//todo delete it
                }
            }
        }
        while (!theThreadManager.allDone()) {
            synchronized (theThreadManager.getLock()) {
                try {
                    theThreadManager.getLock().wait();
                       // threadExecutor.shutdownNow().forEach(t->Thread.currentThread().interrupt());
                    threadExecutor.awaitTermination(2, TimeUnit.SECONDS);
                    System.out.println("after w8ing");//todo check why it doent print
                } catch (InterruptedException ignore) {
                }
            }
        }
    }
    private void printStartingAndExtraInfoAboutTarget(Target u, UiAdapterInterface UiAdapter, FileWriter targetFile) throws IOException {
        String startWorkOnTarget = "Starting the task on target: " + u.getName() + "\n\n";
        targetFile.write(startWorkOnTarget);

        if (u.getExtraInfo() != null) {
            String extraDataStr = "The extra data on target " + u.getName() + " is: " + u.getExtraInfo() + "\n\n";
            targetFile.write(extraDataStr);
        }
    }

    public void runTaskOnTargetWithTime(Target u, FileWriter targetFile,ThreadManager theThreadManager) {
        Instant startTime = Instant.now();
        RunTaskOnTarget(u, targetFile,theThreadManager);
        Instant endTime = Instant.now();
        Duration res = Duration.between(startTime, endTime);
        String taskTime = getFullTimeFromDuration(res)+"\n";

        synchronized (this) {
            targetsRunningTimeMap.put(u, taskTime);
        }
    }

    final public String getFullTimeFromDuration(Duration res) {
        long hours = res.toHours();
        res = res.minusHours(hours);
        long minutes = res.toMinutes();
        res = res.minusMinutes(minutes);
        long seconds = (res.toMinutes() / 60);
        res = res.minusSeconds(seconds);
        long millis = res.toMillis();
        return hours + ":" + minutes + ":" + seconds + ":" + millis+" ";
    }

    private void targetFailureCase(Target u, FileWriter targetFile) throws IOException {
        if (!u.getInTargetList().isEmpty()) {
            String intro = "The targets that depend on " + u.getName() + " directly and will be skipped are: ";
            targetFile.write(intro);
            for (Target x : u.getInTargetList()) {
                String xName = x.getName() + " ";
                targetFile.write(xName);
            }
            targetFile.write("\n\n");

            Set<Target> accessibleTargets = g.getAccessibleTargets(true, u);
            String introAccess = "All the targets that depend on " + u.getName() + " indirectly and directly and will be skipped are: ";
            targetFile.write(introAccess);

            for (Target x : accessibleTargets) {
                synchronized (this) {
                    uiAdapter.updateTargetStatusDuringTask(
                            new UpdateTargetStatusDuringTaskDto(x.getName(),targetRunningStateMap.get(x).toString(),"SKIPPED"));
                    targetRunningStateMap.put(x, RunningState.SKIPPED);
                    targetRunningResultMap.put(x, RunningResult.SKIPPED);
                    if(!runningTargetsThatAlreadyFinishedOrSkippedMap.contains(x)){
                        runningTargetsThatAlreadyFinishedOrSkippedMap.add(x);
                        Double currentProgress=((double)runningTargetsThatAlreadyFinishedOrSkippedMap.size())/amountOfRunningTargets;
                        uiAdapter.updateProgressBarDuringTask(currentProgress);
                    }
                }
                String xName = x.getName() + " ";
                targetFile.write(xName);
            }
            targetFile.write("\n\n");
        } else {
            String notAvailableTargets = "There aren't targets that will not be available at all to work on because " + u.getName() +
                    " has failed \n\n";
            targetFile.write(notAvailableTargets);
        }
    }
    protected String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSSS");
        Date date = new Date();
        return dateFormat.format(date);
    }

    synchronized private void targetNotFailedCase(Target u, UiAdapterInterface UiAdapter, FileWriter targetFile, final String taskFolder
            , ThreadManager theThreadManager, Mediator.TaskType taskType) throws IOException {
        String notAvailableTargets = "There aren't new targets that will be available to work on \n\n";
        if (u.getInTargetList().isEmpty()) {
            targetFile.write(notAvailableTargets);
        } else {// u has at least one target that has been affected
            String affectedInTargets = "All the targets that depend on " + u.getName() + " directly and will be available to work on are: ";
            targetFile.write(affectedInTargets);
            Set<Target> newWaitingSet = new HashSet<>();
            for (Target v : u.getInTargetList()) {
                boolean toAdd = true, isTargetLockedFromASet = false;
                for (Target t : v.getOutTargetList()) {
                    if ((targetRunningResultMap.get(t).equals(RunningResult.FAILURE)) ||
                            (targetRunningResultMap.get(t).equals(RunningResult.SKIPPED)) ||
                            targetRunningResultMap.get(t).equals(RunningResult.FROZEN) ||
                            !targetRunningStateMap.get(t).equals(RunningState.FINISHED)) {
                        toAdd = false;
                        break;
                    }
                }
                isTargetLockedFromASet = theThreadManager.isTargetLockedFromSet(v);
                if (toAdd && targetRunningStateMap.get(v).equals(RunningState.FROZEN)) {
                    uiAdapter.updateTargetStatusDuringTask(
                            new UpdateTargetStatusDuringTaskDto(v.getName(),targetRunningStateMap.get(v).toString(),"WAITING"));
                    targetRunningStateMap.put(v, RunningState.WAITING);
                    targetsWaitingTimeMap.put(v, Instant.now());
                    newWaitingSet.add(v);
                    String vName = v.getName() + " ";
                    targetFile.write(vName);
                    if (!isTargetLockedFromASet) {
                        TaskTargetRun runnableV = new TaskTargetRun(taskFolder, UiAdapter, v, this, theThreadManager,taskType);
                            targetsWaitingTimeMap.put(v, Instant.now());
                            theThreadManager.getThreadPool().execute(runnableV);
                    }
                }
            }
            if (newWaitingSet.isEmpty()) {
                targetFile.write(notAvailableTargets);
            } else {
                targetFile.write("\n\n");
            }
        }
    }
}


