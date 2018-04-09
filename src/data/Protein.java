package data;

import javafx.util.Pair;
import project.PublicInfo;
import sun.reflect.generics.tree.Tree;
import sun.rmi.server.InactiveGroupException;

import java.io.Serializable;
import java.util.*;

/**
 * protein or gene information
 * Created by gpeng on 2/10/17.
 */
@SuppressWarnings("Duplicates")

public class Protein implements Serializable {
    private String name;
    private String sequence;

    private ArrayList<Peptide> peptides;
    //start and end are all included in peptide
    // index from 0
    private ArrayList<Integer> pepStart;
    private ArrayList<Integer> pepEnd;
    private TreeSet<String> modiTypeAll;
    private TreeMap<Integer, PosModiInfo> modiInfo;
    // <= 0, not show
    private ArrayList<Integer> pepShow;
    //Modification information for selected peptides
    private TreeSet<String> modiTypeAllSel;
    private TreeMap<Integer, PosModiInfo> modiInfoSel;

    private boolean pepCombined = false;
    private ArrayList<Peptide> peptidesCombined;
    private ArrayList<Integer> pepStartCombined;
    private ArrayList<Integer> pepEndCombined;
    private TreeMap<Integer, PosModiInfo> modiInfoCombined;
    private TreeSet<String> modiTypeAllCombined;


    // range of peptide data
    /*
    private int chargeMax;
    private int chargeMin;
    private double mzMax;
    private double mzMin;
    private double scoreMax;
    private double scoreMin;
    */
    private double abundanceMax;
    private double abundanceMin;

    private ArrayList<Double> doubleInfoMax;
    private ArrayList<Double> doubleInfoMin;

    // cutoff of peptide data
    /*
    private int chargeCutHigh;
    private int chargeCutLow;
    private double mzCutHigh;
    private double mzCutLow;
    private double scoreCutHigh;
    private double scoreCutLow;
    */
    private double abundanceCutHigh;
    private double abundanceCutLow;

    private ArrayList<Double> doubleInfoCutHigh;
    private ArrayList<Double> doubleInfoCutLow;

    /*
    private double chargeCutPerHigh;
    private double chargeCutPerLow;
    private double mzCutPerHigh;
    private double mzCutPerLow;
    private double scoreCutPerHigh;
    private double scoreCutPerLow;
    */
    private double abundanceCutPerHigh;
    private double abundanceCutPerLow;

    private ArrayList<Double> doubleInfoCutPerHigh;
    private ArrayList<Double> doubleInfoCutPerLow;



    /*
    private ArrayList<Integer> chargeAll;
    private ArrayList<Double> mzAll;
    private ArrayList<Double> scoreAll;
    */
    private ArrayList<Double> abundanceAll;

    private ArrayList<ArrayList<Double> > doubleInfoAll;

    private ArrayList<String> doubleInfoName;

    private ArrayList<Double> abundanceAllCombined;


    //before normalization
    /**
     * raw abundance
     */
    private double rawAbundance=-1;
    /**
     * iBAQ abundance
     */
    private double iBAQAbundance=-1;


    //after median normalization
    private double rawAbundanceMedianNorm;
    private double iBAQAbundanceMedianNorm;
    private double rawAbundanceSelProteinNorm;
    private double iBAQAbundanceSelProteinNorm;

    public String getName() { return  name;}
    public String getSequence() { return sequence;}
    public String getSequence(int st, int ed) { return sequence.substring(st, ed);}
    public int getLength() {return sequence.length(); }
    public ArrayList<Peptide> getPeptides() { return peptides; }
    public ArrayList<Peptide> getPeptidesCombined() {return peptidesCombined; }
    public ArrayList<Integer> getPepStart() { return  pepStart; }
    public ArrayList<Integer> getPepEnd() { return pepEnd; }
    public ArrayList<Integer> getPepStartCombined() { return pepStartCombined;}
    public ArrayList<Integer> getPepEndCombined() { return pepEndCombined; }
    public TreeSet<String> getModiTypeAll() { return modiTypeAll; }
    public Set<Integer> getModiPos() { return  modiInfo.keySet(); }
    public TreeMap<Integer, PosModiInfo> getModiInfo() { return  modiInfo; }
    public TreeSet<String> getModiTypeAllSel() { return  modiTypeAllSel; }
    public Set<Integer> getModiPosSel() { return  modiInfoSel.keySet(); }
    public TreeMap<Integer, PosModiInfo> getModiInfoSel() { return modiInfoSel; }
    public TreeMap<Integer, PosModiInfo> getModiInfoCombined() { return  modiInfoCombined; }
    public ArrayList<Integer> getPepShow() { return  pepShow; }
    public Integer getPepShow(int i) { return pepShow.get(i); }
    public TreeSet<String> getModiTypeAllCombined() { return modiTypeAllCombined; }
    public Integer getPepStart(int idx) { return pepStart.get(idx); }
    public Integer getPepEnd(int idx) { return pepEnd.get(idx); }

    public Peptide getPeptide(int idx) { return peptides.get(idx); }

    public void setPepShow(int i, int val) { pepShow.set(i,val); }
    public void setPepShow(ArrayList<Integer> pepShow) { this.pepShow = pepShow; }

    public Set<String> getPepStrInfo(){
        return peptides.get(0).getStrInfo().keySet();
    }

    public Set<String> getPepDoubleInfo(){
        return peptides.get(0).getDoubleInfo().keySet();
    }


    public ArrayList<Integer> getModiPos(String mt) {
        ArrayList<Integer> rlt = new ArrayList<>();
        for(Map.Entry<Integer, PosModiInfo> entry : modiInfo.entrySet()){
            Integer key = entry.getKey();
            PosModiInfo value = entry.getValue();
            if(value.modiExist(mt)){
                rlt.add(key);
            }
        }
        return rlt;
    }


