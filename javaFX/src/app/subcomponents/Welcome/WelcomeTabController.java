package app.subcomponents.Welcome;

import app.SuperController;
import app.subcomponents.graphviz.Graphviz;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;


public class WelcomeTabController {
    private SuperController sController;

    @FXML private Label loadStatusLabel;
    @FXML private Label currentFileLoadedLabel;
    @FXML private CheckBox animationsCheckBox;
    @FXML private RadioButton BreezeModeThemeRadioButton;
    @FXML private RadioButton DarkModeThemeRadioButton;


    private SimpleStringProperty loadStatusLabelProperty;
    private SimpleStringProperty currentFileLoadedLabelProperty;
    private SimpleBooleanProperty isXmlLoaded;
    private Graphviz graphviz;

    public WelcomeTabController(){
        currentFileLoadedLabelProperty=new SimpleStringProperty("");
        loadStatusLabelProperty=new SimpleStringProperty("");
        isXmlLoaded= new SimpleBooleanProperty(false);
        graphviz=new Graphviz();
    }

    public SimpleBooleanProperty isXmlLoadedProperty(){return isXmlLoaded;}
    public SimpleStringProperty loadStatusLabelProperty(){return loadStatusLabelProperty;}
    public SimpleStringProperty currentFileLoadedLabelProperty(){return currentFileLoadedLabelProperty;}

    @FXML private void initialize() {

        StringExpression loadStatusLabelPropertyExp = Bindings.concat("Load status: ",loadStatusLabelProperty);
        loadStatusLabel.textProperty().bind(loadStatusLabelPropertyExp);

        StringExpression currentFileLoadedLabelExp = Bindings.concat("Current file that loaded successfully: ",currentFileLoadedLabelProperty);
        currentFileLoadedLabel.textProperty().bind(currentFileLoadedLabelExp);

    }

    public void setSuperController(SuperController superCont){
        sController=superCont;
    }
  public boolean enableOrDisableAnimations(){
       return animationsCheckBox.isSelected();
    }

   @FXML private void loadXmlAction(){
       sController.openFileButtonAction();
   }
    @FXML private void setTheme(){
        Scene scene=loadStatusLabel.getScene();
        Scene taskReportScene= sController.getTaskReportSceneFromGrandChild();
        if(BreezeModeThemeRadioButton.isSelected()){
            scene.getStylesheets().clear();
            taskReportScene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("breezeMode.css").toExternalForm());
            taskReportScene.getStylesheets().add(getClass().getResource("breezeMode.css").toExternalForm());
        }
        else if(DarkModeThemeRadioButton.isSelected()){
            scene.getStylesheets().clear();
            taskReportScene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("darkMode.css").toExternalForm());
            taskReportScene.getStylesheets().add(getClass().getResource("darkMode.css").toExternalForm());
        }
        else{
            scene.getStylesheets().clear();
            taskReportScene.getStylesheets().clear();
        }
    }
}
