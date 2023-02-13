package data.structure;

import generated.GPUPDescriptor;
import xml.TargetAlreadyExist;
import xml.TargetNotExist;

import java.util.*;
public class TargetsSerialSet {
     private Set<Target> serialSet;
     private String serialSetName;
     private Boolean setLocker;
     private String lockedBy;

     public TargetsSerialSet(GPUPDescriptor.GPUPSerialSets.GPUPSerialSet gpSerialSet,final Graph g){
         serialSet=new HashSet<>();
         serialSetName=gpSerialSet.getName().trim();//todo where we call it make it case insensitive
         String tmp=gpSerialSet.getTargets();//
         List<String> container=  Arrays.asList(tmp.split(","));
         addTargetsIfValid(container,g);
         setLocker=false;
         lockedBy=null;
     }
     public TargetsSerialSet(TargetsSerialSet originalTargetsSerialSet, Set<Target> serialSetInNewGraph){
         serialSet=new HashSet<>();
         serialSetName=originalTargetsSerialSet.getSerialSetName();
         setLocker=false;
         lockedBy=null;
         for(Target t:originalTargetsSerialSet.getTargetsSerialSet()){
             if(serialSetInNewGraph.contains(t)){
                for(Target targetInNewGraph:serialSetInNewGraph){
                    if(targetInNewGraph.getName().equals(t.getName())){
                        serialSet.add(targetInNewGraph);
                    }
                }

             }
         }
     }
    public Set<Target> getTargetsSerialSet(){return serialSet;}///todo maybe final?

    public void lockSet(){setLocker=true;}
    public void unlockSet(){setLocker=false;}
    public Boolean isSetLocked(){return setLocker;}

    public String getLockedBy(){
         return lockedBy;
    }
    public void setLockedBy(String targetName){
        lockedBy=targetName;
    }

    public String getSerialSetName() {
        return serialSetName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetsSerialSet that = (TargetsSerialSet) o;
        return Objects.equals(serialSet, that.serialSet) && Objects.equals(serialSetName, that.serialSetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialSet, serialSetName);
    }

    final boolean isTargetInSet(Target target){return serialSet.contains(target);}

    private void addTargetsIfValid( List<String> container,final Graph g){
        for(String s: container){
            Target t=g.findTargetByName(s);
            if (t != null) {
                if(serialSet.contains(t)){
                    throw new TargetAlreadyExist( "'" + s + "' already exist in the xml file.\nTry other xml file\n");
                }
                else {
                    serialSet.add(t);
                }
            } else {
                throw new TargetNotExist("The target '" +s+"' does not exist in the xml file.\nTry other xml file\n");
            }
        }
    }

}
