package task.misc;

import java.util.Set;

public class TargetListViewDetails {
    private String name,level,finishRunningResultType,targetRunningState,MsWaitingTimeInQueueAlready,MsInProcessTimeTookAlready;
    private Set<String> serialSetsName,directDependsList,directAndIndirectDependsListThatFailed;
    public TargetListViewDetails(String targetName, String levelIn,Set<String> serialSetsNameIn,
                                 Set<String> directDependsListIn,String MsWaitingTimeInQueueAlreadyIn,
                                 Set<String> directAndIndirectDependsListThatFailedIn,
                                 String MsInProcessTimeTookAlreadyIn,String finishRunningResultTypeIn,String targetRunningStateIn)
    {
        name=targetName;
        level=levelIn;
        finishRunningResultType=finishRunningResultTypeIn;
        serialSetsName=serialSetsNameIn;
        directDependsList=directDependsListIn;
        directAndIndirectDependsListThatFailed=directAndIndirectDependsListThatFailedIn;
        MsWaitingTimeInQueueAlready=MsWaitingTimeInQueueAlreadyIn;
        MsInProcessTimeTookAlready=MsInProcessTimeTookAlreadyIn;
        targetRunningState=targetRunningStateIn;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public String getFinishRunningResultType() {
        return finishRunningResultType;
    }

    public Set<String> getSerialSetsName() {
        return serialSetsName;
    }

    public Set<String> getDirectDependsList() {
        return directDependsList;
    }

    public Set<String> getDirectAndIndirectDependsListThatFailed() {
        return directAndIndirectDependsListThatFailed;
    }

    public String getMsWaitingTimeInQueueAlready() {
        return MsWaitingTimeInQueueAlready;
    }

    public String getTargetRunningState() {return targetRunningState;}

    public String getMsInProcessTimeTookAlready() {
        return MsInProcessTimeTookAlready;
    }
}


