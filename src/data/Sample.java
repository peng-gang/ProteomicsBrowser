package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Created by gpeng on 2/13/17.
 */
public class Sample {
    private String id;
    private TreeMap<String, Double> numInfo;
    private TreeMap<String, String> strInfo;
    private TreeMap<String, Double> pepData;
    private TreeMap<String, Double> proteinData;

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

}
