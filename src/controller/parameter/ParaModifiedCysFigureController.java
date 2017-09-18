package controller.parameter;

import data.Modification;
import data.SampleGroup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by gpeng on 9/13/17.
 */
public class ParaModifiedCysFigureController implements Initializable{
    private String sample;
    private int numResidual;
    private ArrayList<String> samples;

    private SampleGroup sampleGroup;
    private ArrayList<Map<Character, Double>> residualFreq;


    public ArrayList<Map<Character, Double>> getResidualFreq() {return residualFreq; }


    @FXML
    private ComboBox<String> cmbSample;
    @FXML private TextField textNumResidual;
    @FXML private CheckBox cbAcetylation;
    @FXML private CheckBox cbCarbamidomethylation;
    @FXML private CheckBox cbPhosphorylation;
    @FXML private CheckBox cbOxidation;


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

        ArrayList<Modification.ModificationType> modiSelect = new ArrayList<>();
        if(cbAcetylation.isSelected()){
            modiSelect.add(Modification.ModificationType.Acetylation);
        }

        if(cbCarbamidomethylation.isSelected()){
            modiSelect.add(Modification.ModificationType.Carbamidomethylation);
        }

        if(cbOxidation.isSelected()){
            modiSelect.add(Modification.ModificationType.Oxidation);
        }

        if(cbPhosphorylation.isSelected()){
            modiSelect.add(Modification.ModificationType.Phosphorylation);
        }

        if(modiSelect.size()==0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modification Selection (Figure)");
            alert.setHeaderText(null);
            alert.setContentText("Please select one type of modification at least");
            alert.showAndWait();
            cbAcetylation.requestFocus();
            return;
        }

        residualFreq = sampleGroup.outputModiResFreq(sample, modiSelect, numResidual);

        Stage stage = (Stage) textNumResidual.getScene().getWindow();
        stage.close();
    }


    public void init(SampleGroup sampleGroup){
        this.sampleGroup = sampleGroup;
        samples =  new ArrayList<>(sampleGroup.getSampleId());

        cmbSample.getItems().add("");
        cmbSample.getItems().addAll(samples);
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
