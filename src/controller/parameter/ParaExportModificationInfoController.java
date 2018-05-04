package controller.parameter;

import data.Protein;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

/**
 * Created by gpeng on 5/22/17.
 */
public class ParaExportModificationInfoController implements Initializable{
    @FXML private TextField txtModiCutoff;
    @FXML private TextField txtModiOutputFile;

    private File outputFile = null;

    private Protein protein;

    @FXML private void getOutputFile(ActionEvent event){
        Stage stage = (Stage) txtModiCutoff.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Modification Output");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Output File", "*.txt")
        );

        outputFile = fileChooser.showSaveDialog(stage);
        if(outputFile != null){
            txtModiOutputFile.setText(String.valueOf(outputFile.getAbsoluteFile()));
        }
    }

    @FXML private void export(ActionEvent event){
        String txtCutoff = txtModiCutoff.getText();
        if(txtCutoff.equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modification Export");
            alert.setHeaderText(null);
            alert.setContentText("Please set cutoff(0.0~1.0) for Modification Export.");
            alert.showAndWait();
            return;
        }
        double cutoff = Double.parseDouble(txtModiCutoff.getText());
        if(cutoff<0 || cutoff>1){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modification Export");
            alert.setHeaderText(null);
            alert.setContentText("The cutoff should be between 0 and 1.");
            txtModiCutoff.setText("");
            alert.showAndWait();
            return;
        }
        if(outputFile==null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modification Export");
            alert.setHeaderText(null);
            alert.setContentText("Please specify output file.");
            alert.showAndWait();
            return;
        }

        if(cutoff==0){
            cutoff=-9999999;
        }

        //start to output
        FileWriter fileWriter= null;
        try {
            fileWriter = new FileWriter(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        String output = protein.getModiInfoAll(cutoff);
        try {
            bufferedWriter.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(output);
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Stage stage = (Stage) txtModiCutoff.getScene().getWindow();
        stage.close();
    }

    public void setProtein(Protein protein){
        this.protein = protein;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("set parameters for modification output ");

        UnaryOperator<TextFormatter.Change> filter = new UnaryOperator<TextFormatter.Change>() {

            @Override
            public TextFormatter.Change apply(TextFormatter.Change t) {

                if (t.isReplaced())
                    if(t.getText().matches("[^0-9]"))
                        t.setText(t.getControlText().substring(t.getRangeStart(), t.getRangeEnd()));


                if (t.isAdded()) {
                    if (t.getControlText().contains(".")) {
                        if (t.getText().matches("[^0-9]")) {
                            t.setText("");
                        }
                    } else if (t.getText().matches("[^0-9.]")) {
                        t.setText("");
                    }
                }

                return t;
            }
        };

        txtModiCutoff.setTextFormatter(new TextFormatter<>(filter));
        txtModiCutoff.setText("0.9");
    }
}
