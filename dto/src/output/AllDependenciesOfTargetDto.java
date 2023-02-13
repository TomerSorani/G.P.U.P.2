package output;

import java.util.Map;

public class AllDependenciesOfTargetDto {
    private Map<String,String> totalDependsOn,totalRequiredFor;
    public AllDependenciesOfTargetDto(Map<String,String> totalDependsOnIn,Map<String,String> totalRequiredForIn){
        totalDependsOn=totalDependsOnIn;
        totalRequiredFor=totalRequiredForIn;
    }

    public Map<String, String> getTotalDependsOn() {
        return totalDependsOn;
    }

    public Map<String, String> getTotalRequiredFor() {
        return totalRequiredFor;
    }
}
