package data;


import java.io.Serializable;
import java.util.*;
import java.util.function.DoubleToLongFunction;

/**
 * Peptide Class to store peptide information
 * Created by gpeng on 2/10/17.
 */
public class Peptide implements Serializable {
    private String id;
    private String sequence;
    private Integer charge;
    private Double mz;
    private Double score;
    private Double abundance;
    //the position in modification starts from 0
    private ArrayList<Modification> modifications;

    private Integer abundanceRange = -1;

    private TreeMap<String, Double> doubleInfo;
    private TreeMap<String, String> strInfo;


    public String getId() {return id;}
    public int getLength() {return sequence.length();}
    public String getSequence() {return  sequence; }
    public Integer getCharge() { return charge;}
    public Double getMz() { return mz;}
    public Double getScore() { return score;}
    public Double getAbundance() { return  abundance;}
    public int getNumModi() { return modifications.size();}
    public ArrayList<Modification> getModifications() { return  modifications;}

    public TreeMap<String, Double> getDoubleInfo() { return doubleInfo; }
    public TreeMap<String, String> getStrInfo() { return strInfo; }
    public Double getDoubleInfo(String dName) { return doubleInfo.get(dName); }
    public String getStrInfo(String strName) { return strInfo.get(strName); }
    public void addDoubleInfo(String dName, Double info){
        doubleInfo.put(dName, info);
    }
    public void addStrInfo(String strName, String info){
        strInfo.put(strName, info);
    }

    public void addModification(Modification modi){
        this.modifications.add(modi);
    }
    public void setModification(ArrayList<Modification> modi) { this.modifications = modi; }

    public Peptide(String id, String sequence, Double abundance, TreeMap<String, Double> doubleInfo, TreeMap<String, String> strInfo){
        this.id = id;
        this.sequence = sequence.toUpperCase();
        this.abundance = abundance;
        this.doubleInfo = doubleInfo;
        this.strInfo = strInfo;
        this.modifications = new ArrayList<>();
    }

    public Peptide(String id, String sequence, Double abundance,TreeMap<String, Double> doubleInfo, TreeMap<String, String> strInfo,
                   ArrayList<Modification> modifications){
        this.id = id;
        this.sequence = sequence.toUpperCase();
        this.abundance = abundance;
        this.doubleInfo = doubleInfo;
        this.strInfo = strInfo;
        this.modifications = modifications;
    }

    public Peptide(String id, String sequence, Integer charge, Double mz, Double score, Double abundance){
        this.id = id;
        this.sequence = sequence.toUpperCase();
        this.charge = charge;
        this.mz = mz;
        this.score = score;
        this.abundance = abundance;
        this.modifications = new ArrayList<>();
    }

    public Peptide(String id, String sequence, Integer charge, Double mz, Double score, Double abundance, ArrayList<Modification> modifications){
        this.id = id;
        this.sequence = sequence.toUpperCase();
        this.charge = charge;
        this.mz = mz;
        this.score = score;
        this.abundance = abundance;
        this.modifications = modifications;
    }

    public boolean isModified() {
        return modifications.size() > 0;
    }

    public String toString(){
        String rlt;
        rlt = "ID: " + id + "\n";
        for(Map.Entry<String, Double> entry : doubleInfo.entrySet()){
            rlt = rlt + entry.getKey() + ": " + entry.getValue() + "\n";
        }
        for(Map.Entry<String, String> entry : strInfo.entrySet()){
            rlt = rlt + entry.getKey() + ": " + entry.getValue() + "\n";
        }
        /*
        rlt = rlt + "Charge: " + charge + "\n";
        rlt = rlt + "mz: " + mz + "\n";
        rlt = rlt + "Score: " + score + "\n";
        */
        rlt = rlt + "Abundance: " + abundance + "\n\t" +  String.format("%.2f", Math.log10(abundance)) + "(log10)\n";
        if(modifications.size()==0){
            rlt += "Modification: None\n";
        } else {
            rlt += "Modification: \n";
            for(Modification md : modifications){
                rlt += "\t" + md.getType() + ": ";
                rlt += (md.getPos().get(0) + 1) + "(" + md.getPercent().get(0) + ")";
                for(int i=1; i<md.getPos().size();i++){
                    rlt += ";" + md.getPos().get(i) + "(" + md.getPercent().get(i) + ")";
                }
                rlt += "\n";
            }
        }

        return rlt;
    }

    public String getModiInfo(ArrayList<Modification.ModificationType> modiSelected){
        if(modifications.size() == 0){
            return null;
        }

        String rlt = sequence;
        rlt = rlt + "\t";

        TreeMap<Integer, Modification.ModificationType> modiPosAll = new TreeMap<>();
        for(Modification modification : modifications){
            if(modiSelected.contains(modification.getType())){
                for(int i=0; i<modification.getPos().size();i++){
                    modiPosAll.put(modification.getPos().get(i), modification.getType());
                }
            }
        }

        if(modiPosAll.size() == 0){
            return null;
        }

        for(Map.Entry<Integer, Modification.ModificationType> entry : modiPosAll.entrySet()){
            rlt = rlt + "[" + (entry.getKey() + 1) + "]" + entry.getValue() + "(" + sequence.charAt(entry.getKey()) + ")";
            if(!entry.equals(modiPosAll.lastEntry())){
                rlt = rlt + "|";
            }
        }

        return rlt;
    }

    //get modified positions in a peptide
    public ArrayList<Integer> getModiPos(ArrayList<Modification.ModificationType> modiSelected){
        if(modifications.size() == 0){
            return null;
        }

        Set<Integer> pos = new TreeSet<>();
        for(Modification modification : modifications){
            if(modiSelected.contains(modification.getType())){
                for(int i=0; i<modification.getPos().size();i++){
                    pos.add(modification.getPos().get(i));
                }
            }
        }

        if(pos.size() == 0){
            return null;
        }

        ArrayList<Integer> rlt = new ArrayList<>(pos);
        return rlt;

    }

    public void setAbundanceRange(ArrayList<Double> cutoff){
        for(int i=0;i<cutoff.size();i++){
            if(abundance < cutoff.get(i)){
                abundanceRange = i;
                return;
            }
        }
        abundanceRange = cutoff.size();
    }

    public int getAbundanceRange(){return abundanceRange;}
}
