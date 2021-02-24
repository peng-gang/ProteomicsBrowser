package controller.result;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by gpeng on 2/14/17.
 */
public class PValueTableController {
    @FXML private TableView tbvPValue;

    private ArrayList<String> id;
    private ArrayList<Double> pv;
    private ArrayList<Double> ratio;

    @FXML private void volPlot(ActionEvent event){
        System.out.println("VolcanoPlot");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/result/VolcanoPlot.fxml"));
        Parent root = null;
        try{
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage volStage = new Stage();
        volStage.setTitle("Volcano Plot");
        volStage.setScene(new Scene(root, 300, 400));

        volStage.initModality(Modality.WINDOW_MODAL);
        volStage.initOwner(tbvPValue.getScene().getWindow());

        VolcanoPlotController controller = loader.getController();
        controller.init(pv, ratio);
        volStage.showAndWait();
    }

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
                String fline;
                if(pv.get(i) > 0.1){
                    fline = id.get(i) + "," + String.format("%.2f", pv.get(i));
                } else if(pv.get(i) > 0.01){
                    fline = id.get(i) + "," + String.format("%.3f", pv.get(i));
                } else if(pv.get(i) > 0.001){
                    fline = id.get(i) + "," + String.format("%.4f", pv.get(i));
                } else if(pv.get(i) > 0.0001){
                    fline = id.get(i) + "," + String.format("%.5f", pv.get(i));
                } else {
                    fline = id.get(i) + "," + String.format("%.1e", pv.get(i));
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
        Stage stage = (Stage) tbvPValue.getScene().getWindow();
        stage.close();
    }


    public void set(ArrayList<String> id, ArrayList<Double> pv, ArrayList<Double> ratio){
        this.id = id;
        this.pv = pv;
        this.ratio = ratio;
    }

    public void init(){
        ArrayList<PValueResult> tmp = new ArrayList<>();
        for(int i=0; i<id.size();i++){
            tmp.add(new PValueResult(id.get(i), pv.get(i), ratio.get(i)));
        }

        ObservableList<PValueResult> data = FXCollections.observableArrayList(tmp);

        TableColumn idCol = new TableColumn("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<PValueResult, String>("id"));

        TableColumn pvCol = new TableColumn("PValue");
        pvCol.setCellValueFactory(new PropertyValueFactory<PValueResult, Double>("pv"));

        pvCol.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell cell = new TableCell<PValueResult, Double> () {
                    public void updateItem(Double item, boolean empty){
                        super.updateItem(item, empty);
                        setText(empty ? null : getString());
                        setGraphic(null);
                    }

                    private String getString(){
                        String ret = "";
                        if (getItem() != null) {
                            if(getItem() > 0.1){
                                String gi = getItem().toString();
                                //NumberFormat df = DecimalFormat.getInstance();
                                NumberFormat df = new DecimalFormat("0.00");
                                df.setMinimumFractionDigits(2);
                                df.setRoundingMode(RoundingMode.HALF_UP);
                                ret = df.format(Double.parseDouble(gi));
                            } else if(getItem() > 0.01){
                                String gi = getItem().toString();
                                //NumberFormat df = DecimalFormat.getInstance();
                                NumberFormat df = new DecimalFormat("0.000");
                                df.setMinimumFractionDigits(2);
                                df.setRoundingMode(RoundingMode.HALF_UP);
                                ret = df.format(Double.parseDouble(gi));
                            } else if(getItem() > 0.001){
                                String gi = getItem().toString();
                                //NumberFormat df = DecimalFormat.getInstance();
                                NumberFormat df = new DecimalFormat("0.0000");
                                df.setMinimumFractionDigits(2);
                                df.setRoundingMode(RoundingMode.HALF_UP);
                                ret = df.format(Double.parseDouble(gi));
                            } else if(getItem() > 0.0001){
                                String gi = getItem().toString();
                                //NumberFormat df = DecimalFormat.getInstance();
                                NumberFormat df = new DecimalFormat("0.00000");
                                df.setMinimumFractionDigits(2);
                                df.setRoundingMode(RoundingMode.HALF_UP);
                                ret = df.format(Double.parseDouble(gi));
                            } else {
                                String gi = getItem().toString();
                                //NumberFormat df = DecimalFormat.getInstance();
                                NumberFormat df = new DecimalFormat("0.00E0");
                                df.setMinimumFractionDigits(2);
                                df.setRoundingMode(RoundingMode.HALF_UP);
                                ret = df.format(Double.parseDouble(gi));
                            }

                        } else {
                            ret = "NULL";
                        }
                        return ret;
                    }
                };
                cell.setStyle("-fx-alignment: baseline-right");
                return cell;
            }
        });

        TableColumn ratioCol = new TableColumn("Ratio");
        ratioCol.setCellValueFactory(new PropertyValueFactory<PValueResult, Double>("ratio"));


        ratioCol.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell cell = new TableCell<PValueResult, Double> () {
                    public void updateItem(Double item, boolean empty){
                        super.updateItem(item, empty);
                        setText(empty ? null : getString());
                        setGraphic(null);
                    }

                    private String getString(){
                        String ret = "";
                        if (getItem() != null) {
                            String gi = getItem().toString();
                            NumberFormat df = new DecimalFormat("0.00");
                            df.setMinimumFractionDigits(2);
                            df.setRoundingMode(RoundingMode.HALF_UP);
                            ret = df.format(Double.parseDouble(gi));
                        } else {
                            ret = "NULL";
                        }
                        return ret;
                    }
                };
                cell.setStyle("-fx-alignment: baseline-right");
                return cell;
            }
        });

        tbvPValue.setItems(data);
        tbvPValue.getColumns().addAll(idCol, pvCol, ratioCol);
    }


    public static class PValueResult{
        private SimpleStringProperty id;
        private SimpleDoubleProperty pv;
        private SimpleDoubleProperty ratio;

        private PValueResult(String id, double pv, double ratio){
            this.id = new SimpleStringProperty(id);
            this.pv = new SimpleDoubleProperty(pv);
            this.ratio = new SimpleDoubleProperty(ratio);
        }

        public String getId(){ return id.get(); }
        public void setId(String id) {this.id.set(id);}

        public double getPv(){ return pv.get();}
        public void setPv(double pv) {this.pv.set(pv);}

        public double getRatio() { return  ratio.get();}
        public void setRatio(double ratio) {this.ratio.set(ratio);}
    }
}
