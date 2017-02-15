package data;

import java.util.*;

/**
 * Created by gpeng on 2/13/17.
 */
public class SampleGroup {
    private TreeMap<String, Sample> samples;
    private HashSet<String> numInfoName;
    private HashSet<String> strInfoName;
    private HashSet<String> pepId;
    private HashSet<String> proteinId; //for abundance value
    private HashSet<String> proteinName; //for modification

    public SampleGroup(){
        samples = new TreeMap<>();
        numInfoName = new HashSet<>();
        strInfoName = new HashSet<>();
        pepId = new HashSet<>();
        proteinId = new HashSet<>();
        proteinName = new HashSet<>();
    }


    // add data functions
    public void addNumInfo(String sample, String name, double value){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addNumInfo(name, value);
            samples.put(sample, tmp);
            numInfoName.add(name);
        } else {
            samples.get(sample).addNumInfo(name, value);
            numInfoName.add(name);
        }
    }

    public void addStrInfo(String sample, String name, String value){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addStrInfo(name, value);
            samples.put(sample, tmp);
            strInfoName.add(name);
        } else {
            samples.get(sample).addStrInfo(name, value);
            strInfoName.add(name);
        }
    }

    public void addPepData(String sample, String pepId, double value){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addPepData(pepId, value);
            samples.put(sample, tmp);
            this.pepId.add(pepId);
        } else {
            samples.get(sample).addPepData(pepId, value);
            this.pepId.add(pepId);
        }
    }

    public void increaseProteinData(String sample, String proteinId, double value){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.increaseProteinData(proteinId, value);
            samples.put(sample, tmp);
            this.proteinId.add(proteinId);
        } else {
            samples.get(sample).increaseProteinData(proteinId, value);
            this.proteinId.add(proteinId);
        }
    }

    public void addProteinData(String sample, String proteinId, double value){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addProteinData(proteinId, value);
            samples.put(sample, tmp);
            this.proteinId.add(proteinId);
        } else {
            samples.get(sample).addProteinData(proteinId, value);
            this.proteinId.add(proteinId);
        }
    }

    public void addProtein(String sample, Protein pt){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addProtein(pt);
            samples.put(sample, tmp);
            this.proteinName.add(pt.getName());
        } else {
            samples.get(sample).addProtein(pt);
            this.proteinName.add(pt.getName());
        }
    }

    public void addPeptide(String sample, String proteinName, String proteinSequence, Peptide pep){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addPeptide(proteinName, proteinSequence, pep);
            samples.put(sample, tmp);
            this.proteinName.add(proteinName);
        } else {
            samples.get(sample).addPeptide(proteinName, proteinSequence, pep);
            this.proteinName.add(proteinName);
        }
    }


    //get data functions
    public Set<String> getSampleId(){
        return samples.keySet();
    }

    public HashSet<String> getNumInfoName(){
        return numInfoName;
    }

    public HashSet<String> getStrInfoName(){
        return strInfoName;
    }

    public HashSet<String> getPepId(){
        return pepId;
    }

    public HashSet<String> getProteinId(){
        return proteinId;
    }

    public HashSet<String> getProteinName(){
        return proteinName;
    }

    public ArrayList<Double> getNumInfo(String numInfoName){
        if(!this.numInfoName.contains(numInfoName)){
            return null;
        }

        ArrayList<Double> rlt = new ArrayList<>();
        for(String spName : samples.keySet()){
            rlt.add(samples.get(spName).getNumInfo(numInfoName));
        }

        return rlt;
    }

    public Double getNumInfo(String sample, String numInfoName){
        if((samples.keySet()).contains(sample)){
            return samples.get(sample).getNumInfo(numInfoName);
        } else {
            return null;
        }
    }

    public ArrayList<String> getStrInfo(String strInfoName){
        if(!this.strInfoName.contains(strInfoName)){
            return null;
        }

        ArrayList<String> rlt = new ArrayList<>();
        for(String spName : samples.keySet()){
            rlt.add(samples.get(spName).getStrInfo(strInfoName));
        }
        return rlt;
    }

    public String getStrInfo(String sample, String strInfoName){
        if((samples.keySet()).contains(sample)){
            return samples.get(sample).getStrInfo(strInfoName);
        } else {
            return null;
        }
    }

    public ArrayList<Double> getPepData(String pepId){
        if(!this.pepId.contains(pepId)){
            return null;
        }

        ArrayList<Double> rlt = new ArrayList<>();
        for(String spName : samples.keySet()){
            rlt.add(samples.get(spName).getPepData(pepId));
        }
        return rlt;
    }

    public Double getPepData(String sample, String pepId){
        if(samples.keySet().contains(sample)){
            return samples.get(sample).getPepData(pepId);
        } else {
            return null;
        }
    }

    public ArrayList<Double> getProteinData(String proteinId){
        if(!this.proteinId.contains(proteinId)){
            return null;
        }

        ArrayList<Double> rlt = new ArrayList<>();
        for(String spName : samples.keySet()){
            rlt.add(samples.get(spName).getProteinData(proteinId));
        }
        return rlt;
    }

    public Double getProteinData(String sample, String proteinId){
        if(samples.keySet().contains(sample)){
            return samples.get(sample).getProteinData(proteinId);
        } else {
            return null;
        }
    }

    public ArrayList<Protein> getProtein(String proteinName){
        if(!this.proteinName.contains(proteinName)){
            return null;
        }

        ArrayList<Protein> rlt = new ArrayList<>();
        for(String spName : samples.keySet()){
            rlt.add(samples.get(spName).getProtein(proteinName));
        }
        return rlt;
    }

    public Protein getProtein(String sample, String proteinName){
        if(samples.keySet().contains(sample)){
            return samples.get(sample).getProtein(proteinName);
        } else {
            return null;
        }
    }


    public Collection<Protein> getSampleProtein(String sample){
        if(samples.keySet().contains(sample)){
            return samples.get(sample).getProteins();
        } else {
            return null;
        }
    }

}