    public ArrayList<Integer> getModiPos(ArrayList<String> mts) {
        ArrayList<Integer> rlt = new ArrayList<>();
        for(Map.Entry<Integer, PosModiInfo> entry : modiInfo.entrySet()){
            Integer key = entry.getKey();
            PosModiInfo value = entry.getValue();
            if(value.modiExist(mts)){
                rlt.add(key);
            }
        }
        return rlt;
    }

    public ArrayList<Integer> getModiCombinedPos(ArrayList<String> mts){
        ArrayList<Integer> rlt = new ArrayList<>();
        for(Map.Entry<Integer, PosModiInfo> entry : modiInfoCombined.entrySet()){
            Integer key = entry.getKey();
            PosModiInfo value = entry.getValue();
            if(value.modiExist(mts)){
                rlt.add(key);
            }
        }
        return rlt;
    }

    public ArrayList<Integer> getModiPosSel(String mt){
        ArrayList<Integer> rlt = new ArrayList<>();
        for(Map.Entry<Integer, PosModiInfo> entry : modiInfoSel.entrySet()){
            Integer key = entry.getKey();
            PosModiInfo value = entry.getValue();
            if(value.modiExist(mt)){
                rlt.add(key);
            }
        }
        return rlt;
    }

    public ArrayList<Integer> getModiPosSel(ArrayList<String> mts){
        ArrayList<Integer> rlt = new ArrayList<>();
        for(Map.Entry<Integer, PosModiInfo> entry : modiInfoSel.entrySet()){
            Integer key = entry.getKey();
            PosModiInfo value = entry.getValue();
            if(value.modiExist(mts)){
                rlt.add(key);
            }
        }
        return rlt;
    }


    public ArrayList<Integer> getModiPosSel1(String mt){
        ArrayList<Integer> rlt = new ArrayList<>();
        for(Map.Entry<Integer, PosModiInfo> entry : modiInfoSel.entrySet()){
            Integer key = entry.getKey();
            PosModiInfo value = entry.getValue();
            if(value.modiExist(mt)){
                rlt.add(key+1);
            }
        }
        return rlt;
    }

    public ArrayList<Integer> getModiPosSel1(ArrayList<String> mts){
        ArrayList<Integer> rlt = new ArrayList<>();
        for(Map.Entry<Integer, PosModiInfo> entry : modiInfoSel.entrySet()){
            Integer key = entry.getKey();
            PosModiInfo value = entry.getValue();
            if(value.modiExist(mts)){
                rlt.add(key+1);
            }
        }
        return rlt;
    }

    public ArrayList<Integer> getModiCombinedPos1(ArrayList<String> mts){
        ArrayList<Integer> rlt = new ArrayList<>();
        for(Map.Entry<Integer, PosModiInfo> entry : modiInfoCombined.entrySet()){
            Integer key = entry.getKey();
            PosModiInfo value = entry.getValue();
            if(value.modiExist(mts)){
                rlt.add(key+1);
            }
        }
        return rlt;
    }

    public ArrayList<String> getDoubleInfoName(){
        return doubleInfoName;
    }

    /*
    public int getChargeMax() { return chargeMax; }
    public int getChargeMin() { return chargeMin; }
    public int getChargeCutHigh() { return chargeCutHigh; }
    public int getChargeCutLow() { return chargeCutLow; }
    public double getMzMax() { return mzMax; }
    public double getMzMin() { return mzMin; }
    public double getMzCutHigh() { return mzCutHigh; }
    public double getMzCutLow() { return  mzCutLow; }
    public double getScoreMax() { return scoreMax; }
    public double getScoreMin() { return scoreMin; }
    public double getScoreCutHigh() { return scoreCutHigh; }
    public double getScoreCutLow() { return scoreCutLow; }
    */
    public double getAbundanceMax() { return abundanceMax; }
    public double getAbundanceMin() { return abundanceMin; }
    public double getAbundanceCutHigh() { return abundanceCutHigh; }
    public double getAbundanceCutLow() { return abundanceCutLow; }
    public double getDoubleInfoMax(int i) { return doubleInfoMax.get(i);}
    public double getDoubleInfoMin(int i) { return doubleInfoMin.get(i);}
    public double getDoubleInfoCutHigh(int i) { return  doubleInfoCutHigh.get(i);}
    public double getDoubleInfoCutLow(int i) { return  doubleInfoCutLow.get(i);}

    /*
    public double getChargeCutPerHigh() {return chargeCutPerHigh; }
    public double getChargeCutPerLow() { return chargeCutPerLow; }
    public double getMzCutPerHigh() { return mzCutPerHigh; }
    public double getMzCutPerLow() { return mzCutPerLow; }
    public double getScoreCutPerHigh() { return scoreCutPerHigh; }
    public double getScoreCutPerLow() { return scoreCutPerLow; }
    */
    public double getAbundanceCutPerHigh() { return abundanceCutPerHigh; }
    public double getAbundanceCutPerLow() { return abundanceCutPerLow; }
    public double getDoubleInfoCutPerHigh(int i) { return doubleInfoCutPerHigh.get(i);}
    public double getDoubleInfoCutPerLow(int i) { return doubleInfoCutPerLow.get(i);}

