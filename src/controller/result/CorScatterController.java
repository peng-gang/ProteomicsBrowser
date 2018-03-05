package controller.result;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.math3.analysis.function.Log10;
import project.PublicInfo;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by gpeng on 2/16/17.
 */
public class CorScatterController implements Initializable{
    @FXML private Pane pane;

    private ScatterChart<Number, Number> sChart;

    private ArrayList<Double> d1;
    private ArrayList<Double> d2;
    private String t1;
    private String t2;
    private ArrayList<String> group;

    private PublicInfo.ScaleType scaleType;

    @FXML private void save(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Output Scatter Plot");
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

    @FXML private void scaleLog2(ActionEvent event){
        scaleType = PublicInfo.ScaleType.Log2;
        init();
    }

    @FXML private void scaleRegular(ActionEvent event){
        scaleType = PublicInfo.ScaleType.Regular;
        init();
    }

    @FXML private void scaleLog10(ActionEvent event){
        scaleType = PublicInfo.ScaleType.Log10;
        init();
    }

    public void set(String t1, String t2, ArrayList<Double> d1, ArrayList<Double> d2, ArrayList<String> group){
        this.t1 = t1;
        this.t2 = t2;
        this.d1 = d1;
        this.d2 = d2;
        this.group = group;
    }

    public void init(){

        //NumberAxis xAxis = new NumberAxis(18, 22, 0.4);
        NumberAxis xAxis;
        NumberAxis yAxis;
        if(scaleType == PublicInfo.ScaleType.Regular){
            double dMin = Collections.min(d1);

            double min1 = dMin;
            double max1 = Collections.max(d1);
            double seg1 = (max1-min1)/10;
            xAxis = new NumberAxis(min1-seg1, max1+seg1, seg1);

            dMin = Collections.min(d2);
            double min2 = dMin;
            double max2 = Collections.max(d2);
            double seg2 = (max2-min2)/10;
            yAxis = new NumberAxis(min2-seg2, max2+seg2, seg2);

            NumberFormat format = new DecimalFormat("#.#E0");
            xAxis.setTickLabelFormatter(new StringConverter<Number>() {
                @Override
                public String toString(Number object) {
                    return format.format(object.doubleValue());
                }

                @Override
                public Number fromString(String string) {
                    try{
                        return format.parse(string);
                    } catch (ParseException e){
                        e.printStackTrace();
                        return null;
                    }
                }
            });

            yAxis.setTickLabelFormatter(new StringConverter<Number>() {
                @Override
                public String toString(Number object) {
                    return format.format(object.doubleValue());
                }

                @Override
                public Number fromString(String string) {
                    try{
                        return format.parse(string);
                    } catch (ParseException e){
                        e.printStackTrace();
                        return null;
                    }
                }
            });
        } else {
            if(scaleType== PublicInfo.ScaleType.Log2) {
                double dMin = Collections.min(d1);
                if (dMin < 0.001) {
                    dMin = 0.001;
                }
                double min1 = Math.log(dMin) / Math.log(2.0);
                double max1 = Math.log(Collections.max(d1)) / Math.log(2.0);
                double seg1 = (max1 - min1) / 10;
                xAxis = new NumberAxis(min1 - seg1, max1 + seg1, seg1);

                dMin = Collections.min(d2);
                if (dMin < 0.001) {
                    dMin = 0.001;
                }
                double min2 = Math.log(dMin) / Math.log(2.0);
                double max2 = Math.log(Collections.max(d2)) / Math.log(2.0);
                double seg2 = (max2 - min2) / 10;
                yAxis = new NumberAxis(min2 - seg2, max2 + seg2, seg2);
            }else{
                //(scaleType == PublicInfo.ScaleType.Log10)
                double dMin = Collections.min(d1);
                if(dMin < 0.001){
                    dMin = 0.001;
                }
                double min1 = Math.log10(dMin);
                double max1 = Math.log10(Collections.max(d1));
                double seg1 = (max1-min1)/10;
                xAxis = new NumberAxis(min1-seg1, max1+seg1, seg1);

                dMin = Collections.min(d2);
                if(dMin < 0.001){
                    dMin = 0.001;
                }
                double min2 = Math.log10(dMin);
                double max2 = Math.log10(Collections.max(d2));
                double seg2 = (max2-min2)/10;
                yAxis = new NumberAxis(min2-seg2, max2+seg2, seg2);
            }
        }

        //NumberAxis yAxis = new NumberAxis(18, 22, 0.4);
        sChart = new ScatterChart<Number, Number>(xAxis, yAxis);
        pane.getChildren().clear();
        pane.getChildren().add(sChart);

        xAxis.setLabel(t1);
        yAxis.setLabel(t2);

        if(group == null){
            XYChart.Series<Number, Number>  series = new XYChart.Series<>();
            series.setName("Correlation");
            for(int i=0; i<d1.size(); i++){
                double tmp1 = d1.get(i);
                double tmp2 = d2.get(i);
                if(scaleType == PublicInfo.ScaleType.Regular){

                } else{
                    if(tmp1< 0.001){
                        tmp1 = 0.001;
                    }
                    if(tmp2 < 0.001){
                        tmp2 = 0.001;
                    }
                    if(scaleType == PublicInfo.ScaleType.Log2){
                        tmp1 = Math.log(tmp1)/Math.log(2);
                        tmp2 = Math.log(tmp2)/Math.log(2);
                    } else {
                        tmp1 = Math.log10(tmp1);
                        tmp2 = Math.log10(tmp2);
                    }
                }

                XYChart.Data<Number, Number> tmp = new XYChart.Data<>(tmp1, tmp2);
                series.getData().add(tmp);
            }
            sChart.getData().add(series);
            sChart.setLegendVisible(false);
            return;
        }

        Set<String> groupName = new TreeSet<>();
        groupName.addAll(group);

        //int numGroup = groupName.size();
        for(String id : groupName){
            XYChart.Series<Number, Number>  series = new XYChart.Series<>();
            series.setName(id);
            for(int i=0; i< d1.size();i++){
                if(group.get(i).equals(id)){
                    double tmp1 = d1.get(i);
                    double tmp2 = d2.get(i);
                    if(scaleType == PublicInfo.ScaleType.Regular){

                    } else{
                        if(tmp1< 0.001){
                            tmp1 = 0.001;
                        }
                        if(tmp2 < 0.001){
                            tmp2 = 0.001;
                        }
                        if(scaleType == PublicInfo.ScaleType.Log2){
                            tmp1 = Math.log(tmp1)/Math.log(2);
                            tmp2 = Math.log(tmp2)/Math.log(2);
                        } else {
                            tmp1 = Math.log10(tmp1);
                            tmp2 = Math.log10(tmp2);
                        }
                    }

                    XYChart.Data<Number, Number> tmp = new XYChart.Data<>(tmp1, tmp2);
                    series.getData().add(tmp);
                }
            }
            sChart.getData().add(series);
        }

        sChart.setLegendSide(Side.RIGHT);
        //sChart.setLegendVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scaleType = PublicInfo.ScaleType.Log2;
    }
}
