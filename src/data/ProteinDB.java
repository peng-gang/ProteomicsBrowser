package data;

import java.util.ArrayList;

/**
 * Created by gpeng on 9/15/17.
 */
public class ProteinDB {
    private ArrayList<String> name;
    private ArrayList<String> id;
    private ArrayList<String> spName;
    private ArrayList<String> seq;

    private String species;

    public ProteinDB(){
        name = new ArrayList<>();
        id = new ArrayList<>();
        spName = new ArrayList<>();
        seq = new ArrayList<>();
    }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public void add(String name, String id, String seq){
        this.name.add(name);
        this.id.add(id);
        this.seq.add(seq);
        this.spName.add((name + "_" + species));
    }

    public String getSeqId(String id){
        int idx = this.id.indexOf(id);
        if(idx < 0){
            return null;
        }

        return seq.get(idx);
    }

    public String getSeqName(String name){
        int idx = this.name.indexOf(name);
        if(idx < 0){
            return null;
        }

        return seq.get(idx);
    }

    public String getSeqSpName(String spName){
        int idx = this.spName.indexOf(spName);
        if(idx < 0){
            return null;
        }

        return seq.get(idx);
    }

    public String getSeq(String any){
        int idx;
        idx = name.indexOf(any);
        if(idx >=0){
            return seq.get(idx);
        }

        idx = id.indexOf(any);
        if(idx >=0){
            return seq.get(idx);
        }

        idx = spName.indexOf(any);
        if(idx >=0){
            return seq.get(idx);
        }

        return null;
    }
}
