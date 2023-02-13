package output;

import javafx.beans.property.SimpleStringProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SerialSetInfoTableViewDto {
  private final SimpleStringProperty serialSetName;
  private SimpleStringProperty targetsInclude;

    public SerialSetInfoTableViewDto(String serialSetNameIn, Set<String> targetsIncludeIn){
        serialSetName=new SimpleStringProperty(serialSetNameIn);
        String tmp="";

       List<String> targetsIncludeList= targetsIncludeIn.stream().collect(Collectors.toList());
        for(int i=0;i<targetsIncludeList.size();i++){
            if(i==0){
                tmp=targetsIncludeList.get(i);
            }
            else{
                tmp=tmp+","+targetsIncludeList.get(i);
            }
        }

        targetsInclude=new SimpleStringProperty(tmp);
    }

    public String getSerialSetName() {
        return serialSetName.get();
    }

    public SimpleStringProperty serialSetNameProperty() {
        return serialSetName;
    }

    public void setSerialSetName(String serialSetName) {
        this.serialSetName.set(serialSetName);
    }

    public String getTargetsInclude() {
        return targetsInclude.get();
    }

    public SimpleStringProperty targetsIncludeProperty() {
        return targetsInclude;
    }

    public void setTargetsInclude(String targetsInclude) {
        this.targetsInclude.set(targetsInclude);
    }
}
