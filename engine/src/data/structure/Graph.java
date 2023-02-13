package data.structure;

import generated.GPUPDescriptor;
import generated.GPUPTarget;
import generated.GPUPTargetDependencies;
import output.AllDependenciesOfTargetDto;
import output.SerialSetInfoTableViewDto;
import output.TargetInfoForTableViewDto;
import xml.DependenyConflict;
import xml.SerialSetAlreadyExist;
import xml.TargetAlreadyExist;
import xml.TargetNotExist;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


public class Graph implements Serializable {
    private String name, workingDirectory;
    private Set<Target> targetSet;
    private Set<TargetsSerialSet> targetsSerialSets;

    public Graph(GPUPDescriptor gpupDesc) throws FileNotFoundException {
        targetSet = new HashSet<>();
        targetsSerialSets = new HashSet<>();
        name = gpupDesc.getGPUPConfiguration().getGPUPGraphName().trim();
        workingDirectory = gpupDesc.getGPUPConfiguration().getGPUPWorkingDirectory().trim();
        File f = new File(workingDirectory);
        if (!f.exists()) {
            throw new FileNotFoundException("Working directory does not exist!");
        }
        for (GPUPTarget gpupTar : gpupDesc.getGPUPTargets().getGPUPTarget()) {
            Target targetToAdd = new Target(gpupTar);
            if (targetSet.contains(targetToAdd)) {
                String errorMessage = "'" + targetToAdd + "' already exist in the xml file.\nTry other xml file\n";
                throw new TargetAlreadyExist(errorMessage);
            } else {
                targetSet.add(targetToAdd);
            }
        }

        for (GPUPTarget gpupTar : gpupDesc.getGPUPTargets().getGPUPTarget()) {
            String currTargetName = gpupTar.getName();//the current gpup target we work on now
            GPUPTargetDependencies dependenciesHolder = gpupTar.getGPUPTargetDependencies();
            if (dependenciesHolder != null) { //there are neighbors
                List<GPUPTargetDependencies.GPUGDependency> currList = gpupTar.getGPUPTargetDependencies().getGPUGDependency();
                Target target = findTargetByName(currTargetName);
                for (GPUPTargetDependencies.GPUGDependency gpupNode : currList) {
                    String dependType = gpupNode.getType();
                    String neighborName = gpupNode.getValue();
                    Target neighbor = findTargetByName(neighborName);

                    if (neighbor == null) {
                        throw new TargetNotExist("The target '" + neighborName + "' does not exist in the xml file.\nTry other xml file\n");
                    } else if (dependType.equals("requiredFor")) {
                        if (!neighbor.getOutTargetList().contains(target)) {
                            neighbor.addOutTarget(target);
                            target.addInTarget(neighbor);
                        }
                        if (neighbor.getInTargetList().contains(target) || target.getOutTargetList().contains(neighbor)) {//check circle of two targets
                            throw new DependenyConflict("if '" + target + "' requiredFor '" + neighbor + "' then '" + neighbor + "' can’t be requiredFor '" + target + "'\n");
                        }
                    } else//"depends on case"
                    {
                        if (!target.getOutTargetList().contains(neighbor)) {
                            target.addOutTarget(neighbor);
                            neighbor.addInTarget(target);
                        }

                        if (target.getInTargetList().contains(neighbor) || neighbor.getOutTargetList().contains(target)) {//check circle of two targets
                            throw new DependenyConflict("if '" + target + "' dependsOn '" + neighbor + "' then '" + neighbor + "' can’t be dependsOn '" + target + "'\n");
                        }
                    }
                }
            }
        }
        updateTargetsStatus(targetSet);
        if (gpupDesc.getGPUPSerialSets() != null) {
            for (GPUPDescriptor.GPUPSerialSets.GPUPSerialSet gpupSet : gpupDesc.getGPUPSerialSets().getGPUPSerialSet()) {
                TargetsSerialSet tmp = new TargetsSerialSet(gpupSet, this);
                if (targetsSerialSets.contains(tmp)) {
                    throw new SerialSetAlreadyExist("Serial set already exist in the xml file. Try other xml file");
                } else {
                    targetsSerialSets.add(tmp);//todo this function throws exception we need to find out who catches it
                }
            }
            for (Target t : targetSet) {
                for (TargetsSerialSet set : targetsSerialSets) {
                    if (set.isTargetInSet(t))
                        t.addSet(set);
                }
            }
        }
    }
    public Graph(Set<String> filteredTargetsNames, final Graph originalGraph){
        targetSet = new HashSet<>();
        targetsSerialSets = new HashSet<>();
        name= originalGraph.getName();
        workingDirectory =originalGraph.getWorkingDirectory();
        for (Target Tar : originalGraph.targetSet) {
            if(filteredTargetsNames.contains(Tar.getName())) {
                targetSet.add(new Target(Tar));
            }
        }
        for (Target tarAtNewGraph : targetSet) {
            Target tarAtOriginalGraph=originalGraph.findTargetByName(tarAtNewGraph.getName());
            for(Target nOutAtOriginalGraph:tarAtOriginalGraph.getOutTargetList()) {
                if(filteredTargetsNames.contains(nOutAtOriginalGraph.getName())) {
                    tarAtNewGraph.getOutTargetList().add(findTargetByName(nOutAtOriginalGraph.getName()));
                }
            }
            for(Target nInAtOriginalGraph:tarAtOriginalGraph.getInTargetList()) {
                if(filteredTargetsNames.contains(nInAtOriginalGraph.getName())) {
                    tarAtNewGraph.getInTargetList().add(findTargetByName(nInAtOriginalGraph.getName()));
                }
            }
        }
        updateTargetsStatus(targetSet);
        for(TargetsSerialSet originalSetS:originalGraph.getTargetSerialSets()){
            TargetsSerialSet tmp = new TargetsSerialSet(originalSetS,targetSet);
            if(!tmp.getTargetsSerialSet().isEmpty()) {
                targetsSerialSets.add(tmp);
            }
        }
        for (Target t : targetSet) {
            for (TargetsSerialSet set : targetsSerialSets) {
                if (set.isTargetInSet(t))
                    t.addSet(set);
            }
        }
    }
    final public Set<SerialSetInfoTableViewDto> getAllSerialSetsInfoForTableView() {
        Set<SerialSetInfoTableViewDto> res=new HashSet<>();
        for (TargetsSerialSet s : targetsSerialSets) {
            String serialSetName = s.getSerialSetName();
            Set<String> serialSet = s.getTargetsSerialSet().stream().map(Target::getName).collect(Collectors.toSet());
            SerialSetInfoTableViewDto addToRes=new SerialSetInfoTableViewDto(serialSetName,serialSet);
            res.add(addToRes);
        }
        return res;
    }
    final public AllDependenciesOfTargetDto getAllDependenciesOfTargetDto(String targetName) {
        Target t=findTargetByName(targetName);
        Set<String> totalDependsOn = getAccessibleTargets( false, t).stream().map(Target::getName).collect(Collectors.toSet());
        Set<String> totalRequiredFor = getAccessibleTargets( true, t).stream().map(Target::getName).collect(Collectors.toSet());
      return new AllDependenciesOfTargetDto(getTargetsMapWithStatusPerTarget(totalDependsOn),getTargetsMapWithStatusPerTarget(totalRequiredFor));
    }
    private Map<String,String> getTargetsMapWithStatusPerTarget(Set<String> targets){
        Map<String,String> res=new HashMap<>();
        for(String s:targets){
            res.put(s,findTargetByName(s).getStatus());
        }
        return  res;
    }
    final public Set<String> getAllTargetsNamesInSet(){return targetSet.stream().map(Target::getName).collect(Collectors.toSet());}
    final public Set<TargetInfoForTableViewDto> getAllTargetsInfoForTableView() {
        Set<TargetInfoForTableViewDto> res = new HashSet<>();
        Set<String> totalDependsOn, totalRequiredFor;
        for (Target t : targetSet) {
            String name = t.getName();
            String level = t.getStatus();
            if (findCircleFromTarget(t).isEmpty()) {//if the target is not taking part in a circle
                totalDependsOn = getAccessibleTargets(false, t).stream().map(Target::getName).collect(Collectors.toSet());
                totalRequiredFor = getAccessibleTargets(true, t).stream().map(Target::getName).collect(Collectors.toSet());
            }
            else{
                totalDependsOn=new HashSet<>();
                totalRequiredFor=new HashSet<>();
                totalDependsOn.add("In a circle");
                totalRequiredFor.add("In a circle");
            }
            String extraData = t.getExtraInfo();
            if (extraData == null) {
                extraData = "";
            }
            int amountOfSerialSetsTBelongsTo = t.getTheSetsThatTheTargetBelongsTo().size();
            List<String> directDependsOn = t.getOutTargetList().stream().map(Target::getName).collect(Collectors.toList());
            List<String> directRequiredFor = t.getInTargetList().stream().map(Target::getName).collect(Collectors.toList());
            TargetInfoForTableViewDto addToRes = new TargetInfoForTableViewDto(amountOfSerialSetsTBelongsTo, name, level, extraData, directDependsOn,
                    directRequiredFor, totalDependsOn, totalRequiredFor);
            res.add(addToRes);
        }
        return res;
    }
    final public String getName(){return name;}
    private void updateTargetsStatus(Collection<Target> targets) {
        for (Target t : targets) {
            if (t.getInDeg().equals(0) && t.getOutDeg().equals(0))
                t.setTargetStatus(Target.TargetStatus.INDEPENDENT);
            else if (t.getInDeg().equals(0) && !(t.getOutDeg().equals(0)))
                t.setTargetStatus(Target.TargetStatus.ROOT);
            else if (!t.getInDeg().equals(0) && !(t.getOutDeg().equals(0)))
                t.setTargetStatus(Target.TargetStatus.MIDDLE);
            else
                t.setTargetStatus(Target.TargetStatus.LEAF);
        }
    }

