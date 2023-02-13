package task;
import javafx.beans.property.SimpleBooleanProperty;
import manager.Mediator;
import task.misc.UiAdapterInterface;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

public interface Task extends Serializable {
    void runTaskOnTargetList(final String taskFolder, Integer maxThreadsAmount, Mediator.TaskType taskType,SimpleBooleanProperty pausePropertyIn);
}
