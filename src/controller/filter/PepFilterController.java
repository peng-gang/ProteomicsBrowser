package controller.filter;

import data.Protein;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.controlsfx.control.RangeSlider;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by gpeng on 6/29/17.
 */
public class PepFilterController implements Initializable {
    @FXML private Button btnSubmit;
    @FXML private HBox hbValCharge;
    @FXML private HBox hbValMz;
    @FXML private HBox hbValScore;
    @FXML private HBox hbValAbundance;
    @FXML private HBox hbPerCharge;
    @FXML private HBox hbPerMz;
    @FXML private HBox hbPerScore;
    @FXML private HBox hbPerAbundance;
    @FXML private TabPane tabPane;
    @FXML private Label numPep;

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



    private Protein protein;


    @FXML private void submit(ActionEvent event){
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.close();
    }

    @FXML private void reset(ActionEvent event){
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
    }

    public void init(Protein protein){
        this.protein = protein;
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


        numPep.setText("Peptides Remain: " + protein.numPepShow());


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

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
    }
}
