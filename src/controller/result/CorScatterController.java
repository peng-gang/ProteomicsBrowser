package controller.result;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

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

    public void set(String t1, String t2, ArrayList<Double> d1, ArrayList<Double> d2){
        this.t1 = t1;
        this.t2 = t2;
        this.d1 = d1;
        this.d2 = d2;
    }

    public void init(){
        double min1 = Math.log(Collections.min(d1));
        double max1 = Math.log(Collections.max(d1));
        double seg1 = (max1-min1)/10;
        NumberAxis xAxis = new NumberAxis(min1-seg1, max1+seg1, seg1);
        //NumberAxis xAxis = new NumberAxis(18, 22, 0.4);

        double min2 = Math.log(Collections.min(d2));
        double max2 = Math.log(Collections.max(d2));
        double seg2 = (max2-min2)/10;
        NumberAxis yAxis = new NumberAxis(min2-seg2, max2+seg2, seg2);
        //NumberAxis yAxis = new NumberAxis(18, 22, 0.4);
        sChart = new ScatterChart<Number, Number>(xAxis, yAxis);

        pane.getChildren().add(sChart);

        xAxis.setLabel(t1);
        yAxis.setLabel(t2);

        XYChart.Series<Number, Number> series = new XYChart.Series();
        series.setName("Correlation");
        for(int i =0; i< d1.size();i++){
            double tmp1 = Math.log(d1.get(i));
            double tmp2 = Math.log(d2.get(i));
            XYChart.Data<Number, Number> tmp = new XYChart.Data<>(tmp1, tmp2);
            series.getData().add(tmp);
        }
        sChart.getData().addAll(series);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
