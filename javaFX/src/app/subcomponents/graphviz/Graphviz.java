package app.subcomponents.graphviz;

import app.SuperController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import output.AllGraphDto;
import output.TargetInfoDto;

import java.io.*;

public class Graphviz  {
    private SuperController SController;
    @FXML private ImageView myImage;
    @FXML private TextField dotFileTextField;
    @FXML private TextField pngFileTextField;

    public void setSuperController(SuperController sControllerIn) {
        SController = sControllerIn;
    }

    public void getVisualGraphImage(String savingFolder,String fileName) throws IOException {
        AllGraphDto graphDto = SController.getAllGraphDtoFromMed();

        File dir = new File(savingFolder);
        dir.mkdirs();

        File file = new File(dir, "src.dot");//todo check what is dot and why not viz
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        FileWriter myWriter = new FileWriter(file.getAbsolutePath(), true);
        myWriter.write("digraph Graphviz {"+"\n");

        for (TargetInfoDto currentTarget : graphDto.getAllTargetsDto()) {
            for (String currentTargetNeighborName : currentTarget.getOutTargetListNames()) {
                myWriter.write(currentTarget.getTargetName()+" -> "+ currentTargetNeighborName+"\n");
            }
        }
        myWriter.write("}");
        myWriter.close();

        String[] command = {"dot", "-Tpng", file.getAbsolutePath(), "-o", dir.getAbsolutePath() + "/to.png"};
        ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(false);
        Process p = processBuilder.start();
        try {
            p.waitFor();
        } catch (InterruptedException ignored) {}

        File visualGraphImg = new File(dir,"to.png");
        InputStream isImage = new FileInputStream(visualGraphImg);

        Platform.runLater(()->{
           myImage.setImage(new Image(isImage));
           dotFileTextField.setText(file.getAbsolutePath());
           pngFileTextField.setText(visualGraphImg.getAbsolutePath());
        });
    }
}