    public void combinePep(boolean isCharge, ArrayList<String> modiCriteria){
        if(pepCombined){
            if(peptides.size()==0){
                return;
            }

            peptidesCombined.clear();
            pepStartCombined.clear();
            pepEndCombined.clear();
            modiInfoCombined.clear();
            modiTypeAllCombined.clear();
            abundanceAllCombined.clear();

        } else {
            peptidesCombined = new ArrayList<>();
            pepStartCombined = new ArrayList<>();
            pepEndCombined = new ArrayList<>();
            modiInfoCombined = new TreeMap<>();
            modiTypeAllCombined = new TreeSet<>();

            if(peptides.size()==0){
                return;
            }
        }

        ArrayList<Integer> flag = new ArrayList<>();
        for(int i=0; i<peptides.size(); i++){
            flag.add(i);
        }

        ArrayList<ArrayList<Integer> > groupIndex = new ArrayList<>();
        while(true){
            if(flag.size()==0){
                break;
            }

            ArrayList<Integer> giTmp = new ArrayList<>();
            giTmp.add(flag.get(0));
            Peptide pep = peptides.get(flag.get(0));
            for(int i=1; i<flag.size();i++){
                if(pep.isSimilar(peptides.get(flag.get(i)), isCharge, modiCriteria)){
                    giTmp.add(flag.get(i));
                }
            }

            for(Integer idx : giTmp){
                flag.remove(idx);
            }

            groupIndex.add(giTmp);

            if(flag.size()==0){
                break;
            }
        }

        abundanceAllCombined.clear();

        for(int i=0; i<groupIndex.size(); i++){
            ArrayList<Integer> giTmp = groupIndex.get(i);
            String id = peptides.get(giTmp.get(0)).getId();
            String sequence = peptides.get(giTmp.get(0)).getSequence();
            Integer charge = peptides.get(giTmp.get(0)).getCharge();
            Double abundance = peptides.get(giTmp.get(0)).getAbundance();
            TreeMap<String, Double> doubleInfo = new TreeMap<>();
            TreeMap<String, String> strInfo = new TreeMap<>();
            ArrayList<Modification> modifications = peptides.get(giTmp.get(0)).getModifications(modiCriteria);

            for(int j=1; j<giTmp.size(); j++){
                id = id + "_" + peptides.get(giTmp.get(j)).getId();
                abundance = abundance + peptides.get(giTmp.get(j)).getAbundance();
            }


            if(!isCharge){
                charge = null;
            }
            Peptide pep = new Peptide(id, sequence, abundance, charge, doubleInfo, strInfo, modifications, 0);
            peptidesCombined.add(pep);
            pepStartCombined.add(pepStart.get(giTmp.get(0)));
            pepEndCombined.add(pepEnd.get(giTmp.get(0)));
            abundanceAllCombined.add(abundance);
            //modiInfoCombined
            if(pep.isModified()){
                for(Modification m : pep.getModifications()){
                    modiTypeAllCombined.add(m.getModificationType());
                    for(int j=0; j<m.getPos().size(); j++){
                        int modiPos = m.getPos().get(j) + pepStart.get(giTmp.get(0));
                        PosModiInfo tmp = modiInfoCombined.get(modiPos);
                        if(tmp == null){
                            PosModiInfo posModiInfo = new PosModiInfo(m.getModificationType(), m.getPercent().get(j));
                            modiInfoCombined.put(modiPos, posModiInfo);
                        } else {
                            tmp.addModi(m.getModificationType(), m.getPercent().get(j));
                        }
                    }
                }
            }
        }

        pepCombined = true;
        Collections.sort(abundanceAllCombined);
        return;
    }

    public ArrayList<Integer> getPepIndex(int pos){
        ArrayList<Integer> rlt = new ArrayList<>();

        for(int i=0; i<pepStart.size(); i++){
            if(pepStart.get(i) <= pos && pepEnd.get(i) >= pos){
                rlt.add(i);
            }
        }

        return rlt;
    }

    /*
    public void combinePep(ArrayList<String> doubleInfoCriteria, ArrayList<String> strInfoCriteria, ArrayList<String> modiCriteria){
        if(pepCombined){
            if(peptides.size()==0){
                return;
            }

            peptidesCombined.clear();
            pepStartCombined.clear();
            pepEndCombined.clear();
            modiInfoCombined.clear();
            modiTypeAllCombined.clear();
            abundanceAllCombined.clear();

        } else {
            peptidesCombined = new ArrayList<>();
            pepStartCombined = new ArrayList<>();
            pepEndCombined = new ArrayList<>();
            modiInfoCombined = new TreeMap<>();
            modiTypeAllCombined = new TreeSet<>();

            if(peptides.size()==0){
                return;
            }
        }

        ArrayList<Integer> flag = new ArrayList<>();
        for(int i=0; i<peptides.size(); i++){
            flag.add(i);
        }

        ArrayList<ArrayList<Integer> > groupIndex = new ArrayList<>();
        while(true){
            if(flag.size()==0){
                break;
            }

            ArrayList<Integer> giTmp = new ArrayList<>();
            giTmp.add(flag.get(0));
            Peptide pep = peptides.get(flag.get(0));
            for(int i=1; i<flag.size();i++){
                if(pep.isSimilar(peptides.get(flag.get(i)), doubleInfoCriteria, strInfoCriteria, modiCriteria)){
                    giTmp.add(flag.get(i));
                }
            }

            for(Integer idx : giTmp){
                flag.remove(idx);
            }

            groupIndex.add(giTmp);

            if(flag.size()==0){
                break;
            }
        }

        abundanceAllCombined.clear();

        for(int i=0; i<groupIndex.size(); i++){
            ArrayList<Integer> giTmp = groupIndex.get(i);
            String id = peptides.get(giTmp.get(0)).getId();
            String sequence = peptides.get(giTmp.get(0)).getSequence();
            Double abundance = peptides.get(giTmp.get(0)).getAbundance();
            TreeMap<String, Double> doubleInfo = new TreeMap<>();
            TreeMap<String, String> strInfo = new TreeMap<>();
            ArrayList<Modification> modifications = peptides.get(giTmp.get(0)).getModifications(modiCriteria);

            for(String dic : doubleInfoCriteria){
                doubleInfo.put(dic, peptides.get(giTmp.get(0)).getDoubleInfo(dic));
            }

            for(String sic : strInfoCriteria){
                strInfo.put(sic, peptides.get(giTmp.get(0)).getStrInfo(sic));
            }

            for(int j=1; j<giTmp.size(); j++){
                id = id + "_" + peptides.get(giTmp.get(j)).getId();
                abundance = abundance + peptides.get(giTmp.get(j)).getAbundance();
            }

            Peptide pep = new Peptide(id, sequence, abundance, doubleInfo, strInfo, modifications);
            peptidesCombined.add(pep);
            pepStartCombined.add(pepStart.get(giTmp.get(0)));
            pepEndCombined.add(pepEnd.get(giTmp.get(0)));
            abundanceAllCombined.add(abundance);
            //modiInfoCombined
            if(pep.isModified()){
                for(Modification m : pep.getModifications()){
                    modiTypeAllCombined.add(m.getModificationType());
                    for(int j=0; j<m.getPos().size(); j++){
                        int modiPos = m.getPos().get(j) + pepStart.get(giTmp.get(0));
                        PosModiInfo tmp = modiInfoCombined.get(modiPos);
                        if(tmp == null){
                            PosModiInfo posModiInfo = new PosModiInfo(m.getModificationType(), m.getPercent().get(j));
                            modiInfoCombined.put(modiPos, posModiInfo);
                        } else {
                            tmp.addModi(m.getModificationType(), m.getPercent().get(j));
                        }
                    }
                }
            }
        }

        pepCombined = true;
        Collections.sort(abundanceAllCombined);
        return;
    }
    */

