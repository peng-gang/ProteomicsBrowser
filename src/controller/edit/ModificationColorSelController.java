package controller.edit;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Map;

/**
 * Created by gpeng on 10/11/18.
 */
public class ModificationColorSelController {
    @FXML private ComboBox<String> cmbModification;
    @FXML private ColorPicker colorPicker;

    private Map<String, Color> colors;

    public void init(Map<String, Color> colors){
        this.colors = colors;
        cmbModification.getItems().addAll(this.colors.keySet());
        cmbModification.getSelectionModel().select(0);
        colorPicker.setValue(this.colors.get(cmbModification.getValue()));

        cmbModification.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                colorPicker.setValue(colors.get(newValue));
            }
        });
    }

    @FXML private void submit(ActionEvent event){
        colors.put(cmbModification.getValue(), colorPicker.getValue());

        Stage stage = (Stage) cmbModification.getScene().getWindow();
        stage.close();
    }

    @FXML private void cancel(ActionEvent event){
        Stage stage = (Stage) cmbModification.getScene().getWindow();
        stage.close();
    }
}
