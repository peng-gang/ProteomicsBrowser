package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;

/**
 * modification information
 * Created by gpeng on 2/10/17.
 */
public class Modification implements Serializable {
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

    public Modification(String modi){
        String[] tmp = modi.split("\\[");
        String typeTmp = tmp[0].toLowerCase();
        String infoTmp = tmp[1].substring(0, (tmp[1].length()-1));
        String[] info = infoTmp.split(":");

        //System.out.println(modi);

        pos = new ArrayList<>();
        percent = new ArrayList<>();
        for(int i=0; i<info.length; i++){
            String[] pp = info[i].split("\\(");

            String pc = pp[1].substring(0, (pp[1].length()-1));
            double dPc = Double.parseDouble(pc);
            if(dPc > 1){
                pos.add(Integer.parseInt(pp[0])-1);
                percent.add(Double.parseDouble(pc));
            }
        }

        switch (typeTmp){
            case "acetylation":
                type = ModificationType.Acetylation;
                break;
            case "carbamidomethylation":
                type = ModificationType.Carbamidomethylation;
                break;
            case "phosphorylation":
                type = ModificationType.Phosphorylation;
                break;
            case "oxidation":
                type = ModificationType.Oxidation;
                break;
            default:
                System.err.println("Cannot find " + typeTmp);
                break;
        }
    }
}
