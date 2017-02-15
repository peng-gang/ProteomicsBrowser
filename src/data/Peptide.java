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
}
