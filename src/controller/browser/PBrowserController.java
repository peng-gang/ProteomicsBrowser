package controller.browser;

import controller.MainController;
import controller.result.PepCombineAllController;
import controller.result.PepCombineController;
import data.PepPos;
import data.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.transform.Transform;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by gpeng on 2/14/17.
 */

@SuppressWarnings("Duplicates")

public class PBrowserController implements Initializable {
    // controllers in view
    @FXML private Canvas canvas;
    @FXML private Slider sliderZoom;
    @FXML private Label lblPep;
    @FXML private Label lblPos;
    @FXML private ComboBox<String> combSample;
    @FXML private ComboBox<String> combProtein;
    @FXML private ScrollBar sbarCanvas;
    @FXML private VBox vbLegend;
    @FXML private ComboBox<Integer> combModiPos;
    @FXML private Label lblStart;
    @FXML private Label lblEnd;
    @FXML private VBox vBoxModification;
    @FXML private RadioButton rbYes;
    @FXML private RadioButton rbNo;
    //@FXML private VBox vbPepDoubleInfo;
    //@FXML private VBox vbPepStrInfo;
    @FXML private VBox vbPepInfo;
    @FXML private VBox vbModification;
    @FXML private Button btnCombine;
    @FXML private CheckBox cbCharge;
    @FXML private CheckBox cbSequence;
    @FXML private HBox hBoxView;

    private boolean initialized = false;

    private boolean pepCombined = false;

    //private boolean combinePosSel = false;

    /*
    //Legend 1
    private HBox hbAcetylation;
    private Label lbAcetylation1;
    private Label lbAcetylation2;

    private HBox hbCarbamidomethylation;
    private Label lbCarbamidomethylation1;
    private Label lbCarbamidomethylation2;

    private HBox hbPhosphorylation;
    private Label lbPhosphorylation1;
    private Label lbPhosphorylation2;

    private HBox hbOxidation;
    private Label lbOxidation1;
    private Label lbOxidation2;
    */


    //Legend 2
    private HBox hbAbundance1;
    private Label lbAbundance11;
    private Label lbAbundance12;

    private HBox hbAbundance2;
    private Label lbAbundance21;
    private Label lbAbundance22;

    private HBox hbAbundance3;
    private Label lbAbundance31;
    private Label lbAbundance32;

    private HBox hbAbundance4;
    private Label lbAbundance41;
    private Label lbAbundance42;

    private VBox vbAbundanceTag;
    private Label lbAbundanceTag1;
    private Label lbAbundanceTag2;



    private GraphicsContext gc;




    // data
    private SampleGroup sampleGroup;
    private MainController mainController;
    private ArrayList<String> sampleId;
    private ArrayList<String> proteinId;
    //private ArrayList<String> pepStrInfo;
    //private ArrayList<String> pepDoubleInfo;
    //private ArrayList<String> modiTypeCombined;


    private String selectedSample = null;
    private String selectedProtein = null;
    private Protein protein = null;
    private ArrayList<String> modificationTypeAll;

    private double scaleX = 1;
    private double scaleY = 1;

    private final double canvasWidth = 800;
    private final double canvasHeight = 600;


    //plot parameters
    private final double pixPerLocus = 10;
    private final double pixPerPep = 32;
    private final double pixPepGap = 1;
    private final double leftPix =10;
    private final double rightPix = 10;
    //private final double topPix = 10;
    private final double bottomPix = 10;
    private final double pixXLabel = 30;

    private int maxY;
    private int maxYCombined;
    private ArrayList<PepPos> arrangePep;
    private ArrayList<PepPos> arrangePepCombined;

    //selected modifications to show
    private ArrayList<String> modiSelected;

    public Protein getProtein() { return  protein; }
    public boolean getInitialized() { return initialized; }

    public String getSelectedSample() { return  selectedSample; }


    public ComboBox<String> getCombProtein(){
        return combProtein;
    }

    private void updateCombineNodes(){
        if(rbYes.isSelected()){
            /*
            for(Node node : vbPepDoubleInfo.getChildren()){
                node.setDisable(false);
                ((CheckBox) node).setSelected(false);
            }

            for(Node node : vbPepStrInfo.getChildren()){
                node.setDisable(false);
                ((CheckBox) node).setSelected(false);
            }
            */
            cbCharge.setSelected(false);
            cbCharge.setDisable(false);
            cbSequence.setSelected(true);
            cbSequence.setDisable(false);

            for(Node node : vbModification.getChildren()){
                node.setDisable(false);
                ((CheckBox) node).setSelected(false);
            }
            btnCombine.setDisable(false);
        } else {
            /*
            for(Node node : vbPepDoubleInfo.getChildren()){
                node.setDisable(true);
                ((CheckBox) node).setSelected(false);
            }

            for(Node node : vbPepStrInfo.getChildren()){
                node.setDisable(true);
                ((CheckBox) node).setSelected(false);
            }
            */

            cbCharge.setSelected(false);
            cbCharge.setDisable(true);
            cbSequence.setSelected(true);
            cbSequence.setDisable(true);

            for(Node node : vbModification.getChildren()){
                node.setDisable(true);
                ((CheckBox) node).setSelected(false);
            }
            btnCombine.setDisable(true);
        }
    }

    private void unCombined(){
        modiSelected.clear();
        modiSelected.addAll(protein.getModiTypeAll());
        for(Node node : vBoxModification.getChildren()){
            for(Node node1 :((HBox) node).getChildren()){
                ((CheckBox) node1).setSelected(true);
                node1.setDisable(false);
            }
        }

        combModiPos.getSelectionModel().clearSelection();
        combModiPos.getItems().clear();
        combModiPos.getItems().addAll(protein.getModiPosSel1(modiSelected));
        combModiPos.setDisable(false);
        //combinePosSel = false;
        //scaleX = 1;
        scaleY = 1;
        double sc = (canvasWidth - leftPix - rightPix)/(protein.getLength() * pixPerLocus);
        sliderZoom.setMin(Math.log(sc) /Math.log(2.0));
        sliderZoom.setValue(sliderZoom.getMin());
        scaleX = Math.pow(2.0, sliderZoom.getValue());
        orderPep();
        //too many modification to show in a canvas
        if(maxY * (pixPerPep + 2*pixPepGap) > (canvasHeight-bottomPix-pixXLabel)){
            scaleY = (canvasHeight-bottomPix-pixXLabel)/((pixPerPep + 2*pixPepGap)*maxY);
        }
        //initSBar(0);
        draw();
        mainController.pepFilterSetDisable(false);
    }

    @FXML private void pepCombineYes(ActionEvent event){
        updateCombineNodes();
        if(!rbYes.isSelected()){
            pepCombined = false;
            unCombined();
        }
    }

    @FXML private void pepCombineNo(ActionEvent event){
        updateCombineNodes();
        if(!rbYes.isSelected()){
            pepCombined = false;
            unCombined();
        }
    }

