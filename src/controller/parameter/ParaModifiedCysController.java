package controller.parameter;

import data.Modification;
import data.SampleGroup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by gpeng on 9/10/17.
 */
public class ParaModifiedCysController{
    private File textFile = null;
    //private File figureFile = null;
    private String sample;
    private int numResidual;
    private ArrayList<String> samples;

    private SampleGroup sampleGroup;


    @FXML private ComboBox<String> cmbSample;
    @FXML private TextField textNumResidual;
    @FXML private TextField textTextFile;
    //@FXML private TextField textFigureFile;
    @FXML private Button btnTextFile;
    //@FXML private Button btnFigureFile;
    @FXML private CheckBox cbAcetylation;
    @FXML private CheckBox cbCarbamidomethylation;
    @FXML private CheckBox cbPhosphorylation;
    @FXML private CheckBox cbOxidation;


    /*
    @FXML private void getFigureFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Modified Cys");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Output Figure", "*.png")
        );

        Stage stage = (Stage) cmbSample.getScene().getWindow();
        figureFile = fileChooser.showSaveDialog(stage);
        textFigureFile.setText(figureFile.toString());
    }
    */

    @FXML private void getTextFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Modified Cys");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Output Text", "*.txt")
        );

        Stage stage = (Stage) cmbSample.getScene().getWindow();
        textFile = fileChooser.showSaveDialog(stage);
        textTextFile.setText(textFile.toString());
    }

    @FXML private void export(ActionEvent event){
        if(textFile == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Residual Output");
            alert.setHeaderText(null);
            alert.setContentText("Please set output residual file");
            alert.showAndWait();
            btnTextFile.requestFocus();
            return;
        }

        /*
        if(figureFile == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Image Output");
            alert.setHeaderText(null);
            alert.setContentText("Please set output image file");
            alert.showAndWait();
            btnFigureFile.requestFocus();
            return;
        }
        */

        sample = cmbSample.getSelectionModel().getSelectedItem().toString();
        if(sample.equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sample Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a sample for output");
            alert.showAndWait();
            cmbSample.requestFocus();
            return;
        }

        numResidual = Integer.parseInt(textNumResidual.getText());
        if(numResidual < 1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Number of residual");
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
            alert.setTitle("Modification Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select one type of modification at least");
            alert.showAndWait();
            cbAcetylation.requestFocus();
            return;
        }

        sampleGroup.outputModiResText(textFile, sample, modiSelect, numResidual);

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
}
