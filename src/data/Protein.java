package data;

import project.PublicInfo;

import java.io.Serializable;
import java.util.*;

/**
 * protein or gene information
 * Created by gpeng on 2/10/17.
 */
public class Protein implements Serializable {
    private String name;
    private String sequence;

    private ArrayList<Peptide> peptides;
    //start and end are all included in peptide
    private ArrayList<Integer> pepStart;
    private ArrayList<Integer> pepEnd;
    private TreeSet<Modification.ModificationType> modiTypeAll;
    private TreeMap<Integer, PosModiInfo> modiInfo;

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

    public String getName() { return  name;}
    public String getSequence() { return sequence;}
    public int getLength() {return sequence.length(); }
    public ArrayList<Peptide> getPeptides() { return peptides; }
    public ArrayList<Integer> getPepStart() { return  pepStart; }
    public ArrayList<Integer> getPepEnd() { return pepEnd; }
    public TreeSet<Modification.ModificationType> getModiTypeAll() { return modiTypeAll; }
    public Set<Integer> getModiPos() { return  modiInfo.keySet(); }
    public TreeMap<Integer, PosModiInfo> getModiInfo() { return  modiInfo; }


    public ArrayList<Integer> getModiPos(Modification.ModificationType mt) {
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


    public ArrayList<Integer> getModiPos(ArrayList<Modification.ModificationType> mts) {
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



    public Protein(String name, String sequence){
        this.name = name;
        this.sequence = sequence;
        peptides = new ArrayList<>();
        pepStart = new ArrayList<>();
        pepEnd = new ArrayList<>();
        modiTypeAll = new TreeSet<>();
        modiInfo = new TreeMap<>();
    }

    public boolean addPeptide(Peptide pep){
        //compare peptide sequence to protein sequence
        int indexF = sequence.indexOf(pep.getSequence());
        int indexL = sequence.lastIndexOf(pep.getSequence());

        if(indexF == -1){
            System.out.println("Cannot find peptide " + pep.getSequence() + " in protein " + sequence);
            return  false;
        } else {
            if(indexF!=indexL){
                System.out.println("Find multiple match for peptide " + pep.getSequence() + " in protein " + sequence);
                return  false;
            } else {
                peptides.add(pep);
                pepStart.add(indexF);
                pepEnd.add(indexF + pep.getLength() - 1);
                if(pep.isModified()){
                    for(Modification m : pep.getModifications()){
                        modiTypeAll.add(m.getType());
                        for(int i = 0; i < m.getPos().size(); i++){
                            int modiPos = m.getPos().get(i) + indexF;
                            PosModiInfo tmp = modiInfo.get(modiPos);
                            if(tmp == null){
                                PosModiInfo posModiInfo = new PosModiInfo(m.getType(), m.getPercent().get(i));
                                modiInfo.put(modiPos, posModiInfo);
                            } else {
                                tmp.addModi(m.getType(), m.getPercent().get(i));
                            }
                        }
                    }
                }
                return true;
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
                    default:
                        return -1;
                }
            case iBAQ:
                switch (proteinStatus) {
                    case Unnormalized:
                        return iBAQAbundance;
                    case Median:
                        return iBAQAbundanceMedianNorm;
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
            rawAbundance += peptide.getAbundance();
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

    public void setAbundanceRange(ArrayList<Double> cutoff){
        for(Peptide pt : peptides){
            pt.setAbundanceRange(cutoff);
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
            TreeMap<Modification.ModificationType, ArrayList<Double>> modis = posModiInfo.getModifications();
            Set modiTypeSet = modis.entrySet();
            Iterator it = modiTypeSet.iterator();
            while(it.hasNext()){
                Map.Entry me = (Map.Entry) it.next();
                Modification.ModificationType mt = (Modification.ModificationType) me.getKey();
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
}
