package controller.filter;

import data.Protein;
import data.Sample;
import data.SampleGroup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by gpeng on 10/12/18.
 */
public class ProteinFilterController {
    @FXML private TabPane tabPane;

    @FXML private VBox vBoxModiType;
    @FXML private Label lblMinNumModification;
    @FXML private Slider sliderNumModification;
    @FXML private TextField txtNumModification;
    @FXML private Label lblMaxNumModification;

    @FXML private Label lblMinNumModiType;
    @FXML private Slider sliderNumModiType;
    @FXML private TextField txtNumModiType;
    @FXML private Label lblMaxNumModiType;

    @FXML private Label lblMinNumPep;
    @FXML private Slider sliderNumPep;
    @FXML private TextField txtNumPep;
    @FXML private Label lblMaxNumPep;

    @FXML private RadioButton rbAny;
    @FXML private RadioButton rbAll;

    private SampleGroup sampleGroup;
    private ArrayList<String> selectedSamples;
    private Set<String> modiSelected;
    private Set<String> modiRm;
    private boolean anySample = true;

    private int maxNumModification = 0;
    private int maxNumPep = 0;
    private int maxNumModiType = 0;

    private boolean submitted = false;

    private String tabSelected = "";

    public boolean getSubmitted(){
        return submitted;
    }

    public Set<String> getModiSelected(){
        return modiSelected;
    }

    public Set<String> getModiRm() {
        return modiRm;
    }

    public int getNumPep(){
        return (int) sliderNumPep.getValue();
    }

    public int getNumModiPos(){
        return (int) sliderNumModification.getValue();
    }

    public int getNumModiType(){
        return (int) sliderNumModiType.getValue();
    }

    public String getTabSelected(){
        return tabSelected;
    }

    public boolean getAnySample() { return anySample; }

