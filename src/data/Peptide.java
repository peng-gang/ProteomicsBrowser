package data;


import java.util.ArrayList;

/**
 * Peptide Class to store peptide information
 * Created by gpeng on 2/10/17.
 */
public class Peptide {
    private int id;
    private String sequence;
    private int charge;
    private double mz;
    private double score;
    private double abundance;
    private ArrayList<Modification> modifications;


    public int getId() {return id;}
    public int getLength() {return sequence.length();}
    public String getSequence() {return  sequence; }
    public int getCharge() { return charge;}
    public double getMz() { return mz;}
    public double getScore() { return score;}
    public double getAbundance() { return  abundance;}
    public int getNumModi() { return modifications.size();}
    public ArrayList<Modification> getModifications() { return  modifications;}

    public void addModification(Modification modi){
        modifications.add(modi);
    }

    public Peptide(int id, String sequence, int charge, double mz, double score, double abundance){
        this.id = id;
        this.sequence = sequence.toUpperCase();
        this.charge = charge;
        this.mz = mz;
        this.score = score;
        this.abundance = abundance;
        modifications = new ArrayList<>();
    }

    public boolean isModified() {
        return modifications.size() > 0;
    }
}
