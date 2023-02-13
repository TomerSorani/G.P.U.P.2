package data.structure;

import generated.GPUPTarget;

import java.io.Serializable;
import java.util.*;

public class Target implements Serializable {
    private TargetStatus status;
    final private String name,extraInfo;
    private List<Target> outTargetList,inTargetList;
    private Set<TargetsSerialSet> theSetsThatTheTargetBelongsTo;

    public enum TargetStatus {
        INDEPENDENT, LEAF, ROOT, MIDDLE, UNINITIALIZED;
    }

    public enum Color {
        WHITE, GRAY, BLACK;
    }
    public Target(final GPUPTarget gt) {
        name = gt.getName().trim();
        if (gt.getGPUPUserData() != null) {
            extraInfo = gt.getGPUPUserData().trim();
        } else {
            extraInfo = null;
        }
        status = TargetStatus.UNINITIALIZED;
        outTargetList = new LinkedList<>();
        inTargetList = new LinkedList<>();
        theSetsThatTheTargetBelongsTo=new HashSet<>();
    }
     public Target(final Target t){
       name=t.getName();
       extraInfo =t.getExtraInfo();
       status = TargetStatus.UNINITIALIZED;
       outTargetList = new LinkedList<>();
       inTargetList = new LinkedList<>();
       theSetsThatTheTargetBelongsTo=new HashSet<>();
   }
    public Set<TargetsSerialSet> getTheSetsThatTheTargetBelongsTo(){return theSetsThatTheTargetBelongsTo;}
    final public Integer getOutDeg() {
        return this.outTargetList.size();
    }

    final public Integer getInDeg() {
        return this.inTargetList.size();
    }

    final public String getName() {
        return name;
    }

    final public String getStatus() {
        return status.toString();
    }

    public List<Target> getOutTargetList() {
        return outTargetList;
    }

    public List<Target> getInTargetList() {
        return inTargetList;
    }

    public void setTargetStatus(TargetStatus s) {
        status = s;
    }

    final public String getExtraInfo() {
        return extraInfo;
    }

    public void addOutTarget(Target n) {
        this.getOutTargetList().add(n);
    }

    public void addInTarget(Target n) {
        this.getInTargetList().add(n);
    }

    public void addSet(TargetsSerialSet set){theSetsThatTheTargetBelongsTo.add(set);}

    @Override
    final public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Target target = (Target) o;
        return Objects.equals(name, target.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
