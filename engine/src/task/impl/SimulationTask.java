package task.impl;

import data.structure.Graph;
import data.structure.Target;
import input.task.TaskInputDto;
import input.task.impl.SimulationTaskInputDto;
import output.UpdateTargetStatusDuringTaskDto;
import task.misc.UiAdapterInterface;
import task.thread.ThreadManager;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SimulationTask extends AbstractTaskManager implements Serializable {
    private Long processTime;
    private Double probabilityOfSuccess, probabilityOfWarning;

    public SimulationTask(final Graph g,TaskInputDto taskInput,UiAdapterInterface UiAdapter) {
        super(g,UiAdapter);
        updateMembers(taskInput);
    }

    @Override
    public void updateMembers(TaskInputDto taskInput) {
        SimulationTaskInputDto simulationInput=(SimulationTaskInputDto) taskInput;
        processTime = simulationInput.getProcessTime();
        Boolean isRandom = simulationInput.getIsRandom();

        if (isRandom && processTime!=0) {
            processTime = (long) Math.floor(Math.random() * (processTime + 1));
        }
        probabilityOfSuccess = simulationInput.getProbabilitySuccess();
        probabilityOfWarning = simulationInput.getProbabilityWarning();
    }

    @Override
    protected void RunTaskOnTarget(Target t, FileWriter targetFile, ThreadManager theThreadManager) {
        try {
            String sleepingTime = "'" + t.getName() + "' is going to sleep for: " + processTime + " milliseconds\n\n";
            targetFile.write(sleepingTime);

            String beforeSleepingTime = "Before sleeping time of "+t.getName()+": " + getTime() + "\n\n";
            targetFile.write(beforeSleepingTime);
            Thread.sleep(processTime);

            String afterSleepingTime = "After sleeping time of "+t.getName()+": " + getTime() + "\n\n";
            targetFile.write(afterSleepingTime);
        } catch (IOException | InterruptedException ignore) {}
        synchronized (this) {
            if (Math.random() <= probabilityOfSuccess) {
                if (Math.random() <= probabilityOfWarning){
                    targetRunningResultMap.put(t, RunningResult.WARNING);

                }
                else {
                    targetRunningResultMap.put(t, RunningResult.SUCCESS);
                }
            }
            else {
                targetRunningResultMap.put(t, RunningResult.FAILURE);
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


}
