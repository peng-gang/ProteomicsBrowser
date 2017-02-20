package data;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * protein or gene information
 * Created by gpeng on 2/10/17.
 */
public class Protein {
    private String name;
    private String sequence;

    private ArrayList<Peptide> peptides;
    private ArrayList<Integer> pepStart;
    private ArrayList<Integer> pepEnd;
    private TreeSet<Modification.ModificationType> modiTypeAll;
    private TreeMap<Integer, PosModiInfo> modiInfo;

    public String getName() { return  name;}
    public String getSequence() { return sequence;}
    public int getLength() {return sequence.length(); }
    public ArrayList<Peptide> getPeptides() { return peptides; }
    public ArrayList<Integer> getPepStart() { return  pepStart; }
    public ArrayList<Integer> getPepEnd() { return pepEnd; }
    public TreeSet<Modification.ModificationType> getModiTypeAll() { return modiTypeAll; }
    public Set<Integer> getModiPos() { return  modiInfo.keySet(); }



    public Protein(String name, String sequence){
        this.name = name;
        this.sequence = sequence;
        peptides = new ArrayList<>();
        pepStart = new ArrayList<>();
        pepEnd = new ArrayList<>();
        modiTypeAll = new TreeSet<>();
        modiInfo = new TreeMap<>();
    }

    public boolean addPeptide(Peptide pep){
        //compare peptide sequence to protein sequence
        int indexF = sequence.indexOf(pep.getSequence());
        int indexL = sequence.lastIndexOf(pep.getSequence());

        if(indexF == -1){
            System.out.println("Cannot find peptide " + pep.getSequence() + " in protein " + sequence);
            return  false;
        } else {
            if(indexF!=indexL){
                System.out.println("Find multiple match for peptide " + pep.getSequence() + " in protein " + sequence);
                return  false;
            } else {
                peptides.add(pep);
                pepStart.add(indexF);
                pepEnd.add(indexF + pep.getLength() - 1);
                if(pep.isModified()){
                    for(Modification m : pep.getModifications()){
                        modiTypeAll.add(m.getType());
                        for(int i = 0; i < m.getPos().size(); i++){
                            int modiPos = m.getPos().get(i) + indexF;
                            PosModiInfo tmp = modiInfo.get(modiPos);
                            if(tmp == null){
                                PosModiInfo posModiInfo = new PosModiInfo(m.getType(), m.getPercent().get(i));
                                modiInfo.put(modiPos, posModiInfo);
                            } else {
                                tmp.addModi(m.getType(), m.getPercent().get(i));
                            }
                        }
                    }
                }
                return true;
            }
        }

    }


}
