package output;

import javafx.beans.property.SimpleStringProperty;


import java.util.List;
import java.util.Set;

public class TargetInfoForTableViewDto {
    private final SimpleStringProperty name,level,extraData,amountOfSerialSetsTBelongsTo,
            directDependsOn,directRequiredFor, totalDependsOn,totalRequiredFor;
    public TargetInfoForTableViewDto(int amountOfSerialSetsTBelongsToIn, String nameIn, String levelIn,
                                     String extraDataIn, List<String> directDependsOnIn, List<String>directRequiredForIn,
                                     Set<String> totalDependsOnIn, Set<String> totalRequiredForIn) {

        amountOfSerialSetsTBelongsTo=new SimpleStringProperty(String.valueOf(amountOfSerialSetsTBelongsToIn));
        name=new SimpleStringProperty(nameIn);
        level=new SimpleStringProperty(levelIn);
        extraData=new SimpleStringProperty(extraDataIn);
        directDependsOn=new SimpleStringProperty(String.valueOf(directDependsOnIn.size()));
        directRequiredFor=new SimpleStringProperty(String.valueOf(directRequiredForIn.size()));

        if(totalDependsOnIn.contains("In a circle")){
            totalDependsOn = new SimpleStringProperty("In a circle");
            totalRequiredFor = new SimpleStringProperty("In a circle");
        }
        else {
            totalDependsOn = new SimpleStringProperty(String.valueOf(totalDependsOnIn.size()));
            totalRequiredFor = new SimpleStringProperty(String.valueOf(totalRequiredForIn.size()));
        }
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getLevel() {
        return level.get();
    }

    public SimpleStringProperty levelProperty() {
        return level;
    }

    public void setLevel(String level) {
        this.level.set(level);
    }

    public String getExtraData() {
        return extraData.get();
    }

    public SimpleStringProperty extraDataProperty() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData.set(extraData);
    }

    public String getAmountOfSerialSetsTBelongsTo() {
        return amountOfSerialSetsTBelongsTo.get();
    }

    public SimpleStringProperty amountOfSerialSetsTBelongsToProperty() {
        return amountOfSerialSetsTBelongsTo;
    }

    public void setAmountOfSerialSetsTBelongsTo(String amountOfSerialSetsTBelongsTo) {
        this.amountOfSerialSetsTBelongsTo.set(amountOfSerialSetsTBelongsTo);
    }

    public String getDirectDependsOn() {
        return directDependsOn.get();
    }

    public SimpleStringProperty directDependsOnProperty() {
        return directDependsOn;
    }

    public void setDirectDependsOn(String directDependsOn) {
        this.directDependsOn.set(directDependsOn);
    }

    public String getDirectRequiredFor() {
        return directRequiredFor.get();
    }

    public SimpleStringProperty directRequiredForProperty() {
        return directRequiredFor;
    }

    public void setDirectRequiredFor(String directRequiredFor) {
        this.directRequiredFor.set(directRequiredFor);
    }

    public String getTotalDependsOn() {
        return totalDependsOn.get();
    }

    public SimpleStringProperty totalDependsOnProperty() {
        return totalDependsOn;
    }

    public void setTotalDependsOn(String totalDependsOn) {
        this.totalDependsOn.set(totalDependsOn);
    }

    public String getTotalRequiredFor() {
        return totalRequiredFor.get();
    }

    public SimpleStringProperty totalRequiredForProperty() {
        return totalRequiredFor;
    }

    public void setTotalRequiredFor(String totalRequiredFor) {
        this.totalRequiredFor.set(totalRequiredFor);
    }
}
