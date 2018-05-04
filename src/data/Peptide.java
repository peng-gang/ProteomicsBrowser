package data;



import javax.print.attribute.SetOfIntegerSyntax;
import java.io.Serializable;
import java.util.*;

/**
 * Peptide Class to store peptide information
 * Created by gpeng on 2/10/17.
 */
public class Peptide implements Serializable {
    private String id;
    private String sequence;
    private Integer charge;
    //private Double mz;
    //private Double score;
    private Double abundance;
    //the position in modification starts from 0
    private ArrayList<Modification> modifications;

    private Integer abundanceRange = -1;

    private TreeMap<String, Double> doubleInfo;
    private TreeMap<String, String> strInfo;

    /**
     * indicate whether the peptide has multiple matches in a protein
     * 0: no multiple match
     * 1,2,3,...: multiple match index
     */

    Integer multiMatch = 0;


    public String getId() {return id;}
    public int getLength() {return sequence.length();}
    public String getSequence() {return  sequence; }
    public Integer getCharge() { return charge;}
    public Integer getMultiMatch() { return  multiMatch; }
    public void setMultiMatch(Integer dup) { this.multiMatch = dup; }
    //public Double getMz() { return mz;}
    //public Double getScore() { return score;}
    public Double getAbundance() { return  abundance;}
    public int getNumModi() { return modifications.size();}
    public ArrayList<Modification> getModifications() { return  modifications;}
    public ArrayList<Modification> getModifications(ArrayList<String> modiType) {
        ArrayList<Modification> rlt = new ArrayList<>();
        if(modiType.size()==0){
            return rlt;
        }

        for(Modification modi : modifications){
            if(modiType.contains(modi.getModificationType())){
                rlt.add(modi);
            }
        }

        return rlt;
    }

    public TreeMap<String, Double> getDoubleInfo() { return doubleInfo; }
    public TreeMap<String, String> getStrInfo() { return strInfo; }
    public Double getDoubleInfo(String dName) { return doubleInfo.get(dName); }
    public String getStrInfo(String strName) { return strInfo.get(strName); }
    public void addDoubleInfo(String dName, Double info){
        doubleInfo.put(dName, info);
    }
    public void addStrInfo(String strName, String info){
        strInfo.put(strName, info);
    }

    public void addModification(Modification modi){
        this.modifications.add(modi);
    }
    public void setModification(ArrayList<Modification> modi) { this.modifications = modi; }

    public String getModiType(int pos){
        for(Modification modi : modifications){
            if(modi.getPos().contains(pos)){
                return modi.getModificationType();
            }
        }
        return null;
    }

    public String getModiRes(int pos){
        for(Modification modi: modifications){
            int idx = modi.getPos().indexOf(pos);
            if(idx >=0){
                return modi.getResidue().get(idx);
            }
        }
        return null;
    }

    public TreeSet<Integer> getModificationPos(String modiType) {
        TreeSet<Integer> rlt = new TreeSet<>();
        for(Modification modi : modifications){
            if(modi.getModificationType().equals(modiType)){
                rlt.addAll(modi.getPos());
            }
        }
        return rlt;
    }



    public Peptide(String id, String sequence, Double abundance, Integer charge, TreeMap<String, Double> doubleInfo, TreeMap<String, String> strInfo, Integer multiMatch){
        this.id = id;
        this.sequence = sequence.toUpperCase();
        this.abundance = abundance;
        this.charge = charge;
        this.doubleInfo = doubleInfo;
        this.strInfo = strInfo;
        this.modifications = new ArrayList<>();
        this.multiMatch = multiMatch;
    }

    public Peptide(String id, String sequence, Double abundance, Integer charge, TreeMap<String, Double> doubleInfo, TreeMap<String, String> strInfo,
                   ArrayList<Modification> modifications, Integer multiMatch){
        this.id = id;
        this.sequence = sequence.toUpperCase();
        this.abundance = abundance;
        this.charge = charge;
        this.doubleInfo = doubleInfo;
        this.strInfo = strInfo;
        this.modifications = modifications;
        this.multiMatch = multiMatch;
    }

    public Set<String> getModificationTypes(){
        Set<String> rlt = new HashSet<>();
        for(Modification modi : modifications){
            rlt.add(modi.getModificationType());
        }
        return rlt;
    }

