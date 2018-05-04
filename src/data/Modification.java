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
    /*
    public enum ModificationType {
        Acetylation, Carbamidomethylation, Phosphorylation, Oxidation
    }
    */

    //private ModificationType type;
    private String modificationType;
    private ArrayList<String> residue;
    private ArrayList<Integer> pos;
    private ArrayList<Double>  percent;

    public String getModificationType() { return modificationType; }
    public ArrayList<String> getResidue() {return  residue;}
    public ArrayList<Integer> getPos() { return  pos; }
    public ArrayList<Double> getPercent() { return percent; }

    public String getResidueByPos(Integer pos) {
        Integer idx = this.pos.indexOf(pos);
        if(idx < 0){
            return null;
        }

        return residue.get(idx);
    }

    public Modification(String modificationType, ArrayList<Integer> pos, ArrayList<String> residue, ArrayList<Double> percent){
        this.modificationType = modificationType;
        this.pos = pos;
        this.residue = residue;
        this.percent = percent;
    }

    public Modification(String modi){
        //System.out.println(modi);
        String[] tmp = modi.split(";");
        pos = new ArrayList<>();
        percent = new ArrayList<>();
        residue = new ArrayList<>();
        for(int i=0; i<tmp.length; i++){
            String posTmp = tmp[i].substring(tmp[i].indexOf("[")+1, tmp[i].indexOf("]"));
            String typeTmp = tmp[i].substring(tmp[i].indexOf("]")+1, tmp[i].indexOf("("));
            String otherTmp = tmp[i].substring(tmp[i].indexOf("(")+1, tmp[i].indexOf(")"));
            String[]  resPer = otherTmp.split(",");
            pos.add(Integer.parseInt(posTmp.trim()) - 1);
            modificationType = typeTmp.trim();
            if(resPer.length == 1){
                residue.add(resPer[0].trim());
                percent.add(100.0);
            } else {
                residue.add(resPer[0].trim());
                double perTmp;
                try{
                    perTmp = Double.parseDouble(resPer[1].trim());
                } catch (NumberFormatException e){
                    perTmp = -1.0;
                }
                percent.add(perTmp);
            }
        }
        /*
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

        modificationType = typeTmp;
        */

        /*
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
        */
    }
}
