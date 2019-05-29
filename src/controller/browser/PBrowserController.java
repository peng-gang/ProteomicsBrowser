package controller.browser;

import controller.MainController;
import controller.result.PepCombineAllController;
import controller.result.PepCombineController;
import data.PepPos;
import data.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
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
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.transform.Transform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    //@FXML private ComboBox<String> combSample;
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
    @FXML private MenuButton mbSamples;
    @FXML private ListView<String> lvSamples;


    private boolean initialized = false;

    private boolean pepCombined = false;


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


    //private String selectedSample = null;
    private String selectedProtein = null;
    //private Protein protein = null;
    private ArrayList<Protein> proteins;

    private ArrayList<String> modificationTypeAll;
    private ArrayList<String> selectedSamples;

    private double scaleX = 1;
    private double scaleY = 1;
    private double scaleYCombined = 1;
    private  double pixPerSample;

    private final double canvasWidth = 800;
    private final double canvasHeight = 600;


    //plot parameters
    private final double pixPerLocus = 10;
    private final double pixPerPep = 32;
    private final double pixPepGap = 1;
    private final double leftPix =10;
    private final double rightPix = 10;
    private final double topPix = 10;
    private final double bottomPix = 10;
    private final double pixXLabel = 30;
    private final double sampleGapPix = 20;

    private ArrayList<Integer> maxYs;
    private ArrayList<Integer> maxYsCombined;
    //private ArrayList<PepPos> arrangePep;
    //private ArrayList<PepPos> arrangePepCombined;

    private ArrayList<ArrayList<PepPos>> arrangePeps;
    private ArrayList<ArrayList<PepPos>> arrangePepsCombined;

    //selected modifications to show
    private ArrayList<String> modiSelected;

    public ArrayList<Protein> getProteins() { return  proteins; }
    public ArrayList<Protein> getProteinsAllSamples() {
        return sampleGroup.getProtein(selectedProtein);
    }

    public boolean getInitialized() { return initialized; }

    public ArrayList<String> getSelectedSamples() { return  selectedSamples; }


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
        Set<String> modiSelectedTmp = new TreeSet<>();
        for(Protein protein : proteins){
            modiSelectedTmp.addAll(protein.getModiTypeAll());
        }
        modiSelected.addAll(modiSelectedTmp);
        for(Node node : vBoxModification.getChildren()){
            for(Node node1 :((HBox) node).getChildren()){
                ((CheckBox) node1).setSelected(true);
                node1.setDisable(false);
            }
        }

        combModiPos.getSelectionModel().clearSelection();
        combModiPos.getItems().clear();
        Set<Integer> modiPosTmp = new TreeSet<>();
        for(Protein protein : proteins){
            modiPosTmp.addAll(protein.getModiPosSel1(modiSelected));
        }
        combModiPos.getItems().addAll(modiPosTmp);
        combModiPos.setDisable(false);

        //combinePosSel = false;
        double sc = (canvasWidth - leftPix - rightPix)/(proteins.get(0).getLength() * pixPerLocus);
        sliderZoom.setMin(Math.log(sc) /Math.log(2.0));
        sliderZoom.setValue(sliderZoom.getMin());
        scaleX = Math.pow(2.0, sliderZoom.getValue());
        orderPep();
        //too many modification to show in a canvas
        calScaleY();
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
                pepCombine.setScene(new Scene(root, 1050, 650));
                pepCombine.initModality(Modality.WINDOW_MODAL);
                pepCombine.initOwner(canvas.getScene().getWindow());

                PepCombineController controller = loader.getController();
                controller.setData(sampleGroup, proteins, selectedSamples, pos);

                pepCombine.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }


        pepCombined = true;
        ArrayList<String> modiCriteria = new ArrayList<>();


        for(Node node : vbModification.getChildren()){
            if(((CheckBox) node).isSelected()){
                modiCriteria.add(node.getUserData().toString());
            }
        }

        for(Protein protein : proteins){
            protein.combinePep(cbCharge.isSelected(), modiCriteria);
            protein.setAbundanceCombinedRange();
        }

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
        Set<Integer> modiPosTmp = new TreeSet<>();
        for(Protein protein : proteins){
            modiPosTmp.addAll(protein.getModiCombinedPos1(modiSelected));
        }
        combModiPos.getItems().addAll(modiPosTmp);
        combModiPos.setDisable(false);

        //combinePosSel = false;
        //scaleX = 1;
        //scaleY = 1;
        double sc = (canvasWidth - leftPix - rightPix)/(proteins.get(0).getLength() * pixPerLocus);
        sliderZoom.setMin(Math.log(sc) /Math.log(2.0));
        sliderZoom.setValue(sliderZoom.getMin());
        scaleX = Math.pow(2.0, sliderZoom.getValue());
        orderPep();
        //too many modification to show in a canvas
        //if(maxYCombined * (pixPerPep + 2*pixPepGap) > (canvasHeight-bottomPix-pixXLabel)){
        //    scaleY = (canvasHeight-bottomPix-pixXLabel)/((pixPerPep + 2*pixPepGap)*maxYCombined);
        //}
        calScaleYCombined();
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

    public void proteinFilter(Set<String> modiSel, Set<String> modiRm, int numPep,  boolean anySample){
        Set<String> ptSort = new TreeSet<>();
        if(anySample){
            for(String pid : proteinId){
                for(String selectedSample : selectedSamples){
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
                            break;
                        } else {
                            continue;
                        }
                    }
                }
            }
        } else {
            for(String pid : proteinId){
                int numMeet = 0;
                for(String selectedSample : selectedSamples){
                    boolean include = true;

                    if(modiRm.size()!=0) {
                        for(String mType: sampleGroup.getProtein(selectedSample, pid).getModiTypeAll()){
                            if(modiRm.contains(mType)){
                                include = false;
                                break;
                            }
                        }
                        if(!include){
                            break;
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
                            break;
                        }
                    }

                    if(include){
                        if(sampleGroup.getProtein(selectedSample, pid).getPeptides().size() >= numPep){
                            numMeet++;
                            continue;
                        } else {
                            break;
                        }
                    }
                }
                if(numMeet == selectedSamples.size()){
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

        combProtein.getItems().clear();
        combProtein.getItems().addAll(ptSort);
        combProtein.getSelectionModel().select(0);
        selectedProtein = combProtein.getValue();
    }

    public void proteinFilter(int numModiPos, int numPep,  boolean anySample){
        Set<String> ptSort = new TreeSet<>();
        if(anySample){
            for(String pid : proteinId){
                for(String selectedSample : selectedSamples){
                    if(sampleGroup.getProtein(selectedSample, pid).getModiPos().size() >= numModiPos &&
                            sampleGroup.getProtein(selectedSample, pid).getPeptides().size() >= numPep){
                        ptSort.add(pid);
                        break;
                    }
                }
            }
        } else {
            for(String pid : proteinId){
                int numMeet = 0;
                for(String selectedSample : selectedSamples){
                    if(sampleGroup.getProtein(selectedSample, pid).getModiPos().size() >= numModiPos &&
                            sampleGroup.getProtein(selectedSample, pid).getPeptides().size() >= numPep){
                        numMeet++;
                    } else {
                        break;
                    }
                }
                if(numMeet == selectedSamples.size()){
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

        combProtein.getItems().clear();
        combProtein.getItems().addAll(ptSort);
        combProtein.getSelectionModel().select(0);
        selectedProtein = combProtein.getValue();
    }

    public void proteinFilterType(int numModiType, int numPep, boolean anySample){
        Set<String> ptSort = new TreeSet<>();
        if(anySample){
            for(String pid : proteinId){
                for(String selectedSample : selectedSamples){
                    if(sampleGroup.getProtein(selectedSample, pid).getModiTypeAll().size() >= numModiType &&
                            sampleGroup.getProtein(selectedSample, pid).getPeptides().size() >= numPep){
                        ptSort.add(pid);
                        break;
                    }
                }
            }
        } else {
            for(String pid : proteinId){
                int numMeet = 0;
                for(String selectedSample : selectedSamples){
                    if(sampleGroup.getProtein(selectedSample, pid).getModiTypeAll().size() >= numModiType &&
                            sampleGroup.getProtein(selectedSample, pid).getPeptides().size() >= numPep){
                        numMeet++;
                    } else {
                        break;
                    }
                }
                if(numMeet==selectedSamples.size()){
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
        ArrayList<PepPos> arrangePep = arrangePeps.get(0);
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
        selectedSamples = new ArrayList<>();

        for(String id : sampleId){
            CheckMenuItem tmp = new CheckMenuItem(id);
            tmp.selectedProperty().addListener(((observableValue, oldValue, newValue) -> {
                if(newValue){
                    if(lvSamples.getItems().size()>3){
                        System.out.println("NO MORE!");
                    }
                    lvSamples.getItems().add(tmp.getText());
                    if(lvSamples.getItems().size()!=1){
                        mainController.pepFilterSetDisable(true);
                    }
                } else {
                    lvSamples.getItems().remove(tmp.getText());
                    if(lvSamples.getItems().size()==1){
                        mainController.pepFilterSetDisable(false);
                    }
                }
            }));
            mbSamples.getItems().add(tmp);
        }

        final IntegerProperty dragFromIndex = new SimpleIntegerProperty(-1);
        lvSamples.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item,  empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item);
                        }
                    }
                };

                cell.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (! cell.isEmpty()) {
                            dragFromIndex.set(cell.getIndex());
                            Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                            ClipboardContent cc = new ClipboardContent();
                            cc.putString(cell.getItem());
                            db.setContent(cc);
                            db.setDragView(cell.snapshot(null, null));
                            // Java 8 only:
//                          db.setDragView(cell.snapshot(null, null));
                        }
                    }
                });

                cell.setOnDragOver(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        if (dragFromIndex.get() >= 0 && dragFromIndex.get() != cell.getIndex()) {
                            event.acceptTransferModes(TransferMode.MOVE);
                        }
                    }
                });


                // highlight drop target by changing background color:
                cell.setOnDragEntered(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        if (dragFromIndex.get() >= 0 && dragFromIndex.get() != cell.getIndex()) {
                            // should really set a style class and use an external style sheet,
                            // but this works for demo purposes:
                            cell.setStyle("-fx-background-color: gold;");
                        }
                    }
                });

                // remove highlight:
                cell.setOnDragExited(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        cell.setStyle("");
                    }
                });

                cell.setOnDragDropped(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {

                        int dragItemsStartIndex ;
                        int dragItemsEndIndex ;
                        int direction ;
                        if (cell.isEmpty()) {
                            dragItemsStartIndex = dragFromIndex.get();
                            dragItemsEndIndex = lvSamples.getItems().size();
                            direction = -1;
                        } else {
                            if (cell.getIndex() < dragFromIndex.get()) {
                                dragItemsStartIndex = cell.getIndex();
                                dragItemsEndIndex = dragFromIndex.get() + 1 ;
                                direction = 1 ;
                            } else {
                                dragItemsStartIndex = dragFromIndex.get();
                                dragItemsEndIndex = cell.getIndex() + 1 ;
                                direction = -1 ;
                            }
                        }

                        List<String> rotatingItems = lvSamples.getItems().subList(dragItemsStartIndex, dragItemsEndIndex);
                        List<String> rotatingItemsCopy = new ArrayList<>(rotatingItems);
                        Collections.rotate(rotatingItemsCopy, direction);
                        rotatingItems.clear();
                        rotatingItems.addAll(rotatingItemsCopy);
                        dragFromIndex.set(-1);
                    }
                });

                cell.setOnDragDone(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        dragFromIndex.set(-1);
                        lvSamples.getSelectionModel().select(event.getDragboard().getString());
                    }
                });

                return cell ;
            }
        });


        ArrayList<String> ptSort = new ArrayList<>(proteinId);
        Collections.sort(ptSort);
        combProtein.getItems().clear();
        combProtein.getItems().addAll(ptSort);
        combProtein.getSelectionModel().select(0);

        lvSamples.getItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                if(lvSamples.getItems().size()==0){
                    selectedSamples.clear();
                    return;
                }

                selectedSamples.clear();
                selectedSamples.addAll(lvSamples.getItems());
                selectedProtein = combProtein.getValue();
                if(selectedProtein != null && selectedSamples.size() > 0){
                    proteins.clear();
                    modificationTypeAll.clear();
                    Set<String> modiTypeTmp = new TreeSet<>();
                    for(String selectedSample : selectedSamples){
                        Protein protein = sampleGroup.getProtein(selectedSample, selectedProtein);
                        proteins.add(protein);
                        modiTypeTmp.addAll(protein.getModiTypeAll());
                    }
                    //remove duplicates
                    modificationTypeAll.addAll(modiTypeTmp);

                    //init modification selection
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
                    Set<Integer> modiPosTmp = new TreeSet<>();
                    for(Protein protein : proteins){
                        modiPosTmp.addAll(protein.getModiPosSel1(modiSelected));
                    }
                    combModiPos.getItems().addAll(modiPosTmp);
                    combModiPos.setDisable(false);
                    //combinePosSel = false;
                    //scaleX = 1;
                    //scaleY = 1;
                    double sc = (canvasWidth - leftPix - rightPix)/(proteins.get(0).getLength() * pixPerLocus);
                    sliderZoom.setMin(Math.log(sc) /Math.log(2.0));
                    sliderZoom.setValue(sliderZoom.getMin());
                    scaleX = Math.pow(2.0, sliderZoom.getValue());
                    orderPep();
                    //too many modification to show in a canvas
                    //if(maxY * (pixPerPep + 2*pixPepGap) > (canvasHeight-bottomPix-pixXLabel)){
                    //    scaleY = (canvasHeight-bottomPix-pixXLabel)/((pixPerPep + 2*pixPepGap)*maxY);
                    //}
                    calScaleY();
                    initSBar(0);
                    draw();
                }
            }
        });
    }

    public void updatePep(){
        double sc = (canvasWidth - leftPix - rightPix)/(proteins.get(0).getLength() * pixPerLocus);
        sliderZoom.setMin(Math.log(sc) /Math.log(2.0));
        sliderZoom.setValue(sliderZoom.getMin());
        scaleX = Math.pow(2.0, sliderZoom.getValue());
        orderPep();
        //too many modification to show in a canvas
        //if(maxY * (pixPerPep + 2*pixPepGap) > (canvasHeight-bottomPix-pixXLabel)){
        //    scaleY = (canvasHeight-bottomPix-pixXLabel)/((pixPerPep + 2*pixPepGap)*maxY);
        //}
        calScaleY();
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
        ArrayList<String> ptSort = new ArrayList<>(proteinId);
        Collections.sort(ptSort);
        combProtein.getItems().addAll(ptSort);

        combProtein.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selectedProtein = newValue;
                if(selectedProtein != null && selectedSamples.size() > 0){
                    proteins.clear();
                    modificationTypeAll.clear();
                    Set<String> modiTypeTmp = new TreeSet<>();
                    for(String selectedSample : selectedSamples){
                        Protein protein = sampleGroup.getProtein(selectedSample, selectedProtein);
                        proteins.add(protein);
                        modiTypeTmp.addAll(protein.getModiTypeAll());
                    }

                    modificationTypeAll.addAll(modiTypeTmp);

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
                    Set<Integer> modiPosTmp = new TreeSet<>();
                    for(Protein protein : proteins){
                        modiPosTmp.addAll(protein.getModiPosSel1(modiSelected));
                    }
                    combModiPos.getItems().addAll(modiPosTmp);
                    combModiPos.setDisable(false);
                    //combinePosSel = false;
                    //scaleX = 1;
                    //scaleY = 1;
                    double sc = (canvasWidth - leftPix - rightPix)/(proteins.get(0).getLength() * pixPerLocus);
                    sliderZoom.setMin(Math.log(sc) / Math.log(2.0));
                    sliderZoom.setValue(sliderZoom.getMin());
                    scaleX = Math.pow(2.0, sliderZoom.getValue());
                    orderPep();
                    //if(maxY * (pixPerPep + 2*pixPepGap) > (canvasHeight-bottomPix-pixXLabel)){
                    //    scaleY = (canvasHeight-bottomPix-pixXLabel)/((pixPerPep + 2*pixPepGap)*maxY);
                    //}
                    calScaleY();
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

        Set<Integer> modiPosTmp = new TreeSet<>();
        for(Protein protein : proteins){
            modiPosTmp.addAll(protein.getModiPosSel1(modiSelected));
        }
        //combModiPos.getItems().addAll(protein.getModiPosSel1(modiSelected));
        combModiPos.getItems().addAll(modiPosTmp);
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

        vbLegend.getChildren().add(hbAbundance1);
        vbLegend.getChildren().add(hbAbundance2);
        vbLegend.getChildren().add(hbAbundance3);
        vbLegend.getChildren().add(hbAbundance4);

        if(pepCombined){
            double maxAb = 0.0;
            for(Protein protein : proteins){
                if(maxAb < protein.getAbundanceMaxCombined()){
                    maxAb = protein.getAbundanceMaxCombined();
                }
            }
            lbAbundanceTag2.setText(String.format("%9.3g",maxAb));
        } else {
            double maxAb = 0.0;
            for(Protein protein : proteins){
                if(maxAb < protein.getAbundanceMax()){
                    maxAb = protein.getAbundanceMax();
                }
            }
            lbAbundanceTag2.setText(String.format("%9.3g",maxAb));
        }

        vbLegend.getChildren().addAll(vbAbundanceTag);

    }

    private void initSBar(double val){
        double plotWidth = proteins.get(0).getLength() * pixPerLocus * scaleX + leftPix + rightPix;
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
        initialized = true;
        double st = sbarCanvas.getValue();

        //Start to plot
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        if(combProtein.getItems().size()==0){
            return;
        }

        if(pepCombined){
            int idxSample = 0;
            double maxAbundanceCombined = 0;
            for(Protein protein : proteins){
                if(maxAbundanceCombined < protein.getAbundanceMaxCombined()){
                    maxAbundanceCombined = protein.getAbundanceMaxCombined();
                }
            }


            for(ArrayList<PepPos> arrangePepCombined : arrangePepsCombined){
                double bottomSampleY = topPix + (idxSample + 1) * pixPerSample + idxSample * sampleGapPix;
                for(PepPos pp : arrangePepCombined){
                    double tlX = leftPix + pp.getStart()*pixPerLocus*scaleX + 1;
                    //double tlY = canvasHeight - pixXLabel - bottomPix - (pp.getY() + 1) * (pixPerPep  + pixPepGap * 2) * scaleY + 1;
                    double tlY = bottomSampleY - (pixPerPep * scaleYCombined + pixPepGap) * (pp.getY() + 1);
                    double w = pixPerLocus * pp.getPep().getLength()*scaleX - 2;
                    double h = pixPerPep * scaleYCombined;

                    tlX = tlX - st;
                    double rX = tlX + w;


                    if(tlX < canvasWidth && rX > 0){
                        //int range = pp.getPep().getAbundanceRange();
                        int range = calRange(pp.getPep().getAbundance(), maxAbundanceCombined);
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
                                            double height = scaleYCombined * pixPerPep;

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
                                            double topY = tlY + (1.0-modi.getPercent().get(i)/100.0) * pixPerPep * scaleYCombined;
                                            double width = pixPerLocus * scaleX - 0.5;
                                            double height = scaleYCombined * pixPerPep * modi.getPercent().get(i)/100.0;

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

                idxSample++;
            }

            /*
            if(proteins.size()>1){
                gc.setStroke(Color.rgb(255, 255, 255));
                for(int i=1;i<proteins.size();i++){
                    double posY = topPix + (pixPerSample + sampleGapPix)*i - sampleGapPix/2;
                    gc.strokeLine(0,posY,canvasWidth,posY);
                }
            }
             */


            double fontSize = pixPerLocus * scaleX / 2.0 + 3.5;
            String proteinSeq = proteins.get(0).getSequence();
            //ArrayList<Integer> modiPos = proteins.get(0).getModiCombinedPos(modiSelected);
            ArrayList<Integer> modiPos = new ArrayList<>(combModiPos.getItems());
            int xAxisStart = (int)(st / (pixPerLocus * scaleX));
            int xAxisEnd = (int)(0.5 + (st + canvasWidth)/(pixPerLocus * scaleX));

            for(int i = xAxisStart; i <= xAxisEnd ;i++){
                gc.setFont(new Font("Courier New", fontSize));
                if(i>=proteinSeq.length()){
                    break;
                }
                double leftX = leftPix + (i + 0.3) * pixPerLocus * scaleX - st;
                double topY =  canvasHeight - pixXLabel - bottomPix + pixPerLocus * 2;
                if(modiPos.contains(i+1)){
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

        int idxSample = 0;
        double maxAbundance = 0;
        for(Protein protein : proteins){
            if(maxAbundance < protein.getAbundanceMax()){
                maxAbundance = protein.getAbundanceMax();
            }
        }
        for(ArrayList<PepPos> arrangePep : arrangePeps){
            double bottomSampleY = topPix + (idxSample + 1) * pixPerSample + idxSample * sampleGapPix;
            for(PepPos pp : arrangePep){
                //top left coordinate
                double tlX = leftPix + pp.getStart()*pixPerLocus*scaleX + 1;
                //double tlY = canvasHeight - pixXLabel - bottomPix - (pp.getY() + 1) * (pixPerPep  + pixPepGap * 2) * scaleY + 1;
                double tlY = bottomSampleY - (pixPerPep * scaleY + pixPepGap) * (pp.getY() + 1);
                double w = pixPerLocus * pp.getPep().getLength()*scaleX - 2;
                double h = pixPerPep * scaleY;

                tlX = tlX - st;
                double rX = tlX + w;


                if(tlX < canvasWidth && rX > 0){
                    //int range = pp.getPep().getAbundanceRange();
                    int range = calRange(pp.getPep().getAbundance(), maxAbundance);
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
                }
            }
            idxSample++;
        }



        //X Axis
        double fontSize = pixPerLocus * scaleX /2.0  + 3.5;

        //if(fontsize > 16){
        //    fontsize = 16;
        //}

        String proteinSeq = proteins.get(0).getSequence();
        ArrayList<Integer> modiPos = new ArrayList<>(combModiPos.getItems());// proteins.get(0).getModiPosSel(modiSelected);
        int xAxisStart = (int)(st / (pixPerLocus * scaleX));
        int xAxisEnd = (int)(0.5 + (st + canvasWidth)/(pixPerLocus * scaleX));

        for(int i = xAxisStart; i <= xAxisEnd ;i++){
            gc.setFont(new Font("Courier New", fontSize));
            if(i>=proteinSeq.length()){
                break;
            }
            double leftX = leftPix + (i + 0.3) * pixPerLocus * scaleX - st;
            double topY =  canvasHeight - pixXLabel - bottomPix + pixPerLocus * 2;
            if(modiPos.contains(i+1)){
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
            arrangePepsCombined.clear();
            maxYsCombined.clear();
            for(Protein protein : proteins){
                ArrayList<Integer> startCombined = protein.getPepStartCombined();
                ArrayList<Integer> endCombined = protein.getPepEndCombined();
                ArrayList<Peptide> pepCombined = protein.getPeptidesCombined();
                ArrayList<PepPos> arrangePepCombined = new ArrayList<>();
                if(startCombined.size()==0){
                    arrangePepsCombined.add(arrangePepCombined);
                    maxYsCombined.add(0);
                    continue;
                }

                for(int i=0; i<startCombined.size(); i++){
                    PepPos tmp = new PepPos(startCombined.get(i), endCombined.get(i), pepCombined.get(i));
                    arrangePepCombined.add(tmp);
                }

                if(arrangePepCombined.size() == 0){
                    arrangePepsCombined.add(arrangePepCombined);
                    maxYsCombined.add(0);
                    continue;
                }

                Collections.sort(arrangePepCombined);

                int maxYCombined = 0;
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
                arrangePepsCombined.add(arrangePepCombined);
                maxYsCombined.add(maxYCombined);
            }
            return;
        }

        //Arrange the position
        arrangePeps.clear();
        maxYs.clear();
        for(Protein protein : proteins){
            ArrayList<Integer> start = protein.getPepStart();
            ArrayList<Integer> end = protein.getPepEnd();
            ArrayList<Peptide> peps = protein.getPeptides();
            ArrayList<PepPos> arrangePep = new ArrayList<>();
            if(start.size()==0){
                arrangePeps.add(arrangePep);
                maxYs.add(0);
                continue;
            }

            for(int i =0;i<start.size();i++){
                // decide what kind of data to show
                if(protein.getPepShow(i) > 0){
                    PepPos tmp = new PepPos(start.get(i), end.get(i), peps.get(i));
                    arrangePep.add(tmp);
                }

            }

            if(arrangePep.size()==0){
                arrangePeps.add(arrangePep);
                maxYs.add(0);
                continue;
            }

            Collections.sort(arrangePep);

            int maxY = 0;
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
            arrangePeps.add(arrangePep);
            maxYs.add(maxY);
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

        arrangePeps = new ArrayList<>();
        arrangePepsCombined = new ArrayList<>();
        proteins = new ArrayList<>();
        modiSelected = new ArrayList<>();
        modificationTypeAll = new ArrayList<>();
        maxYs = new ArrayList<>();
        maxYsCombined = new ArrayList<>();

        sbarCanvas.setVisible(false);
        combModiPos.setDisable(true);

        gc = canvas.getGraphicsContext2D();

        sliderZoom.setMax(2);
        sliderZoom.setMin(-2);
        sliderZoom.setValue(-2);
        sliderZoom.setMajorTickUnit(0.25);


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
                // get sample and peptide
                int idxSample;
                int idxY;
                //int idxY = (int) ((canvasHeight - pixXLabel - bottomPix - y - 1) / ((pixPerPep + pixPepGap * 2) * scaleY));
                if(y<topPix || y > (canvasHeight-bottomPix - pixXLabel)){
                    return;
                }

                double yAdj = y-topPix;
                //Start from 0
                idxSample = (int)(yAdj / (pixPerSample + sampleGapPix));
                yAdj = yAdj - idxSample * (pixPerSample + sampleGapPix);


                if(pepCombined){
                    idxY = (int)((pixPerSample-yAdj)/(pixPerPep*scaleYCombined + pixPepGap));
                    for(PepPos tmp : arrangePepsCombined.get(idxSample)){
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

                for(PepPos tmp : arrangePeps.get(idxSample)){
                    idxY = (int)((pixPerSample-yAdj)/(pixPerPep*scaleY + pixPepGap));
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

                int idxSample;
                int idxY;
                //int idxY = (int) ((canvasHeight - pixXLabel - bottomPix - y - 1) / ((pixPerPep + pixPepGap * 2) * scaleY));

                if(y<topPix || y > (canvasHeight-bottomPix - pixXLabel)){
                    return;
                }

                double yAdj = y-topPix;
                //Start from 0
                idxSample = (int)(yAdj / (pixPerSample + sampleGapPix));
                yAdj = yAdj - idxSample * (pixPerSample + sampleGapPix);


                if(pepCombined){
                    idxY = (int)((pixPerSample-yAdj)/(pixPerPep*scaleYCombined + pixPepGap));
                    lblPep.setText("No Peptide");
                    for(PepPos tmp : arrangePepsCombined.get(idxSample)){
                        if(tmp.contains(idxX, idxY)) {
                            String info = tmp.getPep().toString(tmp.getStart());
                            info = info + "Start: " + (tmp.getStart() + 1) + System.lineSeparator();
                            info = info + "End: " + (tmp.getEnd() + 1);
                            lblPep.setText(info);
                            break;
                        }
                    }


                    if(proteins.size()==1){
                        PosModiInfo posModiInfo = proteins.get(0).getModiInfoCombined().get(idxX);
                        if(posModiInfo == null){
                            lblPos.setText("No Modification at position: " + (idxX + 1));
                        } else {
                            int numTotal = 0;
                            for(PepPos tmp : arrangePepsCombined.get(0)){
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
                    } else if(proteins.size()>1) {
                        String textShow = "Modifications at position: " + (idxX+1) + System.lineSeparator();
                        int idx = 0;
                        for(Protein protein:proteins){
                            textShow += selectedSamples.get(idx) + System.lineSeparator();

                            PosModiInfo posModiInfo = protein.getModiInfoCombined().get(idxX);
                            if(posModiInfo == null){
                                textShow += "No Modification" + System.lineSeparator();
                            } else {
                                int numTotal = 0;
                                for(PepPos tmp : arrangePepsCombined.get(idx)){
                                    if(tmp.contains(idxX)){
                                        numTotal++;
                                    }
                                }
                                textShow += "Total peptides: " + numTotal + System.lineSeparator();

                                TreeMap<String, ArrayList<Double>> modis =  posModiInfo.getModifications();
                                Set set = modis.entrySet();
                                Iterator it = set.iterator();
                                while(it.hasNext()){
                                    Map.Entry me = (Map.Entry) it.next();
                                    String mt = (String) me.getKey();
                                    ArrayList<Double> pc = (ArrayList<Double>) me.getValue();
                                    textShow += mt + ": " + pc.size() + System.lineSeparator();
                                    textShow += System.lineSeparator();
                                }

                            }

                            idx++;
                        }
                        lblPos.setText(textShow);
                    }


                    return;
                }

                idxY = (int)((pixPerSample-yAdj)/(pixPerPep*scaleY + pixPepGap));
                lblPep.setText("No Peptide");
                for(PepPos tmp : arrangePeps.get(idxSample)){
                    if(tmp.contains(idxX, idxY)) {
                        String info = tmp.getPep().toString(tmp.getStart());
                        info = info + "Start: " + (tmp.getStart() + 1) + System.lineSeparator();
                        info = info + "End: " + (tmp.getEnd() + 1);
                        lblPep.setText(info);
                        break;
                    }
                }

                if(proteins.size()==1){
                    PosModiInfo posModiInfo = proteins.get(0).getModiInfoSel().get(idxX);
                    if(posModiInfo == null){
                        lblPos.setText("No Modification at position: " + (idxX + 1));
                    } else {
                        int numTotal = 0;
                        for(PepPos tmp : arrangePeps.get(0)){
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
                } else if(proteins.size()>1){
                    String textShow = "Modifications at position: " + (idxX+1) + System.lineSeparator();
                    int idx = 0;
                    for(Protein protein:proteins){
                        textShow += selectedSamples.get(idx) + System.lineSeparator();

                        PosModiInfo posModiInfo = protein.getModiInfo().get(idxX);
                        if(posModiInfo == null){
                            textShow += "No Modification" + System.lineSeparator();
                        } else {
                            int numTotal = 0;
                            for(PepPos tmp : arrangePeps.get(idx)){
                                if(tmp.contains(idxX)){
                                    numTotal++;
                                }
                            }
                            textShow += "Total peptides: " + numTotal + System.lineSeparator();

                            TreeMap<String, ArrayList<Double>> modis =  posModiInfo.getModifications();
                            Set set = modis.entrySet();
                            Iterator it = set.iterator();
                            while(it.hasNext()){
                                Map.Entry me = (Map.Entry) it.next();
                                String mt = (String) me.getKey();
                                ArrayList<Double> pc = (ArrayList<Double>) me.getValue();
                                textShow += mt + ": " + pc.size() + System.lineSeparator();
                                textShow += System.lineSeparator();
                            }

                        }

                        idx++;
                    }
                    lblPos.setText(textShow);
                }


            }
        });
    }

    private void calScaleY(){
        int numSample = proteins.size();
        pixPerSample = (canvasHeight - bottomPix - pixXLabel - topPix - sampleGapPix * (numSample-1))/numSample;
        int maxY = Collections.max(maxYs);

        scaleY = (pixPerSample/maxY - pixPepGap)/pixPerPep;
        if(scaleY > 1){
            scaleY = 1;
        }
    }

    private void calScaleYCombined(){
        int numSample = proteins.size();
        pixPerSample = (canvasHeight - bottomPix - pixXLabel - topPix - sampleGapPix * (numSample-1))/numSample;
        int maxYCombined = Collections.max(maxYsCombined);

        scaleYCombined = (pixPerSample/maxYCombined - pixPepGap)/pixPerPep;
        if(scaleYCombined > 1){
            scaleYCombined = 1;
        }
    }

    private int calRange(double abundance, double maxAbundance){
        int rlt = (int) (4.0 * abundance/maxAbundance);
        if(rlt>4){
            rlt=4;
        }
        return rlt;
    }
}