    /*
    public void adjModification(Peptide pep){
        ArrayList<Modification> modiBackup = new ArrayList<>(modifications);
        TreeMap<String, Set<Integer>> oldModi = new TreeMap<>();
        TreeMap<String, Set<Integer>> newModi = new TreeMap<>();

        Map<Integer, String> resAll = new TreeMap<>();

        for(Modification m : modiBackup){
            for(int i=0; i<m.getPos().size();i++){
                resAll.put(m.getPos().get(i), m.getResidue().get(i));
            }

            if(oldModi.containsKey(m.getModificationType())){
                oldModi.get(m.getModificationType()).addAll(m.getPos());
            } else {
                Set<Integer> tmp = new TreeSet<>(m.getPos());
                oldModi.put(m.getModificationType(), tmp);
            }
        }

        for(Modification m : pep.getModifications()){
            for(int i=0; i<m.getPos().size();i++){
                resAll.put(m.getPos().get(i), m.getResidue().get(i));
            }

            if(newModi.containsKey(m.getModificationType())){
                newModi.get(m.getModificationType()).addAll(m.getPos());
            } else {
                Set<Integer> tmp = new TreeSet<>(m.getPos());
                newModi.put(m.getModificationType(), tmp);
            }
        }

        Set<String> modiAll = new TreeSet<>();
        modiAll.addAll(oldModi.keySet());
        modiAll.addAll(newModi.keySet());

        modifications.clear();
        for(String mt : modiAll){
            if(oldModi.containsKey(mt) && newModi.containsKey(mt)){
                Set<Integer> inter = new TreeSet<>();
                Set<Integer> oldUnique = new TreeSet<>();
                Set<Integer> newUnique = new TreeSet<>();

                inter.addAll(oldModi.get(mt));
                inter.retainAll(newModi.get(mt));

                oldUnique.addAll(oldModi.get(mt));
                oldUnique.removeAll(newModi.get(mt));

                newUnique.addAll(newModi.get(mt));
                newUnique.removeAll(oldModi.get(mt));

                Set<Integer> uniqueAll = new TreeSet<>();
                uniqueAll.addAll(oldUnique);
                uniqueAll.addAll(newUnique);

                for(Integer i : inter){
                    ArrayList<Integer> posTmp = new ArrayList<>();
                    posTmp.add(i);
                    ArrayList<String> resTmp = new ArrayList<>();
                    resTmp.add(resAll.get(i));
                    ArrayList<Double> perTmp = new ArrayList<>();
                    perTmp.add(100.0);
                    Modification modiTmp = new Modification(mt, posTmp, resTmp, perTmp);
                    modifications.add(modiTmp);
                }

                ArrayList<Integer> posTmp = new ArrayList<>(uniqueAll);
                ArrayList<String> resTmp = new ArrayList<>();
                ArrayList<Double> perTmp = new ArrayList<>();
                for(Integer i : posTmp){
                    resTmp.add(resAll.get(i));
                    perTmp.add(null);
                }

                Modification modiTmp = new Modification(mt, posTmp, resTmp, perTmp);
                modifications.add(modiTmp);
            } else if(oldModi.containsKey(mt)){
                ArrayList<Integer> posTmp = new ArrayList<>();
                ArrayList<String> resTmp = new ArrayList<>();
                ArrayList<Double> perTmp = new ArrayList<>();
                for(Integer i : oldModi.get(mt)){
                    posTmp.add(i);
                    resTmp.add(resAll.get(i));
                    perTmp.add(null);
                }
                Modification modiTmp = new Modification(mt, posTmp, resTmp, perTmp);
                modifications.add(modiTmp);
            } else {
                ArrayList<Integer> posTmp = new ArrayList<>();
                ArrayList<String> resTmp = new ArrayList<>();
                ArrayList<Double> perTmp = new ArrayList<>();
                for(Integer i : newModi.get(mt)){
                    posTmp.add(i);
                    resTmp.add(resAll.get(i));
                    perTmp.add(null);
                }
                Modification modiTmp = new Modification(mt, posTmp, resTmp, perTmp);
                modifications.add(modiTmp);
            }
        }
    }
    */

    /*
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
    */


    public boolean isModified() {
        return modifications.size() > 0;
    }

    // check whether the two peptides are similar
    public boolean isSimilar(Peptide pep, ArrayList<String> doubleInfoCriteria, ArrayList<String> strInfoCriteria, ArrayList<String> modiCriteria) {
        if(!this.sequence.equals(pep.getSequence())){
            return false;
        }

        for(String criterion : doubleInfoCriteria){
            if(!this.doubleInfo.get(criterion).equals(pep.getDoubleInfo(criterion))){
                return false;
            }
        }

        for(String criterion : strInfoCriteria){
            if(!this.strInfo.get(criterion).equals(pep.getStrInfo(criterion))){
                return false;
            }
        }

        for(String modiType : modiCriteria){
            if(!this.getModificationPos(modiType).equals(pep.getModificationPos(modiType))){
                return false;
            }
        }

        return true;
    }

