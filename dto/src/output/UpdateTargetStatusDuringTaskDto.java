package output;

public class UpdateTargetStatusDuringTaskDto {
    private String targetName,prevStatusOnTask, newStatusOnTask;
    public UpdateTargetStatusDuringTaskDto(String targetNameIn,String prevStatusOnTaskIn,String newStatusOnTaskIn){
        targetName=targetNameIn;
        prevStatusOnTask=prevStatusOnTaskIn;
        newStatusOnTask=newStatusOnTaskIn;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getPrevStatusOnTask() {
        return prevStatusOnTask;
    }

    public String getNewStatusOnTask() {
        return newStatusOnTask;
    }
}
