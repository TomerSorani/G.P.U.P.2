package task.impl;

import data.structure.Graph;
import data.structure.Target;
import input.task.TaskInputDto;
import input.task.impl.CompilationTaskInputDto;
import output.UpdateTargetStatusDuringTaskDto;
import task.misc.UiAdapterInterface;
import task.thread.ThreadManager;

import java.io.*;

public class CompilationTask extends AbstractTaskManager implements Serializable {
    private String src;
    private String dest;
    public CompilationTask(final Graph g, TaskInputDto taskInput,UiAdapterInterface UiAdapter){
       super(g,UiAdapter);
       updateMembers(taskInput);
   }
private String convertExtraInfoToPath(String ExtraInfo){
    return ExtraInfo.replaceAll("\\.","\\\\");
}
    @Override
    protected void RunTaskOnTarget(Target t, FileWriter targetFile, ThreadManager theThreadManager) {
        String convertedExtraInfo=convertExtraInfoToPath(t.getExtraInfo());
       try {
           String resourceName = src + "\\" + convertedExtraInfo+".java";
           String reportLog = "";
           String compileFileInfo="Compiling file: " + convertedExtraInfo+"\n";
           File f =new File(resourceName);
           if (!f.exists()) {
               synchronized (this) {
                   UpdateTargetStatusDuringTaskDto targetStatusDuringTaskDto =new UpdateTargetStatusDuringTaskDto(t.getName(), targetRunningStateMap.get(t).toString(), RunningResult.FAILURE.toString());
                   targetRunningResultMap.put(t, RunningResult.FAILURE);
                   targetRunningStateMap.put(t, RunningState.FINISHED);
                   theThreadManager.unlockAllSetsOfTheTarget(t);
                   uiAdapter.updateTargetStatusDuringTask(targetStatusDuringTaskDto);
                   if(!runningTargetsThatAlreadyFinishedOrSkippedMap.contains(t)){
                       runningTargetsThatAlreadyFinishedOrSkippedMap.add(t);
                       Double currentProgress=((double)runningTargetsThatAlreadyFinishedOrSkippedMap.size())/amountOfRunningTargets;
                       uiAdapter.updateProgressBarDuringTask(currentProgress);
                   }
               }
               targetFile.write("resource does not exists");
           }
           targetFile.write(compileFileInfo);
           String compilerIsGoingToRunInfo="Compiler is going to run now the next line:\n" +
                   "javac -d " + dest + " -cp " + dest + " " + resourceName + "\n";
           targetFile.write(compilerIsGoingToRunInfo+"\n");
           Process javac = null;
           try {
                   javac = new ProcessBuilder(
                           "javac", "-d", dest, "-cp", dest, resourceName)
                           .redirectErrorStream(false)
                           .start();
               BufferedReader r = new BufferedReader(new InputStreamReader(javac.getErrorStream()));
               String line;
               boolean error = false;
                   while (true) {
                       line=r.readLine();
                       if (line == null) {
                           break;
                       }
                       error =true;
                       reportLog += line+ "\n";
                   }
                   synchronized (this) {
                       if (error) {
                           targetFile.write(reportLog+"\n");
                               targetRunningResultMap.put(t, RunningResult.FAILURE);
                       }
                       else {
                               targetRunningResultMap.put(t, RunningResult.SUCCESS);
                       }
                   UpdateTargetStatusDuringTaskDto targetStatusDuringTaskDto =new UpdateTargetStatusDuringTaskDto(t.getName(), targetRunningStateMap.get(t).toString(), targetRunningResultMap.get(t).toString());
                   targetRunningStateMap.put(t,RunningState.FINISHED);
                   theThreadManager.unlockAllSetsOfTheTarget(t);
                   uiAdapter.updateTargetStatusDuringTask(targetStatusDuringTaskDto);
                       if(!runningTargetsThatAlreadyFinishedOrSkippedMap.contains(t)){
                           runningTargetsThatAlreadyFinishedOrSkippedMap.add(t);
                           Double currentProgress=((double)runningTargetsThatAlreadyFinishedOrSkippedMap.size())/amountOfRunningTargets;
                           uiAdapter.updateProgressBarDuringTask(currentProgress);
                       }
                   }
           }
           catch (SecurityException | IOException javacFailed) {}
       }
       catch (IOException ignore) {}
    }

    @Override
    public void updateMembers(TaskInputDto taskInput) {
        CompilationTaskInputDto compilationInput=(CompilationTaskInputDto) taskInput;
        dest=compilationInput.getCompilationDestFolderPath();
        src=compilationInput.getCompilationSourceFolderPath();
    }
}