    @FXML private void combine(ActionEvent event){
        if(!cbSequence.isSelected()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Peptide Combination");
            alert.setHeaderText("Please choose the samples for combination:");
            ButtonType allSample = new ButtonType("All Samples");
            ButtonType selSample = new ButtonType("Sample(s) shown in the Browser");

            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(allSample, selSample);

            Optional<ButtonType> option = alert.showAndWait();
            if(option.get()==null){
                return;
            } else if(option.get()==allSample){
                int pos = combModiPos.getValue()-1;

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/result/PepCombineAll.fxml"));
                try{
                    Parent root = loader.load();

                    Stage pepCombine = new Stage();
                    pepCombine.setTitle("Peptide Combination");
                    pepCombine.setScene(new Scene(root, 400, 500));
                    pepCombine.initModality(Modality.WINDOW_MODAL);
                    pepCombine.initOwner(canvas.getScene().getWindow());

                    PepCombineAllController controller = loader.getController();
                    controller.setData(sampleGroup, selectedProtein, pos);
                    controller.combinePeptides();

                    pepCombine.showAndWait();

                } catch (IOException e){
                    e.printStackTrace();
                }

                return;
            }

            int pos = combModiPos.getValue()-1;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/result/PepCombine.fxml"));
            try {
                Parent root = loader.load();

                Stage pepCombine = new Stage();
                pepCombine.setTitle("Peptide Combination");
                pepCombine.setScene(new Scene(root, 950, 450));
                pepCombine.initModality(Modality.WINDOW_MODAL);
                pepCombine.initOwner(canvas.getScene().getWindow());

                PepCombineController controller = loader.getController();
                controller.setData(sampleGroup, protein, pos);

                pepCombine.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }


        pepCombined = true;
        //ArrayList<String> doubleInfoCriteria = new ArrayList<>();
        //ArrayList<String> strInfoCriteria = new ArrayList<>();
        ArrayList<String> modiCriteria = new ArrayList<>();

        /*
        for(Node node : vbPepDoubleInfo.getChildren()){
            if(((CheckBox) node).isSelected()){
                doubleInfoCriteria.add(node.getUserData().toString());
            }
        }

        for(Node node : vbPepStrInfo.getChildren()){
            if(((CheckBox) node).isSelected()){
                strInfoCriteria.add(node.getUserData().toString());
            }
        }
        */

        for(Node node : vbModification.getChildren()){
            if(((CheckBox) node).isSelected()){
                modiCriteria.add(node.getUserData().toString());
            }
        }

        //System.out.println(doubleInfoCriteria);
        //System.out.println(strInfoCriteria);
        //System.out.println(modiCriteria);

        //.combinePep(doubleInfoCriteria, strInfoCriteria, modiCriteria);
        protein.combinePep(cbCharge.isSelected(), modiCriteria);
        protein.setAbundanceCombinedRange();
        modiSelected.clear();
        modiSelected.addAll(modiCriteria);
        for(Node node : vBoxModification.getChildren()){
            for(Node node1 : ((HBox) node).getChildren()){
                if(!modiCriteria.contains(node1.getUserData().toString())){
                    ((CheckBox) node1).setSelected(false);
                    node1.setDisable(true);
                } else {
                    ((CheckBox) node1).setSelected(true);
                    node1.setDisable(false);
                }
            }

        }

        combModiPos.getSelectionModel().clearSelection();
        combModiPos.getItems().clear();
        combModiPos.getItems().addAll(protein.getModiCombinedPos1(modiSelected));
        combModiPos.setDisable(false);
        //combinePosSel = false;
        //scaleX = 1;
        scaleY = 1;
        double sc = (canvasWidth - leftPix - rightPix)/(protein.getLength() * pixPerLocus);
        sliderZoom.setMin(Math.log(sc) /Math.log(2.0));
        sliderZoom.setValue(sliderZoom.getMin());
        scaleX = Math.pow(2.0, sliderZoom.getValue());
        orderPep();
        //too many modification to show in a canvas
        if(maxYCombined * (pixPerPep + 2*pixPepGap) > (canvasHeight-bottomPix-pixXLabel)){
            scaleY = (canvasHeight-bottomPix-pixXLabel)/((pixPerPep + 2*pixPepGap)*maxYCombined);
        }
        //initSBar(0);
        draw();

        mainController.pepFilterSetDisable(true);

        //update show

        return;
    }

    @FXML private void selSequence(ActionEvent event){
        if(combModiPos.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Peptide Combination");
            alert.setHeaderText(null);
            alert.setContentText("Please select a position with modification from combobox above before peptide combination.");
            alert.showAndWait();
            cbSequence.setSelected(true);
        }

        if(cbSequence.isSelected()){
            cbCharge.setDisable(false);

            for(Node node : vbModification.getChildren()){
                node.setDisable(false);
            }

        } else {
            cbCharge.setSelected(false);
            cbCharge.setDisable(true);

            for(Node node : vbModification.getChildren()){
                ((CheckBox) node).setSelected(false);
                node.setDisable(true);
            }
        }
        return;
    }

    public void proteinFilter(Set<String> modiSel, Set<String> modiRm, int numPep){
        ArrayList<String> ptSort = new ArrayList<> ();

        for(String pid : proteinId){
            boolean include = true;

            if(modiRm.size()!=0) {
                for(String mType: sampleGroup.getProtein(selectedSample, pid).getModiTypeAll()){
                    if(modiRm.contains(mType)){
                        include = false;
                        break;
                    }
                }
                if(!include){
                    continue;
                }
            }

            if(modiSel.size()!=0){
                int numInclude = 0;
                for(String mType: sampleGroup.getProtein(selectedSample, pid).getModiTypeAll()){
                    if(modiSel.contains(mType)){
                        numInclude++;
                    }
                }
                if(numInclude!=modiSel.size()){
                    continue;
                }
            }

            if(include){
                if(sampleGroup.getProtein(selectedSample, pid).getPeptides().size() >= numPep){
                    ptSort.add(pid);
                }
            }
        }

        if(ptSort.size()==0){
            combProtein.getItems().clear();
            selectedProtein=null;
            draw();
            return;
        }

        Collections.sort(ptSort);
        combProtein.getItems().clear();
        combProtein.getItems().addAll(ptSort);
        combProtein.getSelectionModel().select(0);
        selectedProtein = combProtein.getValue();
    }

    public void proteinFilter(int numModiPos, int numPep){
        ArrayList<String> ptSort = new ArrayList<> ();
        for(String pid : proteinId){
            if(sampleGroup.getProtein(selectedSample, pid).getModiPos().size() >= numModiPos &&
                    sampleGroup.getProtein(selectedSample, pid).getPeptides().size() >= numPep){
                ptSort.add(pid);
            }
        }

        if(ptSort.size()==0){
            combProtein.getItems().clear();
            selectedProtein=null;
            draw();
            return;
        }

        Collections.sort(ptSort);
        combProtein.getItems().clear();
        combProtein.getItems().addAll(ptSort);
        combProtein.getSelectionModel().select(0);
        selectedProtein = combProtein.getValue();
    }

    public void proteinFilterType(int numModiType, int numPep){
        ArrayList<String> ptSort = new ArrayList<> ();
        for(String pid : proteinId){
            if(sampleGroup.getProtein(selectedSample, pid).getModiTypeAll().size() >= numModiType &&
                    sampleGroup.getProtein(selectedSample, pid).getPeptides().size() >= numPep){
                ptSort.add(pid);
            }
        }

        if(ptSort.size()==0){
            combProtein.getItems().clear();
            selectedProtein=null;
            draw();
            return;
        }

        Collections.sort(ptSort);
        combProtein.getItems().clear();
        combProtein.getItems().addAll(ptSort);
        combProtein.getSelectionModel().select(0);
        selectedProtein = combProtein.getValue();
    }

    public void saveProteinFilter(File txtFile){
        if(combProtein.getItems().size()==0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Export Protein after Filtering");
            alert.setHeaderText(null);
            alert.setContentText("There is no protein after filtering");
            alert.showAndWait();

            return;
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(txtFile);
        } catch (IOException e){
            e.printStackTrace();
        }

        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        try {
            bufferedWriter.write("Protein");
            bufferedWriter.newLine();
            for(String pt : combProtein.getItems()){
                bufferedWriter.write(pt);
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

    public void savePeptideFilter(File txtFile){
        if(selectedProtein==null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Export Peptides after Filtering");
            alert.setHeaderText(null);
            alert.setContentText("There is no protein selected");
            alert.showAndWait();
            return;
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(txtFile);
        } catch (IOException e){
            e.printStackTrace();
        }

        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        try {
            bufferedWriter.write("Peptide id");
            bufferedWriter.newLine();
            for(PepPos pepPos : arrangePep){
                bufferedWriter.write(pepPos.getPep().getId());
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

    public void setData(SampleGroup sampleGroup, MainController mainController){
        this.sampleGroup = sampleGroup;
        this.mainController = mainController;
        sampleId = new ArrayList<>(sampleGroup.getSampleId());
        proteinId = new ArrayList<>(sampleGroup.getProteinId());

        //pepStrInfo = new ArrayList<>(sampleGroup.getPepStrInfo());
        //pepDoubleInfo = new ArrayList<>(sampleGroup.getPepDoubleInfo());

        /*
        for(String info : pepDoubleInfo){
            CheckBox checkBox = new CheckBox(info);
            checkBox.setSelected(false);
            checkBox.setDisable(true);
            checkBox.setUserData(info);
            vbPepDoubleInfo.getChildren().add(checkBox);
        }

        for(String info : pepStrInfo){
            CheckBox checkBox = new CheckBox(info);
            checkBox.setSelected(false);
            checkBox.setDisable(true);
            checkBox.setUserData(info);
            vbPepStrInfo.getChildren().addAll(checkBox);
        }
        */
    }

    public void updatePep(){
        //scaleX = 1;
        scaleY = 1;
        double sc = (canvasWidth - leftPix - rightPix)/(protein.getLength() * pixPerLocus);
        sliderZoom.setMin(Math.log(sc) /Math.log(2.0));
        sliderZoom.setValue(sliderZoom.getMin());
        scaleX = Math.pow(2.0, sliderZoom.getValue());
        orderPep();
        //too many modification to show in a canvas
        if(maxY * (pixPerPep + 2*pixPepGap) > (canvasHeight-bottomPix-pixXLabel)){
            scaleY = (canvasHeight-bottomPix-pixXLabel)/((pixPerPep + 2*pixPepGap)*maxY);
        }
        initSBar(0);
        draw();
    }

    private void initModification(){
        vBoxModification.getChildren().clear();
        modiSelected.clear();
        modiSelected.addAll(modificationTypeAll);

        int numModi = modificationTypeAll.size();
        for(int i=0; i<(int)(numModi/2); i++){
            HBox hb = new HBox();
            hb.setSpacing(20);

            CheckBox checkBox1 = new CheckBox(modificationTypeAll.get(i*2));
            checkBox1.setUserData(modificationTypeAll.get(i*2));
            CheckBox checkBox2 = new CheckBox(modificationTypeAll.get(i*2+1));
            checkBox2.setUserData(modificationTypeAll.get(i*2+1));

            checkBox1.setSelected(true);
            checkBox2.setSelected(true);
            checkBox1.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(modiSelected.contains(checkBox1.getText())){
                        if(!newValue){
                            modiSelected.remove(checkBox1.getText());
                        }
                    } else {
                        if(newValue){
                            modiSelected.add(checkBox1.getText());
                        }
                    }
                    updateCombModiPos();
                    draw();
                }
            });

            checkBox2.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(modiSelected.contains(checkBox2.getText())){
                        if(!newValue){
                            modiSelected.remove(checkBox2.getText());
                        }
                    } else {
                        if(newValue){
                            modiSelected.add(checkBox2.getText());
                        }
                    }
                    updateCombModiPos();
                    draw();
                }
            });

            hb.getChildren().addAll(checkBox1, checkBox2);
            vBoxModification.getChildren().add(hb);
        }

        if(numModi%2 == 1){
            HBox hb = new HBox();
            CheckBox checkBox = new CheckBox(modificationTypeAll.get(modificationTypeAll.size()-1));
            checkBox.setSelected(true);
            checkBox.setUserData(modificationTypeAll.get(modificationTypeAll.size()-1));

            checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(modiSelected.contains(checkBox.getText())){
                        if(!newValue){
                            modiSelected.remove(checkBox.getText());
                        }
                    } else {
                        if(newValue){
                            modiSelected.add(checkBox.getText());
                        }
                    }
                    updateCombModiPos();
                    draw();
                }
            });

