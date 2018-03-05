package controller.dataselect;

import data.SampleGroup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.controlsfx.control.RangeSlider;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by gpeng on 2/16/17.
 */
public class CorScatterDataSelectController {
    @FXML private ComboBox<String> combData1;
    @FXML private ComboBox<String> combData2;
    @FXML private ComboBox<String> combCategory;
    @FXML private HBox hbCategory;
    @FXML private Button btnSubmit;

    private RangeSlider rslinder;
    private Label lbLow;
    private Label lbHigh;

    private ArrayList<String> numInfoName ;
    private ArrayList<String> strInfoName ;
    private ArrayList<Double> val;
    private boolean submitted;

    public String getData1Id() { return combData1.getValue();}
    public String getData2Id() { return combData2.getValue();}
    public String getCategory() {return combCategory.getValue();}
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
        if(combData1.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Correlation Data Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a data in Data1.");
            return;
        }

        if(combData2.getItems() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Correlation Data Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a data in Data2.");
            return;
        }
        submitted = true;
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.close();
    }

    private SampleGroup sampleGroup;
    private String type;

    public boolean getSubmitted() {return submitted;}

    public void set(SampleGroup sampleGroup) {
        this.sampleGroup = sampleGroup;
        numInfoName = new ArrayList<>(sampleGroup.getNumInfoName());
        strInfoName = new ArrayList<>(sampleGroup.getStrInfoName());
    }

    public void init(String type){
        this.type = type;
        submitted = false;
        combCategory.getItems().add("NULL");
        combCategory.getItems().addAll(numInfoName);
        combCategory.getItems().addAll(strInfoName);
        combCategory.setValue("NULL");

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
        hbCategory.getChildren().addAll(lbLow, rslinder, lbHigh);

        combCategory.valueProperty().addListener(new ChangeListener<String>() {
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

                    Integer numLow = num/3;
                    Integer numHigh = numLow;
                    lbLow.setVisible(true);
                    lbLow.setVisible(true);
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

        if(type.equals("Peptide")){
            ArrayList<String> pepId = new ArrayList<>(sampleGroup.getPepId());
            ArrayList<String> pepIdSort = new ArrayList<>(pepId);
            Collections.sort(pepIdSort);
            combData1.getItems().addAll(pepIdSort);
            combData2.getItems().addAll(pepIdSort);
        } else {
            ArrayList<String> proteinId = new ArrayList<>(sampleGroup.getProteinId());
            ArrayList<String> proteinIdSort = new ArrayList<>(proteinId);
            Collections.sort(proteinIdSort);
            combData1.getItems().addAll(proteinIdSort);
            combData2.getItems().addAll(proteinIdSort);
        }
    }
}
