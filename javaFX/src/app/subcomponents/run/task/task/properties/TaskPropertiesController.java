package app.subcomponents.run.task.task.properties;

import app.subcomponents.run.task.RunTaskTabController;
import app.subcomponents.run.task.task.properties.tableViewRow.Row;
import input.task.TaskInputDto;
import input.task.impl.CompilationTaskInputDto;
import input.task.impl.SimulationTaskInputDto;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import output.AllDependenciesOfTargetDto;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.UnaryOperator;

public class TaskPropertiesController {
    private RunTaskTabController runTaskTabCont;
    private Tooltip sourceJavaFilesFolderTooltip;
    private Tooltip destinationCompiledFilesFolderTooltip;

    private Set<Row> allRowsForTableView;
    private List<String> targetsNamesFromLastRun;
    private Integer maxThreadsAmount;
    private SimpleStringProperty pathToSourceJavaFilesFolder;
    private SimpleStringProperty pathToDestinationCompiledFilesFolder;
    private SimpleBooleanProperty readyToSetDependenciesOnTableView;
    private SimpleBooleanProperty isThereAtLeastOneCheckBoxSelectedInTableView;
    private SimpleBooleanProperty haveAllSimulationParamsReady;
    private SimpleBooleanProperty haveAllCompilationParamsReady;
    private SimpleBooleanProperty commonParamsForAllTaskReady;
    private SimpleBooleanProperty finishedRunTaskPropertiesController;
    private SimpleBooleanProperty canUseIncremental;

    @FXML private TableView<Row> dynamicTargetsTableView;
    @FXML private TableColumn<Row,String> targetsColumn;
    @FXML private TableColumn<Row,String> SelectTargetColumn;
    @FXML private CheckBox runTaskOnSpecificTargetWithDirectionCheckBox;
    @FXML private VBox directionsVbox;
    @FXML private RadioButton dependsOnRadioButton;
    @FXML private RadioButton requiredForRadioButton;
    @FXML private RadioButton simulationRadioButton;
    @FXML private RadioButton compilationRadioButton;
    @FXML private RadioButton runningScratchRadioButton;
    @FXML private RadioButton runningIncrementRadioButton;
    @FXML private Slider threadsAmountSlider;
    @FXML private Button runTaskButton;
    @FXML private Label successProbabilityViewLabel;
    @FXML private Slider simulationProbabilityOfSuccessSlider;
    @FXML private Label warningProbabilityViewLabel;
    @FXML private Slider simulationProbabilityOfWarningSlider;
    @FXML private TextField processTimeTextField;
    @FXML private CheckBox isRandomTimeCheckBox;
    @FXML private Label chooseOneTargetLabel;
    @FXML private VBox simulationParametersVbox;
    @FXML private VBox compilationParametersVbox;
    @FXML private Button sourceJavaFilesFolderButton;
    @FXML private Button destinationCompiledFilesFolderButton;
    @FXML private ToolBar selectAllAndRemoveAllToolbar;
    @FXML private VBox chooseDirectionAndTaskTypeDirectionVbox;

   public TaskPropertiesController(){
       targetsNamesFromLastRun=null;
       maxThreadsAmount=1;//this is the default
       pathToSourceJavaFilesFolder=new SimpleStringProperty("Not selected yet!");
       pathToDestinationCompiledFilesFolder=new SimpleStringProperty("Not selected yet!");
       readyToSetDependenciesOnTableView =new SimpleBooleanProperty(false);
       isThereAtLeastOneCheckBoxSelectedInTableView=new SimpleBooleanProperty(false);
       sourceJavaFilesFolderTooltip=new Tooltip();
       destinationCompiledFilesFolderTooltip=new Tooltip();
       haveAllSimulationParamsReady=new SimpleBooleanProperty(false);
       haveAllCompilationParamsReady=new SimpleBooleanProperty(false);
       commonParamsForAllTaskReady=new SimpleBooleanProperty(false);
       finishedRunTaskPropertiesController=new SimpleBooleanProperty(false);
       canUseIncremental=new SimpleBooleanProperty(false);
   }

