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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    private String selProteinFileName;

    private SampleGroup sampleGroup;
    private ArrayList<Map<Character, Double>> residualFreq;

    private String modificationSel;

    public ArrayList<Map<Character, Double>> getResidualFreq() {return residualFreq; }


    @FXML private ComboBox<String> cmbSample;
    @FXML private TextField textNumResidual;
    @FXML private VBox vbModification;
    @FXML private Button btnShowFigure;
    @FXML private ComboBox<String> comboProtein;
    @FXML private Label lblSelProtein;


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

        ArrayList<String> selProteinName = new ArrayList<>();

        if(selProteinFileName==null || selProteinFileName.equals("")){
            residualFreq = sampleGroup.outputModiResFreq(sample, modiSelect, numResidual, null);
        } else {
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(selProteinFileName);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String fline = null;
                while((fline = bufferedReader.readLine()) != null){
                    selProteinName.add(fline);
                }

                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            residualFreq = sampleGroup.outputModiResFreq(sample, modiSelect, numResidual, selProteinName);
        }

        Stage stage = (Stage) textNumResidual.getScene().getWindow();
        stage.close();
    }


    public void init(SampleGroup sampleGroup){
        this.sampleGroup = sampleGroup;
        selProteinFileName = null;
        samples =  new ArrayList<>(sampleGroup.getSampleId());
        btnShowFigure.setDisable(true);

        cmbSample.getItems().add("");
        cmbSample.getItems().addAll(samples);

        modificationSel = null;

        comboProtein.getItems().addAll("All Proteins");
        comboProtein.getItems().addAll("Select Proteins:");
        comboProtein.setValue("All Proteins");

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

        comboProtein.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.equals("All Proteins")){
                    lblSelProtein.setText("");
                    selProteinFileName = null;
                } else {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Select Proteins");
                    fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                    fileChooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("Protein Name File", "*.txt")
                    );

                    Stage stage = (Stage) cmbSample.getScene().getWindow();
                    File selProteinNameFile = fileChooser.showOpenDialog(stage);
                    if(selProteinNameFile == null){
                        lblSelProtein.setText("");
                        comboProtein.setValue("All Proteins");
                        return;
                    }
                    selProteinFileName = selProteinNameFile.toString();
                    if(selProteinFileName.equals("")){
                        lblSelProtein.setText("");
                        comboProtein.setValue("All Proteins");
                        return;
                    }

                    lblSelProtein.setText(selProteinFileName);
                }
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
