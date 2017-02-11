package controller.data;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by gpeng on 2/10/17.
 */
public class ProteomicsDataController implements Initializable {

    @FXML private TableView tbvProteomicsData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("Proteomics Data Initialization");
    }
}