    final public Integer getTargetsAmount() {
        return targetSet.size();
    }

    final public Integer getIndependentAmount() {
        return getStatusSet(Target.TargetStatus.INDEPENDENT).size();
    }

    final public Integer getRootsAmount() {
        return getStatusSet(Target.TargetStatus.ROOT).size();
    }

    final public Integer getLeavesAmount() {
        return getStatusSet(Target.TargetStatus.LEAF).size();
    }

    final public Integer getMiddlesAmount() {
        return getStatusSet(Target.TargetStatus.MIDDLE).size();
    }

    private Set<Target> getTargetSet() {
        return targetSet;
    }

    final public String getWorkingDirectory() {
        return workingDirectory;
    }


    private Set<Target> getStatusSet(Target.TargetStatus s) {
        Set<Target> statusSet = new HashSet<>();
        for (Target t : targetSet) {
            if (t.getStatus().equals(s.toString())) {
                statusSet.add(t);
            }
        }
        return statusSet;
    }

    final public Target findTargetByName(String nameToLookFor) {
        Target res = null;
        for (Target t : targetSet) {
            if(t.getName()==null){
                System.out.println("hi");
            }
            if (t.getName().compareToIgnoreCase(nameToLookFor) == 0) {
                res = t;
                break;
            }
        }
        return res;
    }

