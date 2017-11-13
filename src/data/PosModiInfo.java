package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Modification information at each position
 * Created by gpeng on 2/18/17.
 */
public class PosModiInfo implements Serializable {
    private TreeMap<String, ArrayList<Double>> modifications;


    public PosModiInfo(){
        modifications = new TreeMap<>();
    }

    public TreeMap<String, ArrayList<Double>> getModifications() { return  modifications; }

    public PosModiInfo(String mt, double percent){
        modifications = new TreeMap<>();
        ArrayList<Double> tmp = new ArrayList<>();
        tmp.add(percent);
        modifications.put(mt, tmp);
    }

    public boolean modiExist(ArrayList<String> mTypes){
        for(String mt : mTypes){
            if((modifications.keySet()).contains(mt)){
                return true;
            }
        }
        return false;
    }

    public boolean modiExist(String mt){
        if(modifications.keySet().contains(mt)){
            return true;
        } else {
            return false;
        }
    }

    public void addModi(String mt, double percent){
        ArrayList<Double> tmp = modifications.get(mt);
        if(tmp == null){
            ArrayList<Double> val = new ArrayList<>();
            val.add(percent);
            modifications.put(mt, val);
        } else {
            tmp.add(percent);
        }
    }

    public ArrayList<Double> getPercent(String mt){
        return modifications.get(mt);
    }
}