            hb.getChildren().add(checkBox);
            vBoxModification.getChildren().add(hb);
        }
    }


    private void initModiCombine(){
        vbModification.getChildren().clear();

        for(String modiType : modificationTypeAll){
            CheckBox checkBox = new CheckBox(modiType);
            checkBox.setSelected(false);
            checkBox.setDisable(!pepCombined);
            checkBox.setUserData(modiType);
            vbModification.getChildren().add(checkBox);
        }
    }

    public void show(){
        combSample.getItems().addAll(sampleId);
        ArrayList<String> ptSort = new ArrayList<>(proteinId);
        Collections.sort(ptSort);
        combProtein.getItems().addAll(ptSort);

        combSample.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selectedSample = newValue;
                ArrayList<String> ptSort = new ArrayList<>(proteinId);
                Collections.sort(ptSort);
                combProtein.getItems().clear();
                combProtein.getItems().addAll(ptSort);
                combProtein.getSelectionModel().select(0);
                selectedProtein = combProtein.getValue();

                if(selectedProtein != null && selectedSample !=null){
                    protein = sampleGroup.getProtein(selectedSample, selectedProtein);
                    //init modification selection
                    modificationTypeAll = new ArrayList<>(protein.getModiTypeAll());

                    rbYes.setDisable(false);
                    rbNo.setDisable(false);

                    vBoxModification.getChildren().clear();
                    initModification();

                    pepCombined = false;
                    rbNo.setSelected(true);
                    updateCombineNodes();
                    initModiCombine();

                    //clear combModiPos first
                    combModiPos.getSelectionModel().clearSelection();
                    combModiPos.getItems().clear();
                    combModiPos.getItems().addAll(protein.getModiPosSel1(modiSelected));
                    combModiPos.setDisable(false);
                    //combinePosSel = false;
                    //scaleX = 1;
                    scaleY = 1;
                    double sc = (canvasWidth - leftPix - rightPix)/(protein.getLength() * pixPerLocus);
                    sliderZoom.setMin(Math.log(sc) /Math.log(2.0));
                    sliderZoom.setValue(sliderZoom.getMin());
                    scaleX = Math.pow(2.0, sliderZoom.getValue());
                    orderPep();
                    //too many modification to show in a canvas
                    if(maxY * (pixPerPep + 2*pixPepGap) > (canvasHeight-bottomPix-pixXLabel)){
                        scaleY = (canvasHeight-bottomPix-pixXLabel)/((pixPerPep + 2*pixPepGap)*maxY);
                    }
                    initSBar(0);
                    draw();
                }
            }
        });

        combProtein.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selectedProtein = newValue;
                if(selectedProtein != null && selectedSample !=null){
                    protein = sampleGroup.getProtein(selectedSample, selectedProtein);
                    modificationTypeAll = new ArrayList<String>(protein.getModiTypeAll());

                    rbYes.setDisable(false);
                    rbNo.setDisable(false);

                    vBoxModification.getChildren().clear();
                    initModification();

                    pepCombined = false;
                    rbNo.setSelected(true);
                    updateCombineNodes();
                    initModiCombine();

                    combModiPos.getSelectionModel().clearSelection();
                    combModiPos.getItems().clear();
                    combModiPos.getItems().addAll(protein.getModiPosSel1(modiSelected));
                    combModiPos.setDisable(false);
                    //combinePosSel = false;
                    //scaleX = 1;
                    scaleY = 1;
                    double sc = (canvasWidth - leftPix - rightPix)/(protein.getLength() * pixPerLocus);
                    sliderZoom.setMin(Math.log(sc) / Math.log(2.0));
                    sliderZoom.setValue(sliderZoom.getMin());
                    scaleX = Math.pow(2.0, sliderZoom.getValue());
                    orderPep();
                    if(maxY * (pixPerPep + 2*pixPepGap) > (canvasHeight-bottomPix-pixXLabel)){
                        scaleY = (canvasHeight-bottomPix-pixXLabel)/((pixPerPep + 2*pixPepGap)*maxY);
                    }
                    initSBar(0);
                    draw();
                }
            }
        });

        sbarCanvas.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                draw();
            }
        });

        sliderZoom.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double newV = Math.pow(2.0, newValue.doubleValue());
                double oldV = Math.pow(2.0, oldValue.doubleValue());
                scaleX = newV;
                double sbVal = sbarCanvas.getValue();
                initSBar(sbVal * newV/oldV);
                combModiPos.getSelectionModel().clearSelection();
                draw();
            }
        });


        /*
        cbAcetylation.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(modiSelected.contains(Modification.ModificationType.Acetylation)){
                    if(!cbAcetylation.isSelected()){
                        modiSelected.remove(Modification.ModificationType.Acetylation);
                    }
                } else {
                    if(cbAcetylation.isSelected()){
                        modiSelected.add(Modification.ModificationType.Acetylation);
                    }
                }
                updateCombModiPos();
                draw();
            }
        });

        cbCarbamidomethylation.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(modiSelected.contains(Modification.ModificationType.Carbamidomethylation)){
                    if(!cbCarbamidomethylation.isSelected()){
                        modiSelected.remove(Modification.ModificationType.Carbamidomethylation);
                    }
                } else {
                    if(cbCarbamidomethylation.isSelected()){
                        modiSelected.add(Modification.ModificationType.Carbamidomethylation);
                    }
                }
                updateCombModiPos();
                draw();
            }
        });

        cbOxidation.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(modiSelected.contains(Modification.ModificationType.Oxidation)){
                    if(!cbOxidation.isSelected()){
                        modiSelected.remove(Modification.ModificationType.Oxidation);
                    }
                } else {
                    if(cbOxidation.isSelected()){
                        modiSelected.add(Modification.ModificationType.Oxidation);
                    }
                }
                updateCombModiPos();
                draw();
            }
        });

        cbPhosphorylation.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(modiSelected.contains(Modification.ModificationType.Phosphorylation)){
                    if(!cbPhosphorylation.isSelected()){
                        modiSelected.remove(Modification.ModificationType.Phosphorylation);
                    }
                } else {
                    if(cbPhosphorylation.isSelected()){
                        modiSelected.add(Modification.ModificationType.Phosphorylation);
                    }
                }
                updateCombModiPos();
                draw();
            }
        });
        */


        combModiPos.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if(newValue != null){
                    //combinePosSel = true;
                    int centerPos = newValue-1;
                    double st = centerPos * pixPerLocus * scaleX - canvas.getWidth() / 2.0;
                    if(st < 0){
                        st = 0;
                    }
                    if(st > sbarCanvas.getMax()){
                        st = sbarCanvas.getMax();
                    }
                    sbarCanvas.setValue(st);
                    draw();
                }
            }
        });
    }

    public void updateCombModiPos(){
        combModiPos.getSelectionModel().clearSelection();
        combModiPos.setValue(null);
        combModiPos.getItems().clear();
        //combinePosSel = false;

        combModiPos.getItems().addAll(protein.getModiPosSel1(modiSelected));
        if(combModiPos.getItems().size() > 0){
            combModiPos.setDisable(false);
        } else {
            combModiPos.setDisable(true);
        }
    }

    private String computeRgbString(Color color) {
        String rlt = "rgb(" + (int) (255 * color.getRed()) + ","
                + (int) (255 * color.getGreen()) + ","
                + (int) (255 * color.getBlue()) + ")";
        return rlt;
    }

    private void legend(){
        vbLegend.getChildren().clear();

        for(String modiType : modiSelected){
            HBox hbLegend = new HBox();
            hbLegend.setAlignment(Pos.CENTER_LEFT);
            hbLegend.setSpacing(5);
            Label l1 = new Label("");
            String st = "-fx-background-color: " + computeRgbString(sampleGroup.getModificationColor(modiType));
            l1.setStyle(st);
            l1.setPrefHeight(10);
            l1.setPrefWidth(10);
            Label l2;
            if(modiType.length() > 12){
                l2 = new Label(modiType.substring(0, 10) + "...");
            } else {
                l2 = new Label(modiType);
            }

            hbLegend.getChildren().addAll(l1, l2);
            vbLegend.getChildren().add(hbLegend);
        }
        /*
        if(cbAcetylation.isSelected()){
            vbLegend.getChildren().add(hbAcetylation);
        }
        if(cbCarbamidomethylation.isSelected()){
            vbLegend.getChildren().add(hbCarbamidomethylation);
        }
        if(cbOxidation.isSelected()){
            vbLegend.getChildren().add(hbOxidation);
        }
        if(cbPhosphorylation.isSelected()){
            vbLegend.getChildren().add(hbPhosphorylation);
        }*/

        vbLegend.getChildren().add(hbAbundance1);
        vbLegend.getChildren().add(hbAbundance2);
        vbLegend.getChildren().add(hbAbundance3);
        vbLegend.getChildren().add(hbAbundance4);
        if(pepCombined){
            lbAbundanceTag2.setText(String.format("%9.3g",protein.getAbundanceMaxCombined()));
        } else {
            lbAbundanceTag2.setText(String.format("%9.3g",protein.getAbundanceMax()));
        }

        vbLegend.getChildren().addAll(vbAbundanceTag);

    }

    private void initSBar(double val){
        double plotWidth = protein.getLength() * pixPerLocus * scaleX + leftPix + rightPix;
        if(plotWidth > canvasWidth){
            sbarCanvas.setVisible(true);
            sbarCanvas.setMax(plotWidth-canvasWidth);
            sbarCanvas.setMin(0);
            if(val < 0){
                val = 0;
            }
            if(val > plotWidth-canvasWidth){
                val = plotWidth-canvasWidth;
            }
            sbarCanvas.setValue(val);
        } else {
            sbarCanvas.setMax(0);
            sbarCanvas.setMin(0);
            sbarCanvas.setValue(0);
            sbarCanvas.setVisible(false);
        }
    }

    private Image createHatch(int r, int g, int b){
        Pane pane = new Pane();
        pane.setPrefSize(20, 20);
        //pane.setStyle("-fx-background-color: rgb(r,g,b);");
        Line l1 = new Line(0, 0, 20, 0);
        Line l2 = new Line(0, 10, 20, 10);
        Line l3 = new Line(0, 20, 20, 20);
        //Line bw = new Line(0, 20, 20, 0);
        l1.setStroke(Color.rgb(r,g,b));
        l2.setStroke(Color.rgb(r,g,b));
        l3.setStroke(Color.rgb(r,g,b));
        //-fx-stroke
        //fw.setStyle("-fx-stroke: rgb(r,g,b);");
        //bw.setStroke(Color.WHITE);
        l1.setStrokeWidth(5);
        l2.setStrokeWidth(5);
        l3.setStrokeWidth(5);
        //bw.setStrokeWidth(5);
        pane.getChildren().addAll(l1, l2, l3);
        new Scene(pane);
        return pane.snapshot(null, null);
    }

    public void draw(){
        /*
        int canvasHeight = (pixPepGap*2 + pixPerPep) * maxY + pixXLabel + topPix + bottomPix;
        if(canvasHeight > 500){
            scaleY = 500.0 / canvasHeight;
        } else {
            scaleY = 1;
        }
        */
        //start 0, end 0

        initialized = true;
        double st = sbarCanvas.getValue();

        //Start to plot
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        if(combProtein.getItems().size()==0){
            return;
        }

        if(pepCombined){
            for(PepPos pp : arrangePepCombined){
                double tlX = leftPix + pp.getStart()*pixPerLocus*scaleX + 1;
                double tlY = canvasHeight - pixXLabel - bottomPix - (pp.getY() + 1) * (pixPerPep  + pixPepGap * 2) * scaleY + 1;
                double w = pixPerLocus * pp.getPep().getLength()*scaleX - 2;
                double h = pixPerPep * scaleY;

                tlX = tlX - st;
                double rX = tlX + w;


                if(tlX < canvasWidth && rX > 0){
                    int range = pp.getPep().getAbundanceRange();
                    gc.setFill(Color.rgb(220-15*range, 220-15*range, 220-15*range));
                    tlX = Math.max(tlX, 0);
                    rX = Math.min(rX, canvasWidth);
                    gc.fillRect(tlX, tlY, (rX-tlX), h);
                }

                if(pp.getPep().isModified()){
                    ArrayList<Modification> modifications = pp.getPep().getModifications();
                    for(String modiType:modiSelected){
                        gc.setFill(sampleGroup.getModificationColor(modiType));
                        for(Modification modi : modifications){
                            if(modi.getModificationType().equals(modiType)){
                                for(int i=0; i<modi.getPos().size(); i++){


                                    if(modi.getPercent().get(i) < 0){
                                        double leftX = leftPix + (modi.getPos().get(i) + pp.getStart()) * pixPerLocus * scaleX + 0.5;
                                        double topY = tlY;
                                        double width = pixPerLocus * scaleX - 0.5;
                                        double height = scaleY * pixPerPep;

                                        leftX = leftX - st;
                                        double rightX = leftX + width;
                                        if(leftX < canvasWidth && rightX > 0){
                                            leftX = Math.max(leftX, 0);
                                            rightX = Math.min(rightX, canvasWidth);

                                            //
                                            Stop[] stops = new Stop[] {new Stop(0, Color.WHITE), new Stop(1, sampleGroup.getModificationColor(modiType))};
                                            LinearGradient lg = new LinearGradient(0, 0, 0.1, 0.1, true, CycleMethod.REPEAT, stops);
                                            gc.setFill(lg);
                                            gc.fillRect(leftX, topY, (rightX-leftX), height);
                                        }
                                    } else {
                                        double leftX = leftPix + (modi.getPos().get(i) + pp.getStart()) * pixPerLocus * scaleX + 0.5;
                                        double topY = tlY + (1.0-modi.getPercent().get(i)/100.0) * pixPerPep * scaleY;
                                        double width = pixPerLocus * scaleX - 0.5;
                                        double height = scaleY * pixPerPep * modi.getPercent().get(i)/100.0;

                                        leftX = leftX - st;
                                        double rightX = leftX + width;
                                        if(leftX < canvasWidth && rightX > 0){
                                            leftX = Math.max(leftX, 0);
                                            rightX = Math.min(rightX, canvasWidth);
                                            gc.setFill(sampleGroup.getModificationColor(modiType));
                                            gc.fillRect(leftX, topY, (rightX-leftX), height);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            double fontSize = pixPerLocus * scaleX / 2.0 + 3.5;
            String proteinSeq = protein.getSequence();
            ArrayList<Integer> modiPos = protein.getModiCombinedPos(modiSelected);
            int xAxisStart = (int)(st / (pixPerLocus * scaleX));
            int xAxisEnd = (int)(0.5 + (st + canvasWidth)/(pixPerLocus * scaleX));

            for(int i = xAxisStart; i <= xAxisEnd ;i++){
                gc.setFont(new Font("Courier New", fontSize));
                if(i>=proteinSeq.length()){
                    break;
                }
                double leftX = leftPix + (i + 0.3) * pixPerLocus * scaleX - st;
                double topY =  canvasHeight - pixXLabel - bottomPix + pixPerLocus * 2;
                if(modiPos.contains(i)){
                    gc.setFill(Color.CRIMSON);
                } else {
                    gc.setFill(Color.BLACK);
                }
                gc.fillText(Character.toString(proteinSeq.charAt(i)), leftX, topY);
            }

            lblStart.setText("Start:" + (xAxisStart + 1));
            //Warning: check whether need to + 1 here
            lblEnd.setText("End:" + (xAxisEnd-1));

            legend();

            return;
        }


        for(PepPos pp : arrangePep){
            //top left coordinate
            double tlX = leftPix + pp.getStart()*pixPerLocus*scaleX + 1;
            double tlY = canvasHeight - pixXLabel - bottomPix - (pp.getY() + 1) * (pixPerPep  + pixPepGap * 2) * scaleY + 1;
            double w = pixPerLocus * pp.getPep().getLength()*scaleX - 2;
            double h = pixPerPep * scaleY;

            tlX = tlX - st;
            double rX = tlX + w;


            if(tlX < canvasWidth && rX > 0){
                int range = pp.getPep().getAbundanceRange();
                if(pp.getPep().getMultiMatch() > 0){
                    //Image hatch = createHatch(220-15*range, 220-15*range, 220-15*range);

                    Stop[] stops = new Stop[] {new Stop(0, Color.WHITE), new Stop(1, Color.rgb(220-15*range, 220-15*range, 220-15*range))};
                    LinearGradient lg = new LinearGradient(0, 0, 0, 0.2, true, CycleMethod.REPEAT, stops);
                    gc.setFill(lg);
                    tlX = Math.max(tlX, 0);
                    rX = Math.min(rX, canvasWidth);

                    //ImagePattern pattern = new ImagePattern(hatch, 0, 0, 20, 20, false);
                    //gc.setFill(pattern);

                    gc.fillRect(tlX, tlY, (rX-tlX), h);

                    if(pp.getPep().isOtherProtein()){
                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(tlX, tlY, (rX-tlX), h);
                    }
                } else {
                    gc.setFill(Color.rgb(220-15*range, 220-15*range, 220-15*range));
                    tlX = Math.max(tlX, 0);
                    rX = Math.min(rX, canvasWidth);
                    gc.fillRect(tlX, tlY, (rX-tlX), h);

                    if(pp.getPep().isOtherProtein()){
                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(tlX, tlY, (rX-tlX), h);
                    }
                }
            }

            if(pp.getPep().isModified()){
                ArrayList<Modification> modifications = pp.getPep().getModifications();
                for(String modiType:modiSelected){

                    for(Modification modi : modifications){
                        if(modi.getModificationType().equals(modiType)){
                            for(int i=0; i<modi.getPos().size(); i++){
                                if(modi.getPercent().get(i) < 0){
                                    double leftX = leftPix + (modi.getPos().get(i) + pp.getStart()) * pixPerLocus * scaleX + 0.5;
                                    double topY = tlY;
                                    double width = pixPerLocus * scaleX - 0.5;
                                    double height = scaleY * pixPerPep;

                                    leftX = leftX - st;
                                    double rightX = leftX + width;
                                    if(leftX < canvasWidth && rightX > 0){
                                        leftX = Math.max(leftX, 0);
                                        rightX = Math.min(rightX, canvasWidth);

                                        //
                                        Stop[] stops = new Stop[] {new Stop(0, Color.WHITE), new Stop(1, sampleGroup.getModificationColor(modiType))};
                                        LinearGradient lg = new LinearGradient(0, 0, 0.1, 0.1, true, CycleMethod.REPEAT, stops);
                                        gc.setFill(lg);
                                        gc.fillRect(leftX, topY, (rightX-leftX), height);
                                    }
                                } else {
                                    double leftX = leftPix + (modi.getPos().get(i) + pp.getStart()) * pixPerLocus * scaleX + 0.5;
                                    double topY = tlY + (1.0-modi.getPercent().get(i)/100.0) * pixPerPep * scaleY;
                                    double width = pixPerLocus * scaleX - 0.5;
                                    double height = scaleY * pixPerPep * modi.getPercent().get(i)/100.0;

                                    leftX = leftX - st;
                                    double rightX = leftX + width;
                                    if(leftX < canvasWidth && rightX > 0){
                                        leftX = Math.max(leftX, 0);
                                        rightX = Math.min(rightX, canvasWidth);
                                        gc.setFill(sampleGroup.getModificationColor(modiType));
                                        gc.fillRect(leftX, topY, (rightX-leftX), height);
                                    }
                                }

                            }
                        }
                    }
                }

                /*
                if(cbAcetylation.isSelected()){
                    gc.setFill(Color.MAGENTA);
                    for(Modification modi : modifications){
                        if(modi.getType() == Modification.ModificationType.Acetylation){
                            for(int i=0; i<modi.getPos().size(); i++){
                                double leftX = leftPix + (modi.getPos().get(i) + pp.getStart()) * pixPerLocus * scaleX + 0.5;
                                double topY = tlY + (1.0-modi.getPercent().get(i)/100.0) * pixPerPep * scaleY;
                                double width = pixPerLocus * scaleX - 0.5;
                                double height = scaleY * pixPerPep * modi.getPercent().get(i)/100.0;

                                leftX = leftX - st;
                                double rightX = leftX + width;
                                if(leftX < canvasWidth && rightX > 0){
                                    leftX = Math.max(leftX, 0);
                                    rightX = Math.min(rightX, canvasWidth);
                                    gc.fillRect(leftX, topY, (rightX-leftX), height);
                                }
                            }
                        }
                    }
                }

                if(cbCarbamidomethylation.isSelected()){
                    gc.setFill(Color.FORESTGREEN);
                    for(Modification modi : modifications){
                        if(modi.getType() == Modification.ModificationType.Carbamidomethylation){
                            for(int i=0; i<modi.getPos().size(); i++){
                                double leftX = leftPix + (modi.getPos().get(i) + pp.getStart()) * pixPerLocus * scaleX + 0.5;
                                double topY = tlY + (1.0-modi.getPercent().get(i)/100.0) * pixPerPep * scaleY;
                                double width = pixPerLocus * scaleX - 0.5;
                                double height = scaleY * pixPerPep * modi.getPercent().get(i)/100.0;

                                leftX = leftX - st;
                                double rightX = leftX + width;
                                if(leftX < canvasWidth && rightX > 0){
                                    leftX = Math.max(leftX, 0);
                                    rightX = Math.min(rightX, canvasWidth);
                                    gc.fillRect(leftX, topY, (rightX-leftX), height);
                                }
                            }
                        }
                    }
                }

                if(cbPhosphorylation.isSelected()){
                    gc.setFill(Color.CYAN);
                    for(Modification modi : modifications){
                        if(modi.getType() == Modification.ModificationType.Phosphorylation){
                            for(int i=0; i<modi.getPos().size(); i++){
                                double leftX = leftPix + (modi.getPos().get(i) + pp.getStart()) * pixPerLocus * scaleX + 0.5;
                                double topY = tlY + (1.0-modi.getPercent().get(i)/100.0) * pixPerPep * scaleY;
                                double width = pixPerLocus * scaleX - 0.5;
                                double height = scaleY * pixPerPep * modi.getPercent().get(i)/100.0;

                                leftX = leftX - st;
                                double rightX = leftX + width;
                                if(leftX < canvasWidth && rightX > 0){
                                    leftX = Math.max(leftX, 0);
                                    rightX = Math.min(rightX, canvasWidth);
                                    gc.fillRect(leftX, topY, (rightX-leftX), height);
                                }
                            }
                        }
                    }
                }

                if(cbOxidation.isSelected()){
                    gc.setFill(Color.GOLD);
                    for(Modification modi : modifications){
                        if(modi.getType() == Modification.ModificationType.Oxidation){
                            for(int i=0; i<modi.getPos().size(); i++){
                                double leftX = leftPix + (modi.getPos().get(i) + pp.getStart()) * pixPerLocus * scaleX + 0.5;
                                double topY = tlY + (1.0-modi.getPercent().get(i)/100.0) * pixPerPep * scaleY;
                                double width = pixPerLocus * scaleX - 0.5;
                                double height = scaleY * pixPerPep * modi.getPercent().get(i)/100.0;

                                leftX = leftX - st;
                                double rightX = leftX + width;
                                if(leftX < canvasWidth && rightX > 0){
                                    leftX = Math.max(leftX, 0);
                                    rightX = Math.min(rightX, canvasWidth);
                                    gc.fillRect(leftX, topY, (rightX-leftX), height);
                                }
                            }
                        }
                    }
                }
                */
            }
        }

        //X Axis
        double fontSize = pixPerLocus * scaleX /2.0  + 3.5;

        //if(fontsize > 16){
        //    fontsize = 16;
        //}

        String proteinSeq = protein.getSequence();
        ArrayList<Integer> modiPos = protein.getModiPosSel(modiSelected);
        int xAxisStart = (int)(st / (pixPerLocus * scaleX));
        int xAxisEnd = (int)(0.5 + (st + canvasWidth)/(pixPerLocus * scaleX));

        for(int i = xAxisStart; i <= xAxisEnd ;i++){
            gc.setFont(new Font("Courier New", fontSize));
            if(i>=proteinSeq.length()){
                break;
            }
            double leftX = leftPix + (i + 0.3) * pixPerLocus * scaleX - st;
            double topY =  canvasHeight - pixXLabel - bottomPix + pixPerLocus * 2;
            if(modiPos.contains(i)){
                gc.setFill(Color.CRIMSON);
            } else {
                gc.setFill(Color.BLACK);
            }
            gc.fillText(Character.toString(proteinSeq.charAt(i)), leftX, topY);
            //position
            //gc.setFill(Color.BLACK);

            //gc.setFont(new Font("Courier New", fontSize/2));

            //gc.fillText(Integer.toString(i+1), leftX, (topY + fontSize));
        }

        lblStart.setText("Start:" + (xAxisStart + 1));
        //Warning: check whether need to + 1 here
        lblEnd.setText("End:" + (xAxisEnd-1));


        legend();

    }

    private void orderPep(){
        if(pepCombined){
            arrangePepCombined.clear();
            ArrayList<Integer> startCombined = protein.getPepStartCombined();
            ArrayList<Integer> endCombined = protein.getPepEndCombined();
            ArrayList<Peptide> pepCombined = protein.getPeptidesCombined();
            if(startCombined.size()==0){
                return;
            }

            for(int i=0; i<startCombined.size(); i++){
                PepPos tmp = new PepPos(startCombined.get(i), endCombined.get(i), pepCombined.get(i));
                arrangePepCombined.add(tmp);
            }

            if(arrangePepCombined.size() == 0){
                return;
            }

            Collections.sort(arrangePepCombined);

            maxYCombined = 0;
            if(arrangePepCombined.size() == 1){
                arrangePepCombined.get(0).setY(0);
                maxYCombined = 1;
            } else {
                arrangePepCombined.get(0).setY(0);
                ArrayList<Integer> plotQueue = new ArrayList<>();
                plotQueue.add(arrangePepCombined.get(0).getEnd());
                for(int i=1; i<arrangePepCombined.size(); i++){
                    boolean find = false;
                    for(int j=0; j<plotQueue.size(); j++){
                        if(arrangePepCombined.get(i).getStart() > plotQueue.get(j)){
                            arrangePepCombined.get(i).setY(j);
                            plotQueue.set(j, arrangePepCombined.get(i).getEnd());
                            find = true;
                            break;
                        }
                    }

                    if(!find){
                        arrangePepCombined.get(i).setY(plotQueue.size());
                        plotQueue.add(arrangePepCombined.get(i).getEnd());
                    }
                }
                maxYCombined = plotQueue.size();
            }
            return;
        }

        //Arrange the position
        arrangePep.clear();
        ArrayList<Integer> start = protein.getPepStart();
        ArrayList<Integer> end = protein.getPepEnd();
        ArrayList<Peptide> peps = protein.getPeptides();
        if(start.size()==0){
            return;
        }

        for(int i =0;i<start.size();i++){
            // decide what kind of data to show
            if(protein.getPepShow(i) > 0){
                PepPos tmp = new PepPos(start.get(i), end.get(i), peps.get(i));
                arrangePep.add(tmp);
            }

        }

        if(arrangePep.size()==0){
            return;
        }

        Collections.sort(arrangePep);

        maxY = 0;
        if(arrangePep.size()==1){
            arrangePep.get(0).setY(0);
            maxY = 1;
        } else {
            arrangePep.get(0).setY(0);
            ArrayList<Integer> plotQueue = new ArrayList<>();
            plotQueue.add(arrangePep.get(0).getEnd());
            for(int i=1;i<arrangePep.size();i++){
                boolean find = false;
                for(int j=0; j<plotQueue.size();j++){
                    if(arrangePep.get(i).getStart() > plotQueue.get(j)){
                        arrangePep.get(i).setY(j);
                        plotQueue.set(j, arrangePep.get(i).getEnd());
                        find = true;
                        break;
                    }
                }
                if(!find){
                    arrangePep.get(i).setY(plotQueue.size());
                    plotQueue.add(arrangePep.get(i).getEnd());
                }
            }
            maxY = plotQueue.size();
        }
    }

    public void saveImage(File figureFile){
        double pixelScale = 3.0;
        WritableImage image = new WritableImage((int)(hBoxView.getWidth()*pixelScale), (int)((hBoxView.getHeight()-20)*pixelScale));
        SnapshotParameters sp = new SnapshotParameters();
        sp.setTransform(Transform.scale(pixelScale, pixelScale));
        image = hBoxView.snapshot(sp, image);

        try{
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", figureFile);
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init Browser");

        arrangePep = new ArrayList();
        arrangePepCombined = new ArrayList<>();
        modiSelected = new ArrayList<>();

        //cbAcetylation.setSelected(true);
        //cbCarbamidomethylation.setSelected(true);
        //cbOxidation.setSelected(true);
        //cbPhosphorylation.setSelected(true);
        sbarCanvas.setVisible(false);
        combModiPos.setDisable(true);

        gc = canvas.getGraphicsContext2D();

        //sliderZoom.setMax(4);
        //sliderZoom.setMin(0.25);
        //sliderZoom.setValue(1);
        //sliderZoom.setShowTickLabels(true);
        //sliderZoom.setShowTickMarks(true);
        //sliderZoom.setMajorTickUnit(0.25);
        //sliderZoom.setMin(0.1);
        sliderZoom.setMax(2);
        sliderZoom.setMin(-2);
        sliderZoom.setValue(-2);
        sliderZoom.setMajorTickUnit(0.25);


        /*
        hbAcetylation = new HBox();
        hbAcetylation.setAlignment(Pos.CENTER_LEFT);
        hbAcetylation.setSpacing(5);
        lbAcetylation1 = new Label("");
        lbAcetylation1.setStyle("-fx-background-color: magenta;");
        lbAcetylation1.setPrefHeight(10);
        lbAcetylation1.setPrefWidth(10);
        lbAcetylation2 = new Label("Acetylation");
        hbAcetylation.getChildren().addAll(lbAcetylation1, lbAcetylation2);

        hbCarbamidomethylation = new HBox();
        hbCarbamidomethylation.setAlignment(Pos.CENTER_LEFT);
        hbCarbamidomethylation.setSpacing(5);
        lbCarbamidomethylation1 = new Label("");
        lbCarbamidomethylation1.setStyle("-fx-background-color: forestgreen");
        lbCarbamidomethylation1.setPrefWidth(10);
        lbCarbamidomethylation1.setPrefHeight(10);
        //lbCarbamidomethylation2 = new Label("Carbamidomethylation");
        lbCarbamidomethylation2 = new Label("Carbamidom");
        hbCarbamidomethylation.getChildren().addAll(lbCarbamidomethylation1, lbCarbamidomethylation2);

        hbPhosphorylation = new HBox();
        hbPhosphorylation.setAlignment(Pos.CENTER_LEFT);
        hbPhosphorylation.setSpacing(5);
        lbPhosphorylation1 = new Label("");
        lbPhosphorylation1.setStyle("-fx-background-color: cyan");
        lbPhosphorylation1.setPrefHeight(10);
        lbPhosphorylation1.setPrefWidth(10);
        lbPhosphorylation2 = new Label("Phosphorylation");
        hbPhosphorylation.getChildren().addAll(lbPhosphorylation1, lbPhosphorylation2);

        hbOxidation = new HBox();
        hbOxidation.setAlignment(Pos.CENTER_LEFT);
        hbOxidation.setSpacing(5);
        lbOxidation1 = new Label("");
        lbOxidation1.setStyle("-fx-background-color: gold");
        lbOxidation1.setPrefHeight(10);
        lbOxidation1.setPrefWidth(10);
        lbOxidation2 = new Label("Oxidation");
        hbOxidation.getChildren().addAll(lbOxidation1, lbOxidation2);
        */



        hbAbundance1 = new HBox();
        hbAbundance1.setAlignment(Pos.CENTER_LEFT);
        hbAbundance1.setSpacing(5);
        lbAbundance11 = new Label("");
        lbAbundance11.setStyle("-fx-background-color: #CDCDCD");
        lbAbundance11.setPrefHeight(10);
        lbAbundance11.setPrefWidth(10);
        lbAbundance12 = new Label("0~25%");
        hbAbundance1.getChildren().addAll(lbAbundance11, lbAbundance12);
        HBox.setMargin(lbAbundance11, new Insets(20,0,0,0));
        HBox.setMargin(lbAbundance12, new Insets(20,0,0,0));

        hbAbundance2 = new HBox();
        hbAbundance2.setAlignment(Pos.CENTER_LEFT);
        hbAbundance2.setSpacing(5);
        lbAbundance21 = new Label("");
        lbAbundance21.setStyle("-fx-background-color: #BEBEBE");
        lbAbundance21.setPrefHeight(10);
        lbAbundance21.setPrefWidth(10);
        lbAbundance22 = new Label("25~50%");
        hbAbundance2.getChildren().addAll(lbAbundance21, lbAbundance22);

        hbAbundance3 = new HBox();
        hbAbundance3.setAlignment(Pos.CENTER_LEFT);
        hbAbundance3.setSpacing(5);
        lbAbundance31 = new Label("");
        lbAbundance31.setStyle("-fx-background-color: #AFAFAF");
        lbAbundance31.setPrefHeight(10);
        lbAbundance31.setPrefWidth(10);
        lbAbundance32 = new Label("50~75%");
        hbAbundance3.getChildren().addAll(lbAbundance31, lbAbundance32);

        hbAbundance4 = new HBox();
        hbAbundance4.setAlignment(Pos.CENTER_LEFT);
        hbAbundance4.setSpacing(5);
        lbAbundance41 = new Label("");
        lbAbundance41.setStyle("-fx-background-color: #A0A0A0");
        lbAbundance41.setPrefHeight(10);
        lbAbundance41.setPrefWidth(10);
        lbAbundance42 = new Label("75~100%");
        hbAbundance4.getChildren().addAll(lbAbundance41, lbAbundance42);

        vbAbundanceTag = new VBox();
        vbAbundanceTag.setAlignment(Pos.TOP_LEFT);
        lbAbundanceTag1 = new Label("Max Intensity:");
        lbAbundanceTag2 = new Label("");
        vbAbundanceTag.getChildren().addAll(lbAbundanceTag1, lbAbundanceTag2);


        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!initialized){
                    return;
                }

                if(combProtein.getItems().size()==0){
                    return;
                }

                double x = event.getX();
                double y = event.getY();


                // start from 0
                int idxX = (int) ((x + sbarCanvas.getValue() - leftPix) /(pixPerLocus*scaleX));
                if(idxX < 0){
                    return;
                }
                int idxY = (int) ((canvasHeight - pixXLabel - bottomPix - y - 1) / ((pixPerPep + pixPepGap * 2) * scaleY));

                if(pepCombined){
                    for(PepPos tmp : arrangePepCombined){
                        if(tmp.contains(idxX, idxY)) {
                            String info = tmp.getPep().toString(tmp.getStart());
                            info = info + "Start: " + (tmp.getStart() + 1) + System.lineSeparator();
                            info = info + "End: " + (tmp.getEnd() + 1);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Peptide Information");
                            alert.setHeaderText(null);
                            alert.setContentText(info);
                            alert.showAndWait();
                            break;
                        } //else {
                        //    lblPep.setText("No Peptide");
                        //}
                    }
                    return;
                }

                for(PepPos tmp : arrangePep){
                    if(tmp.contains(idxX, idxY)) {
                        String info = tmp.getPep().toString(tmp.getStart());
                        info = info + "Start: " + (tmp.getStart() + 1) + System.lineSeparator();
                        info = info + "End: " + (tmp.getEnd() + 1);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Peptide Information");
                        alert.setHeaderText(null);
                        alert.setContentText(info);
                        alert.showAndWait();
                        break;
                    } //else {
                      //  lblPep.setText("No Peptide");
                    //}
                }
            }
        });


        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!initialized){
                    return;
                }


                if(combProtein.getItems().size()==0){
                    return;
                }

                double x = event.getX();
                double y = event.getY();


                // start from 0
                int idxX = (int) ((x + sbarCanvas.getValue() - leftPix) /(pixPerLocus*scaleX));
                if(idxX < 0){
                    return;
                }
                int idxY = (int) ((canvasHeight - pixXLabel - bottomPix - y - 1) / ((pixPerPep + pixPepGap * 2) * scaleY));

                if(pepCombined){
                    for(PepPos tmp : arrangePepCombined){
                        if(tmp.contains(idxX, idxY)) {
                            String info = tmp.getPep().toString(tmp.getStart());
                            info = info + "Start: " + (tmp.getStart() + 1) + System.lineSeparator();
                            info = info + "End: " + (tmp.getEnd() + 1);
                            lblPep.setText(info);
                            break;
                        } else {
                            lblPep.setText("No Peptide");
                        }
                    }


                    PosModiInfo posModiInfo = protein.getModiInfoCombined().get(idxX);
                    if(posModiInfo == null){
                        lblPos.setText("No Modification at position: " + (idxX + 1));
                    } else {
                        int numTotal = 0;
                        for(PepPos tmp : arrangePepCombined){
                            if(tmp.contains(idxX)){
                                numTotal++;
                            }
                        }

                        String txtShow = "Modifications at position: " + (idxX +1) + System.lineSeparator() + "Total peptides: " + numTotal + System.lineSeparator();

                        TreeMap<String, ArrayList<Double>> modis =  posModiInfo.getModifications();
                        Set set = modis.entrySet();
                        Iterator it = set.iterator();
                        while(it.hasNext()){
                            Map.Entry me = (Map.Entry) it.next();
                            String mt = (String) me.getKey();
                            ArrayList<Double> pc = (ArrayList<Double>) me.getValue();
                            txtShow += mt + ": " + pc.size() + System.lineSeparator();
                            if(pc.get(0) < 0){
                                txtShow += "NA";
                            } else {
                                txtShow += pc.get(0);
                            }

                            for(int i = 1; i < pc.size(); i++){
                                if(pc.get(i) < 0){
                                    txtShow += ";NA";
                                } else {
                                    txtShow += ";" + pc.get(i);
                                }

                            }
                            txtShow += System.lineSeparator();
                        }

                        lblPos.setText(txtShow);

                    }

                    return;
                }

                for(PepPos tmp : arrangePep){
                    if(tmp.contains(idxX, idxY)) {
                        String info = tmp.getPep().toString(tmp.getStart());
                        info = info + "Start: " + (tmp.getStart() + 1) + System.lineSeparator();
                        info = info + "End: " + (tmp.getEnd() + 1);
                        lblPep.setText(info);
                        break;
                    } else {
                        lblPep.setText("No Peptide");
                    }
                }

                //PosModiInfo posModiInfo = protein.getModiInfo().get(idxX);
                //if(idxX < 0 || idxX >= protein.getModiInfoSel().size()){
                //    return;
                //}
                PosModiInfo posModiInfo = protein.getModiInfoSel().get(idxX);
                if(posModiInfo == null){
                    lblPos.setText("No Modification at position: " + (idxX + 1));
                } else {
                    int numTotal = 0;
                    for(PepPos tmp : arrangePep){
                        if(tmp.contains(idxX)){
                            numTotal++;
                        }
                    }

                    String txtShow = "Modifications at position: " + (idxX +1) + System.lineSeparator() + "Total peptides: " + numTotal + System.lineSeparator();

                    TreeMap<String, ArrayList<Double>> modis =  posModiInfo.getModifications();
                    Set set = modis.entrySet();
                    Iterator it = set.iterator();
                    while(it.hasNext()){
                        Map.Entry me = (Map.Entry) it.next();
                        String mt = (String) me.getKey();
                        ArrayList<Double> pc = (ArrayList<Double>) me.getValue();
                        txtShow += mt + ": " + pc.size() + System.lineSeparator();
                        if(pc.get(0) < 0){
                            txtShow += "NA";
                        } else {
                            txtShow += pc.get(0);
                        }
                        for(int i = 1; i < pc.size(); i++){
                            if(pc.get(i) < 0){
                                txtShow += ";NA";
                            } else {
                                txtShow += ";" + pc.get(i);
                            }

                        }
                        txtShow += System.lineSeparator();
                    }

                    lblPos.setText(txtShow);

                }

                //txtPep.setText("Pos x " + x + ": " + idxX);
                //txtPos.setText("Pos y " + y + ": " + idxY);
            }
        });
    }

    /*
    public class PepPos implements Comparable<PepPos>{
        // start, end, and y are all from 0
        private int start;
        private int end;
        private int y;
        private Peptide pep;

        public PepPos(int start, int end, Peptide pep){
            this.start = start;
            this.end = end;
            this.pep = pep;
            this.y = 0;
        }

        public void setY(int y) {this.y = y;}
        public int getY() {return y;}

        public int getStart() {return start;}
        public int getEnd() {return end;}
        public Peptide getPep() {return pep;}

        public boolean contains(int x){
            if(start <= x && end >= x){
                return true;
            }
            return  false;
        }

        public boolean contains(int x, int y){
            if(this.y == y && start <= x && end >= x){
                return true;
            }
            return  false;
        }

        @Override
        public int compareTo(PepPos o) {
            if(this.start < o.getStart()){
                return -1;
            } else if(this.start > o.getStart()){
                return 1;
            } else {
                if(this.end > o.getEnd()){
                    return -1;
                } else if(this.end < o.getEnd()){
                    return 1;
                } else{
                    return 0;
                }
            }
        }
    }
    */
}