    @FXML public void initialize() {
        runningScratchRadioButton.selectedProperty().addListener((observ) -> {
                    if (finishedRunTaskPropertiesController.getValue()) {
                        targetsNamesFromLastRun = runTaskTabCont.getTargetsNamesInGraphTaskOnlyFromSuper();
                        selectAllAndRemoveAllToolbar.setDisable(false);
                        showAllTargetsInTableView();
                        chooseDirectionAndTaskTypeDirectionVbox.setDisable(false);
                    }});

        runningIncrementRadioButton.selectedProperty().addListener((observ) -> {
                      selectAllAndRemoveAllToolbar.setDisable(true);
                      showOnlyTargetsFromLastRun();
                      chooseDirectionAndTaskTypeDirectionVbox.setDisable(true);
            });
        canUseIncremental.addListener((observ)-> {
            if (!canUseIncremental.getValue()) {
                runningIncrementRadioButton.setDisable(true);

            } else {
                runningIncrementRadioButton.setDisable(false);
            }
        } );
        finishedRunTaskPropertiesController.addListener(observable ->
                {
                    if (finishedRunTaskPropertiesController.getValue()) {
                        canUseIncremental.setValue(true);
                        targetsNamesFromLastRun = runTaskTabCont.getTargetsNamesInGraphTaskOnlyFromSuper();//added
                        showOnlyTargetsFromLastRun();
                        selectAllAndRemoveAllToolbar.setDisable(true);
                        chooseDirectionAndTaskTypeDirectionVbox.setDisable(true);
                        runningIncrementRadioButton.setSelected(false);
                        runningScratchRadioButton.setSelected(false);
                    }
                    else{
                        initSliders();
                    }
                }
        );
       commonParamsForAllTaskReady.bind(isThereAtLeastOneCheckBoxSelectedInTableView
               .and(runningIncrementRadioButton.selectedProperty().or(runningScratchRadioButton.selectedProperty())));

         haveAllSimulationParamsReady.bind(simulationRadioButton.selectedProperty()
                 .and(processTimeTextField.textProperty().isEmpty().not()));

        haveAllCompilationParamsReady.bind(compilationRadioButton.selectedProperty()
                .and(pathToDestinationCompiledFilesFolder.isEqualTo("Not selected yet!").not()
                        .and(pathToSourceJavaFilesFolder.isEqualTo("Not selected yet!").not())));

        readyToSetDependenciesOnTableView.bind(runTaskOnSpecificTargetWithDirectionCheckBox.selectedProperty()
                .and(dependsOnRadioButton.selectedProperty()
                        .or(requiredForRadioButton.selectedProperty()))
                .and(isThereAtLeastOneCheckBoxSelectedInTableView));

           runTaskButton.disableProperty().bind((commonParamsForAllTaskReady.and(haveAllCompilationParamsReady.or(haveAllSimulationParamsReady))).not());
             readyToSetDependenciesOnTableView.addListener((observer, oldValue, newValue) -> {
                 if(newValue) {updateTargetDependenciesOnTableView();}});

        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        processTimeTextField.setTextFormatter(new TextFormatter<String>(integerFilter));

        simulationParametersVbox.disableProperty().bind(simulationRadioButton.selectedProperty().not());
        compilationParametersVbox.disableProperty().bind(compilationRadioButton.selectedProperty().not());

        directionsVbox.disableProperty().bind(runTaskOnSpecificTargetWithDirectionCheckBox.selectedProperty().not());
        chooseOneTargetLabel.visibleProperty().bind(runTaskOnSpecificTargetWithDirectionCheckBox.selectedProperty());

        successProbabilityViewLabel.textProperty().bind(simulationProbabilityOfSuccessSlider.accessibleTextProperty());
        StringExpression successProbabilityViewLabelExp = Bindings.concat("Probability: ",simulationProbabilityOfSuccessSlider.valueProperty());
        successProbabilityViewLabel.textProperty().bind(successProbabilityViewLabelExp);

        warningProbabilityViewLabel.textProperty().bind(simulationProbabilityOfWarningSlider.accessibleTextProperty());
        StringExpression warningProbabilityViewLabelExp = Bindings.concat("Probability: ",simulationProbabilityOfWarningSlider.valueProperty());
        warningProbabilityViewLabel.textProperty().bind(warningProbabilityViewLabelExp);

        dynamicTargetsTableView.disableProperty().bind(runTaskOnSpecificTargetWithDirectionCheckBox.selectedProperty()
                       .and(requiredForRadioButton.selectedProperty().not()
                .and(dependsOnRadioButton.selectedProperty().not())));
        ///************************************************************

        sourceJavaFilesFolderTooltip.setText(pathToSourceJavaFilesFolder.getValue());
        try {
            Field fieldBehavior = sourceJavaFilesFolderTooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(sourceJavaFilesFolderTooltip);
            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);
            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(500)));
        } catch (Exception e) {}
        sourceJavaFilesFolderButton.setTooltip(sourceJavaFilesFolderTooltip);

        destinationCompiledFilesFolderTooltip.setText(pathToDestinationCompiledFilesFolder.getValue());
        try {
            Field fieldBehavior = destinationCompiledFilesFolderTooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(destinationCompiledFilesFolderTooltip);
            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);
            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(500)));
        } catch (Exception e) {}
        destinationCompiledFilesFolderButton.setTooltip(destinationCompiledFilesFolderTooltip);
