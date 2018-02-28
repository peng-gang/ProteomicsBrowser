package controller.dataselect;

import data.SampleGroup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Created by gpeng on 2/16/17.
 */
public class CorScatterDataSelectController {
    @FXML private ComboBox<String> combData1;
    @FXML private ComboBox<String> combData2;
    @FXML private Button btnSubmit;

    public String getData1Id() { return combData1.getValue();}
    public String getData2Id() { return combData2.getValue();}

    @FXML private void submit(ActionEvent event){
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.close();
    }

    private SampleGroup sampleGroup;
    private String type;

    public void set(SampleGroup sampleGroup) { this.sampleGroup = sampleGroup; }

    public void init(String type){
        this.type = type;
        if(type.equals("Peptide")){
            ArrayList<String> pepId = new ArrayList<>(sampleGroup.getPepId());
            combData1.getItems().addAll(pepId);
            combData2.getItems().addAll(pepId);
        } else {
            ArrayList<String> proteinId = new ArrayList<>(sampleGroup.getProteinId());
            combData1.getItems().addAll(proteinId);
            combData2.getItems().addAll(proteinId);
        }
    }
}