    /*
    public void setChargeCutHigh(int chargeCutHigh){
        this.chargeCutHigh = chargeCutHigh;
        int idx = -1;
        for(int i=0; i<chargeAll.size();i++){
            if(chargeAll.get(i) > chargeCutHigh){
                idx = i;
                break;
            }
        }
        if(idx < 0){
            chargeCutPerHigh = 1;
        } else {
            chargeCutPerHigh = (double)idx / chargeAll.size();
        }
        updatePepShow();
    }

    public void setChargeCutLow(int chargeCutLow){
        this.chargeCutLow = chargeCutLow;
        int idx = -1;
        for(int i=0; i<chargeAll.size();i++){
            if(chargeAll.get(i) >= chargeCutLow){
                idx = i;
                break;
            }
        }
        if(idx < 0){
            chargeCutPerLow = 1;
        } else {
            chargeCutPerLow = (double)idx / chargeAll.size();
        }
        updatePepShow();
    }

    public void setMzCutHigh(double mzCutHigh){
        this.mzCutHigh = mzCutHigh;
        int idx = -1;
        for(int i=0; i<mzAll.size();i++){
            if(mzAll.get(i) > mzCutHigh){
                idx = i;
                break;
            }
        }
        if(idx < 0){
            mzCutPerHigh = 1;
        } else {
            mzCutPerHigh = (double)idx / mzAll.size();
        }
        updatePepShow();
    }

    public void setMzCutLow(double mzCutLow){
        this.mzCutLow = mzCutLow;
        int idx = -1;
        for(int i=0; i<mzAll.size();i++){
            if(mzAll.get(i) >= mzCutLow){
                idx = i;
                break;
            }
        }
        if(idx < 0){
            mzCutPerLow= 1;
        } else {
            mzCutPerLow = (double)idx / mzAll.size();
        }
        updatePepShow();
    }

    public void setScoreCutHigh(double scoreCutHigh){
        this.scoreCutHigh = scoreCutHigh;
        int idx = -1;
        for(int i=0; i<scoreAll.size();i++){
            if(scoreAll.get(i) > scoreCutHigh){
                idx = i;
                break;
            }
        }
        if(idx < 0){
            scoreCutPerHigh = 1;
        } else {
            scoreCutPerHigh = (double)idx / scoreAll.size();
        }
        updatePepShow();
    }

    public void setScoreCutLow(double scoreCutLow){
        this.scoreCutLow = scoreCutLow;
        int idx = -1;
        for(int i=0; i<scoreAll.size();i++){
            if(scoreAll.get(i) >= scoreCutLow){
                idx = i;
                break;
            }
        }
        if(idx < 0){
            scoreCutPerLow = 1;
        } else {
            scoreCutPerLow = (double)idx / scoreAll.size();
        }
        updatePepShow();
    }
    */

    public void setAbundanceCutHigh(double abundanceCutHigh){
        this.abundanceCutHigh = abundanceCutHigh;
        int idx = -1;
        for(int i=0; i<abundanceAll.size();i++){
            if(abundanceAll.get(i) > abundanceCutHigh){
                idx = i;
                break;
            }
        }
        if(idx < 0){
            abundanceCutPerHigh = 1;
        } else {
            abundanceCutPerHigh = (double)idx / abundanceAll.size();
        }
        updatePepShow();
    }

    public void setAbundanceCutLow(double abundanceCutLow){
        this.abundanceCutLow = abundanceCutLow;
        int idx = -1;
        for(int i=0; i<abundanceAll.size();i++){
            if(abundanceAll.get(i) >= abundanceCutLow){
                idx = i;
                break;
            }
        }
        if(idx < 0 || idx == (abundanceAll.size()-1)){
            abundanceCutPerLow = 1;
        } else {
            abundanceCutPerLow = (double)idx / abundanceAll.size();
        }
        updatePepShow();
    }