    public boolean isSimilar(Peptide pep, boolean charge, ArrayList<String> modiCriteria){
        if(!this.sequence.equals(pep.getSequence())){
            return false;
        }

        if(charge){
            if(this.charge != pep.getCharge()){
                return false;
            }
        }

        for(String modiType : modiCriteria){
            if(!this.getModificationPos(modiType).equals(pep.getModificationPos(modiType))){
                return false;
            }
        }

        return true;
    }

    public String toString(){
        String rlt;
        rlt = "ID: " + id + "\n";
        rlt = rlt + "Charge: " + charge + "\n";
        if(doubleInfo!=null){
            for(Map.Entry<String, Double> entry : doubleInfo.entrySet()){
                rlt = rlt + entry.getKey() + ": " + entry.getValue() + "\n";
            }
        }
        if(strInfo!=null){
            for(Map.Entry<String, String> entry : strInfo.entrySet()){
                rlt = rlt + entry.getKey() + ": " + entry.getValue() + "\n";
            }
        }

        /*
        rlt = rlt + "mz: " + mz + "\n";
        rlt = rlt + "Score: " + score + "\n";
        */
        rlt = rlt + "Abundance: " + abundance + "\n\t" +  String.format("%.2f", Math.log10(abundance)) + "(log10)\n";
        if(modifications.size()==0){
            rlt += "Modification: None\n";
        } else {
            rlt += "Modification: \n";
            for(Modification md : modifications){
                rlt += "\t" + md.getModificationType() + ": ";
                rlt += (md.getPos().get(0) + 1) + "(" + md.getPercent().get(0) + ")";
                for(int i=1; i<md.getPos().size();i++){
                    rlt += ";" + md.getPos().get(i) + "(" + md.getPercent().get(i) + ")";
                }
                rlt += "\n";
            }
        }

        return rlt;
    }

    public String toString(int st){
        String rlt;
        rlt = "ID: " + id + "\n";
        rlt = rlt + "Charge: " + charge + "\n";
        if(multiMatch>0){
            rlt = rlt + "Multiple Match Number: " + multiMatch + "\n";
        }
        if(doubleInfo!=null){
            for(Map.Entry<String, Double> entry : doubleInfo.entrySet()){
                rlt = rlt + entry.getKey() + ": " + entry.getValue() + "\n";
            }
        }
        if(strInfo!=null){
            for(Map.Entry<String, String> entry : strInfo.entrySet()){
                rlt = rlt + entry.getKey() + ": " + entry.getValue() + "\n";
            }
        }

        /*
        rlt = rlt + "mz: " + mz + "\n";
        rlt = rlt + "Score: " + score + "\n";
        */
        rlt = rlt + "Abundance: " + abundance + "\n\t" +  String.format("%.2f", Math.log10(abundance)) + "(log10)\n";
        if(modifications.size()==0){
            rlt += "Modification: None\n";
        } else {
            rlt += "Modification: \n";
            for(Modification md : modifications){
                rlt += "\t" + md.getModificationType() + ": ";
                rlt += (md.getPos().get(0) + 1) + "/" + (md.getPos().get(0) + st + 1) + "(" + md.getPercent().get(0) + ")";
                for(int i=1; i<md.getPos().size();i++){
                    rlt += ";" + md.getPos().get(i) + "/" + (md.getPos().get(i) + st + 1) + "(" + md.getPercent().get(i) + ")";
                }
                rlt += "\n";
            }
        }

        return rlt;
    }

    public String getModiInfo(ArrayList<String> modiSelected){
        if(modifications.size() == 0){
            return null;
        }

        String rlt = sequence;
        rlt = rlt + "\t";

        TreeMap<Integer, String> modiPosAll = new TreeMap<>();
        for(Modification modification : modifications){
            if(modiSelected.contains(modification.getModificationType())){
                for(int i=0; i<modification.getPos().size();i++){
                    modiPosAll.put(modification.getPos().get(i), modification.getModificationType());
                }
            }
        }

        if(modiPosAll.size() == 0){
            return null;
        }

        for(Map.Entry<Integer, String> entry : modiPosAll.entrySet()){
            rlt = rlt + "[" + (entry.getKey() + 1) + "]" + entry.getValue() + "(" + sequence.charAt(entry.getKey()) + ")";
            if(!entry.equals(modiPosAll.lastEntry())){
                rlt = rlt + "|";
            }
        }

        return rlt;
    }

    //get modified positions in a peptide
    public ArrayList<Integer> getModiPos(ArrayList<String> modiSelected){
        if(modifications.size() == 0){
            return null;
        }

        Set<Integer> pos = new TreeSet<>();
        for(Modification modification : modifications){
            if(modiSelected.contains(modification.getModificationType())){
                for(int i=0; i<modification.getPos().size();i++){
                    pos.add(modification.getPos().get(i));
                }
            }
        }

        if(pos.size() == 0){
            return null;
        }

        ArrayList<Integer> rlt = new ArrayList<>(pos);
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
