package task.misc;

import input.task.TaskInputDto;
import manager.Mediator;

import java.util.Set;

public class PrepareTaskContainer {
   private Mediator med;
   private Mediator.TaskType taskType;
   private TaskInputDto taskInput;
   private Set<String> targetsToRun;

    public PrepareTaskContainer(Mediator medIn,Mediator.TaskType taskTypeIn,TaskInputDto taskInputIn
    , Set<String> targetsToRunIn){
        med=medIn;
        taskType=taskTypeIn;
        taskInput=taskInputIn;
        targetsToRun=targetsToRunIn;
    }
    public Mediator getMed() {return med;}
    public Mediator.TaskType getTaskType() {return taskType;}
    public TaskInputDto getTaskInput() {return taskInput;}
    public Set<String> getTargetsToRun(){return targetsToRun;}
}
