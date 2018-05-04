package controller.result;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by gpeng on 2/14/17.
 */
public class PValueTableController {
    @FXML private TableView tbvPValue;

    private ArrayList<String> id;
    private ArrayList<Double> pv;

    @FXML private void save(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Output p-value");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Output p-value", "*.csv")
        );

        Stage stage = (Stage) tbvPValue.getScene().getWindow();
        File outputFile = fileChooser.showSaveDialog(stage);

        if(outputFile == null){
            return;
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(outputFile);
        } catch (IOException e){
            e.printStackTrace();
        }

        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        try {
            for(int i=0; i<id.size(); i++){
                String fline = id.get(i) + "," + pv.get(i) + "\n";
                bufferedWriter.write(fline);
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e){
                e.printStackTrace();
            }

        }

    }

    @FXML private void exit(ActionEvent event){
        Stage stage = (Stage) tbvPValue.getScene().getWindow();
        stage.close();
    }


    public void set(ArrayList<String> id, ArrayList<Double> pv){
        this.id = id;
        this.pv = pv;
    }

    public void init(){
        ArrayList<PValueResult> tmp = new ArrayList<>();
        for(int i=0; i<id.size();i++){
            tmp.add(new PValueResult(id.get(i), pv.get(i)));
        }

        ObservableList<PValueResult> data = FXCollections.observableArrayList(tmp);

        TableColumn idCol = new TableColumn("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<PValueResult, String>("id"));

        TableColumn pvCol = new TableColumn("PValue");
        pvCol.setCellValueFactory(new PropertyValueFactory<PValueResult, Double>("pv"));

        tbvPValue.setItems(data);
        tbvPValue.getColumns().addAll(idCol, pvCol);
    }


    public static class PValueResult{
        private SimpleStringProperty id;
        private SimpleDoubleProperty pv;

        private PValueResult(String id, double pv){
            this.id = new SimpleStringProperty(id);
            this.pv = new SimpleDoubleProperty(pv);
        }

        public String getId(){ return id.get(); }
        public void setId(String id) {this.id.set(id);}

        public double getPv(){ return pv.get();}
        public void setPv(double pv) {this.pv.set(pv);}
    }
}
