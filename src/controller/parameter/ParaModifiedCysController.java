package controller.parameter;

import data.Modification;
import data.SampleGroup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
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

    private String modificationSel;

    private String selProteinFileName;


    @FXML private ComboBox<String> cmbSample;
    @FXML private TextField textNumResidual;
    @FXML private TextField textTextFile;
    @FXML private Button btnTextFile;
    @FXML private VBox vbModification;
    @FXML private Button btnExportFile;
    @FXML private ComboBox<String> comboProtein;
    @FXML private Label lblSelProtein;


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
        if(textFile == null){
            textTextFile.setText("");
            return;
        }
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

        if(modificationSel == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modification Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select one type of modification");
            alert.showAndWait();
            return;
        }

        ArrayList<String> modiSelect = new ArrayList<>();
        modiSelect.add(modificationSel);
        ArrayList<String> selProteinName = new ArrayList<>();

        if(selProteinFileName==null || selProteinFileName.equals("")){
            sampleGroup.outputModiResText(textFile, sample, modiSelect, numResidual, null);
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

            sampleGroup.outputModiResText(textFile, sample, modiSelect, numResidual, selProteinName);
        }

        Stage stage = (Stage) textNumResidual.getScene().getWindow();
        stage.close();
    }


    public void init(SampleGroup sampleGroup){
        this.sampleGroup = sampleGroup;
        selProteinFileName = null;
        samples =  new ArrayList<>(sampleGroup.getSampleId());
        btnExportFile.setDisable(true);

        cmbSample.getItems().add("");
        cmbSample.getItems().addAll(samples);
        textNumResidual.setText("15");

        modificationSel = null;


        comboProtein.getItems().addAll("All Proteins");
        comboProtein.getItems().addAll("Select Proteins:");
        comboProtein.setValue("All Proteins");

        cmbSample.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.equals("")){
                    vbModification.getChildren().clear();
                    btnExportFile.setDisable(true);
                    return;
                }

                ToggleGroup group = new ToggleGroup();
                ArrayList<String> modiTypeAll = new ArrayList<String>(sampleGroup.getModificationTypeSample(newValue));
                int numModiTypeAll = modiTypeAll.size();
                int idx = 0;
                for(int i=0; i<(int) (numModiTypeAll/3); i++){
                    HBox hBox = new HBox();
                    hBox.setPrefHeight(400);
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
                if(residual > 0){
                    HBox hBox = new HBox();
                    hBox.setPrefHeight(400);
                    hBox.setSpacing(20);
                    hBox.setAlignment(Pos.CENTER_LEFT);

                    for(int i=0; i<residual; i++){
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

                btnExportFile.setDisable(false);
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
