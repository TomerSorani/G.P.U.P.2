package app.subcomponents.graph.Info.table.view;

import app.subcomponents.graph.Info.GraphInfoTabController;
import com.sun.org.apache.xpath.internal.operations.String;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import output.GraphInfoDto;
import output.SerialSetInfoTableViewDto;
import output.TargetInfoForTableViewDto;

import java.util.ArrayList;
import java.util.Set;


public class TableViewTabController {

   private GraphInfoTabController graphInfoTabCont;

   @FXML private TableView<TargetInfoForTableViewDto> infoOnGraphTable;
   @FXML private TableView<SerialSetInfoTableViewDto> serialInfoTable;
   @FXML private TableColumn<TargetInfoForTableViewDto,String> targetNameColumn;
   @FXML private TableColumn<TargetInfoForTableViewDto,String> levelColumn;
   @FXML private TableColumn<TargetInfoForTableViewDto,String> totalDependsOnColumn;
   @FXML private TableColumn<TargetInfoForTableViewDto,String> directDependsOnColumn;
   @FXML private TableColumn<TargetInfoForTableViewDto,String> totalRequiredForColumn;
   @FXML private TableColumn<TargetInfoForTableViewDto,String> directRequiredForColumn;
   @FXML private TableColumn<TargetInfoForTableViewDto,String> extraDataColumn;
   @FXML private TableColumn<TargetInfoForTableViewDto,String> amountOfSerialSetsWithTheTargetColumn;
   @FXML private TableColumn<SerialSetInfoTableViewDto,String> serialSetNameColumn;
   @FXML private TableColumn<SerialSetInfoTableViewDto,String> targetsIncludeColumn;
   @FXML private Label totalTargetsAmountLabel;
   @FXML private Label totalIndependentsAmountLabel;
   @FXML private Label totalRootsAmountLabel;
   @FXML private Label totalMiddlesAmountLabel;
   @FXML private Label totalLeavesAmountLabel;

    private SimpleIntegerProperty totalTargetsAmountProperty;
    private SimpleIntegerProperty totalIndependentsAmountProperty;
    private SimpleIntegerProperty totalRootsAmountProperty;
    private SimpleIntegerProperty totalMiddlesAmountProperty;
    private SimpleIntegerProperty totalLeavesAmountProperty;
    private Set<TargetInfoForTableViewDto> targetsInfoForTableView;//todo maybe it can be a local in function
    private Set<SerialSetInfoTableViewDto> serialSetsInfoForTableView;//todo maybe it can be a local in function

    public TableViewTabController(){
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

    public void setGraphInfoTabController(GraphInfoTabController GraphInfoTabC){
        graphInfoTabCont=GraphInfoTabC;
    }

    private void setGraphInfoLabels() {
        GraphInfoDto graphInfoDto = graphInfoTabCont.getGraphInfoDtoFromSuperController();
        totalTargetsAmountProperty.setValue(graphInfoDto.getTargetsAmount());
        totalIndependentsAmountProperty.setValue(graphInfoDto.getIndependentAmount());
        totalRootsAmountProperty.setValue(graphInfoDto.getRootsAmount());
        totalMiddlesAmountProperty.setValue(graphInfoDto.getMiddlesAmount());
        totalLeavesAmountProperty.setValue(graphInfoDto.getLeavesAmount());
    }
    public void initAllTablesViews(){
        initTargetsInfoTableView();
        initSerialSetsInfoTableView();
        setGraphInfoLabels();
    }
    private void initTargetsInfoTableView(){
        targetsInfoForTableView=graphInfoTabCont.getAllInformationForTableViewFromSuperController();
        final ObservableList<TargetInfoForTableViewDto> data =FXCollections.observableList(new ArrayList<>(targetsInfoForTableView));
        targetNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        levelColumn.setCellValueFactory(
                new PropertyValueFactory<>("level")
        );
        extraDataColumn.setCellValueFactory(
                new PropertyValueFactory<>("extraData")
        );
        directDependsOnColumn.setCellValueFactory(
                new PropertyValueFactory<>("directDependsOn")
        );
        directRequiredForColumn.setCellValueFactory(
                new PropertyValueFactory<>("directRequiredFor")
        );
        totalDependsOnColumn.setCellValueFactory(
                new PropertyValueFactory<>("totalDependsOn")
        );
        totalRequiredForColumn.setCellValueFactory(
                new PropertyValueFactory<>("totalRequiredFor")
        );
        amountOfSerialSetsWithTheTargetColumn.setCellValueFactory(
                new PropertyValueFactory<>("amountOfSerialSetsTBelongsTo")
        );

        infoOnGraphTable.setItems(data);
        infoOnGraphTable.getColumns().clear();
        infoOnGraphTable.getColumns().addAll(targetNameColumn, levelColumn, totalDependsOnColumn,directDependsOnColumn,
                totalRequiredForColumn,directRequiredForColumn,extraDataColumn,amountOfSerialSetsWithTheTargetColumn);
    }

    private void initSerialSetsInfoTableView(){
         serialSetsInfoForTableView=graphInfoTabCont.getAllSerialSetsInfoForTableViewFromSuperController();
        final ObservableList<SerialSetInfoTableViewDto> data =FXCollections.observableList(new ArrayList<>(serialSetsInfoForTableView));

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
