package app.misc;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import output.ThreadInfoRow;
import output.UpdateTargetStatusDuringTaskDto;
import task.misc.UiAdapterInterface;
import java.util.function.Consumer;

public class UIAdapter implements UiAdapterInterface{
  private Consumer<String> updateTaskDisplay;
  private Consumer<String> updateReportDisplay;
  private Consumer<UpdateTargetStatusDuringTaskDto> updateTargetStatusInListView;
  private Consumer<Double> updateProgressBar;
  private Consumer<ThreadInfoRow> updateThreadInfoDuringTask;

//todo later pause and progress bar

    public UIAdapter(Consumer<String> updateTaskDisplayIn,Consumer<String> updateReportDisplayIn
                     ,Consumer<UpdateTargetStatusDuringTaskDto> updateTargetStatusInListViewIn,
                     Consumer<Double> updateProgressBarIn,Consumer<ThreadInfoRow> updateThreadInfoDuringTaskIn)
    {
        updateTaskDisplay=updateTaskDisplayIn;
        updateReportDisplay=updateReportDisplayIn;
        updateTargetStatusInListView=updateTargetStatusInListViewIn;
        updateProgressBar=updateProgressBarIn;
        updateThreadInfoDuringTask=updateThreadInfoDuringTaskIn;
    }
    public void updateTaskOnDisplay(String infoDuringTask) {Platform.runLater(() -> updateTaskDisplay.accept(infoDuringTask));}
    public void updateTaskReport(String strLine){Platform.runLater(()->updateReportDisplay.accept(strLine));}
    public void updateTargetStatusDuringTask(UpdateTargetStatusDuringTaskDto dto){Platform.runLater(()->updateTargetStatusInListView.accept(dto));}
    public void updateProgressBarDuringTask(Double progress){Platform.runLater(()->updateProgressBar.accept(progress));}
   public void updateThreadInfoDuringTask(ThreadInfoRow threadDto){Platform.runLater(()->updateThreadInfoDuringTask.accept(threadDto));}
}
