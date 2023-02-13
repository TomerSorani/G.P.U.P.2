package app.subcomponents.graph.Info.tree.view;

import app.subcomponents.graph.Info.GraphInfoTabController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import output.GraphInfoDto;
import output.SerialSetInfoTableViewDto;
import output.TargetInfoDto;
import output.TargetInfoForTableViewDto;
import java.util.*;

public class TreeViewTabController {
    private GraphInfoTabController graphInfoTabCont;
    private TreeItem<String> targetsNames;
    private Set<TargetInfoForTableViewDto> setsInfo;

    @FXML private TreeView<String> infoOnGraphTreeView;
    @FXML private TableView<SerialSetInfoTableViewDto> serialInfoTable;
    @FXML private Label totalTargetsAmountLabel;
    @FXML private Label totalIndependentsAmountLabel;
    @FXML private Label totalRootsAmountLabel;
    @FXML private Label totalMiddlesAmountLabel;
    @FXML private Label totalLeavesAmountLabel;
    @FXML private TableColumn<SerialSetInfoTableViewDto, String> serialSetNameColumn;
    @FXML private TableColumn<SerialSetInfoTableViewDto,String> targetsIncludeColumn;
    private SimpleIntegerProperty totalTargetsAmountProperty;
    private SimpleIntegerProperty totalIndependentsAmountProperty;
    private SimpleIntegerProperty totalRootsAmountProperty;
    private SimpleIntegerProperty totalMiddlesAmountProperty;
    private SimpleIntegerProperty totalLeavesAmountProperty;

    public TreeViewTabController() {
        setsInfo = null;
        totalTargetsAmountProperty=new SimpleIntegerProperty();
        totalIndependentsAmountProperty=new SimpleIntegerProperty();
        totalRootsAmountProperty=new SimpleIntegerProperty();
        totalMiddlesAmountProperty=new SimpleIntegerProperty();
        totalLeavesAmountProperty=new SimpleIntegerProperty();
    }

    @FXML private void initialize() {
        StringExpression totalTargetsAmountLabelExp = Bindings.concat("Total targets amount: ",totalTargetsAmountProperty);
        totalTargetsAmountLabel.textProperty().bind(totalTargetsAmountLabelExp);

        StringExpression totalIndependentsAmountLabelExp = Bindings.concat("Total independents amount: ",totalIndependentsAmountProperty);
        totalIndependentsAmountLabel.textProperty().bind(totalIndependentsAmountLabelExp);

        StringExpression totalRootsAmountLabelExp = Bindings.concat("Total roots amount: ",totalRootsAmountProperty);
        totalRootsAmountLabel.textProperty().bind(totalRootsAmountLabelExp);

        StringExpression totalMiddlesAmountLabelExp = Bindings.concat("Total middles amount: ",totalMiddlesAmountProperty);
        totalMiddlesAmountLabel.textProperty().bind(totalMiddlesAmountLabelExp);

        StringExpression totalLeavesAmountLabelExp = Bindings.concat("Total leaves amount: ",totalLeavesAmountProperty);
        totalLeavesAmountLabel.textProperty().bind(totalLeavesAmountLabelExp);
    }

    public void initTargetsInfoTreeView() {
        targetsNames = new TreeItem<>("Targets");
        setsInfo = graphInfoTabCont.getAllInformationForTableViewFromSuperController();
        initTreeView(targetsNames);
        initSerialSetsInfoTableView();
        setGraphInfoLabels();
}

    private TreeItem<String> initTreeItems(TargetInfoForTableViewDto t) {
           TargetInfoDto tInfo = graphInfoTabCont.getTargetInfoDtoFromSuper(t.getName());
        List<String> inTargetListNames = tInfo.getInTargetListNames();
        TreeItem<String> name = new TreeItem<>(t.getName());
        TreeItem<String> data = new TreeItem<>("Data about "+t.getName());
        TreeItem<String> level = new TreeItem<>("Level: " + t.getLevel());
        TreeItem<String> extraData = new TreeItem<>("Extra data: " + (t.getExtraData().equals("") ? "none" : t.getExtraData()));
        TreeItem<String> directDependsOn = new TreeItem<>("Amount of direct depends on: " + t.getDirectDependsOn());
        TreeItem<String> directRequiredFor = new TreeItem<>("Amount of direct required for: " + t.getDirectRequiredFor());
        TreeItem<String> totalDependsOn = new TreeItem<>("Amount of total depends on: " + t.getTotalDependsOn());
        TreeItem<String> totalRequiredFor = new TreeItem<>("Amount of total required for: " + t.getTotalRequiredFor());
        TreeItem<String> amountOfSerialSetsTBelongsTo = new TreeItem<>("Amount of serial sets with the target: " + t.getAmountOfSerialSetsTBelongsTo());
        data.getChildren().addAll(level, extraData, directDependsOn, directRequiredFor, totalDependsOn, totalRequiredFor,
                amountOfSerialSetsTBelongsTo);

        List<TreeItem<String>> children= new LinkedList<>();
        for(String neighbor : inTargetListNames){
            for(TargetInfoForTableViewDto x : setsInfo){
                if(x.getName().equals(neighbor)){
                    children.add(initTreeItems(x));
                }
            }
        }
        children.forEach(child->name.getChildren().add(child));
        name.getChildren().add(data);
        return name;
    }


