package task;

import data.structure.Target;
import manager.Mediator;
import task.impl.AbstractTaskManager;
import task.misc.UiAdapterInterface;
import task.thread.ThreadManager;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TaskTargetRun implements Runnable {
    private Target t;
    UiAdapterInterface UiAdapter;
    private AbstractTaskManager manager;
    private final String theTaskFolder;
    private ThreadManager theThreadManager;
    private Mediator.TaskType taskType;

    public TaskTargetRun(final String taskFolder, UiAdapterInterface UiAdapterIn, Target u,
                         AbstractTaskManager a, ThreadManager theThreadManagerIn,Mediator.TaskType taskTypeIn) {
        t = u;
        UiAdapter = UiAdapterIn;
        manager = a;
        theTaskFolder = taskFolder;
        theThreadManager = theThreadManagerIn;
        taskType=taskTypeIn;
    }

    final public Target getTarget() {
        return t;
    }

    @Override
    public void run() {
        System.out.println("Thread "+Thread.currentThread().getName()+" running. id:"+Thread.currentThread().getId()+"\n");
        manager.executeTaskOnTarget(theTaskFolder, t,theThreadManager,taskType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskTargetRun that = (TaskTargetRun) o;
        return Objects.equals(t, that.t);
    }

    @Override
    public int hashCode() {return Objects.hash(t);}
}

