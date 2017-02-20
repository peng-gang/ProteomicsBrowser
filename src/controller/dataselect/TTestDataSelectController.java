package controller.dataselect;

import data.SampleGroup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.SpinnerValueFactory;
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
    @FXML private VBox vbGroup;

    private RangeSlider rslinder;

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
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.close();
    }

    private SampleGroup sampleGroup;
    private String type;

    public void set(SampleGroup sampleGroup){
        this.sampleGroup = sampleGroup;
    }

    public void init(String type){
        this.type = type;
        ArrayList<String> numInfoName = new ArrayList<>(sampleGroup.getNumInfoName());
        ArrayList<String> strInfoName = new ArrayList<>(sampleGroup.getStrInfoName());
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
                    ArrayList<Double> val = new ArrayList<>(sampleGroup.getNumInfo(newValue));
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

                } else {
                    rslinder.setVisible(false);
                }
            }
        });

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rslinder = new RangeSlider();
        rslinder.setVisible(false);
        rslinder.setShowTickMarks(true);
        rslinder.setShowTickLabels(true);
        vbGroup.getChildren().addAll(rslinder);
    }
}