    final public Set<TargetsSerialSet> getTargetSerialSets() {
        return targetsSerialSets;
    }

    final public LinkedList<Target> bottomUpSort() {
        LinkedList<Target> res = new LinkedList<>();
        Queue<Target> Q = new LinkedList<>();
        Map<Target, Integer> outDeg = new HashMap<>();
        for (Target t : targetSet)
            outDeg.put(t, t.getOutDeg());
        for (Target t : targetSet) {
            if (outDeg.get(t).equals(0))
                Q.add(t);
        }
        while (!Q.isEmpty()) {
            Target u = Q.remove();
            res.add(u);
            for (Target v : u.getInTargetList()) {
                outDeg.put(v, outDeg.get(v) - 1);
                if (outDeg.get(v).equals(0)) {
                    Q.add(v);
                }
            }
        }
        return res;
    }

    final public LinkedList<String> findCircleFromTarget(final Target x) {
        Map<Target, Integer> d = new HashMap<>();
        Map<Target, Target> p = new HashMap<>();
        Queue<Target> Q = new LinkedList<>();
        Q.add(x);
        d.put(x, 0);
        p.put(x, null);
        boolean found = false;
        Target helper = null;
        for (Target t : targetSet) {
            if (!t.equals(x)) {
                d.put(t, -1);//-1= infinity
                p.put(t, null);//-2= 'null'
            }
        }
        while (!Q.isEmpty() && !found) {
            Target u = Q.remove();
            for (Target v : u.getOutTargetList()) {
                if (d.get(v).equals(-1)) {
                    d.put(v, d.get(u) + 1);
                    p.put(v, u);
                    Q.add(v);
                } else {
                    for (Target y : u.getOutTargetList()) {
                        if (y.equals(x)) {
                            found = true;
                            helper = u;
                            break;
                        }
                    }
                }
            }
        }
        LinkedList<String> res = new LinkedList<>();
        if (found) {
            res.addFirst(x.getName());
            res.addFirst(helper.getName());
            while (!p.get(helper).equals(x)) {
                res.addFirst(p.get(helper).getName());
                helper = p.get(helper);
            }
            res.addFirst(x.getName());
        }
        return res;
    }

