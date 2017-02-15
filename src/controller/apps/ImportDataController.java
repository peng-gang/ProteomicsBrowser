package controller.apps;

import controller.MainController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by gpeng on 2/12/17.
 */
public class ImportDataController implements Initializable {

    @FXML private TextField txtPtDataFile;
    @FXML private TextField txtSpDataFile;
    @FXML private Button btnPtDataFile;
    @FXML private Button btnSpDataFile;

    private File ptFile = null;
    private File spFile = null;

    public File getPtFile() { return ptFile; }
    public File getSpFile() { return  spFile; }


    @FXML private void getPtDataFile(ActionEvent event){
        Stage stage = (Stage) btnPtDataFile.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Proteomics Data");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Proteomics Data", "*.csv")
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
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Sample Data", "*.csv")
        );
        spFile = fileChooser.showOpenDialog(stage);
        if(spFile != null){
            txtSpDataFile.setText(String.valueOf(spFile.getAbsoluteFile()));
        }
    }

    @FXML private void importData(ActionEvent event){
        Stage stage = (Stage) btnPtDataFile.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("Import Data Initialization");

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
