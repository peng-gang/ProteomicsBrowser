package controller.filter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Created by gpeng on 6/29/17.
 */
public class PepFilterController {
    @FXML private Button btnSubmit;

    @FXML private void submit(ActionEvent event){
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.close();
    }

}
