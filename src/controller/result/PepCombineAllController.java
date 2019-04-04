package controller.result;

import data.Peptide;
import data.Protein;
import data.SampleGroup;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by gpeng on 4/3/19.
 */
public class PepCombineAllController {
    private SampleGroup sampleGroup;
    private int pos;
    private ArrayList<Protein> proteins;
    private LinkedHashMap<String, ArrayList<String>> outputRlt;


    @FXML private TableView tbvRlt;
    @FXML private Label lblInfo;

    public void setData(SampleGroup sampleGroup, String proteinName, int pos){
        this.sampleGroup = sampleGroup;
        this.pos = pos;

        proteins = new ArrayList<>();
        proteins = sampleGroup.getProtein(proteinName);

        lblInfo.setText("Peptide Abundance after Combination for " + proteinName);
        outputRlt = new LinkedHashMap<String, ArrayList<String>>();
    }

    private TreeMap<String, Double> combinePeptidesProtein(Protein protein){
        TreeMap<String, Double> rlt = new TreeMap<>();
        ArrayList<Integer> pepSelIndex = protein.getPepIndex(pos);
        if(pepSelIndex.size()==0){
            return rlt;
        }

        ArrayList<Peptide> peps = new ArrayList<>();
        Integer minX = 9000000;
        Integer maxX = 0;
        TreeMap<String, ArrayList<Integer>> groupIndex = new TreeMap<>();

        int pepSelIdx = 0;
        for(Integer idx : pepSelIndex){
            int st = protein.getPepStart(idx);
            int ed = protein.getPepEnd(idx);
            if(minX > st){
                minX = st;
            }
            if(maxX < ed){
                maxX = ed;
            }

            Peptide pepTmp = protein.getPeptide(idx);
            String modiTypeTmp = pepTmp.getModiType(pos-st);
            if(modiTypeTmp == null){
                modiTypeTmp = "NULL";
            }

            if(groupIndex.containsKey(modiTypeTmp)){
                groupIndex.get(modiTypeTmp).add(pepSelIdx);
            } else {
                ArrayList<Integer> tmp = new ArrayList<>();
                tmp.add(pepSelIdx);
                groupIndex.put(modiTypeTmp, tmp);
            }
            peps.add(pepTmp);
            pepSelIdx++;
        }

        double abundanceTotal = 0;
        for(Map.Entry<String, ArrayList<Integer>> entry : groupIndex.entrySet()){
            double abundance = 0;
            for(int idx : entry.getValue()){
                abundance += peps.get(idx).getAbundance();
                abundanceTotal += peps.get(idx).getAbundance();
            }
            rlt.put(entry.getKey(), abundance);
        }
        rlt.put("Total", abundanceTotal);
        return rlt;
    }

    public void combinePeptides(){
        ArrayList<String> sampleId = new ArrayList<>(sampleGroup.getSampleId());
        int idx=0;
        TreeMap<String, ArrayList<Double>> abundance = new TreeMap<>();
        ArrayList<Double> nullAbundance = new ArrayList<>();
        ArrayList<Double> totalAbundance = new ArrayList<>();
        boolean flagNULL = false;
        for(Protein protein : proteins){
            nullAbundance.add(0.0);
            totalAbundance.add(0.0);
            for(Map.Entry<String, ArrayList<Double>> entry : abundance.entrySet()){
                entry.getValue().add(0.0);
            }

            TreeMap<String, Double> abTmp = new TreeMap<>();
            abTmp = combinePeptidesProtein(protein);

            if(abTmp.size()==0){
                idx++;
                continue;
            }

            for(Map.Entry<String, Double> entry : abTmp.entrySet()){
                if(entry.getKey().equals("NULL")){
                    flagNULL = true;
                    nullAbundance.set(idx, entry.getValue());
                    continue;
                }
                if(entry.getKey().equals("Total")){
                    totalAbundance.set(idx, entry.getValue());
                    continue;
                }

                if(abundance.containsKey(entry.getKey())){
                    abundance.get(entry.getKey()).set(idx, entry.getValue());
                } else {
                    ArrayList<Double> tmp = new ArrayList<>();
                    if(idx>0){
                        for(int i=0; i<idx; i++){
                            tmp.add(0.0);
                        }
                    }
                    tmp.add(entry.getValue());
                    abundance.put(entry.getKey(), tmp);
                }
            }
            idx++;
        }

        tbvRlt.setEditable(false);
        tbvRlt.getColumns().clear();
        tbvRlt.getItems().clear();
        ObservableList<ObservableList> data = FXCollections.observableArrayList();

        outputRlt.clear();
        outputRlt.put("ID", new ArrayList<String>());
        TableColumn colId = new TableColumn("ID");
        colId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(0).toString());
            }
        });
        colId.setSortable(true);
        tbvRlt.getColumns().add(colId);

        int idxCol = 1;
        if(flagNULL){
            TableColumn colNull = new TableColumn("NoModification");
            outputRlt.put("NoModification", new ArrayList<String>());
            colNull.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(1).toString());
                }
            });
            colNull.setSortable(true);
            tbvRlt.getColumns().add(colNull);
            idxCol++;
        }

        if(abundance.size()>0){
            for(Map.Entry<String, ArrayList<Double>> entry : abundance.entrySet()){
                final  int idxSel = idxCol;
                TableColumn colTmp = new TableColumn(entry.getKey());
                outputRlt.put(entry.getKey(), new ArrayList<String>());
                colTmp.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(idxSel).toString());
                    }
                });
                colTmp.setSortable(true);
                tbvRlt.getColumns().add(colTmp);
                idxCol++;
            }
        }

        final  int idxSel = idxCol;
        TableColumn colTotal = new TableColumn("Total");
        outputRlt.put("Total", new ArrayList<String>());
        colTotal.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(idxSel).toString());
            }
        });
        colTotal.setSortable(true);
        tbvRlt.getColumns().add(colTotal);


        for(int i=0; i<sampleId.size(); i++){
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(sampleId.get(i));
            outputRlt.get("ID").add(sampleId.get(i));
            if(flagNULL){
                row.add(nullAbundance.get(i).toString());
                outputRlt.get("NoModification").add(nullAbundance.get(i).toString());
            }
            if(abundance.size()>0){
                for(Map.Entry<String, ArrayList<Double>> entry : abundance.entrySet()){
                    row.addAll(entry.getValue().get(i).toString());
                    outputRlt.get(entry.getKey()).add(entry.getValue().get(i).toString());
                }
            }
            row.add(totalAbundance.get(i).toString());
            outputRlt.get("Total").add(totalAbundance.get(i).toString());
            data.add(row);
        }

        tbvRlt.setItems(data);
    }

    @FXML private void save(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Output p-value");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Output Abundance", "*.csv")
        );

        Stage stage = (Stage) tbvRlt.getScene().getWindow();
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
            String header = "";
            for(String name : outputRlt.keySet()){
                header = header + name + ",";
            }
            bufferedWriter.write(header);
            bufferedWriter.newLine();


            for(int i=0; i<sampleGroup.getSampleId().size(); i++){
                String fline = "";
                for(Map.Entry<String, ArrayList<String>> entry:outputRlt.entrySet()){
                    fline += entry.getValue().get(i) + ",";
                }
                bufferedWriter.write(fline);
                bufferedWriter.newLine();
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
        Stage stage = (Stage) tbvRlt.getScene().getWindow();
        stage.close();
    }
}
