package app.subcomponents.graph.Info;

import app.SuperController;
import app.subcomponents.graph.Info.table.view.TableViewTabController;
import app.subcomponents.graph.Info.tree.view.TreeViewTabController;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import output.GraphInfoDto;
import output.SerialSetInfoTableViewDto;
import output.TargetInfoDto;
import output.TargetInfoForTableViewDto;

import java.util.Set;

public class GraphInfoTabController {
    private SuperController sController;

    @FXML private GridPane tableViewTabContent;
    @FXML private TableViewTabController tableViewTabContentController;

    @FXML private GridPane treeViewTabContent;
    @FXML private TreeViewTabController treeViewTabContentController;

    @FXML public void initialize() {
      if(tableViewTabContentController!=null&&treeViewTabContentController!=null) {
          tableViewTabContentController.setGraphInfoTabController(this);
          treeViewTabContentController.setGraphInfoTabController(this);
      }
    }
    public void setSuperController(SuperController superCont){
        sController=superCont;
    }
    public Set<TargetInfoForTableViewDto> getAllInformationForTableViewFromSuperController(){
      return sController.getAllInformationForTableViewFromMed();
    }
    public GraphInfoDto getGraphInfoDtoFromSuperController(){
        return sController.getGraphInfoDtoFromMed();
    }
    public Set<SerialSetInfoTableViewDto> getAllSerialSetsInfoForTableViewFromSuperController(){
        return sController.getAllSerialSetsInfoForTableViewFromMed();
    }
    public TargetInfoDto getTargetInfoDtoFromSuper(String targetName) {
        return sController.getTargetInfoDtoFromMed(targetName);
    }
    public void initSubTabs(){
        tableViewTabContentController.initAllTablesViews();
        treeViewTabContentController.initTargetsInfoTreeView();
    }
}


