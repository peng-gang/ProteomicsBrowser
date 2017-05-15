package data;


import java.util.ArrayList;

/**
 * Peptide Class to store peptide information
 * Created by gpeng on 2/10/17.
 */
public class Peptide {
    private String id;
    private String sequence;
    private Integer charge;
    private Double mz;
    private Double score;
    private Double abundance;
    private ArrayList<Modification> modifications;

    private Integer abundanceRange = -1;


    public String getId() {return id;}
    public int getLength() {return sequence.length();}
    public String getSequence() {return  sequence; }
    public Integer getCharge() { return charge;}
    public Double getMz() { return mz;}
    public Double getScore() { return score;}
    public Double getAbundance() { return  abundance;}
    public int getNumModi() { return modifications.size();}
    public ArrayList<Modification> getModifications() { return  modifications;}

    public void addModification(Modification modi){
        this.modifications.add(modi);
    }
    public void setModification(ArrayList<Modification> modi) { this.modifications = modi; }

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
        rlt = rlt + "Charge: " + charge + "\n";
        rlt = rlt + "mz: " + mz + "\n";
        rlt = rlt + "Score: " + score + "\n";
        rlt = rlt + "Abundance: " + abundance + "\n";
        if(modifications.size()==0){
            rlt += "Modification: None\n";
        } else {
            rlt += "Modification: \n";
            for(Modification md : modifications){
                rlt += "\t" + md.getType() + ": ";
                rlt += md.getPos().get(0) + "(" + md.getPercent().get(0) + ")";
                for(int i=1; i<md.getPos().size();i++){
                    rlt += ";" + md.getPos().get(i) + "(" + md.getPercent().get(i) + ")";
                }
                rlt += "\n";
            }
        }

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