    public void setDoubleInfoCutHigh(int index, double cutHigh){
        doubleInfoCutHigh.set(index, cutHigh);
        int idx = -1;
        for(int i = 0; i<doubleInfoAll.get(index).size(); i++){
            if(doubleInfoAll.get(index).get(i) >  cutHigh){
                idx = i;
                break;
            }
        }
        if(idx < 0){
            doubleInfoCutPerHigh.set(index, 1.0);
        } else {
            doubleInfoCutPerHigh.set(index, (double) idx / doubleInfoAll.get(index).size());
        }
        updatePepShow();
    }

    public void setDoubleInfoCutLow(int index, double cutLow){
        doubleInfoCutLow.set(index, cutLow);
        int idx = -1;
        for(int i=0; i<doubleInfoAll.get(index).size();i++){
            if(doubleInfoAll.get(index).get(i) >= cutLow){
                idx = i;
                break;
            }
        }
        if(idx < 0 || idx == (doubleInfoAll.get(index).size()-1)){
            doubleInfoCutPerLow.set(index, 1.0);
        } else {
            doubleInfoCutPerLow.set(index, (double) idx/ doubleInfoAll.get(index).size());
        }
        updatePepShow();
    }

    /*
    public void setChargeCutPerHigh(double chargeCutPerHigh){
        this.chargeCutPerHigh = chargeCutPerHigh;
        int idx = (int) (chargeAll.size() * chargeCutPerHigh);
        chargeCutHigh = chargeAll.get(idx);
        updatePepShow();
    }

    public void setChargeCutPerLow(double chargeCutPerLow){
        this.chargeCutPerLow = chargeCutPerLow;
        int idx = (int) (chargeAll.size() * chargeCutPerLow);
        chargeCutLow = chargeAll.get(idx);
        updatePepShow();
    }

    public void setMzCutPerHigh(double mzCutPerHigh){
        this.mzCutPerHigh = mzCutPerHigh;
        int idx = (int) (mzAll.size() * mzCutPerHigh);
        mzCutHigh = mzAll.get(idx);
        updatePepShow();
    }

    public void setMzCutPerLow(double mzCutPerLow){
        this.mzCutPerLow = mzCutPerLow;
        int idx = (int) (mzAll.size() * mzCutPerLow);
        mzCutLow = mzAll.get(idx);
        updatePepShow();
    }

    public void setScoreCutPerHigh(double scoreCutPerHigh){
        this.scoreCutPerHigh = scoreCutPerHigh;
        int idx = (int) (scoreAll.size() * scoreCutPerHigh);
        scoreCutHigh = scoreAll.get(idx);
        updatePepShow();
    }

    public void setScoreCutPerLow(double scoreCutPerLow){
        this.scoreCutPerLow = scoreCutPerLow;
        int idx = (int) (scoreAll.size() * scoreCutPerLow);
        scoreCutLow = scoreAll.get(idx);
        updatePepShow();
    }
    */

    public void setAbundanceCutPerHigh(double abundanceCutPerHigh){
        this.abundanceCutPerHigh = abundanceCutPerHigh;
        int idx = (int) (abundanceAll.size() * abundanceCutPerHigh);
        if(idx == abundanceAll.size()){
            idx = abundanceAll.size() - 1;
        }
        abundanceCutHigh = abundanceAll.get(idx);
        updatePepShow();
    }

    public void setAbundanceCutPerLow(double abundanceCutPerLow){
        this.abundanceCutPerLow = abundanceCutPerLow;
        int idx = (int) (abundanceAll.size() * abundanceCutPerLow);
        if(idx == abundanceAll.size()){
            idx = abundanceAll.size()-1;
        }
        abundanceCutLow = abundanceAll.get(idx);
        updatePepShow();
    }

    public void setDoubleInfoCutPerHigh(int index, double cutPerHigh){
        doubleInfoCutPerHigh.set(index, cutPerHigh);
        int idx = (int) (doubleInfoAll.get(index).size()*cutPerHigh);
        if(idx == doubleInfoAll.get(index).size()){
            idx = doubleInfoAll.get(index).size() - 1;
        }
        doubleInfoCutHigh.set(index, doubleInfoAll.get(index).get(idx));
        updatePepShow();
    }

    public void setDoubleInfoCutPerLow(int index, double cutPerLow){
        doubleInfoCutPerLow.set(index, cutPerLow);
        int idx = (int) (doubleInfoAll.get(index).size() * cutPerLow);
        if(idx == doubleInfoAll.get(index).size()){
            idx = doubleInfoAll.get(index).size()-1;
        }
        doubleInfoCutLow.set(index, doubleInfoAll.get(index).get(idx));
        updatePepShow();
    }

    //update pepShow
    private void updatePepShow(){
        for(int i=0; i<peptides.size(); i++){
            pepShow.set(i, 1);

            /*
            if(peptides.get(i).getCharge() < chargeCutLow){
                pepShow.set(i, 0);
            }

            if(peptides.get(i).getCharge() > chargeCutHigh){
                pepShow.set(i, 0);
            }

            if(peptides.get(i).getMz() < mzCutLow){
                pepShow.set(i, 0);
            }

            if(peptides.get(i).getMz() > mzCutHigh){
                pepShow.set(i, 0);
            }

            if(peptides.get(i).getScore() < scoreCutLow){
                pepShow.set(i, 0);
            }

            if(peptides.get(i).getScore() > scoreCutHigh){
                pepShow.set(i, 0);
            }
            */

            if(peptides.get(i).getAbundance() < abundanceCutLow){
                pepShow.set(i, 0);
                continue;
            }

            if(peptides.get(i).getAbundance() > abundanceCutHigh){
                pepShow.set(i, 0);
                continue;
            }

            int idx = 0;
            for(double dInfo : peptides.get(i).getDoubleInfo().values()){
                if(dInfo < doubleInfoCutLow.get(idx)){
                    pepShow.set(i, 0);
                    break;
                }

                if(dInfo > doubleInfoCutHigh.get(idx)){
                    pepShow.set(i, 0);
                    break;
                }
                idx++;
            }
        }
    }

