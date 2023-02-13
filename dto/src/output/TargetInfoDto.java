package output;

import java.util.List;

public class TargetInfoDto {

    final private String targetName;
    final private List<String> outTargetListNames;
    final private List<String> inTargetListNames;
    final private String targetExtraInfo;
    final private String targetStatus;

    public TargetInfoDto(String targetNameInput, List<String> outTargetListNamesInput, List<String> inTargetListNamesInput,
                         String targetExtraInfoInput, String targetStatusInput) {
        targetName = targetNameInput;
        outTargetListNames = outTargetListNamesInput;
        inTargetListNames = inTargetListNamesInput;
        targetExtraInfo = targetExtraInfoInput;
        targetStatus = targetStatusInput;
    }

    final public String getTargetName() {
        return targetName;
    }

    final public List<String> getOutTargetListNames() {
        return outTargetListNames;
    }

    final public List<String> getInTargetListNames() {
        return inTargetListNames;
    }

    final public String getTargetExtraInfo() {
        return targetExtraInfo;
    }

    final public String getTargetStatus() {
        return targetStatus;
    }
}
