package controller.result;

import data.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by gpeng on 1/8/18.
 */
public class PepCombineController {
    private Protein protein;
    private SampleGroup sampleGroup;
    private int pos;
    private int maxY;
    private int minX, maxX;
    private String seq;
    private GraphicsContext gc;
    private ArrayList<PepPos> arrangePep;

    private double scaleX = 1;
    private double scaleY = 1;

    private final double canvasWidth = 800;
    private final double canvasHeight = 200;
    private final double pixPerLocus = 10;
    private final double pixPerPep = 32;
    private final double pixPepGap = 1;
    private final double leftPix =10;
    //private final double rightPix = 10;
    private final double bottomPix = 10;
    private final double pixXLabel = 30;
    //private ArrayList<Modification> modiSelected;
    private ArrayList<String> modiSelected;


    @FXML private Canvas canvas;
    @FXML private Label lblStart;
    @FXML private Label lblEnd;
    @FXML private Label lblPep;
    @FXML private Label lblPos;


    public void setData(SampleGroup sampleGroup, Protein protein, int pos){
        this.sampleGroup = sampleGroup;
        this.protein = protein;
        this.pos = pos;

        ArrayList<Integer> pepSelIndex = protein.getPepIndex(pos);
        if(pepSelIndex.size()==0){
            return;
        }
        arrangePep = new ArrayList<>();
        ArrayList<Peptide> peps = new ArrayList<>();

        minX = 100000;
        maxX = 0;
        TreeMap<String, ArrayList<Integer>> groupIndex = new TreeMap<>();
        ArrayList<Integer> startSel = new ArrayList<>();
        ArrayList<Integer> endSel = new ArrayList<>();
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
            String modiTypeTmp = pepTmp.getModiType(pos - st);
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
            peps.add(protein.getPeptide(idx));
            startSel.add(st);
            endSel.add(ed);
            //PepPos tmp = new PepPos(protein.getPepStart(idx), protein.getPepEnd(idx), protein.getPeptide(idx));
            //arrangePep.add(tmp);
            pepSelIdx++;
        }
        seq = protein.getSequence(minX, maxX+1);
        scaleX = ((canvasWidth-20.0)/seq.length())/pixPerLocus;

        modiSelected = new ArrayList<>();
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
                Peptide pepTmp = new Peptide(id, protein.getSequence(st, ed+1), abundance, null, null, null);
                PepPos pepPosTmp = new PepPos(st, ed, pepTmp);
                arrangePep.add(pepPosTmp);
            } else {
                ArrayList<Modification> modisTmp = new ArrayList<>();
                ArrayList<Integer> posTmp =  new ArrayList<>();
                ArrayList<Double> percentTmp = new ArrayList<>();
                posTmp.add(pos-st);
                percentTmp.add(100.0);
                Modification modi = new Modification(entry.getKey(), posTmp, percentTmp);
                modisTmp.add(modi);
                Peptide pepTmp = new Peptide(id, protein.getSequence(st, ed+1), abundance, null, null, null, modisTmp);
                PepPos pepPosTmp = new PepPos(st, ed, pepTmp);
                arrangePep.add(pepPosTmp);
            }


            //ArrayList<Integer> modiPos = new ArrayList<>();
            //ArrayList<Double> modiPercent = new ArrayList<>();
            //modiPos.add(pos - st);
            //modiPercent.add(100.0);
            //Modification modi = new Modification(entry.getKey(), modiPos, modiPercent);
            if(!entry.getKey().equals("NULL")){
                modiSelected.add(entry.getKey());
            }
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

        gc = canvas.getGraphicsContext2D();
        draw();


        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();

                // start from 0
                int idxX = (int) ((x + minX * pixPerLocus * scaleX - leftPix) /(pixPerLocus*scaleX));
                if(idxX < 0){
                    return;
                }
                int idxY = (int) ((canvasHeight - pixXLabel - bottomPix - y - 1) / ((pixPerPep + pixPepGap * 2) * scaleY));

                for(PepPos tmp : arrangePep){
                    if(tmp.contains(idxX, idxY)) {
                        String info = tmp.getPep().toString();
                        info = info + "Start: " + (tmp.getStart() + 1) + "\n";
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

        double st = minX * pixPerLocus*scaleX;

        for(PepPos pp : arrangePep){
            //top left coordinate
            double tlX = leftPix + pp.getStart()*pixPerLocus*scaleX + 0.5;
            double tlY = canvasHeight - pixXLabel - bottomPix - (pp.getY() + 1) * (pixPerPep  + pixPepGap * 2) * scaleY + 1;
            double w = pixPerLocus * pp.getPep().getLength()*scaleX - 0.5;
            double h = pixPerPep * scaleY;

            tlX = tlX - st;
            double rX = tlX + w;


            if(tlX < canvasWidth && rX > 0){
                //int range = pp.getPep().getAbundanceRange();
                gc.setFill(Color.LIGHTGRAY);
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
        //legend();
    }
}
