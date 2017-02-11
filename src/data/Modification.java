package data;

import java.util.ArrayList;

/**
 * modification information
 * Created by gpeng on 2/10/17.
 */
public class Modification {
    public enum ModificationType {
        Acetylation, Carbamidomethylation, Phosphorylation, Oxidation
    }

    private ModificationType type;
    private ArrayList<Integer> pos;
    private ArrayList<Double>  percent;

    public ModificationType getType() { return type; }
    public ArrayList<Integer> getPos() { return  pos; }
    public ArrayList<Double> getPercent() { return percent; }

    public Modification(ModificationType type, ArrayList<Integer> pos, ArrayList<Double> percent){
        this.type = type;
        this.pos = pos;
        this.percent = percent;
    }
}
