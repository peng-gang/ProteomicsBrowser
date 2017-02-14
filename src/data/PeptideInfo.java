package data;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by gpeng on 2/12/17.
 */
public class PeptideInfo {
    private SimpleStringProperty sampleId;
    private SimpleStringProperty pepId;
    private SimpleStringProperty sequence;
    private SimpleIntegerProperty charge;
    private SimpleDoubleProperty mz;
    private SimpleDoubleProperty score;
    private SimpleDoubleProperty abundance;
    private SimpleStringProperty modification;

    public PeptideInfo(String sampleId, String pepId, String sequence, int charge, double mz, double score,
                       double abundance, String modification){
        this.sampleId = new SimpleStringProperty(sampleId);
        this.pepId = new SimpleStringProperty(pepId);
        this.sequence = new SimpleStringProperty(sequence);
        this.charge = new SimpleIntegerProperty(charge);
        this.mz = new SimpleDoubleProperty(mz);
        this.score = new SimpleDoubleProperty(score);
        this.abundance = new SimpleDoubleProperty(abundance);
        this.modification = new SimpleStringProperty(modification);
    }

    public String getSampleId() { return sampleId.get(); }
    public void setSampleId(String sampleId) { this.sampleId.set(sampleId); }

    public String getPepId() { return  pepId.get(); }
    public void setPepId(String pepId) { this.pepId.set(pepId); }

    public String getSequence() { return  sequence.get(); }
    public void setSequence(String sequence) { this.sequence.set(sequence); }

    public int getCharge() { return charge.get(); }
    public void setCharge(int charge) { this.charge.set(charge); }

    public double getMz() { return mz.get(); }
    public void setMz(double mz) { this.mz.set(mz); }

    public double getScore() { return score.get(); }
    public void setScore(double score) { this.score.set(score); }

    public double getAbundance() { return abundance.get(); }
    public void setAbundance(double abundance) { this.abundance.set(abundance); }

    public String getModification() { return modification.get(); }
    public void setModification(String modification) { this.modification.set(modification); }

    public String toString(){
        return (sampleId + "\t" + pepId);
    }
}
