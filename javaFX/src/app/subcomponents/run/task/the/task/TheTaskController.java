package app.subcomponents.run.task.the.task;

import app.misc.UIAdapter;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import output.UpdateTargetStatusDuringTaskDto;
import task.misc.PrepareTaskContainer;
import app.subcomponents.run.task.RunTaskTabController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import task.misc.TargetListViewDetails;
import task.misc.UiAdapterInterface;
import task.thread.ThreadTask;
import java.util.*;
import java.util.function.Consumer;

public class TheTaskController {
    private RunTaskTabController runTaskTabCont;
    private Stage taskReportStage;
    private Scene taskReportScene;
    private TextArea taskReportText;
    private SimpleStringProperty taskReportLine;
    private VBox taskReportVbox;
    private SimpleBooleanProperty finishedRunTaskTheTaskController;
    private SimpleBooleanProperty pauseProperty;

    @FXML private Label progressTaskLabel;
    @FXML private ProgressBar taskProgressBar;
    @FXML private Button resumeTaskButton;
    @FXML private Button pauseTaskButton;
    @FXML private TextArea taskDisplayedInText;
    @FXML private TextArea infoAboutSelectedTargetDuringTaskTextArea;

    @FXML private ListView<String> frozenTargetsListView;
    @FXML private ListView<String> skippedTargetsListView;
    @FXML private ListView<String> waitingTargetsListView;
    @FXML private ListView<String> inProgressTargetsListView;
    @FXML private ListView<String> finishedWIthSuccessTargetsListView;
    @FXML private ListView<String> finishedWIthWarningTargetsListView;
    @FXML private ListView<String> finishedWIthFailureTargetsListView;
    @FXML private ListView<String> finishedInPreviousTasksWithSuccessOrWarningListView;

    public TheTaskController(){
        finishedRunTaskTheTaskController=new SimpleBooleanProperty(false);
        taskReportLine=new SimpleStringProperty("");
        taskReportStage=new Stage();
        taskReportStage.initModality(Modality.APPLICATION_MODAL);
        taskReportStage.setTitle("Task final report");
        taskReportStage.setResizable(false);
        taskReportText=new TextArea();
        taskReportText.setEditable(false);
        taskReportVbox = new VBox(30);
        taskReportVbox.setPadding(new Insets(25, 25, 25, 25));
        taskReportScene=new Scene(taskReportVbox,600,400);
        pauseProperty=new SimpleBooleanProperty(false);
    }

    public Scene getTaskReportScene() {
        return taskReportScene;
    }
    @FXML public void initialize() {
        pauseTaskButton.disableProperty().addListener((obserb)-> {
            if (pauseTaskButton.isDisable()) {
                resumeTaskButton.setDisable(false);
            } else {
                resumeTaskButton.setDisable(true);
            }
        });

        resumeTaskButton.disableProperty().addListener((obserb)-> {
            if (resumeTaskButton.isDisable()) {
                pauseTaskButton.setDisable(false);
            } else {
                pauseTaskButton.setDisable(true);
            }
        });

        taskReportLine.addListener((Observable obserev) ->{
        taskReportText.appendText(taskReportLine.getValue());});
        taskReportVbox.getChildren().addAll(taskReportText);
        taskReportStage.setScene(taskReportScene);
    }