    public int numPepShow(){
        int num = 0;
        for(int n:pepShow){
            num += n;
        }
        return num;
    }

    public void initCutoff(){
        //Collections.sort(chargeAll);
        //Collections.sort(mzAll);
        //Collections.sort(scoreAll);
        Collections.sort(abundanceAll);
        for(ArrayList<Double> tmp : doubleInfoAll){
            Collections.sort(tmp);
        }
        //Collections.sort(chargeAll, Comparator.nullsFirst(Comparator.naturalOrder()));
        //Collections.sort(mzAll, Comparator.nullsFirst(Comparator.naturalOrder()));
        //Collections.sort(scoreAll, Comparator.nullsFirst(Comparator.naturalOrder()));
        //Collections.sort(abundanceAll, Comparator.nullsFirst(Comparator.naturalOrder()));

        /*
        chargeCutLow = chargeAll.get(0);
        chargeMin = chargeCutLow;
        chargeCutHigh = chargeAll.get(chargeAll.size()-1);
        chargeMax = chargeCutHigh;
        mzCutLow = mzAll.get(0);
        mzMin = mzCutLow;
        mzCutHigh = mzAll.get(mzAll.size()-1);
        mzMax = mzCutHigh;
        scoreCutLow = scoreAll.get(0);
        scoreMin = scoreCutLow;
        scoreCutHigh = scoreAll.get(scoreAll.size()-1);
        scoreMax = scoreCutHigh;
        */
        abundanceCutLow = abundanceAll.get(0);
        abundanceMin = abundanceCutLow;
        abundanceCutHigh = abundanceAll.get(abundanceAll.size()-1);
        abundanceMax = abundanceCutHigh;
        for(ArrayList<Double> tmp : doubleInfoAll){
            doubleInfoCutLow.add(tmp.get(0));
            doubleInfoMin.add(tmp.get(0));
            doubleInfoCutHigh.add(tmp.get(tmp.size()-1));
            doubleInfoMax.add(tmp.get(tmp.size()-1));
        }


        /*
        chargeCutPerHigh = 1;
        chargeCutPerLow = 0;
        mzCutPerHigh = 1;
        mzCutPerLow = 0;
        scoreCutPerHigh = 1;
        scoreCutPerLow = 0;
        */
        abundanceCutPerHigh = 1;
        abundanceCutPerLow = 0;
        for(int i=0;i<doubleInfoAll.size();i++){
            doubleInfoCutPerHigh.add(1.0);
            doubleInfoCutPerLow.add(0.0);
        }

    }





    public Protein(String name, String sequence){
        this.name = name;
        this.sequence = sequence;
        peptides = new ArrayList<>();
        pepStart = new ArrayList<>();
        pepEnd = new ArrayList<>();
        modiTypeAll = new TreeSet<>();
        modiInfo = new TreeMap<>();
        pepShow = new ArrayList<>();
        modiTypeAllSel = new TreeSet<>();
        modiInfoSel = new TreeMap<>();
        //chargeAll = new ArrayList<>();
        //mzAll = new ArrayList<>();
        //scoreAll = new ArrayList<>();
        abundanceAll = new ArrayList<>();
        abundanceAllCombined = new ArrayList<>();

        doubleInfoMax = new ArrayList<>();
        doubleInfoMin = new ArrayList<>();
        doubleInfoCutHigh = new ArrayList<>();
        doubleInfoCutLow = new ArrayList<>();
        doubleInfoCutPerHigh = new ArrayList<>();
        doubleInfoCutPerLow = new ArrayList<>();

        doubleInfoAll = new ArrayList<>();
        doubleInfoName = new ArrayList<>();

        /*
        chargeMax = Integer.MIN_VALUE;
        chargeMin = Integer.MAX_VALUE;
        mzMax = -Double.MAX_VALUE;
        mzMin = Double.MAX_VALUE;
        scoreMax = -Double.MAX_VALUE;
        scoreMin = Double.MAX_VALUE;
        abundanceMax = -Double.MAX_VALUE;
        abundanceMin = Double.MAX_VALUE;
        */
    }

