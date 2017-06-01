package data;

import project.PublicInfo;

import java.io.Serializable;
import java.util.*;

/**
 * Created by gpeng on 2/13/17.
 */
public class Sample implements Serializable {
    /**
     * sample id
     */
    private String id;
    /**
     * numeric information for this sample
     * String: numeric information id
     * Double: numeric information
     */
    private TreeMap<String, Double> numInfo;
    /**
     * string information for this sample
     * String1: numeric information id
     * String2: numeric information
     */
    private TreeMap<String, String> strInfo;
    /**
     * peptide abundance data for this sample
     * String: peptide id
     * Double: abundance
     */
    private TreeMap<String, Double> pepData;
    private TreeMap<String, Double> proteinData;

    /**
     * protein information for this sample
     * String: protein id
     * Protein: Protein
     */
    private TreeMap<String, Protein> proteins;

    public Sample(String id){
        this.id = id;
        numInfo = new TreeMap();
        strInfo = new TreeMap<>();
        pepData = new TreeMap<>();
        proteinData = new TreeMap<>();
        proteins = new TreeMap<>();
    }

    public void addNumInfo(String name, double value){
        numInfo.put(name, value);
    }

    public void addStrInfo(String name, String value){
        strInfo.put(name, value);
    }

    public void addPepData(String pepId, double value){
        pepData.put(pepId, value);
    }

    public void addProteinData(String proteinId, double value){
        proteinData.put(proteinId, value);
    }

    public void increaseProteinData(String proteinId, double value){
        if(proteinData.get(proteinId)==null){
            proteinData.put(proteinId, value);
        } else {
            proteinData.put(proteinId, proteinData.get(proteinId) + value);
        }
    }

    public void addPeptide(String proteinName, String proteinSeq, Peptide pep){
        if(proteins.get(proteinName) == null){
            Protein tmp = new Protein(proteinName, proteinSeq);
            tmp.addPeptide(pep);
            this.addProtein(tmp);
        } else {
            proteins.get(proteinName).addPeptide(pep);
        }
    }

    public Double getNumInfo(String name){
        return numInfo.get(name);
    }

    public String getStrInfo(String name){
        return strInfo.get(name);
    }

    public Double getPepData(String name){
        return pepData.get(name);
    }

    public Double getProteinData(String name){
        return proteinData.get(name);
    }

    public Double getProteinData(String name, PublicInfo.ProteinIntegrationType proteinIntegrationType, PublicInfo.ProteinStatus proteinStatus){
        return proteins.get(name).getAbundance(proteinIntegrationType, proteinStatus);
    }

    public void addProtein(Protein pt){
        proteins.put(pt.getName(), pt);
    }

    public Collection<Protein> getProteins(){
        return proteins.values();
    }

    public Protein getProtein(String proteinId){
        return proteins.get(proteinId);
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    /**
     * Use iBAQ method to get abundance of each protein from the abundance of each peptide
     */
    public void iBAQ(){
        double numPeptide = (double) pepData.size();
        long totalLength = 0;
        for(Map.Entry<String, Protein> entry : proteins.entrySet()){
            totalLength += entry.getValue().getLength();
        }

        for(Map.Entry<String, Protein> entry : proteins.entrySet()){
            double theoCount = numPeptide * entry.getValue().getLength()/totalLength;
            entry.getValue().setiBAQAbundance(theoCount);
        }
    }

    /**
     * set raw abundance for each protein as the summation of the abundance of each peptide
     */
    public void rawAbundance(){
        for(Map.Entry<String, Protein> entry : proteins.entrySet()){
            entry.getValue().sumRawAbundance();
        }
    }

    public void iBAQMedianNorm(double diff){
        for(Map.Entry<String, Protein> entry : proteins.entrySet()){
            entry.getValue().setiBAQAbundanceMedianNorm(diff);
        }
    }

    public void rawAbundanceMedianNorm(double diff){
        for(Map.Entry<String, Protein> entry : proteins.entrySet()){
            entry.getValue().setRawAbundanceMedianNorm(diff);
        }
    }

    public ArrayList<Double> getRawAbundance(){
        ArrayList<Double> rlt = new ArrayList<>();
        for(Map.Entry<String, Protein> entry : proteins.entrySet()){
            double tmp = entry.getValue().getRawAbundance();
            if(tmp >= 0) {
                rlt.add(tmp);
            }
        }

        return rlt;
    }

    public ArrayList<Double> getiBAQAbundance(){
        ArrayList<Double> rlt = new ArrayList<>();
        for(Map.Entry<String, Protein> entry : proteins.entrySet()){
            double tmp = entry.getValue().getiBAQAbundance();
            if(tmp >= 0) {
                rlt.add(tmp);
            }
        }

        return rlt;
    }

    public void setAbundanceRange(){
        ArrayList<Double> abundance = new ArrayList<>(pepData.values());
        Collections.sort(abundance);
        ArrayList<Double> cutoff = new ArrayList<>();
        int idx = abundance.size()/4;
        cutoff.add(abundance.get(idx));
        idx = abundance.size()/2;
        cutoff.add(abundance.get(idx));
        idx = abundance.size()*3/4;
        cutoff.add(abundance.get(idx));

        for(Protein pt : proteins.values()){
            pt.setAbundanceRange(cutoff);
        }
    }

}
