package app.subcomponents.run.task.task.properties.tableViewRow;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

public class Row {
    private final SimpleStringProperty targetName;
    private CheckBox selectTarget;

    public Row(String targetNameIn){
        targetName=new SimpleStringProperty(targetNameIn);
        selectTarget=new CheckBox();
    }

    public String getTargetName() {
        return targetName.get();
    }

    public SimpleStringProperty targetNameProperty() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName.set(targetName);
    }

    public CheckBox getSelectTarget() {
        return selectTarget;
    }

    public void setSelectTarget(CheckBox selectTarget) {
        this.selectTarget = selectTarget;
    }
}
