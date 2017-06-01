package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Sample information
 * Created by gpeng on 2/10/17.
 */
public class SampleInfo implements Serializable {
    private ArrayList<String> id;

    private TreeMap<String, ArrayList<Double> > numInfo;
    private TreeMap<String, ArrayList<String> > strInfo;
    private TreeMap<String, ArrayList<Double> > pepData;
    private TreeMap<String, ArrayList<Double> > proteinData;

    public SampleInfo() {
        id = new ArrayList<>();
        numInfo = new TreeMap<>();
        strInfo = new TreeMap<>();
        pepData = new TreeMap<>();
        proteinData = new TreeMap<>();
    }

    public ArrayList<String> getId() { return id;}
    public TreeMap<String, ArrayList<Double> > getNumInfo() { return numInfo; }
    public TreeMap<String, ArrayList<String> > getStrInfo() { return strInfo; }
    public TreeMap<String, ArrayList<Double> > getPepData() { return pepData; }
    public TreeMap<String, ArrayList<Double> > getProteinData() { return proteinData; }

    public boolean addId(String id){
        this.id.add(id);
        return true;
    }

    public boolean addNumInfo(String key, double value){
        if(!numInfo.containsKey(key)){
            ArrayList<Double> tmp = new ArrayList<>();
            numInfo.put(key, tmp);
        }
        numInfo.get(key).add(value);
        return true;
    }

    public boolean addStrInfo(String key, String value){
        if(!strInfo.containsKey(key)){
            ArrayList<String> tmp = new ArrayList<>();
            strInfo.put(key, tmp);
        }
        strInfo.get(key).add(value);
        return true;
    }

    public boolean addPepData(String key, double value){
        if(!pepData.containsKey(key)){
            ArrayList<Double> tmp = new ArrayList<>();
            pepData.put(key, tmp);
        }
        pepData.get(key).add(value);
        return true;
    }

    public boolean addProteinData(String key, double value){
        if(!proteinData.containsKey(key)){
            ArrayList<Double> tmp = new ArrayList<>();
            proteinData.put(key, tmp);
        }
        proteinData.get(key).add(value);
        return true;
    }

}
