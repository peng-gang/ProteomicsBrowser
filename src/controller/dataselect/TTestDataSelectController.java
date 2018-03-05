package controller.dataselect;

import data.SampleGroup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.RangeSlider;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;


/**
 * Created by gpeng on 2/14/17.
 */
public class TTestDataSelectController implements Initializable{
    @FXML private ComboBox<String> combGroup;
    @FXML private ComboBox<String> combData;
    @FXML private Button btnSubmit;
    @FXML private HBox hbSlider;

    private RangeSlider rslinder;
    private Label lbLow;
    private Label lbHigh;

    private  ArrayList<Double> val;

    private ArrayList<String> numInfoName;
    private ArrayList<String> strInfoName;

    private boolean submitted;

    public boolean getSubmitted() {return submitted; }

    public String getGroupId() { return combGroup.getValue(); }
    public String getDataId() { return combData.getValue(); }
    public Double getLow() {
        if(!rslinder.isVisible())
            return null;
        else
            return rslinder.getLowValue();
    }
    public Double getHigh() {
        if(!rslinder.isVisible())
            return null;
        else
            return rslinder.getHighValue();
    }

    @FXML private void submit(ActionEvent event){
        submitted = true;
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.close();
    }

    private SampleGroup sampleGroup;
    private String type;

    public void set(SampleGroup sampleGroup){
        this.sampleGroup = sampleGroup;
        numInfoName = new ArrayList<>(sampleGroup.getNumInfoName());
        strInfoName = new ArrayList<>(sampleGroup.getStrInfoName());
    }

    public void init(String type){
        this.type = type;
        combGroup.getItems().addAll(numInfoName);
        combGroup.getItems().addAll(strInfoName);
        if(type.equals("Peptide")){
            ArrayList<String> pepId = new ArrayList<>(sampleGroup.getPepId());
            combData.getItems().add("ALL");
            combData.getItems().addAll(pepId);
        } else {
            ArrayList<String> proteinId = new ArrayList<>(sampleGroup.getProteinId());
            combData.getItems().add("ALL");
            combData.getItems().addAll(proteinId);
        }

        combGroup.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(numInfoName.contains(newValue)){
                    val = new ArrayList<>(sampleGroup.getNumInfo(newValue));
                    Collections.sort(val);
                    int num = val.size();
                    Double min = val.get(0);
                    Double max = val.get(num-1);

                    Double st = val.get(num/3);
                    Double ed = val.get(num*2/3);

                    rslinder.setVisible(true);
                    rslinder.setMax(max);
                    rslinder.setMin(min);

                    rslinder.setHighValue(ed);
                    rslinder.setLowValue(st);

                    lbLow.setVisible(true);
                    lbHigh.setVisible(true);
                    Integer numLow = num/3;
                    Integer numHigh = numLow;
                    lbLow.setText(numLow.toString());
                    lbHigh.setText(numHigh.toString());

                } else {
                    rslinder.setVisible(false);
                    lbLow.setVisible(false);
                    lbHigh.setVisible(false);
                }
            }
        });

        rslinder.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Integer num = 0;
                for(Double tmp : val){
                    if(tmp <= newValue.doubleValue()){
                        num++;
                    }
                }
                lbLow.setText(num.toString());
            }
        });

        rslinder.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Integer num = 0;
                for(Double tmp : val){
                    if(tmp > newValue.doubleValue()){
                        num++;
                    }
                }
                lbHigh.setText(num.toString());
            }
        });

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rslinder = new RangeSlider();
        rslinder.setVisible(false);
        rslinder.setShowTickMarks(true);
        rslinder.setShowTickLabels(true);

        lbLow = new Label();
        lbLow.setAlignment(Pos.CENTER_RIGHT);
        lbLow.setPrefWidth(40);
        lbHigh = new Label();
        lbHigh.setAlignment(Pos.CENTER_LEFT);
        lbHigh.setPrefWidth(40);
        hbSlider.getChildren().addAll(lbLow, rslinder, lbHigh);

        submitted = false;
    }
}
