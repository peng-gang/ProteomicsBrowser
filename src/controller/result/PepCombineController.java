package controller.result;

import data.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import share.Util;

import java.util.*;

/**
 * Created by gpeng on 1/8/18.
 */
public class PepCombineController {
    private ArrayList<Protein> proteins;
    private SampleGroup sampleGroup;
    private int pos;
    private ArrayList<Integer> maxYs;
    private ArrayList<Integer> minXs, maxXs;
    private String seq;
    private GraphicsContext gc;
    private ArrayList<ArrayList<PepPos>> arrangePeps;
    private ArrayList<String> selectedSamples;

    private double scaleX = 1;
    private double scaleY = 1;

    private final double canvasWidth = 800;
    private final double canvasHeight = 400;
    private final double pixPerLocus = 10;
    private final double pixPerPep = 32;
    private final double pixPepGap = 1;
    private final double leftPix =10;
    //private final double rightPix = 10;
    private final double bottomPix = 10;
    private final double pixXLabel = 30;
    private final double sampleGapPix = 20;
    private final double topPix = 10;
    //private ArrayList<Modification> modiSelected;
    private ArrayList<String> modiSelected;

    private double pixPerSample;


    @FXML private Canvas canvas;
    @FXML private Label lblStart;
    @FXML private Label lblEnd;
    @FXML private Label lblPep;
    @FXML private Label lblPos;
    @FXML private VBox vbLegend;
    //@FXML private Label lblInfo;


