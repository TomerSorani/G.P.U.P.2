package app;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import task.misc.PrepareTaskContainer;
import app.subcomponents.actions.on.the.graph.ActionsOnTheGraphController;
import app.subcomponents.graph.Info.GraphInfoTabController;
import app.subcomponents.run.task.RunTaskTabController;
import input.task.TaskInputDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import manager.Mediator;
import output.*;
import task.misc.TargetListViewDetails;
import xml.DependenyConflict;
import xml.SerialSetAlreadyExist;
import xml.TargetAlreadyExist;
import xml.TargetNotExist;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class SuperController {

    private Mediator med;
    private Stage primaryStage;

    @FXML private Tab welcomeTab;
    @FXML private Tab graphInfoTab;
    @FXML private Tab RunTaskTab;
    @FXML private Tab ActionsOnTheGraphTab;

    @FXML private ScrollPane WelcomeTabContent;
    @FXML private app.subcomponents.Welcome.WelcomeTabController WelcomeTabContentController;

    @FXML private ScrollPane GraphInfoTabContent;
    @FXML private GraphInfoTabController GraphInfoTabContentController;

    @FXML private ScrollPane RunTaskTabContent;
    @FXML private RunTaskTabController RunTaskTabContentController;

    @FXML private ScrollPane ActionsOnTheGraphTabContent;
    @FXML private ActionsOnTheGraphController ActionsOnTheGraphTabContentController;

    @FXML public void initialize() {
        if(WelcomeTabContentController!=null && GraphInfoTabContentController!=null && RunTaskTabContentController!=null
            && ActionsOnTheGraphTabContentController!=null){
            WelcomeTabContentController.setSuperController(this);
            GraphInfoTabContentController.setSuperController(this);
            RunTaskTabContentController.setSuperController(this);
            ActionsOnTheGraphTabContentController.setSuperController(this);

            graphInfoTab.disableProperty().bind(WelcomeTabContentController.isXmlLoadedProperty().not());
            RunTaskTab.disableProperty().bind(WelcomeTabContentController.isXmlLoadedProperty().not());
            ActionsOnTheGraphTab.disableProperty().bind(WelcomeTabContentController.isXmlLoadedProperty().not());
            //todo check on the two we did not use
        }
    }
    public Scene getTaskReportSceneFromGrandChild(){return RunTaskTabContentController.getTaskReportSceneFromChild();}
    @FXML public void openFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        String absolutePath = selectedFile.getAbsolutePath();
        SimpleStringProperty loadStatusLabelProperty= WelcomeTabContentController.loadStatusLabelProperty();
        SimpleStringProperty currentFileLoadedLabelProperty=WelcomeTabContentController.currentFileLoadedLabelProperty();
        try {
            loadXMlFile(absolutePath);
            WelcomeTabContentController.isXmlLoadedProperty().set(true);
            loadStatusLabelProperty.setValue("Successfully");
            currentFileLoadedLabelProperty.setValue(absolutePath);
            med.updateGraphForTaskOnlyToNullAfterLoadXml();
            GraphInfoTabContentController.initSubTabs();
            RunTaskTabContentController.initSubTabs();
            List<String> names=getAllTargetsNames();
            names.sort(Comparator.comparing(String::toString));
            ActionsOnTheGraphTabContentController.initActionsOnGraphWhenXMLIsLoaded(names);
        }
        catch (IOException | JAXBException | TargetAlreadyExist | TargetNotExist | SerialSetAlreadyExist | DependenyConflict e1) {
            loadStatusLabelProperty.setValue("Error in loading because: " +e1.getMessage());
        }
    }
    public AllGraphDto getAllGraphDtoFromMed(){return med.getAllGraphDto();}

    public void setMediator(Mediator m, Stage primaryStageIn) {
        med= m;
        primaryStage = primaryStageIn;
    }
    final public Integer getMaxThreadsAmountFromMed() {
        return med.getMaxThreadsAmount();
    }

    public AllDependenciesOfTargetDto getAllDependenciesOfTargetDtoFromMed(String targetName){
        return med.getAllDependenciesOfTargetDtoFromGraph(targetName);
    }
    public boolean enableOrDisableAnimations(){
       return WelcomeTabContentController.enableOrDisableAnimations();
    }
    public List<String> getAllTargetsNames(){
        List<String> res=new ArrayList<>();
        Set<TargetInfoForTableViewDto> set=getAllInformationForTableViewFromMed();
        for(TargetInfoForTableViewDto t:set){
            res.add(t.getName());
        }
        return res;
    }
    public List<String> getTargetsNamesInGraphTaskOnly(){return new ArrayList<>(med.getAllTargetsNamesInSetFromGraphTaskOnly());}

    private void loadXMlFile(String fileName) throws JAXBException, IOException {
        boolean succeed = false;
        Mediator temp = null;
        temp = new Mediator();//todo dont reallocate everytime use a boolean
        succeed = temp.loadFile(fileName);
        if (succeed) {//todo check who handles it
            med = temp;
        }
        //todo else-> we need to print the error?
    }

    final public CirclePathForTargetDto checkIfTargetIsInCircle(String targetName){return med.checkIfTargetIsInCircle(targetName);}

    public Set<TargetInfoForTableViewDto> getAllInformationForTableViewFromMed(){
        return med.getAllTargetsInfoForTableViewDtoFromGraph();
    }

    public Set<SerialSetInfoTableViewDto> getAllSerialSetsInfoForTableViewFromMed(){
        return med.getAllSerialSetsInfoForTableViewFromGraph();
    }
    public TargetInfoDto getTargetInfoDtoFromMed(String targetName) {
        return med.getTargetInfoDto(targetName);
    }

    public Set<String> getAllTargetsNamesInSetFromMed(){return med.getAllTargetsNamesInSetFromGraph();}

    public AllPathsOfTwoTargetsDto findAllPathsOfTwoTargetsFromEngine(String name1,String name2,boolean isDepend){
        if(isDepend){
           return med.findBindBetweenTwoTargets(name1,name2);
        }
        else{
            return  med.findBindBetweenTwoTargets(name2, name1);
        }
    }

    public PrepareTaskContainer prepareTaskForRunning(TaskInputDto taskInput, String taskTypeIn,Set<String> targetsToRunIn){
        Mediator.TaskType taskType=null;
       if(taskTypeIn.equals("Simulation")){
        taskType=Mediator.TaskType.SIMULATION;
       }
       else if(taskTypeIn.equals("Compilation")){
           taskType=Mediator.TaskType.COMPILATION;
       }
        return new PrepareTaskContainer(med,taskType,taskInput,targetsToRunIn);
    }

    public GraphInfoDto getGraphInfoDtoFromMed (){return med.getGraphInfoDto();}

    public Stage getPrimaryStage() {return primaryStage;}

    public void getTargetListViewDetailsFromMed(String targetName,Consumer<TargetListViewDetails> updateTargetListViewDetailsInUi){
     new Thread(new BringsTargetListViewDetails(targetName,updateTargetListViewDetailsInUi)).start();
}
    public class BringsTargetListViewDetails implements Runnable{
      private String targetName;
      private Consumer<TargetListViewDetails> updateTargetListViewDetailsInUi;
        public BringsTargetListViewDetails(String targetNameIn,
           Consumer<TargetListViewDetails> updateTargetListViewDetailsInUiIn) {
            targetName=targetNameIn;
            updateTargetListViewDetailsInUi=updateTargetListViewDetailsInUiIn;
        }
        @Override public void run() {
            TargetListViewDetails info = med.getTargetListViewDetailsFromAbstractTask(targetName);
            Platform.runLater(()->updateTargetListViewDetailsInUi.accept(info));
        }
    }
}
