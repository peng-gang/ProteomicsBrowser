package data;

import java.util.*;

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
    public TreeMap<Integer, PosModiInfo> getModiInfo() { return  modiInfo; }


    public ArrayList<Integer> getModiPos(Modification.ModificationType mt) {
        ArrayList<Integer> rlt = new ArrayList<>();
        for(Map.Entry<Integer, PosModiInfo> entry : modiInfo.entrySet()){
            Integer key = entry.getKey();
            PosModiInfo value = entry.getValue();
            if(value.modiExist(mt)){
                rlt.add(key);
            }
        }
        return rlt;
    }


    public ArrayList<Integer> getModiPos(ArrayList<Modification.ModificationType> mts) {
        ArrayList<Integer> rlt = new ArrayList<>();
        for(Map.Entry<Integer, PosModiInfo> entry : modiInfo.entrySet()){
            Integer key = entry.getKey();
            PosModiInfo value = entry.getValue();
            if(value.modiExist(mts)){
                rlt.add(key);
            }
        }
        return rlt;
    }



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
