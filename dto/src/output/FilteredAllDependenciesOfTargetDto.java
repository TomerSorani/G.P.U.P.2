package output;

import java.util.HashMap;
import java.util.Map;

public class FilteredAllDependenciesOfTargetDto {

  private Map<String,String> notFilteredTotalDependsOn,notFilteredTotalRequiredFor;
  private Map<String,String> filteredTotalDependsOn,filteredTotalRequiredFor;
  private boolean checkAllLevels,rootTicked,middleTicked,leafTicked,independentTicked;


    public FilteredAllDependenciesOfTargetDto(AllDependenciesOfTargetDto allDependenciesOfTargetDtoIn,
                    boolean checkAllLevelsIn,boolean rootTickedIn,boolean middleTickedIn,
                    boolean leafTickedIn,boolean independentTickedIn)
    {
        filteredTotalDependsOn=new HashMap<>();
        filteredTotalRequiredFor=new HashMap<>();
        notFilteredTotalDependsOn= allDependenciesOfTargetDtoIn.getTotalDependsOn();
        notFilteredTotalRequiredFor=allDependenciesOfTargetDtoIn.getTotalRequiredFor();
        checkAllLevels=checkAllLevelsIn;
        rootTicked=rootTickedIn;
        middleTicked=middleTickedIn;
        leafTicked=leafTickedIn;
        independentTicked=independentTickedIn;
        updateFilteredMaps();
    }

   private void updateFilteredMaps(){
        if(checkAllLevels){
            filteredTotalDependsOn=notFilteredTotalDependsOn;
            filteredTotalRequiredFor=notFilteredTotalRequiredFor;
            return;
        }
        else{
            filterMap(filteredTotalDependsOn,notFilteredTotalDependsOn);
            filterMap(filteredTotalRequiredFor,notFilteredTotalRequiredFor);
        }
   }

   private void filterMap(Map<String,String> filteredMap,Map<String,String> mapToFilter){

        for(Map.Entry<String,String> m:mapToFilter.entrySet()){
           if(m.getValue().equals("LEAF")&&leafTicked){
               filteredMap.put(m.getKey(),m.getValue());
           }
           else if(m.getValue().equals("ROOT")&&rootTicked){
               filteredMap.put(m.getKey(),m.getValue());
           }
           else if(m.getValue().equals("MIDDLE")&&middleTicked){
               filteredMap.put(m.getKey(),m.getValue());
           }
           else if(m.getKey().equals("INDEPENDENT")&&independentTicked){
               filteredMap.put(m.getKey(),m.getValue());
           }
       }
   }
   public Map<String, String> getTotalFilteredDependsOn() {return filteredTotalDependsOn;}
   public Map<String, String> getTotalFilteredRequiredFor() {return filteredTotalRequiredFor;}
}
