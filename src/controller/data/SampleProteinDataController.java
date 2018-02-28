package controller.data;

import data.SampleGroup;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import project.PublicInfo;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by gpeng on 2/14/17.
 */
public class SampleProteinDataController implements Initializable {
    @FXML private TableView tbvSampleProteinData;

    private SampleGroup sampleGroup;

    private PublicInfo.ScaleType scaleType;

    public void show() {
        System.out.println("Show Sample and Peptide Data dataTable");

        tbvSampleProteinData.setEditable(false);
        tbvSampleProteinData.getItems().clear();
        tbvSampleProteinData.getColumns().clear();
        ObservableList<ObservableList> data = FXCollections.observableArrayList();

        TableColumn colInfo = new TableColumn("Info");
        colInfo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(0).toString());
            }
        });
        colInfo.setSortable(false);
        tbvSampleProteinData.getColumns().add(colInfo);

        ArrayList<String> sampleId = new ArrayList<>(sampleGroup.getSampleId());
        for(int i =0; i<sampleId.size(); i++){
            final int idx = i+1;
            TableColumn col = new TableColumn(sampleId.get(i));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(idx).toString());
                }
            });
            col.setSortable(false);
            tbvSampleProteinData.getColumns().add(col);
        }

        ArrayList<String> numInfoName = new ArrayList<>(sampleGroup.getNumInfoName());
        for(int i=0; i<numInfoName.size(); i++){
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(numInfoName.get(i));
            for(int j=0; j<sampleId.size(); j++){
                row.add(sampleGroup.getNumInfo(sampleId.get(j), numInfoName.get(i)).toString());
            }
            data.add(row);
        }

        ArrayList<String> strInfoName = new ArrayList<>(sampleGroup.getStrInfoName());
        for(int i=0; i<strInfoName.size(); i++){
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(strInfoName.get(i));
            for(int j=0; j<sampleId.size(); j++){
                row.add(sampleGroup.getStrInfo(sampleId.get(j), strInfoName.get(i)).toString());
            }
            data.add(row);
        }

        ArrayList<String> proteinId = new ArrayList<>(sampleGroup.getProteinId());
        for(int i=0; i<proteinId.size(); i++){
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(proteinId.get(i));
            for(int j=0; j<sampleId.size(); j++){
                Double tmp = sampleGroup.getProteinData(sampleId.get(j),proteinId.get(i));
                switch (scaleType){
                    case Log2:
                        if(tmp <=0) {
                            tmp = 0.0;
                        } else {
                            tmp = Math.log(tmp)/Math.log(2.0);
                        }
                        break;
                    case Log10:
                        if(tmp<=0){
                            tmp = 0.0;
                        } else {
                            tmp = Math.log10(tmp);
                        }
                        break;
                    default:
                        break;
                }
                row.add(String.format("%.2f", tmp));
            }
            data.add(row);
        }

        /*
        final int idxSampleId = 0;
        TableColumn colSampleId = new TableColumn("Sample ID");
        colSampleId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(idxSampleId).toString());
            }
        });
        tbvSampleProteinData.getColumns().add(colSampleId);

        ArrayList<String> numInfoName = new ArrayList<>(sampleGroup.getNumInfoName());
        for(int i=0; i<numInfoName.size(); i++){
            final int idx = i + 1;
            TableColumn col = new TableColumn(numInfoName.get(i));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>(){
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(idx).toString());
                }
            });
            tbvSampleProteinData.getColumns().add(col);
        }

        int st = 1 + numInfoName.size();
        ArrayList<String> strInfoName = new ArrayList<>(sampleGroup.getStrInfoName());
        for(int i=0; i<strInfoName.size(); i++){
            final int idx = i + st;
            TableColumn col = new TableColumn(strInfoName.get(i));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>(){
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(idx).toString());
                }
            });
            tbvSampleProteinData.getColumns().add(col);
        }

        st = st + strInfoName.size();
        ArrayList<String> proteinId = new ArrayList<>(sampleGroup.getProteinId());
        for(int i=0; i< proteinId.size(); i++){
            final int idx = i + st;
            TableColumn colPep = new TableColumn(proteinId.get(i));
            colPep.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>(){
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(idx).toString());
                }
            });
            tbvSampleProteinData.getColumns().add(colPep);
        }


        ArrayList<String> sampleId = new ArrayList<>(sampleGroup.getSampleId());
        for(int i=0; i<sampleId.size(); i++){
            ObservableList<String> row = FXCollections.observableArrayList();

            String sampleIdTmp = sampleId.get(i);
            row.add(sampleIdTmp);

            for(int j=0; j<numInfoName.size(); j++){
                row.add((sampleGroup.getNumInfo(sampleIdTmp, numInfoName.get(j)).toString()));
            }

            for(int j=0; j<strInfoName.size(); j++){
                row.add(sampleGroup.getStrInfo(sampleIdTmp, strInfoName.get(j)));
            }

            for(int j=0; j<proteinId.size(); j++){
                Double tmp = sampleGroup.getProteinData(sampleIdTmp, proteinId.get(j));
                switch (scaleType){
                    case Log2:
                        if(tmp <=0) {
                            tmp = 0.0;
                        } else {
                            tmp = Math.log(tmp)/Math.log(2.0);
                        }
                        break;
                    case Log10:
                        if(tmp<=0){
                            tmp = 0.0;
                        } else {
                            tmp = Math.log10(tmp);
                        }
                        break;
                    default:
                        break;
                }
                row.add(String.format("%.2f", tmp));
            }
            data.add(row);
        }
        */

        tbvSampleProteinData.setItems(data);
    }

    public void setData(SampleGroup sampleGroup) {
        this.sampleGroup = sampleGroup;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scaleType = PublicInfo.ScaleType.Regular;
        System.out.println("Init Sample Protein Data Controller");
    }

    public void setScaleType(PublicInfo.ScaleType scaleType){
        this.scaleType = scaleType;
        show();
    }

    public PublicInfo.ScaleType getScaleType(){
        return scaleType;
    }
}
