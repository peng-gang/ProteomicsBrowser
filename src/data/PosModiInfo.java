package data;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Modification information at each position
 * Created by gpeng on 2/18/17.
 */
public class PosModiInfo {
    private TreeMap<Modification.ModificationType, ArrayList<Double>> modifications;


    public PosModiInfo(){
        modifications = new TreeMap<>();
    }

    public TreeMap<Modification.ModificationType, ArrayList<Double>> getModifications() { return  modifications; }

    public PosModiInfo(Modification.ModificationType mt, double percent){
        modifications = new TreeMap<>();
        ArrayList<Double> tmp = new ArrayList<>();
        tmp.add(percent);
        modifications.put(mt, tmp);
    }

    public boolean modiExist(ArrayList<Modification.ModificationType> mTypes){
        for(Modification.ModificationType mt : mTypes){
            if((modifications.keySet()).contains(mt)){
                return true;
            }
        }
        return false;
    }

    public boolean modiExist(Modification.ModificationType mt){
        if(modifications.keySet().contains(mt)){
            return true;
        } else {
            return false;
        }
    }

    public void addModi(Modification.ModificationType mt, double percent){
        ArrayList<Double> tmp = modifications.get(mt);
        if(tmp == null){
            ArrayList<Double> val = new ArrayList<>();
            val.add(percent);
            modifications.put(mt, val);
        } else {
            tmp.add(percent);
        }
    }

    public ArrayList<Double> getPercent(Modification.ModificationType mt){
        return modifications.get(mt);
    }
}