    @FXML private void showTargetInfoDuringTask(MouseEvent event){
        infoAboutSelectedTargetDuringTaskTextArea.clear();
        String selectedTarget=null;
        boolean selectedAny=false;
       if(!frozenTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
             selectedTarget= frozenTargetsListView.getSelectionModel().getSelectedItem();
           frozenTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!skippedTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= skippedTargetsListView.getSelectionModel().getSelectedItem();
           skippedTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!waitingTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= waitingTargetsListView.getSelectionModel().getSelectedItem();
           waitingTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!inProgressTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= inProgressTargetsListView.getSelectionModel().getSelectedItem();
           inProgressTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!finishedWIthSuccessTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= finishedWIthSuccessTargetsListView.getSelectionModel().getSelectedItem();
           finishedWIthSuccessTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!finishedWIthWarningTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= finishedWIthWarningTargetsListView.getSelectionModel().getSelectedItem();
           finishedWIthWarningTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!finishedWIthFailureTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= finishedWIthFailureTargetsListView.getSelectionModel().getSelectedItem();
           finishedWIthFailureTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!finishedInPreviousTasksWithSuccessOrWarningListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= finishedInPreviousTasksWithSuccessOrWarningListView.getSelectionModel().getSelectedItem();
           finishedInPreviousTasksWithSuccessOrWarningListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }

       if(selectedAny) {
           Consumer<TargetListViewDetails> updateTargetListViewDetailsInUi = this::updateInfoAboutSelectedTargetDuringTaskTextArea;
           runTaskTabCont.getTargetListViewDetailsFromSuper(selectedTarget, updateTargetListViewDetailsInUi);
       }
    }
    public void clearTask(){
        frozenTargetsListView.getItems().clear();
        skippedTargetsListView.getItems().clear();
        waitingTargetsListView.getItems().clear();
        inProgressTargetsListView.getItems().clear();
        finishedWIthSuccessTargetsListView.getItems().clear();
        finishedWIthWarningTargetsListView.getItems().clear();
        finishedWIthFailureTargetsListView.getItems().clear();
        finishedInPreviousTasksWithSuccessOrWarningListView.getItems().clear();
        infoAboutSelectedTargetDuringTaskTextArea.clear();
        taskDisplayedInText.clear();
        taskReportText.clear();
        pauseTaskButton.setDisable(false);
    }
  public SimpleBooleanProperty getFinishedRunTaskTheTaskController(){return finishedRunTaskTheTaskController;}
    public void runTheTaskNow(PrepareTaskContainer container){
        finishedRunTaskTheTaskController.setValue(false);
        clearTask();
        UiAdapterInterface uiAdapt= createUIAdapter();
        Task<Boolean> theMission=new ThreadTask(container,uiAdapt, container.getTargetsToRun(),pauseProperty);
        Thread theThreadTask=new Thread(theMission);
        bindTaskToUIComponents(theMission, ()-> taskReportStage.show());
        theThreadTask.setDaemon(true);//?
        theThreadTask.start();
       // theThreadTask.interrupt();//?
    }
    public void setRunTaskTabController(RunTaskTabController runTaskTabCon,SimpleBooleanProperty finishedRunTaskFather){
        runTaskTabCont=runTaskTabCon;
        finishedRunTaskFather.bind(finishedRunTaskTheTaskController);

    }
    private void bindTaskToUIComponents(Task <Boolean> aTask, Runnable onFinish) {
        //task message
        aTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            onTaskFinished(Optional.ofNullable(onFinish));
            finishedRunTaskTheTaskController.setValue(true);
        });
    }
    private void onTaskFinished(Optional<Runnable> onFinish) {
        onFinish.ifPresent(Runnable::run);
    }
    @FXML private void onActionPauseButton(){
        pauseTaskButton.setDisable(true);
        pauseProperty.setValue(true);
    }
    @FXML private void onActionResumeButton(){
        resumeTaskButton.setDisable(true);
        pauseProperty.setValue(false);
    }
    private UIAdapter createUIAdapter() {
        return new UIAdapter(
                str -> taskDisplayedInText.appendText(str),
                s -> taskReportLine.setValue(s),
                this::updateListView,
               progress -> {
                   taskProgressBar.setProgress(progress);
                   String ProgressStr=String.format("%.2f",(progress*100));
                   progressTaskLabel.setText("Progress: "+ProgressStr+"%");
                },
                threadDto->//todo finish this  bonus.
                       runTaskTabCont.addRowToThreadsTableView(threadDto)
                );
    }

    public void updateListView(UpdateTargetStatusDuringTaskDto dto) {
        if (dto.getPrevStatusOnTask() != null) {
            removeTargetStatusDuringTask(dto);
        }
        addTargetStatusDuringTask(dto);
    }
    private void removeTargetStatusDuringTask(UpdateTargetStatusDuringTaskDto dto) {
         switch (dto.getPrevStatusOnTask()) {
             case "FROZEN": {
                 frozenTargetsListView.getItems().remove(dto.getTargetName());
                 break;
             }
             case "WAITING": {
                 waitingTargetsListView.getItems().remove(dto.getTargetName());
                 break;
             }
             case "IN_PROCESS": {
                 inProgressTargetsListView.getItems().remove(dto.getTargetName());
                 break;
             }
         }
     }
    private void addTargetStatusDuringTask(UpdateTargetStatusDuringTaskDto dto){
             switch (dto.getNewStatusOnTask()) {
                 case "FROZEN": {
                     frozenTargetsListView.getItems().add(dto.getTargetName());
                     break;
                 }
                 case "SKIPPED": {
                     String SkippedTargetName= dto.getTargetName();
                     if(!skippedTargetsListView.getItems().contains(SkippedTargetName)) {
                         skippedTargetsListView.getItems().add(SkippedTargetName);
                     }
                     break;
                 }
                 case "WAITING": {
                     waitingTargetsListView.getItems().add(dto.getTargetName());
                     break;
                 }
                 case "IN_PROCESS": {//FROZEN, SKIPPED, WAITING,,FAILURE,IN_PROCESS, SUCCESS, WARNING,DONT_CHECK
                     inProgressTargetsListView.getItems().add(dto.getTargetName());
                     break;
                 }
                 case "SUCCESS": {
                     finishedWIthSuccessTargetsListView.getItems().add(dto.getTargetName());
                     break;
                 }
                 case "WARNING": {
                     finishedWIthWarningTargetsListView.getItems().add(dto.getTargetName());
                     break;
                 }
                 case "FAILURE": {
                     finishedWIthFailureTargetsListView.getItems().add(dto.getTargetName());
                     break;
                 }
                 case "DONT_CHECK": {
                     finishedInPreviousTasksWithSuccessOrWarningListView.getItems().add(dto.getTargetName());
                     break;
                 }
             }

        }
    private void updateInfoAboutSelectedTargetDuringTaskTextArea(TargetListViewDetails targetListViewDetails){
        String targetName=targetListViewDetails.getName();
        String level=targetListViewDetails.getLevel();
        Set<String> serialSets=targetListViewDetails.getSerialSetsName();
        Set<String> directDependsSet= targetListViewDetails.getDirectDependsList();
        String msWaitingTimeInQueueAlready=targetListViewDetails.getMsWaitingTimeInQueueAlready();
        String msInProcessTimeTookAlready=targetListViewDetails.getMsInProcessTimeTookAlready();
        Set<String> directAndIndirectDependsListThatFailed=targetListViewDetails.getDirectAndIndirectDependsListThatFailed();
        String finishRunningResultType=targetListViewDetails.getFinishRunningResultType();
        String targetRunningState=targetListViewDetails.getTargetRunningState();

        String targetNameLine="Target Name: "+targetName+"\n";
        String levelLine="Level: "+level+"\n";
        String serialSetString="Serial sets that "+targetName+" belongs to: ";
        if(serialSets!=null){
            List<String> serialSerialSetsList= new ArrayList<>(serialSets);
            for(String s:serialSerialSetsList){
                if(serialSerialSetsList.get(0).equals(s)){
                    serialSetString+=s;
                }
                else{
                    serialSetString= serialSetString +", "+s;
                }
            }
            serialSetString=serialSetString+"\n";
        }
        else{
            serialSetString=serialSetString+"none\n";
        }
        String res="";
        switch (targetRunningState){
            case "FROZEN": {
                res+=frozenCase(directDependsSet,targetName);
                break;
            }
            case "SKIPPED": {
                res+=skippedCase(directAndIndirectDependsListThatFailed,targetName);
                break;
            }
            case "WAITING": {
                res+="The target has been already waiting "+msWaitingTimeInQueueAlready+"milliseconds\n";
                break;
            }
            case "IN_PROCESS": {
                res+="The process time of the task on the target has already been for "+msInProcessTimeTookAlready+"milliseconds\n";
                break;
            }
            case "FINISHED": {
                if(finishRunningResultType.equals("DONT_CHECK")){
                    res+="The target did not participate in this task- it finished in a previous task\n";
                    break;
                }
                res+="The task has been already finished on the target with "+finishRunningResultType.toLowerCase()+"\n";
                break;
            }
        }
        String finalInfo=targetNameLine+levelLine+serialSetString+res;
        infoAboutSelectedTargetDuringTaskTextArea.appendText(finalInfo);
    }
    private String frozenCase(Set<String> directDependsSet,String targetName){
        String res="The targets that "+targetName+" is waiting for their successful finish are: ";
        List<String> directDependsList= new ArrayList<>(directDependsSet);
        for(String t:directDependsList){
            if(directDependsList.get(0).equals(t)){
                res+=t;
            }
            else{
                res= res +", "+t;
            }
        }
        res=res+"\n";
        return res;
    }
    private String skippedCase( Set<String> directAndIndirectDependsListThatFailed,String targetName){
        String res="The targets that their process have been failed and because of them "+targetName+" is skipped are: ";
        List<String> directAndIndirectDependsListThatFailedList= new ArrayList<>(directAndIndirectDependsListThatFailed);
        for(String t:directAndIndirectDependsListThatFailedList){
            if(directAndIndirectDependsListThatFailedList.get(0).equals(t)){
                res+=t;
            }
            else{
                res= res +", "+t;
            }
        }
        res=res+"\n";
        return res;
    }
}
