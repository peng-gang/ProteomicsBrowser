package controller.parameter;

import data.Modification;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Created by gpeng on 9/13/17.
 */
public class ParaModifiedCysFigureController implements Initializable{
    private String sample;
    private int numResidual;
    private ArrayList<String> samples;

    private SampleGroup sampleGroup;
    private ArrayList<Map<Character, Double>> residualFreq;

    private String modificationSel;

    public ArrayList<Map<Character, Double>> getResidualFreq() {return residualFreq; }


    @FXML private ComboBox<String> cmbSample;
    @FXML private TextField textNumResidual;
    @FXML private VBox vbModification;
    @FXML private Button btnShowFigure;


    @FXML private void export(ActionEvent event){

        sample = cmbSample.getSelectionModel().getSelectedItem().toString();
        if(sample.equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sample Selection (Figure)");
            alert.setHeaderText(null);
            alert.setContentText("Please select a sample for output");
            alert.showAndWait();
            cmbSample.requestFocus();
            return;
        }

        numResidual = Integer.parseInt(textNumResidual.getText());
        if(numResidual < 1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Number of residual (Figure)");
            alert.setHeaderText(null);
            alert.setContentText("The number of residual should be larger than 0");
            alert.showAndWait();
            textNumResidual.requestFocus();
            return;
        }


        if(modificationSel == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modification Selection (Figure)");
            alert.setHeaderText(null);
            alert.setContentText("Please select one type of modification");
            alert.showAndWait();
            return;
        }

        ArrayList<String> modiSelect = new ArrayList<>();
        modiSelect.add(modificationSel);
        residualFreq = sampleGroup.outputModiResFreq(sample, modiSelect, numResidual);

        Stage stage = (Stage) textNumResidual.getScene().getWindow();
        stage.close();
    }


    public void init(SampleGroup sampleGroup){
        this.sampleGroup = sampleGroup;
        samples =  new ArrayList<>(sampleGroup.getSampleId());
        btnShowFigure.setDisable(true);

        cmbSample.getItems().add("");
        cmbSample.getItems().addAll(samples);

        modificationSel = null;

        cmbSample.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.equals("")){
                    vbModification.getChildren().clear();
                    btnShowFigure.setDisable(true);
                    return;
                }

                ToggleGroup group = new ToggleGroup();

                ArrayList<String> modiTypeAll = new ArrayList<String>(sampleGroup.getModificationTypeSample(newValue));
                int numModiTypeAll = modiTypeAll.size();
                int idx = 0;
                for(int i=0; i<(int) (numModiTypeAll/3); i++){
                    HBox hBox = new HBox();
                    hBox.setPrefWidth(400);
                    hBox.setSpacing(20);
                    hBox.setAlignment(Pos.CENTER_LEFT);

                    for(int j=0; j<3; j++){
                        RadioButton radioButton = new RadioButton(modiTypeAll.get(idx));
                        radioButton.setUserData(modiTypeAll.get(idx));
                        radioButton.setPrefWidth(160);
                        radioButton.setToggleGroup(group);
                        hBox.getChildren().add(radioButton);
                        idx++;
                    }
                    vbModification.getChildren().addAll(hBox);
                }


                int residual = numModiTypeAll % 3;
                if(residual>0){
                    HBox hBox = new HBox();
                    hBox.setPrefWidth(400);
                    hBox.setSpacing(20);
                    hBox.setAlignment(Pos.CENTER_LEFT);

                    for(int i=0; i<residual;i++){
                        RadioButton radioButton = new RadioButton(modiTypeAll.get(idx));
                        radioButton.setUserData(modiTypeAll.get(idx));
                        radioButton.setPrefWidth(160);
                        radioButton.setToggleGroup(group);
                        hBox.getChildren().add(radioButton);
                        idx++;
                    }
                    vbModification.getChildren().addAll(hBox);
                }


                group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                    @Override
                    public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                        if(group.getSelectedToggle() != null){
                            modificationSel = group.getSelectedToggle().getUserData().toString();
                        }
                    }
                });
                btnShowFigure.setDisable(false);
            }
        });

        textNumResidual.setText("15");

        textNumResidual.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.matches("\\d*")){
                    textNumResidual.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        residualFreq = null;
    }
}
