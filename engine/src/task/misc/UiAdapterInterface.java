package task.misc;

import output.ThreadInfoRow;
import output.UpdateTargetStatusDuringTaskDto;

public interface UiAdapterInterface {
    void updateTaskOnDisplay(String infoDuringTask);
    void updateProgressBarDuringTask(Double progress);
    void updateTaskReport(String strLine);
    void updateTargetStatusDuringTask(UpdateTargetStatusDuringTaskDto dto);
    void updateThreadInfoDuringTask(ThreadInfoRow threadDto);
}
