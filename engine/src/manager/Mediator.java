package manager;

import data.structure.Graph;
import data.structure.Target;
import generated.GPUPDescriptor;
import input.task.TaskInputDto;
import javafx.beans.property.SimpleBooleanProperty;
import jdk.nashorn.internal.ir.ReturnNode;
import output.*;
import task.impl.AbstractTaskManager;
import task.impl.CompilationTask;
import task.impl.SimulationTask;
import task.misc.TargetListViewDetails;
import task.misc.UiAdapterInterface;
import xml.TargetNotExist;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Mediator implements Serializable {
    public enum TaskType {
        SIMULATION, COMPILATION;
    }

    private Graph OriginalTargetsGraph, graphForTaskOnly;
    private AbstractTaskManager taskManager;
    private Integer maxThreadsAmount;

    public Mediator() {
        OriginalTargetsGraph = null;
        graphForTaskOnly = null;
        taskManager = null;
    }
     public void updateGraphForTaskOnlyToNullAfterLoadXml(){graphForTaskOnly=null;}
    final public GraphInfoDto getGraphInfoDto() {
        Integer targetsAmount = OriginalTargetsGraph.getTargetsAmount();
        Integer rootsAmount = OriginalTargetsGraph.getRootsAmount();
        Integer middlesAmount = OriginalTargetsGraph.getMiddlesAmount();
        Integer leavesAmount = OriginalTargetsGraph.getLeavesAmount();
        Integer independentsAmount = OriginalTargetsGraph.getIndependentAmount();
        return new GraphInfoDto(targetsAmount, rootsAmount, middlesAmount, leavesAmount, independentsAmount);
    }

    public TargetListViewDetails getTargetListViewDetailsFromAbstractTask(String targetName) {
        return taskManager.getTargetListViewDetails(targetName);
    }

    final public AllDependenciesOfTargetDto getAllDependenciesOfTargetDtoFromGraph(String targetName) {
        return OriginalTargetsGraph.getAllDependenciesOfTargetDto(targetName);
    }
    public AllGraphDto getAllGraphDto(){
       int amountOfTargets= OriginalTargetsGraph.getTargetsAmount();
        TargetInfoDto[] allGraphDto=new TargetInfoDto[amountOfTargets];
        int i=0;
        for(String targetName:getAllTargetsNamesInSetFromGraph()){
            allGraphDto[i]=getTargetInfoDto(targetName);
            i++;
        }
        return new AllGraphDto(allGraphDto);
    }
    final public TargetInfoDto getTargetInfoDto(String name) {
        Target t = OriginalTargetsGraph.findTargetByName(name);
        if (t == null) {
            return null;
        }
        String targetName = t.getName(), targetExtraInfo = t.getExtraInfo(), targetStatus = t.getStatus();
        List<String> outTargetListNames = t.getOutTargetList().stream().map(Target::getName).collect(Collectors.toList());
        List<String> inTargetListNames = t.getInTargetList().stream().map(Target::getName).collect(Collectors.toList());
        return new TargetInfoDto(targetName, outTargetListNames, inTargetListNames, targetExtraInfo, targetStatus);
    }

    final public Set<TargetInfoForTableViewDto> getAllTargetsInfoForTableViewDtoFromGraph() {
        return OriginalTargetsGraph.getAllTargetsInfoForTableView();
    }

    final public Set<SerialSetInfoTableViewDto> getAllSerialSetsInfoForTableViewFromGraph() {
        return OriginalTargetsGraph.getAllSerialSetsInfoForTableView();
    }

    final public Set<String> getAllTargetsNamesInSetFromGraph() {
        return OriginalTargetsGraph.getAllTargetsNamesInSet();
    }
    final public Set<String> getAllTargetsNamesInSetFromGraphTaskOnly() {
        return  graphForTaskOnly.getAllTargetsNamesInSet();
    }

    final public Integer getMaxThreadsAmount() {
        return maxThreadsAmount;
    }

    final public AllPathsOfTwoTargetsDto findBindBetweenTwoTargets(final String str1, final String str2) {
        Target t1 = OriginalTargetsGraph.findTargetByName(str1);
        Target t2 = OriginalTargetsGraph.findTargetByName(str2);
        return new AllPathsOfTwoTargetsDto(OriginalTargetsGraph.getAllPaths(t1, t2));
    }

    public boolean runTaskFilter(Mediator.TaskType type, TaskInputDto taskInput, UiAdapterInterface UiAdapter, Set<String> targetsToRun, SimpleBooleanProperty pausePropertyIn) {
        Boolean isScratch = taskInput.getIsScratch();
        Integer amountOfThreadsToRun = taskInput.getAmountOfThreads();
        switch (type) {
            case SIMULATION: {
                if (isScratch) {
                    graphForTaskOnly = new Graph(targetsToRun, OriginalTargetsGraph);
                    taskManager = new SimulationTask(graphForTaskOnly, taskInput, UiAdapter);//todo finish
                }
                else {//do incremental and there are leftovers from last run
                    ((SimulationTask) taskManager).updateMembers(taskInput);//todo wait for Aviad's answer
                    taskManager.incrementor();
                }
                break;
            }
            case COMPILATION: {
                if (isScratch) {
                    graphForTaskOnly = new Graph(targetsToRun, OriginalTargetsGraph);
                    taskManager = new CompilationTask(graphForTaskOnly, taskInput, UiAdapter);
                } else {
                    ((CompilationTask) taskManager).updateMembers(taskInput);//todo check its good as well for compilation
                    taskManager.incrementor();
                }
                break;
            }
        }
        String taskFolder = null;
        taskFolder = taskManager.createTaskFolderAndGetPath(type);
        taskManager.runTaskOnTargetList(taskFolder, amountOfThreadsToRun, type, pausePropertyIn);
        return false;//todo what is that returning value
    }

    public void doTaskReport(UiAdapterInterface uiAdapt, Instant startTime, Mediator.TaskType taskType) {
        FileWriter summaryTaskReport = null;
        try {
            String taskPath = getTaskThePath();
            String fileName = taskPath + "\\" + "report.log";
            summaryTaskReport = new FileWriter(fileName);
            if (taskManager.getAmountOfRunningTargets() == 0) {
                uiAdapt.updateTaskReport("All the targets already finished in previous task!");
                summaryTaskReport.write("All the targets all ready finished in previous task!");
            } else {
                Instant endTime = Instant.now();
                Duration res = Duration.between(startTime, endTime);
                String totalTaskTime = taskManager.getFullTimeFromDuration(res);
                String articleTotalTime = "The total time the task has taken is: " + totalTaskTime + "\n";
                uiAdapt.updateTaskReport(articleTotalTime);
                summaryTaskReport.write(articleTotalTime);

                Map<Target, AbstractTaskManager.RunningResult> targetsRunningResultMapFromTask = getTargetsRunningResultMapFromTask();
                List<String> successfulls = new ArrayList<>(), warnings = new ArrayList<>(), failures = new ArrayList<>(), skipped = new ArrayList<>(), dontChecks = new ArrayList<>();
                boolean isIncrementalTask = false;
                for (Map.Entry<Target, AbstractTaskManager.RunningResult> t : targetsRunningResultMapFromTask.entrySet()) {
                    if (t.getValue().equals(AbstractTaskManager.RunningResult.SUCCESS)) {
                        successfulls.add(t.getKey().getName());
                    } else if (t.getValue().equals(AbstractTaskManager.RunningResult.WARNING)) {
                        warnings.add(t.getKey().getName());
                    } else if (t.getValue().equals(AbstractTaskManager.RunningResult.FAILURE)) {
                        failures.add(t.getKey().getName());
                    } else if (t.getValue().equals(AbstractTaskManager.RunningResult.SKIPPED)) {
                        skipped.add(t.getKey().getName());
                    } else {//DONT_CHEK
                        dontChecks.add(t.getKey().getName());
                        isIncrementalTask = true;
                    }
                }
                String incrementalMessage = "", uniteAll = "";
                if (isIncrementalTask) {
                    incrementalMessage = "In this task there are targets that have already been processed successfully before.\n\n" +
                            "From previous tasks: " + dontChecks.size() + " | ";
                }
                String successfullsStr = "Successfulls amount: " + successfulls.size() + " | ";
                String warningsStr = "Warnings amount: " + warnings.size() + " | ";
                String failuresStr = "Failures amount: " + failures.size() + " | ";
                String skippedStr = "Skipped amount: " + skipped.size() + "\n\n";
                if (isIncrementalTask) {
                    uniteAll = incrementalMessage + successfullsStr + warningsStr + failuresStr + skippedStr;
                } else {
                    uniteAll = successfullsStr + warningsStr + failuresStr + skippedStr;
                }
                String finalUniteAll = uniteAll;

                uiAdapt.updateTaskReport(finalUniteAll);
                summaryTaskReport.write(finalUniteAll);
                if (isIncrementalTask && dontChecks.size() > 0) {
                    String introOfPreviousSuccess = "The targets that already succeeded in previous tasks are: ";
                    uiAdapt.updateTaskReport(introOfPreviousSuccess);
                    summaryTaskReport.write(introOfPreviousSuccess);

                    dontChecks.forEach(str -> uiAdapt.updateTaskReport(str + " "));
                    for (String name : dontChecks) {
                        summaryTaskReport.write(name + " ");
                    }
                    uiAdapt.updateTaskReport("\n");
                    summaryTaskReport.write("\n");
                }
                if (successfulls.size() > 0) {
                    String successfullsIntroduction = "The successful targets are: ";
                    uiAdapt.updateTaskReport(successfullsIntroduction);
                    summaryTaskReport.write(successfullsIntroduction);
                    successfulls.forEach(str -> uiAdapt.updateTaskReport(str + " "));

                    for (String name : successfulls) {
                        summaryTaskReport.write(name + " ");
                    }
                    uiAdapt.updateTaskReport("\n");
                    summaryTaskReport.write("\n");
                }
                if (warnings.size() > 0) {
                    String warningsIntroduction = "The successful with warning targets are: ";
                    uiAdapt.updateTaskReport(warningsIntroduction);
                    summaryTaskReport.write(warningsIntroduction);
                    warnings.forEach(str -> uiAdapt.updateTaskReport(str + " "));
                    for (String name : warnings) {
                        summaryTaskReport.write(name + " ");
                    }
                    uiAdapt.updateTaskReport("\n");
                    summaryTaskReport.write("\n");
                }
                if (failures.size() > 0) {
                    String failureIntroduction = "The failures targets are: ";
                    uiAdapt.updateTaskReport(failureIntroduction);
                    summaryTaskReport.write(failureIntroduction);
                    failures.forEach(str -> uiAdapt.updateTaskReport(str + " "));
                    for (String name : failures) {
                        summaryTaskReport.write(name + " ");
                    }
                    uiAdapt.updateTaskReport("\n");
                    summaryTaskReport.write("\n");
                }
                if (skipped.size() > 0) {
                    String skippedIntroduction = "The skipped targets are: ";
                    uiAdapt.updateTaskReport(skippedIntroduction);
                    summaryTaskReport.write(skippedIntroduction);
                    skipped.forEach(str -> uiAdapt.updateTaskReport(str + " "));
                    for (String name : skipped) {
                        summaryTaskReport.write(name + " ");
                    }
                    uiAdapt.updateTaskReport("\n");
                    summaryTaskReport.write("\n");
                }
                uiAdapt.updateTaskReport("\n");
                summaryTaskReport.write("\n");

                String tName = "", tTaskRes = "", uniteSummary = "", failureReason = "Failure reason: The success rates weren't high enough for this target on the task\n", skippedReason = "";
                if (taskType.equals(TaskType.COMPILATION)) {
                    failureReason = "Failure reason: the compilation process on this target has been failed\n";
                }
                for (Map.Entry<Target, AbstractTaskManager.RunningResult> t : targetsRunningResultMapFromTask.entrySet()) {
                    if (!(t.getValue().equals(AbstractTaskManager.RunningResult.DONT_CHECK))) {
                        tName = "Target Name: " + t.getKey().getName() + "\n";
                        tTaskRes = "Running result: " + t.getValue().toString() + "\n";
                        if (t.getValue().equals(AbstractTaskManager.RunningResult.FAILURE)) {
                            tTaskRes += failureReason;
                        }

                        uniteSummary = tName + tTaskRes;
                        if (!(t.getValue().equals(AbstractTaskManager.RunningResult.SKIPPED))) {
                            uniteSummary = uniteSummary + "Running time: " + getTargetsRunningTimeMapFromTask().get(t.getKey());
                        }
                        String finalUniteSummary = uniteSummary;
                        if (!(t.getValue().equals(AbstractTaskManager.RunningResult.SKIPPED)))
                            finalUniteSummary += "\n\n";
                        String finalUniteSummary1 = finalUniteSummary;
                        uiAdapt.updateTaskReport(finalUniteSummary1);
                        summaryTaskReport.write(finalUniteSummary1);

                        if (t.getValue().equals(AbstractTaskManager.RunningResult.SKIPPED)) {
                            String cause = "";
                            for (Target u : t.getKey().getOutTargetList()) {
                                if (targetsRunningResultMapFromTask.get(u).equals(AbstractTaskManager.RunningResult.FAILURE) ||
                                        targetsRunningResultMapFromTask.get(u).equals(AbstractTaskManager.RunningResult.SKIPPED)) {
                                    cause = u.getName();
                                    break;
                                }
                            }
                            String targetName = t.getKey().getName();
                            skippedReason = "'" + targetName + "' is skipped because '" + cause + "' has been skipped or failed before" +
                                    " and '" + targetName + "' depends on '" + cause + "'.";
                            String finalSkippedReason = skippedReason + "\n\n";
                            uiAdapt.updateTaskReport(finalSkippedReason);
                            summaryTaskReport.write(finalSkippedReason);
                        }

                    }
                }
            }
        } catch (IOException ignore) {}
            finally{
        if (summaryTaskReport != null) {
            try {
                summaryTaskReport.close();
            } catch (IOException ignored) {
            }
        }
    }
}



    public void runTask(TaskType taskType, TaskInputDto taskInput, List<Consumer<String>> consumersListOfStr) {}

    private Map<Target, AbstractTaskManager.RunningResult> getTargetsRunningResultMapFromTask() {
        return taskManager.getTargetRunningResultMap();
    }

    private Map<Target, String> getTargetsRunningTimeMapFromTask() {
        return taskManager.getTargetsRunningTimeMap();
    }

    private String getTaskThePath() {
        return taskManager.getTaskPath();
    }

    private final static String JAXB_XML_GPUP_PACKAGE_NAME = "generated";

    public Boolean loadFile(String fileName) throws IOException, JAXBException {
        GPUPDescriptor descriptor;
        try (InputStream inputStream = new FileInputStream(fileName)) {
            descriptor = deserializeFrom(inputStream);
        }
        OriginalTargetsGraph = new Graph(descriptor);
        maxThreadsAmount = descriptor.getGPUPConfiguration().getGPUPMaxParallelism();//todo maybe check?
        return true;
    }

    private GPUPDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GPUP_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (GPUPDescriptor) u.unmarshal(in);
    }

    public Boolean isFileLoaded() {
        return OriginalTargetsGraph != null;
    }

    public Boolean isFileNameValid(final String fileName) throws IOException {
        int len = fileName.length();
        if (len < 4) {
            throw new IOException("File full name is too short!");
        }
        if (!fileName.endsWith(".xml")) {
            throw new IOException("This is not a full path of a xml file!");
        }
        return true;
    }

    public CirclePathForTargetDto checkIfTargetIsInCircle(String targetName) throws TargetNotExist {
        Target t = OriginalTargetsGraph.findTargetByName(targetName);
        if (t == null) {
            throw new TargetNotExist("target does not exist!\nTry enter other target");
        } else {
            return new CirclePathForTargetDto(OriginalTargetsGraph.findCircleFromTarget(t));
        }
    }


    public void writeToFile(String FileName) throws IOException {
        ArrayList<Mediator> meds = new ArrayList<>();
        meds.add(this);
        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(FileName))) {
            out.writeObject(meds);
            out.flush();
        }
    }

    public void readFromFile(String FileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(FileName))) {
            ArrayList<Mediator> meds = (ArrayList<Mediator>) in.readObject();
            OriginalTargetsGraph = meds.get(0).OriginalTargetsGraph;
            taskManager = meds.get(0).taskManager;
        }
    }
}
