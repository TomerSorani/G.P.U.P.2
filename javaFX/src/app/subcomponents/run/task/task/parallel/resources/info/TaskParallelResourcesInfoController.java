package app.subcomponents.run.task.task.parallel.resources.info;

import app.subcomponents.run.task.RunTaskTabController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import output.ThreadInfoRow;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Collection;

public class TaskParallelResourcesInfoController {
    private RunTaskTabController runTaskTabCont;

    @FXML private TableView<ThreadInfoRow> taskParallelTableView;
    @FXML private TableColumn<ThreadInfoRow,String> absoluteTimestampColumn;
    @FXML private TableColumn<ThreadInfoRow,String> timeFromStartingTheTaskColumn;
    @FXML private TableColumn<ThreadInfoRow,String> amountOfAvailableThreadsColumn;
    @FXML private TableColumn<ThreadInfoRow,String> amountOfThreadsInTheQueueColumn;


    public void setRunTaskTabController(RunTaskTabController runTaskTabCon){
        runTaskTabCont=runTaskTabCon;
    }
     public void addThreadToTableView(ThreadInfoRow threadsInfoSetIn){

        final ObservableList<ThreadInfoRow> data = FXCollections.observableList(new ArrayList<>((Collection<? extends ThreadInfoRow>) threadsInfoSetIn));
         absoluteTimestampColumn.setCellValueFactory(
                new PropertyValueFactory<>("absoluteTimestamp")
        );

         timeFromStartingTheTaskColumn.setCellValueFactory(
                new PropertyValueFactory<>("timeFromStartingTheTask")
        );
         amountOfAvailableThreadsColumn.setCellValueFactory(
                 new PropertyValueFactory<>("amountOfAvailableThreads")
         );
         amountOfThreadsInTheQueueColumn.setCellValueFactory(
                 new PropertyValueFactory<>("amountOfThreadsInTheQueue")
         );

         taskParallelTableView.setItems(data);
         taskParallelTableView.getColumns().addAll(absoluteTimestampColumn,timeFromStartingTheTaskColumn,
                 amountOfAvailableThreadsColumn,amountOfThreadsInTheQueueColumn);
    }
}