    public void init(SampleGroup sampleGroup, ArrayList<String> selectedSamples){
        this.sampleGroup = sampleGroup;
        this.selectedSamples = selectedSamples;

        //Modification Type
        modiSelected = new TreeSet<>();
        modiRm = new TreeSet<>();

        //int numModiAll = sample.getModificationTypeAll().size();

        Set<String> modiTypeAllSel = new HashSet<>();
        for(String selectedSample : selectedSamples){
            modiTypeAllSel.addAll(sampleGroup.getSample(selectedSample).getModificationTypeAll());
        }

        int idx = 0;
        HBox hBox = null;
        for(String modiType:modiTypeAllSel){
            if(idx==0){
                hBox = new HBox();
                hBox.setSpacing(20);
            }

            CheckBox checkBox = new CheckBox(modiType);
            checkBox.setUserData(modiType);
            checkBox.setAllowIndeterminate(true);
            checkBox.setIndeterminate(true);

            checkBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(checkBox.isIndeterminate()){
                        modiSelected.remove(checkBox.getText());
                        modiRm.remove(checkBox.getText());
                    } else {
                        if(checkBox.isSelected()){
                            modiSelected.add(checkBox.getText());
                            modiRm.remove(checkBox.getText());
                        } else {
                            modiSelected.remove(checkBox.getText());
                            modiRm.add(checkBox.getText());
                        }
                    }
                }
            });

            /*
            checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    System.out.println("MM");
                    if(checkBox.isIndeterminate()){
                        System.out.println("indeterminate");
                        modiSelected.remove(checkBox.getText());
                        modiRm.remove(checkBox.getText());

                        if(checkBox.isSelected()){
                            System.out.println("checked");
                            modiSelected.add(checkBox.getText());
                            modiRm.remove(checkBox.getText());
                        } else {
                            System.out.println("unchecked");
                            modiSelected.remove(checkBox.getText());
                            modiRm.add(checkBox.getText());
                        }
                    } else {
                        if(checkBox.isSelected()){
                            System.out.println("checked");
                            modiSelected.add(checkBox.getText());
                            modiRm.remove(checkBox.getText());
                        } else {
                            System.out.println("unchecked");
                            modiSelected.remove(checkBox.getText());
                            modiRm.add(checkBox.getText());
                        }
                    }
                }
            });*/

            hBox.getChildren().add(checkBox);
            if(idx==2){
                vBoxModiType.getChildren().addAll(hBox);
            }
            idx++;
            idx = idx%3;
        }
        if(idx!=0){
            vBoxModiType.getChildren().addAll(hBox);
        }


        maxNumModification = 0;
        maxNumPep = 0;
        for(String selectedSample : selectedSamples){
            Sample sample = sampleGroup.getSample(selectedSample);
            for(Protein protein:sample.getProteins()){
                if(protein.getModiPos().size() > maxNumModification){
                    maxNumModification = protein.getModiPos().size();
                }
                if(protein.getPeptides().size() > maxNumPep){
                    maxNumPep = protein.getPeptides().size();
                }
            }
        }

        maxNumModiType = 0;
        for(String selectedSample : selectedSamples){
            if(maxNumModiType < sampleGroup.getSample(selectedSample).getModificationTypeAll().size()){
                maxNumModiType = sampleGroup.getSample(selectedSample).getModificationTypeAll().size();
            }
        }
        //maxNumModiType = sample.getModificationTypeAll().size();


        //Number of Modification Position
        lblMaxNumModification.setText(String.valueOf(maxNumModification));
        sliderNumModification.setMax(maxNumModification);
        sliderNumModification.setMin(0);
        sliderNumModification.setMinorTickCount(1);
        sliderNumModification.setValue(0);
        sliderNumModification.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                txtNumModification.setText(String.valueOf(newValue.intValue()));
            }
        });

        txtNumModification.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtNumModification.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        txtNumModification.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                    int newVal = Integer.valueOf(txtNumModification.getText());
                    if(newVal > maxNumModification){
                        newVal = maxNumModification;
                        txtNumModification.setText(String.valueOf(maxNumModification));
                    }

                    if(newVal < 0){
                        newVal = 0;
                        txtNumModification.setText(String.valueOf(0));
                    }

                    sliderNumModification.setValue(Integer.valueOf(newVal));
                }
            }
        });

        //Number of Modification Type
        lblMaxNumModiType.setText(String.valueOf(maxNumModiType));
        sliderNumModiType.setMax(maxNumModiType);
        sliderNumModiType.setMin(0);
        sliderNumModiType.setMinorTickCount(1);
        sliderNumModiType.setValue(0);
        sliderNumModiType.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                txtNumModiType.setText(String.valueOf(newValue.intValue()));
            }
        });

        txtNumModiType.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtNumModiType.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        txtNumModiType.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                    int newVal = Integer.valueOf(txtNumModiType.getText());
                    if(newVal > maxNumModiType){
                        newVal = maxNumModiType;
                        txtNumModiType.setText(String.valueOf(maxNumModiType));
                    }

                    if(newVal < 0){
                        newVal = 0;
                        txtNumModiType.setText(String.valueOf(0));
                    }

                    sliderNumModiType.setValue(Integer.valueOf(newVal));
                }
            }
        });


        //Number of peptides
        lblMaxNumPep.setText(String.valueOf(maxNumPep));
        sliderNumPep.setMax(maxNumPep);
        sliderNumPep.setMin(1);
        sliderNumPep.setMinorTickCount(1);
        sliderNumPep.setValue(1);
        sliderNumPep.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                txtNumPep.setText(String.valueOf(newValue.intValue()));
            }
        });

        txtNumPep.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtNumPep.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        txtNumPep.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                    int newVal = Integer.valueOf(txtNumPep.getText());
                    if(newVal > maxNumPep){
                        newVal = maxNumPep;
                        txtNumPep.setText(String.valueOf(maxNumPep));
                    }

                    if(newVal < 1){
                        newVal = 1;
                        txtNumPep.setText(String.valueOf(1));
                    }

                    sliderNumPep.setValue(Integer.valueOf(newVal));
                }
            }
        });

    }

    @FXML private void submit(){
        submitted = true;
        tabSelected = tabPane.getSelectionModel().getSelectedItem().getText();
        Stage stage = (Stage) txtNumPep.getScene().getWindow();
        stage.close();
    }

    @FXML private void cancel(){
        submitted = false;
        Stage stage = (Stage) txtNumPep.getScene().getWindow();
        stage.close();
    }

    @FXML private void anySample(){
        anySample =rbAny.isSelected();
    }

    @FXML private void allSamples(){
        anySample = rbAny.isSelected();
    }

}