    public void setData(SampleGroup sampleGroup, ArrayList<Protein> proteins, ArrayList<String> selectedSamples, int pos){

        this.sampleGroup = sampleGroup;
        this.proteins = proteins;
        this.selectedSamples = selectedSamples;
        this.pos = pos;

        maxYs = new ArrayList<>();
        minXs = new ArrayList<>();
        maxXs = new ArrayList<>();

        modiSelected = new ArrayList<>();

        arrangePeps = new ArrayList<>();
        int numSample = proteins.size();
        pixPerSample = (canvasHeight - bottomPix - pixXLabel - topPix - sampleGapPix * (numSample-1))/numSample;
        int idxSample = 0;
        for(Protein protein : proteins){
            ArrayList<Integer> pepSelIndex = protein.getPepIndex(pos);
            if(pepSelIndex.size()==0){
                continue;
            }

            ArrayList<PepPos> arrangePep = new ArrayList<>();
            ArrayList<Peptide> peps = new ArrayList<>();

            int minX = 100000;
            int maxX = 0;
            TreeMap<String, ArrayList<Integer>> groupIndex = new TreeMap<>();
            ArrayList<Integer> startSel = new ArrayList<>();
            ArrayList<Integer> endSel = new ArrayList<>();
            int pepSelIdx = 0;

            String modiRes = null;
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
                String modiTypeTmp = pepTmp.getModiType(pos - st);
                if(modiTypeTmp == null){
                    modiTypeTmp = "NULL";
                }

                if(modiRes==null){
                    modiRes=pepTmp.getModiRes(pos-st);
                }

                if(groupIndex.containsKey(modiTypeTmp)){
                    groupIndex.get(modiTypeTmp).add(pepSelIdx);
                } else {
                    ArrayList<Integer> tmp = new ArrayList<>();
                    tmp.add(pepSelIdx);
                    groupIndex.put(modiTypeTmp, tmp);
                }
                peps.add(protein.getPeptide(idx));
                startSel.add(st);
                endSel.add(ed);
                //PepPos tmp = new PepPos(protein.getPepStart(idx), protein.getPepEnd(idx), protein.getPeptide(idx));
                //arrangePep.add(tmp);
                pepSelIdx++;
            }
            minXs.add(minX);
            maxXs.add(maxX);

            for(Map.Entry<String, ArrayList<Integer>> entry : groupIndex.entrySet()){
                int st = 100000;
                int ed = 0;
                String id = null;
                double abundance = 0;
                for(int idx : entry.getValue()){
                    if(id == null){
                        id = peps.get(idx).getId();
                    } else {
                        id = id + "_" + peps.get(idx).getId();
                    }
                    if(st > startSel.get(idx)){
                        st = startSel.get(idx);
                    }
                    if(ed < endSel.get(idx)){
                        ed = endSel.get(idx);
                    }
                    abundance += peps.get(idx).getAbundance();
                }
                if(entry.getKey().equals("NULL")){
                    Peptide pepTmp = new Peptide(id, protein.getSequence(st, ed+1), abundance, null, null, null, 0);
                    PepPos pepPosTmp = new PepPos(st, ed, pepTmp);
                    arrangePep.add(pepPosTmp);
                } else {
                    ArrayList<Modification> modisTmp = new ArrayList<>();
                    ArrayList<Integer> posTmp =  new ArrayList<>();
                    ArrayList<String> resTmp = new ArrayList<>();
                    ArrayList<Double> percentTmp = new ArrayList<>();
                    posTmp.add(pos-st);
                    resTmp.add(modiRes);
                    percentTmp.add(100.0);
                    Modification modi = new Modification(entry.getKey(), posTmp, resTmp, percentTmp);
                    modisTmp.add(modi);
                    Peptide pepTmp = new Peptide(id, protein.getSequence(st, ed+1), abundance, null, null, null, modisTmp, 0);
                    PepPos pepPosTmp = new PepPos(st, ed, pepTmp);
                    arrangePep.add(pepPosTmp);
                }

                if(!entry.getKey().equals("NULL")){
                    modiSelected.add(entry.getKey());
                }
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
            maxYs.add(maxY);
            arrangePeps.add(arrangePep);

            Label label = new Label();
            String text = selectedSamples.get(idxSample) + System.lineSeparator();

            double abAll = 0;
            for(PepPos pepPos : arrangePep){
                abAll += pepPos.getPep().getAbundance();
            }

            for(PepPos pepPos : arrangePep){
                Peptide pep = pepPos.getPep();
                if(pep.getModifications().size()==0){
                    text += "NoModi: " + String.format("%.2g",pep.getAbundance()) + ", ";
                    text += Util.percentFormat(pep.getAbundance()/abAll) + "%";
                    text += System.lineSeparator();
                } else {
                    text += pep.getModifications().get(0).getModificationType() + ": " + String.format("%.2g",pep.getAbundance()) + ", ";
                    text += Util.percentFormat(pep.getAbundance()/abAll)+ "%" + System.lineSeparator();
                }

            }
            label.setText(text);
            label.setPrefHeight(pixPerSample);
            label.setAlignment(Pos.CENTER_LEFT);
            vbLegend.getChildren().add(label);

            idxSample++;

        }

        seq = proteins.get(0).getSequence(Collections.min(minXs), Collections.max(maxXs)+1);
        scaleX = ((canvasWidth-20.0)/seq.length())/pixPerLocus;

        int maxY = Collections.max(maxYs);

        scaleY = (pixPerSample/maxY - pixPepGap)/pixPerPep;
        if(scaleY > 1){
            scaleY = 1;
        }

        gc = canvas.getGraphicsContext2D();
        draw();

        vbLegend.setSpacing(sampleGapPix);
        for(String selectedSample : selectedSamples){

        }

        /*
        if(arrangePep.size()==1){
            if(arrangePep.get(0).getPep().getModifications().size()==0){
                lblInfo.setText("One peptide without modification");
            } else {
                lblInfo.setText("One peptide with modification");
            }
        } else {
            double abM = 0;
            double abN = 0;
            for(PepPos tmp : arrangePep){
                if(tmp.getPep().getModifications().size()==0){
                    abN = tmp.getPep().getAbundance();
                } else {
                    abM = tmp.getPep().getAbundance();
                }
            }

            String msg = "Total Abundance of Peptides with Modification: " + String.format("%.2f", abM);
            msg += "; Total Abundance of Peptides without Modification: " + String.format("%.2f", abN);
            double rt = abM/abN;
            msg += "; Ratio=" + String.format("%.2f", rt);
            lblInfo.setText(msg);
        }
        */


        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();

                // start from 0
                int idxX = (int) ((x + Collections.min(minXs) * pixPerLocus * scaleX - leftPix) /(pixPerLocus*scaleX));
                if(idxX < 0){
                    return;
                }
                int idxY = (int) ((canvasHeight - pixXLabel - bottomPix - y - 1) / ((pixPerPep + pixPepGap * 2) * scaleY));

                if(y<topPix || y > (canvasHeight-bottomPix - pixXLabel)){
                    return;
                }

                double yAdj = y-topPix;
                //Start from 0
                int idxSample = (int)(yAdj / (pixPerSample + sampleGapPix));
                yAdj = yAdj - idxSample * (pixPerSample + sampleGapPix);
                idxY = (int)((pixPerSample-yAdj)/(pixPerPep*scaleY + pixPepGap));


                for(PepPos tmp : arrangePeps.get(idxSample)){
                    if(tmp.contains(idxX, idxY)) {
                        String info = tmp.getPep().toString();
                        info = info + "Start: " + (tmp.getStart() + 1) + System.lineSeparator();
                        info = info + "End: " + (tmp.getEnd() + 1);
                        lblPep.setText(info);
                        break;
                    } else {
                        lblPep.setText("No Peptide");
                    }
                }

                if(idxX==pos){
                    lblPos.setText(modiSelected.get(0));
                } else {
                    lblPos.setText("");
                }

            }
        });
    }

    private void draw(){
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        double st = Collections.min(minXs) * pixPerLocus*scaleX;

        double maxAbundance = 0;
        for(ArrayList<PepPos> arrangePep : arrangePeps){
            for(PepPos pp : arrangePep){
                double abTmp = pp.getPep().getAbundance();
                if(maxAbundance < abTmp){
                    maxAbundance = abTmp;
                }
            }
        }


        int idxSample = 0;
        for(ArrayList<PepPos> arrangePep : arrangePeps){
            double bottomSampleY = topPix + (idxSample+1) * pixPerSample + idxSample * sampleGapPix;
            for(PepPos pp : arrangePep){
                //top left coordinate
                double tlX = leftPix + pp.getStart()*pixPerLocus*scaleX + 0.5;
                //double tlY = canvasHeight - pixXLabel - bottomPix - (pp.getY() + 1) * (pixPerPep  + pixPepGap * 2) * scaleY + 1;
                double tlY = bottomSampleY - (pixPerPep*scaleY + pixPepGap) * (pp.getY() + 1);
                double w = pixPerLocus * pp.getPep().getLength()*scaleX - 0.5;
                double h = pixPerPep * scaleY;

                tlX = tlX - st;
                double rX = tlX + w;


                if(tlX < canvasWidth && rX > 0){
                    //int range = pp.getPep().getAbundanceRange();
                    double abTmp = pp.getPep().getAbundance();
                    int range = (int) (4 * abTmp / maxAbundance);
                    if(range == 4){
                        range = 3;
                    }
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
            idxSample++;
        }


        //X Axis
        double fontSize = pixPerLocus * scaleX /2.0  + 3.5;

        if(fontSize > 16){
            fontSize = 16;
        }

        String proteinSeq = proteins.get(0).getSequence();
        //ArrayList<Integer> modiPos = protein.getModiPosSel(modiSelected);
        int xAxisStart = (int)(st / (pixPerLocus * scaleX));
        int xAxisEnd = (int)(0.5 + (st + canvasWidth)/(pixPerLocus * scaleX));

        for(int i = xAxisStart; i <= xAxisEnd ;i++){
            gc.setFont(new Font("Courier New", fontSize));
            if(i>=proteinSeq.length()){
                break;
            }
            double leftX = leftPix + (i + 0.3) * pixPerLocus * scaleX - st;
            double topY =  canvasHeight - pixXLabel - bottomPix + pixPerLocus * 2;
            //if(modiPos.contains(i)){
            if(i==pos){
                gc.setFill(Color.CRIMSON);
            } else {
                gc.setFill(Color.BLACK);
            }
            gc.fillText(Character.toString(proteinSeq.charAt(i)), leftX, topY);
        }

        lblStart.setText("Start:" + (xAxisStart + 1));
        //Warning: check whether need to + 1 here
        lblEnd.setText("End:" + (xAxisEnd-1));
        //legend();
    }
}
