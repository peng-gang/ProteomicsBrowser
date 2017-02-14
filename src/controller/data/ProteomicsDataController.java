package controller.data;

import data.Peptide;
import data.PeptideInfo;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by gpeng on 2/10/17.
 */
public class ProteomicsDataController implements Initializable {

    @FXML private TableView tbvProteomicsData;


    //Data
    private ArrayList<String> proteomicsRawName;
    private ArrayList<ArrayList<String>> proteomicsRaw;
    private ArrayList<String> proteomicsRawType;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Proteomics Data Initialization");

    }

    public void setProteomicsData(ArrayList<String> proteomicsRawName, ArrayList<String> proteomicsRawType, ArrayList<ArrayList<String >> proteomicsRaw) {
        this.proteomicsRawName = proteomicsRawName;
        this.proteomicsRawType = proteomicsRawType;
        this.proteomicsRaw = proteomicsRaw;
    }

    public void show(){
        System.out.println("Show Table");

        ObservableList<ObservableList> data = FXCollections.observableArrayList();

        for(int i=0; i<proteomicsRawName.size(); i++){
            final int idx = i;
            //TableColumn<ObservableList<String>, String> column = new TableColumn<>(proteomicsRawName.get(i));
            //column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(idx)));
            TableColumn col = new TableColumn(proteomicsRawName.get(i));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>(){
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(idx).toString());
                }
            });
            tbvProteomicsData.getColumns().add(col);
        }

        for(int i=0; i<proteomicsRaw.get(0).size();i++){
            ObservableList<String> row = FXCollections.observableArrayList();
            for(int j=0; j<proteomicsRaw.size();j++){
                row.add(proteomicsRaw.get(j).get(i));
            }
            data.addAll(row);
        }

        tbvProteomicsData.setItems(data);


        /*
        proteomicsDataTable = FXCollections.observableArrayList(proteomicsData);

        tbvProteomicsData.setEditable(true);

        TableColumn sampleId = new TableColumn("Sample ID");
        sampleId.setCellValueFactory(new PropertyValueFactory<PeptideInfo, String>("sampleId"));

        TableColumn pepTideId = new TableColumn("Peptide Id");
        pepTideId.setCellValueFactory(new PropertyValueFactory<PeptideInfo, String>("pepId"));

        TableColumn sequence = new TableColumn("Sequence");
        sequence.setCellValueFactory(new PropertyValueFactory<PeptideInfo, String>("sequence"));

        TableColumn charge = new TableColumn("Charge");
        charge.setCellValueFactory(new PropertyValueFactory<PeptideInfo, Integer>("charge"));

        TableColumn mz = new TableColumn("mz");
        mz.setCellValueFactory(new PropertyValueFactory<PeptideInfo, Double>("mz"));

        TableColumn score = new TableColumn("Score");
        score.setCellValueFactory(new PropertyValueFactory<PeptideInfo, Double>("score"));

        TableColumn abundance = new TableColumn("Abundance");
        abundance.setCellValueFactory(new PropertyValueFactory<PeptideInfo, Double>("abundance"));

        TableColumn modification = new TableColumn("Modification");
        modification.setCellValueFactory(new PropertyValueFactory<PeptideInfo, String>("modification"));

        tbvProteomicsData.setItems(proteomicsDataTable);
        tbvProteomicsData.getColumns().addAll(sampleId, pepTideId, sequence, charge, mz, score, abundance, modification);
        */
    }
}
