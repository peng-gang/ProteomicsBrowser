package controller.browser;

import data.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.*;

/**
 * Created by gpeng on 2/14/17.
 */
public class PBrowserController implements Initializable {
    // controllers in view
    @FXML private Canvas canvas;
    @FXML private Slider sliderZoom;
    @FXML private CheckBox cbAcetylation;
    @FXML private CheckBox cbCarbamidomethylation;
    @FXML private CheckBox cbPhosphorylation;
    @FXML private CheckBox cbOxidation;
    @FXML private Label lblPep;
    @FXML private Label lblPos;
    @FXML private ComboBox<String> combSample;
    @FXML private ComboBox<String> combProtein;
    @FXML private ScrollBar sbarCanvas;
    @FXML private VBox vbLegend;
    @FXML private ComboBox<Integer> combModiPos;

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

    private GraphicsContext gc;




    // data
    private SampleGroup sampleGroup;
    private ArrayList<String> sampleId;
    private ArrayList<String> proteinId;

    private String selectedSample = null;
    private String selectedProtein = null;
    private Protein protein = null;

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
    private ArrayList<PepPos> arrangePep;

    //selected modifications to show
    private ArrayList<Modification.ModificationType> modiSelected;



    public void setData(SampleGroup sampleGroup){
        this.sampleGroup = sampleGroup;
        sampleId = new ArrayList<>(sampleGroup.getSampleId());
        proteinId = new ArrayList<>(sampleGroup.getProteinId());
    }

