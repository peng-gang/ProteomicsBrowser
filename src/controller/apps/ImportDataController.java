package controller.apps;

import controller.MainController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.shape.Arc;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * Created by gpeng on 2/12/17.
 */
public class ImportDataController implements Initializable {

    @FXML private TextField txtPtDataFile;
    @FXML private TextField txtSpDataFile;
    @FXML private Button btnPtDataFile;
    @FXML private Button btnSpDataFile;
    @FXML private ComboBox<String> cmbType;
    @FXML private CheckBox checkIncludePepMultiProtein;

    private File ptFile = null;
    private File spFile = null;

    private boolean submitted = false;

    public File getPtFile() { return ptFile; }
    public File getSpFile() { return  spFile; }
    public String getType() { return cmbType.getSelectionModel().getSelectedItem().toString(); }
    public boolean getSubmitted() { return submitted; }
    public boolean getIncludePepInMultiProtein() { return checkIncludePepMultiProtein.isSelected(); }


    @FXML private void getPtDataFile(ActionEvent event){
        Stage stage = (Stage) btnPtDataFile.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Peptide Data");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Peptide Data", "*.csv")
        );

        ptFile = fileChooser.showOpenDialog(stage);
        if(ptFile != null){
            txtPtDataFile.setText(String.valueOf(ptFile.getAbsoluteFile()));
        }
    }

    @FXML private void getSpDataFile(ActionEvent event){
        Stage stage = (Stage) btnSpDataFile.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sample Data");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Sample Data", "*.csv")
        );
        spFile = fileChooser.showOpenDialog(stage);
        if(spFile != null){
            txtSpDataFile.setText(String.valueOf(spFile.getAbsoluteFile()));
        }
    }

    @FXML private void importData(ActionEvent event){
        if(cmbType.getSelectionModel().getSelectedItem().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Organism Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select the type of organism of input data");
            alert.showAndWait();
            cmbType.requestFocus();
            return;
        }

        if(ptFile == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peptide Data Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a peptide data file");
            alert.showAndWait();
            btnPtDataFile.requestFocus();
            return;
        }
        submitted = true;
        Stage stage = (Stage) btnPtDataFile.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("Import Data Initialization");
        submitted = false;
        cmbType.getItems().add("");
        cmbType.getSelectionModel().select(0);

        File dbFolder = new File("db/");
        File[] files = dbFolder.listFiles();
        ArrayList<String> types = new ArrayList<>();
        for(File file : files){
            if(file.isFile()){
                String fileName = file.getName();
                if(fileName.charAt(0) != '.' && fileName.substring(fileName.length()-5).equals("fasta")){
                    types.add(fileName.substring(0, fileName.length()-6));
                }
            }
            System.out.println(file.getName());
        }

        if(types.size()==0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Database");
            alert.setHeaderText(null);
            alert.setContentText("There is no database file in db folder");
            alert.showAndWait();
            return;
        }
        cmbType.getItems().addAll(types);



        /*
        stage = (Stage) btnSpDataFile.getScene().getWindow();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

            }
        });
        */
    }


}
