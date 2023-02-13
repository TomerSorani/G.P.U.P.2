package task.thread;

import input.task.TaskInputDto;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import manager.Mediator;
import task.misc.PrepareTaskContainer;
import task.misc.UiAdapterInterface;
import java.time.Instant;
import java.util.Set;

public class ThreadTask extends Task<Boolean> {
    private Mediator.TaskType taskType;
    private TaskInputDto taskInput;
    private UiAdapterInterface UiAdapter;
    private Mediator med;
    private Set<String> targetsToRun;
    private SimpleBooleanProperty pauseProperty;

    public ThreadTask(PrepareTaskContainer taskContainerIn, UiAdapterInterface uiAdapt, Set<String> targetsToRunIn,
                       SimpleBooleanProperty pausePropertyIn)
    {
        taskType = taskContainerIn.getTaskType();
        taskInput = taskContainerIn.getTaskInput();
        UiAdapter = uiAdapt;
        med = taskContainerIn.getMed();
        targetsToRun=targetsToRunIn;
        pauseProperty=pausePropertyIn;
    }

    @Override
    public Boolean call() {
        Instant startTime = Instant.now();
        med.runTaskFilter(taskType, taskInput, UiAdapter,targetsToRun,pauseProperty);
       med.doTaskReport(UiAdapter, startTime,taskType);
        return Boolean.TRUE;
    }
}