///************************************************************
    }
    private void clearAllPropertiesSelections() {
       OnActionRemoveAllTargetsButton();
       dependsOnRadioButton.setSelected(false);
       requiredForRadioButton.setSelected(false);
       simulationRadioButton.setSelected(false);
       compilationRadioButton.setSelected(false);
       runTaskOnSpecificTargetWithDirectionCheckBox.setSelected(false);
       isRandomTimeCheckBox.setSelected(false);
       threadsAmountSlider.setValue(1);
       simulationProbabilityOfWarningSlider.setValue(0);
       simulationProbabilityOfSuccessSlider.setValue(0);
       processTimeTextField.clear();
       runningIncrementRadioButton.setSelected(false);
       runningScratchRadioButton.setSelected(false);
        sourceJavaFilesFolderTooltip.setText(pathToSourceJavaFilesFolder.getValue());
        pathToSourceJavaFilesFolder.setValue("Not selected yet!");
        pathToDestinationCompiledFilesFolder.setValue("Not selected yet!");
        destinationCompiledFilesFolderTooltip.setText(pathToDestinationCompiledFilesFolder.getValue());
        sourceJavaFilesFolderTooltip.setText(pathToSourceJavaFilesFolder.getValue());
        selectAllAndRemoveAllToolbar.setDisable(false);
        chooseDirectionAndTaskTypeDirectionVbox.setDisable(false);
        OnActionRemoveAllTargetsButton();
        showAllTargetsInTableView();
        isThereAtLeastOneCheckBoxSelectedInTableView.setValue(false);
    }
    public void setRunTaskTabController(RunTaskTabController runTaskTabCon,SimpleBooleanProperty finishedRunTaskFather){
        runTaskTabCont=runTaskTabCon;
        finishedRunTaskPropertiesController.bind(finishedRunTaskFather);
        targetsNamesFromLastRun=null;
    }
    private void updateTargetDependenciesOnTableView(){
       boolean isDepend =dependsOnRadioButton.isSelected();
       String targetName=null;
        for(Row r:allRowsForTableView) {
            if (r.getSelectTarget().isSelected()) {
                targetName=r.getTargetName();
                break;
            }
        }
        AllDependenciesOfTargetDto data = runTaskTabCont.getAllDependenciesOfTargetDtoFromSuper(targetName);
        if(isDepend){
            printTheDependenciesToTableView( data.getTotalDependsOn());
        }
        else {
            printTheDependenciesToTableView(data.getTotalRequiredFor());
        }
    }

    private void printTheDependenciesToTableView(Map<String,String> dependencies){
        for(Row r:allRowsForTableView) {
            if(dependencies.containsKey(r.getTargetName())){
                r.getSelectTarget().setSelected(true);
            }
        }
    }

    @FXML private void OnActionSelectAllTargetsButton(){ChooseTargetsButtonState(true);}
    @FXML private void OnActionRemoveAllTargetsButton(){ChooseTargetsButtonState(false);}

    @FXML private void OnActionRunTaskOnSpecificTargetWithDirectionCheckBox(){
       if(runTaskOnSpecificTargetWithDirectionCheckBox.isSelected()){
           OnActionRemoveAllTargetsButton();
       }
       else{
           dependsOnRadioButton.setSelected(false);
           requiredForRadioButton.setSelected(false);
       }
    }

    @FXML private void OnActionRunTaskButton(){//todo make start available only when all is set
        TaskInputDto taskInput;
        Integer amountOfThreadsIn=(int)(threadsAmountSlider.getValue());
        Boolean isScratchIn=runningScratchRadioButton.isSelected();//todo check incremental case as well
        Set<String> targetsToRun=getSelectedTargets();
       if(simulationRadioButton.isSelected()) {
            Double successRateIn = simulationProbabilityOfSuccessSlider.getValue();
            Double warningRateIn = simulationProbabilityOfWarningSlider.getValue();
            Boolean isRandomIn = isRandomTimeCheckBox.isSelected();
            Long processTimeIn=Long.valueOf(processTimeTextField.getText());
            taskInput=new SimulationTaskInputDto(processTimeIn,isRandomIn,successRateIn,warningRateIn,isScratchIn,amountOfThreadsIn);
            runTaskTabCont.runTaskFromSuper(taskInput,"Simulation",targetsToRun);
        }
        else if(compilationRadioButton.isSelected()){
           taskInput=new CompilationTaskInputDto(isScratchIn,amountOfThreadsIn,pathToSourceJavaFilesFolder.getValue(),pathToDestinationCompiledFilesFolder.getValue());
           runTaskTabCont.runTaskFromSuper(taskInput,"Compilation",targetsToRun);
        }
    }

    @FXML private void onActionSourceJavaFilesFolderButton(){
        DirectoryChooser directoryChooser  = new DirectoryChooser();
        directoryChooser.setTitle("Select the java source files directory");
        File srcDirectory = directoryChooser.showDialog(runTaskTabCont.getPrimaryStageFromSuper());
        if (srcDirectory == null) {
            return;
        }
        String absolutePath = srcDirectory.getAbsolutePath();
        pathToSourceJavaFilesFolder.setValue(absolutePath);
        sourceJavaFilesFolderTooltip.setText("Selected folder:\n"+absolutePath+"\n");
        System.out.println(absolutePath); //todo delete it and check if it's valid
    }

    @FXML private void onActionDestinationCompiledFilesFolderButton(){
        DirectoryChooser directoryChooser  = new DirectoryChooser();
        directoryChooser.setTitle("Select the compiled files destination folder");
        File destDirectory = directoryChooser.showDialog(runTaskTabCont.getPrimaryStageFromSuper());
        if (destDirectory == null) {
            return;
        }
        String absolutePath = destDirectory.getAbsolutePath();
        pathToDestinationCompiledFilesFolder.setValue(absolutePath);
        destinationCompiledFilesFolderTooltip.setText("Selected folder:\n"+absolutePath+"\n");
    }

    private Set<String> getSelectedTargets(){
        Set<String> selectedTargets=new HashSet<>();
        for(Row r:dynamicTargetsTableView.getItems()) {
            if(r.getSelectTarget().isSelected()){
                selectedTargets.add(r.getTargetName());
            }
        }
        return selectedTargets;
    }

    private void updateIfAnyCheckBoxInTableViewIsTicked(){
        for(Row r:dynamicTargetsTableView.getItems()) {
            if(r.getSelectTarget().isSelected()){
                isThereAtLeastOneCheckBoxSelectedInTableView.setValue(true);
                return;
            }
        }
        isThereAtLeastOneCheckBoxSelectedInTableView.setValue(false);
    }

    private Row getTargetRow(String targetName){
        for(Row r:allRowsForTableView) {
            if(r.getTargetName().equals(targetName)){
                return r;
            }
        }
        return null;
    }

    private void showAllTargetsInTableView() {
        if (targetsNamesFromLastRun != null) {
            for(Row r:dynamicTargetsTableView.getItems()) {
                String targetFromTableViewName=r.getTargetName();
                    Row disableThisTargetRow=getTargetRow(targetFromTableViewName);
                    disableThisTargetRow.getSelectTarget().disableProperty().setValue(false);
            }
        }
    }

    @FXML private void showOnlyTargetsFromLastRun() {
        if (targetsNamesFromLastRun != null) {
            for (Row r : dynamicTargetsTableView.getItems()) {
                String targetFromTableViewName = r.getTargetName();
                Row disableThisTargetRow = getTargetRow(targetFromTableViewName);
                disableThisTargetRow.getSelectTarget().disableProperty().setValue(true);
                if (!targetsNamesFromLastRun.contains(targetFromTableViewName)) {
                    disableThisTargetRow.getSelectTarget().setSelected(false);
                }
                else {
                    disableThisTargetRow.getSelectTarget().setSelected(true);
                }
            }
        }
    }

    private void ChooseTargetsButtonState(Boolean state){
       for(Row r:dynamicTargetsTableView.getItems()) {r.getSelectTarget().selectedProperty().setValue(state);}
   }

   public void initAllAfterTheXmlLoaded(Integer maxThreadsAmountIn){
       canUseIncremental.setValue(false);
       maxThreadsAmount=maxThreadsAmountIn;
       initTargetsTableView();
       initSliders();
       clearAllPropertiesSelections();
       targetsNamesFromLastRun=null;
    }

    private void initSliders(){
        threadsAmountSlider.minProperty().setValue(1);
        threadsAmountSlider.maxProperty().setValue(maxThreadsAmount);
    }

    private void initTargetsTableView(){
        allRowsForTableView=runTaskTabCont.getAllTargetsNamesInSetFromRunTask();
        for(Row row : allRowsForTableView)
        {
            row.getSelectTarget().selectedProperty().addListener((observer,prev,newValue)->{
                updateIfAnyCheckBoxInTableViewIsTicked();
            });
        }
        final ObservableList<Row> data =FXCollections.observableList(new ArrayList<>(allRowsForTableView));
        targetsColumn.setCellValueFactory(
                new PropertyValueFactory<>("targetName")
        );
        SelectTargetColumn.setCellValueFactory(
                new PropertyValueFactory<>("selectTarget")
        );
        dynamicTargetsTableView.setItems(data);
        dynamicTargetsTableView.getColumns().clear();
        dynamicTargetsTableView.getColumns().addAll(SelectTargetColumn,targetsColumn);

    }
}
