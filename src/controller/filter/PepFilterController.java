package controller.filter;

import data.Protein;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.RangeSlider;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by gpeng on 6/29/17.
 */
public class PepFilterController implements Initializable {
    @FXML private Label numPep;
    @FXML private VBox vBoxValue;
    @FXML private VBox vBoxPercentage;
    @FXML TabPane tabPane;

    /*
    private RangeSlider rsValCharge;
    private RangeSlider rsValMz;
    private RangeSlider rsValScore;
    private RangeSlider rsValAbundance;
    private Label lblValChargeLow;
    private Label lblValChargeHigh;
    private Label lblValMzLow;
    private Label lblValMzHigh;
    private Label lblValScoreLow;
    private Label lblValScoreHigh;
    private Label lblValAbundanceLow;
    private Label lblValAbundanceHigh;


    private RangeSlider rsPerCharge;
    private RangeSlider rsPerMz;
    private RangeSlider rsPerScore;
    private RangeSlider rsPerAbundance;
    private Label lblPerChargeLow;
    private Label lblPerChargeHigh;
    private Label lblPerMzLow;
    private Label lblPerMzHigh;
    private Label lblPerScoreLow;
    private Label lblPerScoreHigh;
    private Label lblPerAbundanceLow;
    private Label lblPerAbundanceHigh;
    */

    private boolean submitted;

    private Protein protein;

    public boolean getSubmitted() { return submitted; }

    @FXML private void submit(ActionEvent event){
        submitted = true;
        Stage stage = (Stage) numPep.getScene().getWindow();
        stage.close();
    }

    @FXML private void reset(ActionEvent event){
        protein.setAbundanceCutHigh(protein.getAbundanceMax());
        protein.setAbundanceCutLow(protein.getAbundanceMin());
        for(int i=0; i<protein.getDoubleInfoName().size();i++){
            protein.setDoubleInfoCutHigh(i, protein.getDoubleInfoMax(i));
            protein.setDoubleInfoCutLow(i, protein.getDoubleInfoMin(i));
        }

        protein.setAbundanceCutPerHigh(1.0);
        protein.setAbundanceCutPerLow(0.0);
        for(int i=0; i<protein.getDoubleInfoName().size();i++){
            protein.setDoubleInfoCutPerHigh(i, 1.0);
            protein.setDoubleInfoCutPerLow(i, 0.0);
        }

        for(Node hbx : vBoxValue.getChildren()){
            for(Node nd : ((HBox) hbx).getChildren()){
                if(nd instanceof RangeSlider){
                    if("Abundance".equals(nd.getUserData())){
                        ((RangeSlider) nd).setLowValue(protein.getAbundanceCutLow());
                        ((RangeSlider) nd).setHighValue(protein.getAbundanceCutHigh());
                    } else {
                        for(int i=0; i<protein.getDoubleInfoName().size();i++){
                            if(protein.getDoubleInfoName().get(i).equals(nd.getUserData())){
                                ((RangeSlider) nd).setLowValue(protein.getDoubleInfoCutLow(i));
                                ((RangeSlider) nd).setHighValue(protein.getDoubleInfoCutHigh(i));
                                break;
                            }
                        }
                    }
                }
            }
        }

        for(Node hbx : vBoxPercentage.getChildren()){
            for(Node nd : ((HBox) hbx).getChildren()){
                if(nd instanceof RangeSlider){
                    if("Abundance".equals(nd.getUserData())){
                        ((RangeSlider) nd).setLowValue(protein.getAbundanceCutPerLow());
                        ((RangeSlider) nd).setHighValue(protein.getAbundanceCutPerHigh());
                    } else {
                        ((RangeSlider) nd).setLowValue(0.0);
                        ((RangeSlider) nd).setHighValue(1.0);
                    }
                }
            }
        }

        numPep.setText("Peptides Remain: " + protein.numPepShow());
        /*
        protein.setChargeCutHigh(protein.getChargeMax());
        protein.setChargeCutLow(protein.getChargeMin());
        protein.setMzCutHigh(protein.getMzMax());
        protein.setMzCutLow(protein.getMzMin());
        protein.setScoreCutHigh(protein.getScoreMax());
        protein.setScoreCutLow(protein.getScoreMin());
        protein.setAbundanceCutHigh(protein.getAbundanceMax());
        protein.setAbundanceCutLow(protein.getAbundanceMin());


        rsValCharge.setHighValue(protein.getChargeCutHigh());
        rsValCharge.setLowValue(protein.getChargeCutLow());
        rsValMz.setHighValue(protein.getMzCutHigh());
        rsValMz.setLowValue(protein.getMzCutLow());
        rsValScore.setHighValue(protein.getScoreCutHigh());
        rsValScore.setLowValue(protein.getScoreCutLow());
        rsValAbundance.setHighValue(protein.getAbundanceCutHigh());
        rsValAbundance.setLowValue(protein.getAbundanceCutLow());

        rsPerCharge.setHighValue(protein.getChargeCutPerHigh());
        rsPerCharge.setLowValue(protein.getChargeCutPerLow());
        rsPerMz.setHighValue(protein.getMzCutPerHigh());
        rsPerMz.setLowValue(protein.getMzCutPerLow());
        rsPerScore.setHighValue(protein.getScoreCutPerHigh());
        rsPerScore.setLowValue(protein.getScoreCutPerLow());
        rsPerAbundance.setHighValue(protein.getAbundanceCutPerHigh());
        rsPerAbundance.setLowValue(protein.getAbundanceCutPerLow());

        numPep.setText("Peptides Remain: " + protein.numPepShow());
        */
    }