    public boolean addPeptide(Peptide pep){
        //compare peptide sequence to protein sequence
        ArrayList<Integer> idxPos = new ArrayList<>();
        int start = 0;
        while (true){
            int idxTmp = sequence.indexOf(pep.getSequence(), start);
            if(idxTmp == -1){
                break;
            }
            idxPos.add(idxTmp);
            start = idxTmp + 1;
        }

        if(idxPos.size() == 0){
            System.out.println("Cannot find peptide " + pep.getSequence() + " in protein " + name);
            return  false;
        } else if(idxPos.size() == 1){
            peptides.add(pep);
            pepStart.add(idxPos.get(0));
            pepEnd.add(idxPos.get(0) + pep.getLength() - 1);
            pepShow.add(1);

            if(pep.getAbundance() == null){
                abundanceAll.add(-100.0);
            } else {
                abundanceAll.add(pep.getAbundance());
            }

            TreeMap<String, Double> dInfo = pep.getDoubleInfo();
            if(doubleInfoName.size() == 0){
                doubleInfoName.addAll(dInfo.keySet());
                for(int i = 0; i<dInfo.size(); i++){
                    ArrayList<Double> dTmp = new ArrayList<>();
                    doubleInfoAll.add(dTmp);
                }
                int idx = 0;
                for(Double v : dInfo.values()){
                    doubleInfoAll.get(idx).add(v);
                    idx++;
                }
            } else {
                int idx = 0;
                for(Double v : dInfo.values()){
                    doubleInfoAll.get(idx).add(v);
                    idx++;
                }
            }

            if(pep.isModified()){
                for(Modification m : pep.getModifications()){
                    modiTypeAll.add(m.getModificationType());
                    for(int i = 0; i < m.getPos().size(); i++){
                        int modiPos = m.getPos().get(i) + idxPos.get(0);
                        PosModiInfo tmp = modiInfo.get(modiPos);
                        if(tmp == null){
                            PosModiInfo posModiInfo = new PosModiInfo(m.getModificationType(), m.getPercent().get(i));
                            modiInfo.put(modiPos, posModiInfo);
                        } else {
                            tmp.addModi(m.getModificationType(), m.getPercent().get(i));
                        }
                    }
                }
            }
            return true;
        } else {
            //pep.setMultiMatch(true);
            System.out.println("Multiple Match");
            System.out.println(name);
            System.out.println(pep.getSequence());
            TreeMap<String, Double> dInfo = pep.getDoubleInfo();
            if(doubleInfoName.size() == 0){
                doubleInfoName.addAll(dInfo.keySet());
                for(int i = 0; i<dInfo.size(); i++){
                    ArrayList<Double> dTmp = new ArrayList<>();
                    doubleInfoAll.add(dTmp);
                }
                int idx = 0;
                for(Double v : dInfo.values()){
                    doubleInfoAll.get(idx).add(v);
                    idx++;
                }
            } else {
                int idx = 0;
                for(Double v : dInfo.values()){
                    doubleInfoAll.get(idx).add(v);
                    idx++;
                }
            }

            int idxMultiple = 0;
            for(Integer idxPosTmp : idxPos){
                idxMultiple++;
                Peptide ptTmp = new Peptide(pep.getId(), pep.getSequence(),  pep.getAbundance(),
                        pep.getCharge(), pep.getDoubleInfo(), pep.getStrInfo(), pep.getModifications(), idxMultiple);
                peptides.add(ptTmp);
                pepStart.add(idxPosTmp);
                pepEnd.add(idxPosTmp + ptTmp.getLength() - 1);
                pepShow.add(1);
                if(ptTmp.getAbundance() == null){
                    abundanceAll.add(-100.0);
                } else {
                    abundanceAll.add(ptTmp.getAbundance());
                }
                if(ptTmp.isModified()){
                    for(Modification m :ptTmp.getModifications()){
                        modiTypeAll.add(m.getModificationType());
                        for(int i=0; i<m.getPos().size(); i++){
                            int modiPos = m.getPos().get(i) + idxPosTmp;
                            PosModiInfo tmp = modiInfo.get(modiPos);
                            if(tmp == null){
                                PosModiInfo posModiInfo = new PosModiInfo(m.getModificationType(), m.getPercent().get(i));
                                modiInfo.put(modiPos, posModiInfo);
                            } else {
                                tmp.addModi(m.getModificationType(), m.getPercent().get(i));
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    // update modiTypeAllSel and modiInfoSel according to pepShow
    public void updateShow(){
        modiTypeAllSel.clear();
        modiInfoSel.clear();
        for(int i=0; i<peptides.size();i++){
            if(pepShow.get(i) > 0){
                if(peptides.get(i).isModified()){
                    for(Modification m : peptides.get(i).getModifications()){
                        modiTypeAllSel.add(m.getModificationType());
                        for(int j=0; j<m.getPos().size();j++){
                            int modiPos = m.getPos().get(j) + pepStart.get(i);
                            PosModiInfo tmp = modiInfoSel.get(modiPos);
                            if(tmp == null){
                                PosModiInfo posModiInfo = new PosModiInfo(m.getModificationType(), m.getPercent().get(j));
                                modiInfoSel.put(modiPos, posModiInfo);
                            } else {
                                tmp.addModi(m.getModificationType(), m.getPercent().get(j));
                            }
                        }
                    }
                }
            }
        }
    }

    public double getAbundance(PublicInfo.ProteinIntegrationType proteinIntegrationType, PublicInfo.ProteinStatus proteinStatus){
        switch (proteinIntegrationType){
            case Raw:
                switch (proteinStatus){
                    case Unnormalized:
                        return rawAbundance;
                    case Median:
                        return rawAbundanceMedianNorm;
                    case SelProtein:
                        return rawAbundanceSelProteinNorm;
                    default:
                        return -1;
                }
            case iBAQ:
                switch (proteinStatus) {
                    case Unnormalized:
                        return iBAQAbundance;
                    case Median:
                        return iBAQAbundanceMedianNorm;
                    case SelProtein:
                        return iBAQAbundanceSelProteinNorm;
                    default:
                        return -1;
                }
            default:
                return -1;
        }
    }


    /**
     * set the raw abundance
     * summation of peptide abundance
     */
    public void sumRawAbundance(){
        rawAbundance = 0;
        for(Peptide peptide: peptides){
            if(peptide.getMultiMatch() <= 1) {
                rawAbundance += peptide.getAbundance();
            }
        }
    }

    /**
     * get raw abundance
     * @return raw abundance
     */
    public double getRawAbundance(){
        if(rawAbundance==-1){
            this.sumRawAbundance();
        }

        return rawAbundance;
    }

    /**
     * set iBAQ abundance
     * @param theoreticalPepCount theoretical peptide count
     */
    public void setiBAQAbundance(double theoreticalPepCount){
        if(rawAbundance==-1){
            this.sumRawAbundance();
        }

        iBAQAbundance = rawAbundance/theoreticalPepCount;
    }

    /**
     * get iBAQ abundance
     * @return iBAQ abundance
     */
    public double getiBAQAbundance(){
        return iBAQAbundance;
    }


    public void setRawAbundanceMedianNorm(double diff) {
        rawAbundanceMedianNorm = rawAbundance + diff;
    }

    public void setiBAQAbundanceMedianNorm(double diff) {
        iBAQAbundanceMedianNorm = iBAQAbundance + diff;
    }

    public void setRawAbundanceSelProteinNorm(double diff) { rawAbundanceSelProteinNorm = rawAbundance + diff; }

    public void setiBAQAbundanceSelProteinNorm(double diff) { iBAQAbundanceSelProteinNorm = iBAQAbundance + diff; }

    public void setAbundanceRange(ArrayList<Double> cutoff){
        for(Peptide pt : peptides){
            pt.setAbundanceRange(cutoff);
        }
    }

    public void setAbundanceRange(){
        ArrayList<Double> cutoff = new ArrayList<>();
        int idx = abundanceAll.size()/4;
        cutoff.add(abundanceAll.get(idx));
        idx = abundanceAll.size()/2;
        cutoff.add(abundanceAll.get(idx));
        idx = abundanceAll.size()*3/4;
        cutoff.add(abundanceAll.get(idx));


        for(Peptide pt : peptides){
            pt.setAbundanceRange(cutoff);
        }
    }

    public void setAbundanceCombinedRange(){
        ArrayList<Double> cutoffCombined = new ArrayList<>();
        int idx = abundanceAllCombined.size()/4;
        cutoffCombined.add(abundanceAllCombined.get(idx));
        idx = abundanceAllCombined.size()/2;
        cutoffCombined.add(abundanceAllCombined.get(idx));
        idx = abundanceAllCombined.size()*3/4;
        cutoffCombined.add(abundanceAllCombined.get(idx));

        for(Peptide pt : peptidesCombined){
            pt.setAbundanceRange(cutoffCombined);
        }
    }


    public String getModiInfoAll(double cutoff){
        String rlt = "";
        Set<Integer> modiPos = modiInfo.keySet();

        for(Integer pos : modiPos){
            String rltTmp = "";
            int numPep = 0;
            for(int i=0; i < pepStart.size(); i++){
                if(pos>=pepStart.get(i) && pos <= pepEnd.get(i)){
                    numPep++;
                }
            }

            PosModiInfo posModiInfo = modiInfo.get(pos);
            TreeMap<String, ArrayList<Double>> modis = posModiInfo.getModifications();
            Set modiTypeSet = modis.entrySet();
            Iterator it = modiTypeSet.iterator();
            while(it.hasNext()){
                Map.Entry me = (Map.Entry) it.next();
                String mt = (String) me.getKey();
                ArrayList<Double> pc = (ArrayList<Double>) me.getValue();
                ArrayList<Double> pcCutOff = new ArrayList<>();
                for(int i=0; i<pc.size(); i++){
                    if(pc.get(i) >= cutoff){
                        pcCutOff.add(pc.get(i));
                    }
                }
                if(pcCutOff.size() > 0){
                    if(rltTmp.equals("")){
                        rltTmp = "Position:" + (pos+1) + "\tTotalPep:" + numPep;
                    }
                    rltTmp += ("\t" + mt + ":" + pcCutOff.size());
                }
            }

            if(!rltTmp.equals("")){
                rlt += (rltTmp + "\n");
            }
        }

        return rlt;

    }


    public ArrayList<String> getModiResInfo(ArrayList<String> modiSelected, int numResidual){
        ArrayList<String> rlt = new ArrayList<>();

        for(int i=0; i<peptides.size();i++){
            if(pepShow.get(i) > 0){
                Peptide pep = peptides.get(i);
                String infoTmp = pep.getModiInfo(modiSelected);
                if(infoTmp == null){
                    continue;
                }
                infoTmp = infoTmp + "\t" + name + "\t";
                ArrayList<Integer> modiPosTmp = pep.getModiPos(modiSelected);
                //Warning: modiPosTmp null? it should not be
                for(Integer pos : modiPosTmp){
                    int posProtein = pos + pepStart.get(i);
                    int st = posProtein - numResidual;
                    int ed = posProtein + numResidual + 1;

                    if(st < 0){
                        st = 0;
                    }

                    if(ed > sequence.length()){
                        ed = sequence.length();
                    }

                    infoTmp = infoTmp + sequence.substring(st, ed) + "\t" + (posProtein + 1) + "\t" + (st + 1) + "\t" + ed + "\t";
                }
                rlt.add(infoTmp);
            }
        }

        return rlt;
    }

    public ArrayList<Pair<String, Integer> > getModiRes(ArrayList<String> modiSelected, int numResidual){
        ArrayList<Pair<String, Integer> > rlt = new ArrayList<>();
        for(int i=0; i<peptides.size(); i++){
            if(pepShow.get(i) > 0){
                Peptide pep = peptides.get(i);
                ArrayList<Integer> modiPosTmp = pep.getModiPos(modiSelected);
                if(modiPosTmp == null){
                    continue;
                }
                for(Integer pos : modiPosTmp){

                    int posProtein = pos + pepStart.get(i);
                    int st = posProtein - numResidual;
                    //for substring +1
                    int ed = posProtein + numResidual + 1;

                    int posTmp;
                    if(st < 0){
                        posTmp = numResidual + st;
                        st = 0;
                    } else {
                        posTmp = numResidual;
                    }

                    if(ed > sequence.length()){
                        ed = sequence.length();
                    }
                    rlt.add(new Pair<>(sequence.substring(st, ed), posTmp));
                }
            }
        }
        return rlt;
    }
}