    private void initTreeView(TreeItem<String> father){
        infoOnGraphTreeView.setRoot(targetsNames);
        for (TargetInfoForTableViewDto t : setsInfo) {
            if (t.getLevel().equals("LEAF")) {
                TargetInfoDto tInfo = graphInfoTabCont.getTargetInfoDtoFromSuper(t.getName());
                List<String> inTargetListNames = tInfo.getInTargetListNames();
                TreeItem<String> name = new TreeItem<>(t.getName());
                TreeItem<String> data = new TreeItem<>("Data about "+t.getName());
                TreeItem<String> level = new TreeItem<>("Level: " + t.getLevel());
                TreeItem<String> extraData = new TreeItem<>("Extra data: " + (t.getExtraData().equals("") ? "none" : t.getExtraData()));
                TreeItem<String> directDependsOn = new TreeItem<>("Amount of direct depends on: " + t.getDirectDependsOn());
                TreeItem<String> directRequiredFor = new TreeItem<>("Amount of direct required for: " + t.getDirectRequiredFor());
                TreeItem<String> totalDependsOn = new TreeItem<>("Amount of total depends on: " + t.getTotalDependsOn());
                TreeItem<String> totalRequiredFor = new TreeItem<>("Amount of total required for: " + t.getTotalRequiredFor());
                TreeItem<String> amountOfSerialSetsTBelongsTo = new TreeItem<>("Amount of serial sets with the target: " + t.getAmountOfSerialSetsTBelongsTo());
                data.getChildren().addAll(level, extraData, directDependsOn, directRequiredFor, totalDependsOn, totalRequiredFor,
                        amountOfSerialSetsTBelongsTo);

                List<TreeItem<String>> children= new LinkedList<>();
                for(String neighbor : inTargetListNames){
                    for(TargetInfoForTableViewDto x : setsInfo){
                        if(x.getName().equals(neighbor)){
                            children.add(initTreeItems(x));
                        }
                    }
                }
                children.forEach(child->name.getChildren().add(child));
               name.getChildren().add(data);
               father.getChildren().add(name);
            }
        }
        infoOnGraphTreeView.setShowRoot(false);

    }


    public void setGraphInfoTabController(GraphInfoTabController GraphInfoTabC) {
        graphInfoTabCont = GraphInfoTabC;
    }

    private void setGraphInfoLabels() {
        GraphInfoDto graphInfoDto = graphInfoTabCont.getGraphInfoDtoFromSuperController();
        totalTargetsAmountProperty.setValue(graphInfoDto.getTargetsAmount());
        totalIndependentsAmountProperty.setValue(graphInfoDto.getIndependentAmount());
        totalRootsAmountProperty.setValue(graphInfoDto.getRootsAmount());
        totalMiddlesAmountProperty.setValue(graphInfoDto.getMiddlesAmount());
        totalLeavesAmountProperty.setValue(graphInfoDto.getLeavesAmount());
    }

    private void initSerialSetsInfoTableView(){
        Set<SerialSetInfoTableViewDto> serialSetsInfoForTableView=graphInfoTabCont.getAllSerialSetsInfoForTableViewFromSuperController();
        final ObservableList<SerialSetInfoTableViewDto> data = FXCollections.observableList(new ArrayList<>(serialSetsInfoForTableView));

        serialSetNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("serialSetName")
        );
        targetsIncludeColumn.setCellValueFactory(
                new PropertyValueFactory<>("targetsInclude")
        );
        serialInfoTable.setItems(data);
        serialInfoTable.getColumns().clear();
        serialInfoTable.getColumns().addAll(serialSetNameColumn,targetsIncludeColumn);
    }
}