    public void init(Protein protein){
        this.protein = protein;
        submitted = false;

        RangeSlider rsValAbundance = new RangeSlider(protein.getAbundanceMin(), protein.getAbundanceMax(),
                protein.getAbundanceCutLow(), protein.getAbundanceCutHigh());
        rsValAbundance.setUserData("Abundance");
        Label lblValAbundanceLow = new Label(String.format("%.2g",rsValAbundance.getLowValue()));
        Label lblValAbundanceHigh = new Label(String.format("%.2g",rsValAbundance.getHighValue()));
        lblValAbundanceLow.setPrefWidth(70);
        lblValAbundanceHigh.setPrefWidth(70);
        Label lblValAbundanceName = new Label("Abundance");
        lblValAbundanceName.setPrefWidth(80);

        RangeSlider rsPerAbundance = new RangeSlider(0, 1.0, protein.getAbundanceCutPerLow(), protein.getAbundanceCutPerHigh());
        rsPerAbundance.setUserData("Abundance");
        Label lblPerAbundanceLow = new Label(String.format("%.2f",rsPerAbundance.getLowValue() * 100));
        Label lblPerAbundanceHigh = new Label(String.format("%.2f",rsPerAbundance.getHighValue() * 100));
        lblPerAbundanceLow.setPrefWidth(70);
        lblPerAbundanceHigh.setPrefWidth(70);
        Label lblPerAbundanceName = new Label("Abundance");
        lblPerAbundanceName.setPrefWidth(80);

        rsValAbundance.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setAbundanceCutLow(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblValAbundanceLow.setText(String.format("%.2g",newValue.doubleValue()));
            }
        });

        rsValAbundance.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setAbundanceCutHigh(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblValAbundanceHigh.setText(String.format("%.2g",newValue.doubleValue()));
            }
        });

        rsPerAbundance.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setAbundanceCutPerLow(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblPerAbundanceLow.setText(String.format("%.2f", newValue.doubleValue() * 100));
            }
        });

        rsPerAbundance.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setAbundanceCutPerHigh(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblPerAbundanceHigh.setText(String.format("%.2f", newValue.doubleValue() * 100));
            }
        });

        HBox hBoxValAbundance = new HBox();
        hBoxValAbundance.setAlignment(Pos.CENTER_LEFT);
        hBoxValAbundance.setPadding(new Insets(0, 0, 0, 20));
        hBoxValAbundance.setSpacing(10);
        hBoxValAbundance.setPrefWidth(600);
        hBoxValAbundance.setPrefHeight(60);
        hBoxValAbundance.getChildren().addAll(lblValAbundanceName, lblValAbundanceLow, rsValAbundance, lblValAbundanceHigh);
        vBoxValue.getChildren().add(hBoxValAbundance);

        HBox hBoxPerAbundance = new HBox();
        hBoxPerAbundance.setAlignment(Pos.CENTER_LEFT);
        hBoxPerAbundance.setPadding(new Insets(0, 0, 0, 20));
        hBoxPerAbundance.setSpacing(10);
        hBoxPerAbundance.setPrefWidth(600);
        hBoxPerAbundance.setPrefHeight(60);
        hBoxPerAbundance.getChildren().addAll(lblPerAbundanceName, lblPerAbundanceLow, rsPerAbundance, lblPerAbundanceHigh);
        vBoxPercentage.getChildren().add(hBoxPerAbundance);



        ArrayList<String> doubleInfoName = protein.getDoubleInfoName();

        for(int i=0; i<doubleInfoName.size(); i++){
            RangeSlider rsValInfo = new RangeSlider(protein.getDoubleInfoMin(i), protein.getDoubleInfoMax(i),
                    protein.getDoubleInfoCutLow(i), protein.getDoubleInfoCutHigh(i));
            rsValInfo.setUserData(doubleInfoName.get(i));
            Label lblValInfoLow = new Label(String.format("%.2g",rsValInfo.getLowValue()));
            Label lblValInfoHigh = new Label(String.format("%.2g",rsValInfo.getHighValue()));
            lblValInfoLow.setPrefWidth(70);
            lblValInfoHigh.setPrefWidth(70);
            Label lblValInfoName = new Label(doubleInfoName.get(i));
            lblValInfoName.setPrefWidth(80);

            int dInfoIdx = i;
            rsValInfo.lowValueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    protein.setDoubleInfoCutLow(dInfoIdx, newValue.doubleValue());
                    numPep.setText("Peptides Remain: " + protein.numPepShow());
                    lblValInfoLow.setText(String.format("%.2g",newValue.doubleValue()));
                }
            });

            rsValInfo.highValueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    protein.setDoubleInfoCutHigh(dInfoIdx, newValue.doubleValue());
                    numPep.setText("Peptides Remain: " + protein.numPepShow());
                    lblValInfoHigh.setText(String.format("%.2g",newValue.doubleValue()));
                }
            });

            HBox hBoxValInfo = new HBox();
            hBoxValInfo.setAlignment(Pos.CENTER_LEFT);
            hBoxValInfo.setPadding(new Insets(0, 0, 0, 20));
            hBoxValInfo.setSpacing(10);
            hBoxValInfo.setPrefWidth(600);
            hBoxValInfo.setPrefHeight(60);
            hBoxValInfo.getChildren().addAll(lblValInfoName, lblValInfoLow, rsValInfo, lblValInfoHigh);
            vBoxValue.getChildren().add(hBoxValInfo);
        }



        for(int i=0; i<doubleInfoName.size(); i++){
            RangeSlider rsPerInfo = new RangeSlider(0.0, 1.0, protein.getDoubleInfoCutPerLow(i), protein.getDoubleInfoCutHigh(i));
            rsPerInfo.setUserData(doubleInfoName.get(i));
            Label lblPerInfoLow = new Label(String.format("%.2f",rsPerInfo.getLowValue() * 100));
            Label lblPerInfoHigh = new Label(String.format("%.2f",rsPerInfo.getHighValue() * 100));
            lblPerInfoLow.setPrefWidth(70);
            lblPerInfoHigh.setPrefWidth(70);
            Label lblPerInfoName = new Label(doubleInfoName.get(i));
            lblPerInfoName.setPrefWidth(80);

            int dInfoIdx = i;
            rsPerInfo.lowValueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    protein.setDoubleInfoCutPerLow(dInfoIdx, newValue.doubleValue());
                    numPep.setText("Peptides Remain: " + protein.numPepShow());
                    lblPerInfoLow.setText(String.format("%.2f",newValue.doubleValue() * 100));
                }
            });

            rsPerInfo.highValueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    protein.setDoubleInfoCutPerHigh(dInfoIdx, newValue.doubleValue());
                    numPep.setText("Peptides Remain: " + protein.numPepShow());
                    lblPerInfoHigh.setText(String.format("%.2f",newValue.doubleValue() * 100));
                }
            });

            HBox hBoxPerInfo = new HBox();
            hBoxPerInfo.setAlignment(Pos.CENTER_LEFT);
            hBoxPerInfo.setPadding(new Insets(0, 0, 0, 20));
            hBoxPerInfo.setSpacing(10);
            hBoxPerInfo.setPrefWidth(600);
            hBoxPerInfo.setPrefHeight(60);
            hBoxPerInfo.getChildren().addAll(lblPerInfoName, lblPerInfoLow, rsPerInfo, lblPerInfoHigh);
            vBoxPercentage.getChildren().add(hBoxPerInfo);
        }


        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                String selectedValue = newValue.getText();
                if(selectedValue.equals("Value")){
                    rsValAbundance.setHighValue(protein.getAbundanceCutHigh());
                    rsValAbundance.setLowValue(protein.getAbundanceCutLow());
                    for(Node hbs : vBoxValue.getChildren()){
                        for(Node nd : ((HBox) hbs).getChildren()){
                            if(nd instanceof RangeSlider){
                                for(int i=0; i<protein.getDoubleInfoName().size(); i++){
                                    if(protein.getDoubleInfoName().get(i).equals(nd.getUserData())){
                                        ((RangeSlider) nd).setHighValue(protein.getDoubleInfoCutHigh(i));
                                        ((RangeSlider) nd).setLowValue(protein.getDoubleInfoCutLow(i));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    rsPerAbundance.setHighValue(protein.getAbundanceCutPerHigh());
                    rsPerAbundance.setLowValue(protein.getAbundanceCutPerLow());
                    for(Node hbs : vBoxPercentage.getChildren()){
                        for(Node nd : ((HBox) hbs).getChildren()){
                            if(nd instanceof RangeSlider){
                                for(int i=0; i<protein.getDoubleInfoName().size(); i++){
                                    if(protein.getDoubleInfoName().get(i).equals(nd.getUserData())){
                                        ((RangeSlider) nd).setHighValue(protein.getDoubleInfoCutPerHigh(i));
                                        ((RangeSlider) nd).setLowValue(protein.getDoubleInfoCutPerLow(i));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        /*
        rsValCharge.setMax(protein.getChargeMax());
        rsValCharge.setMin(protein.getChargeMin());
        rsValMz.setMax(protein.getMzMax());
        rsValMz.setMin(protein.getMzMin());
        rsValScore.setMax(protein.getScoreMax());
        rsValScore.setMin(protein.getScoreMin());
        rsValAbundance.setMax(protein.getAbundanceMax());
        rsValAbundance.setMin(protein.getAbundanceMin());
        rsPerCharge.setMax(1.0);
        rsPerCharge.setMin(0.0);
        rsPerMz.setMax(1.0);
        rsPerMz.setMin(0.0);
        rsPerScore.setMax(1.0);
        rsPerScore.setMin(0.0);
        rsPerAbundance.setMax(1.0);
        rsPerAbundance.setMin(0.0);

        rsValCharge.setHighValue(protein.getChargeCutHigh());
        rsValCharge.setLowValue(protein.getChargeCutLow());
        rsValMz.setHighValue(protein.getMzCutHigh());
        rsValMz.setLowValue(protein.getMzCutLow());
        rsValScore.setHighValue(protein.getScoreCutHigh());
        rsValScore.setLowValue(protein.getScoreCutLow());
        rsValAbundance.setHighValue(protein.getAbundanceCutHigh());
        rsValAbundance.setLowValue(protein.getAbundanceCutLow());

        rsPerCharge.setHighValue(protein.getChargeCutPerHigh());
        rsPerCharge.setLowValue(protein.getChargeCutPerLow());
        rsPerMz.setHighValue(protein.getMzCutPerHigh());
        rsPerMz.setLowValue(protein.getMzCutPerLow());
        rsPerScore.setHighValue(protein.getScoreCutPerHigh());
        rsPerScore.setLowValue(protein.getScoreCutPerLow());
        rsPerAbundance.setHighValue(protein.getAbundanceCutPerHigh());
        rsPerAbundance.setLowValue(protein.getAbundanceCutPerLow());

        lblValChargeLow.setText(String.format("%d", (int) rsValCharge.getLowValue()));
        lblValChargeHigh.setText(String.format("%d", (int) rsValCharge.getHighValue()));
        lblValMzLow.setText(String.format("%.2g",rsValMz.getLowValue()));
        lblValMzHigh.setText(String.format("%.2g",rsValMz.getHighValue()));
        lblValScoreLow.setText(String.format("%.2g",rsValScore.getLowValue()));
        lblValScoreHigh.setText(String.format("%.2g",rsValScore.getHighValue()));
        lblValAbundanceLow.setText(String.format("%.2g",rsValAbundance.getLowValue()));
        lblValAbundanceHigh.setText(String.format("%.2g",rsValAbundance.getHighValue()));

        lblPerChargeLow.setText(String.format("%.2f",rsPerCharge.getLowValue() * 100));
        lblPerChargeHigh.setText(String.format("%.2f",rsPerCharge.getHighValue() * 100));
        lblPerMzLow.setText(String.format("%.2f",rsPerMz.getLowValue() * 100));
        lblPerMzHigh.setText(String.format("%.2f",rsPerMz.getHighValue() * 100));
        lblPerScoreLow.setText(String.format("%.2f",rsPerScore.getLowValue() * 100));
        lblPerScoreHigh.setText(String.format("%.2f",rsPerScore.getHighValue() * 100));
        lblPerAbundanceLow.setText(String.format("%.2f",rsPerAbundance.getLowValue() * 100));
        lblPerAbundanceHigh.setText(String.format("%.2f",rsPerAbundance.getHighValue() * 100));
        */

        numPep.setText("Peptides Remain: " + protein.numPepShow());


        /*
        rsValCharge.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setChargeCutLow(newValue.intValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblValChargeLow.setText(String.valueOf(newValue.intValue()));
            }
        });

        rsValCharge.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setChargeCutHigh(newValue.intValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblValChargeHigh.setText(String.valueOf(newValue.intValue()));
            }
        });

        rsValMz.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setMzCutLow(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblValMzLow.setText(String.format("%.2g",newValue.doubleValue()));
            }
        });

        rsValMz.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setMzCutHigh(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblValMzHigh.setText(String.format("%.2g",newValue.doubleValue()));
            }
        });

        rsValScore.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setScoreCutLow(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblValScoreLow.setText(String.format("%.2g",newValue.doubleValue()));
            }
        });

        rsValScore.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setScoreCutHigh(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblValScoreHigh.setText(String.format("%.2g",newValue.doubleValue()));
            }
        });

        rsValAbundance.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setAbundanceCutLow(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblValAbundanceLow.setText(String.format("%.2g",newValue.doubleValue()));
            }
        });

        rsValAbundance.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setAbundanceCutHigh(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblValAbundanceHigh.setText(String.format("%.2g",newValue.doubleValue()));
            }
        });


        rsPerCharge.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setChargeCutPerLow(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblPerChargeLow.setText(String.format("%.2f",newValue.doubleValue() * 100));
            }
        });

        rsPerCharge.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setChargeCutPerHigh(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblPerChargeHigh.setText(String.format("%.2f",newValue.doubleValue() * 100));
            }
        });

        rsPerMz.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setMzCutPerLow(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblPerMzLow.setText(String.format("%.2f",newValue.doubleValue() * 100));
            }
        });

        rsPerMz.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setMzCutPerHigh(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblPerMzHigh.setText(String.format("%.2f",newValue.doubleValue() * 100));
            }
        });

        rsPerScore.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setScoreCutPerLow(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblPerScoreLow.setText(String.format("%.2f",newValue.doubleValue() * 100));
            }
        });

        rsPerScore.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setScoreCutPerHigh(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblPerScoreHigh.setText(String.format("%.2f", newValue.doubleValue() * 100));
            }
        });

        rsPerAbundance.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setAbundanceCutPerLow(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblPerAbundanceLow.setText(String.format("%.2f", newValue.doubleValue() * 100));
            }
        });

        rsPerAbundance.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                protein.setAbundanceCutPerHigh(newValue.doubleValue());
                numPep.setText("Peptides Remain: " + protein.numPepShow());
                lblPerAbundanceHigh.setText(String.format("%.2f", newValue.doubleValue() * 100));
            }
        });
        */

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
        rsValCharge = new RangeSlider();
        rsValMz = new RangeSlider();
        rsValScore = new RangeSlider();
        rsValAbundance = new RangeSlider();

        rsPerCharge = new RangeSlider();
        rsPerMz = new RangeSlider();
        rsPerScore = new RangeSlider();
        rsPerAbundance = new RangeSlider();

        lblValChargeLow = new Label();
        lblValChargeLow.setPrefWidth(70);
        lblValChargeHigh = new Label();
        lblValChargeHigh.setPrefWidth(70);
        lblValMzLow = new Label();
        lblValMzLow.setPrefWidth(70);
        lblValMzHigh = new Label();
        lblValMzHigh.setPrefWidth(70);
        lblValScoreLow = new Label();
        lblValScoreLow.setPrefWidth(70);
        lblValScoreHigh = new Label();
        lblValScoreHigh.setPrefWidth(70);
        lblValAbundanceLow = new Label();
        lblValAbundanceLow.setPrefWidth(70);
        lblValAbundanceHigh = new Label();
        lblValAbundanceHigh.setPrefWidth(70);

        lblPerChargeLow = new Label();
        lblPerChargeLow.setPrefWidth(70);
        lblPerChargeHigh = new Label();
        lblPerChargeHigh.setPrefWidth(70);
        lblPerMzLow = new Label();
        lblPerMzLow.setPrefWidth(70);
        lblPerMzHigh = new Label();
        lblPerMzHigh.setPrefWidth(70);
        lblPerScoreLow = new Label();
        lblPerScoreLow.setPrefWidth(70);
        lblPerScoreHigh = new Label();
        lblPerScoreHigh.setPrefWidth(70);
        lblPerAbundanceLow = new Label();
        lblPerAbundanceLow.setPrefWidth(70);
        lblPerAbundanceHigh = new Label();
        lblPerAbundanceHigh.setPrefWidth(70);


        hbValCharge.getChildren().addAll(lblValChargeLow, rsValCharge, lblValChargeHigh);
        hbValMz.getChildren().addAll(lblValMzLow, rsValMz, lblValMzHigh);
        hbValScore.getChildren().addAll(lblValScoreLow, rsValScore, lblValScoreHigh);
        hbValAbundance.getChildren().addAll(lblValAbundanceLow, rsValAbundance, lblValAbundanceHigh);

        hbPerCharge.getChildren().addAll(lblPerChargeLow, rsPerCharge, lblPerChargeHigh);
        hbPerMz.getChildren().addAll(lblPerMzLow, rsPerMz, lblPerMzHigh);
        hbPerScore.getChildren().addAll(lblPerScoreLow, rsPerScore, lblPerScoreHigh);
        hbPerAbundance.getChildren().addAll(lblPerAbundanceLow, rsPerAbundance, lblPerAbundanceHigh);

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                String selectedValue = newValue.getText();
                if(selectedValue.equals("Value")){
                    rsValCharge.setHighValue(protein.getChargeCutHigh());
                    rsValCharge.setLowValue(protein.getChargeCutLow());
                    rsValMz.setHighValue(protein.getMzCutHigh());
                    rsValMz.setLowValue(protein.getMzCutLow());
                    rsValScore.setHighValue(protein.getScoreCutHigh());
                    rsValScore.setLowValue(protein.getScoreCutLow());
                    rsValAbundance.setHighValue(protein.getAbundanceCutHigh());
                    rsValAbundance.setLowValue(protein.getAbundanceCutLow());
                } else {
                    rsPerCharge.setHighValue(protein.getChargeCutPerHigh());
                    rsPerCharge.setLowValue(protein.getChargeCutPerLow());
                    rsPerMz.setHighValue(protein.getMzCutPerHigh());
                    rsPerMz.setLowValue(protein.getMzCutPerLow());
                    rsPerScore.setHighValue(protein.getScoreCutPerHigh());
                    rsPerScore.setLowValue(protein.getScoreCutPerLow());
                    rsPerAbundance.setHighValue(protein.getAbundanceCutPerHigh());
                    rsPerAbundance.setLowValue(protein.getAbundanceCutPerLow());
                }
            }
        });
        */
    }
}