    public void show(){
        combSample.getItems().addAll(sampleId);
        combProtein.getItems().addAll(proteinId);

        modiSelected.add(Modification.ModificationType.Acetylation);
        modiSelected.add(Modification.ModificationType.Carbamidomethylation);
        modiSelected.add(Modification.ModificationType.Oxidation);
        modiSelected.add(Modification.ModificationType.Phosphorylation);

        combSample.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selectedSample = newValue;
                if(selectedProtein != null && selectedSample !=null){
                    protein = sampleGroup.getProtein(selectedSample, selectedProtein);
                    combModiPos.getItems().addAll(protein.getModiPos());
                    combModiPos.setDisable(false);
                    scaleX = 1;
                    scaleY = 1;
                    double sc = (canvasWidth - leftPix - rightPix)/(protein.getLength() * pixPerLocus);
                    sliderZoom.setMin(sc);
                    sliderZoom.setValue(1);
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
                    combModiPos.getItems().addAll(protein.getModiPos());
                    combModiPos.setDisable(false);
                    scaleX = 1;
                    scaleY = 1;
                    double sc = (canvasWidth - leftPix - rightPix)/(protein.getLength() * pixPerLocus);
                    sliderZoom.setMin(sc);
                    sliderZoom.setValue(1);
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
                scaleX = newValue.doubleValue();
                double sbVal = sbarCanvas.getValue();
                initSBar(sbVal * newValue.doubleValue()/oldValue.doubleValue());
                draw();
            }
        });


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


        combModiPos.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if(newValue != null){
                    int centerPos = newValue;
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

    private void updateCombModiPos(){
        combModiPos.getSelectionModel().clearSelection();
        combModiPos.setValue(null);
        combModiPos.getItems().clear();

        combModiPos.getItems().addAll(protein.getModiPos(modiSelected));
        if(combModiPos.getItems().size() > 0){
            combModiPos.setDisable(false);
        } else {
            combModiPos.setDisable(true);
        }
    }

    private void legend(){
        vbLegend.getChildren().clear();

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
        }
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

    private void draw(){
        /*
        int canvasHeight = (pixPepGap*2 + pixPerPep) * maxY + pixXLabel + topPix + bottomPix;
        if(canvasHeight > 500){
            scaleY = 500.0 / canvasHeight;
        } else {
            scaleY = 1;
        }
        */
        //start 0, end 0
        double st = sbarCanvas.getValue();

        //Start to plot
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        for(PepPos pp : arrangePep){
            double tlX = leftPix + pp.getStart()*pixPerLocus*scaleX + 0.5;
            double tlY = canvasHeight - pixXLabel - bottomPix - (pp.getY() + 1) * (pixPerPep  + pixPepGap * 2) * scaleY + 1;
            double w = pixPerLocus * pp.getPep().getLength()*scaleX - 0.5;
            double h = pixPerPep * scaleY;

            tlX = tlX - st;
            double rX = tlX + w;


            if(tlX < canvasWidth && rX > 0){
                gc.setFill(Color.GRAY);
                tlX = Math.max(tlX, 0);
                rX = Math.min(rX, canvasWidth);
                gc.fillRect(tlX, tlY, (rX-tlX), h);
            }

            if(pp.getPep().isModified()){
                ArrayList<Modification> modifications = pp.getPep().getModifications();
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
                    gc.setFill(Color.VIOLET);
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
            }
        }

        //X Axis
        double fontSize = pixPerLocus * scaleX /2.0  + 3.5;

        //if(fontsize > 16){
        //    fontsize = 16;
        //}

        String proteinSeq = protein.getSequence();
        Set<Integer> modiPos = protein.getModiPos();
        int xAxisStart = (int)(st / (pixPerLocus * scaleX));
        int xAxisEnd = (int)(0.5 + (st + canvasWidth)/(pixPerLocus * scaleX));
        gc.setFont(new Font("Courier New", fontSize));
        for(int i = xAxisStart; i <= xAxisEnd ;i++){
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


        legend();

    }

    private void orderPep(){
        //Arrange the position
        arrangePep.clear();
        ArrayList<Integer> start = protein.getPepStart();
        ArrayList<Integer> end = protein.getPepEnd();
        ArrayList<Peptide> peps = protein.getPeptides();
        if(start.size()==0){
            return;
        }

        for(int i =0;i<start.size();i++){
            PepPos tmp = new PepPos(start.get(i), end.get(i), peps.get(i));
            arrangePep.add(tmp);
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
                    plotQueue.add(arrangePep.get(i).getStart());
                }
            }
            maxY = plotQueue.size();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init Browser");

        arrangePep = new ArrayList();
        modiSelected = new ArrayList<>();

        cbAcetylation.setSelected(true);
        cbCarbamidomethylation.setSelected(true);
        cbOxidation.setSelected(true);
        cbPhosphorylation.setSelected(true);
        sbarCanvas.setVisible(false);
        combModiPos.setDisable(true);

        gc = canvas.getGraphicsContext2D();

        sliderZoom.setMax(4);
        sliderZoom.setMin(0.25);
        sliderZoom.setValue(1);
        sliderZoom.setShowTickLabels(true);
        sliderZoom.setShowTickMarks(true);
        sliderZoom.setMajorTickUnit(0.25);
        sliderZoom.setMin(0.1);


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
        lbOxidation1.setStyle("-fx-background-color: violet");
        lbOxidation1.setPrefHeight(10);
        lbOxidation1.setPrefWidth(10);
        lbOxidation2 = new Label("Oxidation");
        hbOxidation.getChildren().addAll(lbOxidation1, lbOxidation2);


        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();


                // start from 0
                int idxX = (int) ((x + sbarCanvas.getValue()) /(pixPerLocus*scaleX)) - 1;
                int idxY = (int) ((canvasHeight - pixXLabel - bottomPix - y - 1) / ((pixPerPep + pixPepGap * 2) * scaleY));

                for(PepPos tmp : arrangePep){
                    if(tmp.contains(idxX, idxY)){
                        lblPep.setText(tmp.getPep().toString());
                        break;
                    }
                }

                PosModiInfo posModiInfo = protein.getModiInfo().get(idxX);
                if(posModiInfo == null){
                    lblPos.setText("No Modification at this position");
                } else {
                    int numTotal = 0;
                    for(PepPos tmp : arrangePep){
                        if(tmp.contains(idxX)){
                            numTotal++;
                        }
                    }

                    String txtShow = "Total peptides: " + numTotal + "\n";

                    TreeMap<Modification.ModificationType, ArrayList<Double>> modis =  posModiInfo.getModifications();
                    Set set = modis.entrySet();
                    Iterator it = set.iterator();
                    while(it.hasNext()){
                        Map.Entry me = (Map.Entry) it.next();
                        Modification.ModificationType mt = (Modification.ModificationType) me.getKey();
                        ArrayList<Double> pc = (ArrayList<Double>) me.getValue();
                        txtShow += mt + ": " + pc.size() + "\n";
                        txtShow += pc.get(0);
                        for(int i = 1; i < pc.size(); i++){
                            txtShow += ";" + pc.get(i);
                        }
                        txtShow += "\n";
                    }

                    lblPos.setText(txtShow);

                }

                //txtPep.setText("Pos x " + x + ": " + idxX);
                //txtPos.setText("Pos y " + y + ": " + idxY);
            }
        });



    }

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
}
