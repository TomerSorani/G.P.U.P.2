package app.subcomponents.run.task;

import app.SuperController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import output.AllDependenciesOfTargetDto;
import output.ThreadInfoRow;
import task.misc.PrepareTaskContainer;
import app.subcomponents.run.task.task.parallel.resources.info.TaskParallelResourcesInfoController;
import app.subcomponents.run.task.task.properties.TaskPropertiesController;
import app.subcomponents.run.task.task.properties.tableViewRow.Row;
import app.subcomponents.run.task.the.task.TheTaskController;
import input.task.TaskInputDto;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import task.misc.TargetListViewDetails;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class RunTaskTabController {

    private SuperController sController;
    private SimpleBooleanProperty finishedRunTaskFather;

    @FXML private TheTaskController TheTaskTabContentController;
    @FXML private ScrollPane TheTaskTabContent;
    @FXML private TaskParallelResourcesInfoController TaskParallelResourcesInfoContentController;
    @FXML private ScrollPane TaskParallelResourcesInfoContent;
    @FXML private TaskPropertiesController TaskPropertiesTabContentController;
    @FXML private ScrollPane TaskPropertiesTabContent;
    @FXML private TabPane thePhysicalRunTaskTab;
    @FXML private Tab TaskPropertiesTab;
    @FXML private Tab TheTaskTab;
    @FXML private Tab TaskParallelResourcesInfoTab;

    public RunTaskTabController(){
        finishedRunTaskFather=new SimpleBooleanProperty(false);
    }
    @FXML public void initialize() {//todo later "&&"
        if (TheTaskTabContentController != null) {
            TheTaskTabContentController.setRunTaskTabController(this,finishedRunTaskFather);
        }
        if (TaskParallelResourcesInfoContentController != null) {
            TaskParallelResourcesInfoContentController.setRunTaskTabController(this);
        }
        if (TaskPropertiesTabContentController != null) {
            TaskPropertiesTabContentController.setRunTaskTabController(this,finishedRunTaskFather);
        }
        TheTaskTab.setDisable(true);
        TaskParallelResourcesInfoTab.setDisable(true);
    }
    public void addRowToThreadsTableView(ThreadInfoRow threadInfoRow){TaskParallelResourcesInfoContentController.addThreadToTableView(threadInfoRow);}
    public void initSubTabs() {
       TheTaskTabContentController.getFinishedRunTaskTheTaskController().setValue(false);
        thePhysicalRunTaskTab.getSelectionModel().select(TaskPropertiesTab);
        TheTaskTab.setDisable(true);
        TaskParallelResourcesInfoTab.setDisable(true);
        TheTaskTabContentController.clearTask();
        //TheTaskTabContentController.updatePopUpTheme();
        TaskPropertiesTabContentController.initAllAfterTheXmlLoaded(sController.getMaxThreadsAmountFromMed());

    }
    public List<String> getTargetsNamesInGraphTaskOnlyFromSuper() {return sController.getTargetsNamesInGraphTaskOnly();}
    public void setSuperController(SuperController superCont){
        sController=superCont;
    }

    public Set<Row> getAllTargetsNamesInSetFromRunTask(){
       Set<String>allTargetsNames= sController.getAllTargetsNamesInSetFromMed();
        Set<Row> allRows=new HashSet<>();
        for(String s:allTargetsNames){
            allRows.add(new Row(s));
        }
        return allRows;
    }

    public Stage getPrimaryStageFromSuper() {return sController.getPrimaryStage();}

    public void runTaskFromSuper(TaskInputDto taskInput, String taskType,Set<String> targetsToRunIn) {
        PrepareTaskContainer container= sController.prepareTaskForRunning(taskInput,taskType,targetsToRunIn);
        TheTaskTab.setDisable(false);
        TaskParallelResourcesInfoTab.setDisable(false);
        thePhysicalRunTaskTab.getSelectionModel().select(TheTaskTab);
        TheTaskTabContentController.runTheTaskNow(container);
    }

    public AllDependenciesOfTargetDto getAllDependenciesOfTargetDtoFromSuper(String name) {
        return sController.getAllDependenciesOfTargetDtoFromMed(name);}
    public void getTargetListViewDetailsFromSuper(String targetName, Consumer<TargetListViewDetails> updateTargetListViewDetailsInUi){
         sController.getTargetListViewDetailsFromMed(targetName,updateTargetListViewDetailsInUi);}
    public Scene getTaskReportSceneFromChild(){return TheTaskTabContentController.getTaskReportScene();}
}
