package controller.result;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VolcanoPlotController {
    @FXML
    private AnchorPane pane;
    private ArrayList<Double> pv;
    private ArrayList<Double> ratio;

    @FXML private void save(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Output Boxplot");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Output Figure", "*.png")
        );

        Stage stage = (Stage) pane.getScene().getWindow();
        File figureFile = fileChooser.showSaveDialog(stage);

        double pixelScale = 3.0;
        WritableImage image = new WritableImage((int)(pane.getWidth()*pixelScale), (int)(pane.getHeight()*pixelScale));
        SnapshotParameters sp = new SnapshotParameters();
        sp.setTransform(Transform.scale(pixelScale, pixelScale));
        image = pane.snapshot(sp, image);

        try{
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", figureFile);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @FXML private void exit(ActionEvent event){
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

    public void init(ArrayList<Double> pv, ArrayList<Double> ratio){
        this.pv = pv;
        this.ratio = ratio;

        volcanoPlot();
    }

    private void volcanoPlot(){
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Log2 Ratio");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("-log10 P value");

        ScatterChart<Double, Double> scatterChart = new ScatterChart(xAxis, yAxis);

        XYChart.Series dataSeries = new XYChart.Series();

        for(int i=0; i<pv.size(); i++){
            dataSeries.getData().add(new XYChart.Data( Math.log(ratio.get(i)) / Math.log(2.0), -Math.log10(pv.get(i))));
        }

        scatterChart.getData().add(dataSeries);

        pane.getChildren().add(scatterChart);
    }
}