    private void visit(Map.Entry<Target, Target.Color> u, Map<Target, Target.Color> colorMap, LinkedList<Target> topoligalList, Boolean isOpposite) {
        colorMap.put(u.getKey(), Target.Color.GRAY);
        List<Target> listToRun;
        if (isOpposite) {
            listToRun = u.getKey().getInTargetList();
        } else {
            listToRun = u.getKey().getOutTargetList();
        }
        for (Target v : listToRun) {
            if (colorMap.get(v).equals(Target.Color.WHITE)) {
                for (Map.Entry<Target, Target.Color> entry : colorMap.entrySet()) {
                    if (entry.getKey().equals(v)) {
                        visit(entry, colorMap, topoligalList, isOpposite);
                        break;
                    }
                }
            }

        }
        colorMap.put(u.getKey(), Target.Color.BLACK);
        if (topoligalList != null) {    //we want to have a topological sort- we have other function that we don't use here it's for the future
            topoligalList.addFirst(u.getKey());
        }
    }

    public List<List<String>> getAllPaths(final Target s, final Target t) {
        Map<Target, Boolean> beingVisited = new HashMap<>();
        for (Target v : targetSet) { //init
            beingVisited.put(v, false);
        }
        List<List<String>> pathsList = new LinkedList<>();
        List<String> currentPath = new LinkedList<>();
        currentPath.add(s.getName());
        recGetAllPaths(s, t, beingVisited, currentPath, pathsList);
        return pathsList;
    }

    private void recGetAllPaths(Target u, Target t, Map<Target, Boolean> beingVisited, List<String> currentPath, List<List<String>> pathsList) {
        beingVisited.put(u, true);
        if (u.equals(t)) {
            pathsList.add(new ArrayList<>(currentPath));
            beingVisited.put(u, false);
            return;
        }
        for (Target v : u.getOutTargetList()) {
            if (!beingVisited.get(v)) {
                currentPath.add(v.getName());
                recGetAllPaths(v, t, beingVisited, currentPath, pathsList);
                currentPath.remove(v.getName());
            }
        }
        beingVisited.put(u, false);
    }

    final public Set<Target> getAccessibleTargets(Boolean isOpposite, Target x) {
        Set<Target> targetSet = getTargetSet();
        Set<Target> res = new HashSet<>();
        Map<Target, Target.Color> colorMap = new HashMap<>();
        Map.Entry<Target, Target.Color> u = null;
        for (Target t : targetSet) { //init
            colorMap.put(t, Target.Color.WHITE);
        }
        for (Map.Entry<Target, Target.Color> entry : colorMap.entrySet()) {
            if (entry.getKey().equals(x)) {
                u = entry;
                break;
            }
        }
        visit(u, colorMap, null, isOpposite);
        for (Map.Entry<Target, Target.Color> entry : colorMap.entrySet()) {
            if (entry.getValue().equals(Target.Color.BLACK) && (!entry.equals(u))) {
                res.add(entry.getKey());
            }
        }
        return res;
    }
}

