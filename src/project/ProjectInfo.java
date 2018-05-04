package project;

import data.Modification;
import data.Peptide;
import data.ProteinDB;
import data.SampleGroup;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.*;

/**
 * Store the project information for save and load
 * Created by gpeng on 2/12/17.
 */
public class ProjectInfo implements Serializable{
    private File projectFile;
    private String projectName;
    private boolean dataImported;

    private ArrayList<String> proteomicsRawName;
    private ArrayList<ArrayList<String>> proteomicsRaw;
    private ArrayList<String> proteomicsRawType;
    private ArrayList<String> sampleInfoName;
    private ArrayList<String> sampleInfoType;
    private ArrayList<ArrayList<String>> sampleInfo;
    private ArrayList<String> sampleId;
    private String dbType;

    //private SampleGroup sampleGroup;


    public File getPorjectFile() { return  projectFile;}
    public void setProjectFile(File projectFile) { this.projectFile = projectFile; }

    public String getProjectName()  { return projectName;}
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public boolean getDataImported() { return dataImported; }
    public void setDataImported(boolean dataImported) { this.dataImported = dataImported; }

    public ArrayList<String> getProteomicsRawName() { return proteomicsRawName;}
    public void setProteomicsRawName(ArrayList<String> proteomicsRawName) { this.proteomicsRawName = proteomicsRawName;}

    public ArrayList<ArrayList<String>> getProteomicsRaw() { return proteomicsRaw; }
    public void setProteomicsRaw(ArrayList<ArrayList<String>> proteomicsRaw) { this.proteomicsRaw = proteomicsRaw;}

    public ArrayList<String> getProteomicsRawType() { return proteomicsRawType; }
    public void setProteomicsRawType(ArrayList<String> proteomicsRawType) { this.proteomicsRawType = proteomicsRawType; }

    public void setSampleInfoName(ArrayList<String> sampleInfoName) { this.sampleInfoName = sampleInfoName;}
    public void setSampleInfoType(ArrayList<String> sampleInfoType) { this.sampleInfoType = sampleInfoType;}
    public void setSampleInfo(ArrayList<ArrayList<String>> sampleInfo) { this.sampleInfo = sampleInfo;}
    public void setSampleid(ArrayList<String> sampleId) { this.sampleId = sampleId; }
    public void setDbType(String dbType) {this.dbType = dbType;}

    public SampleGroup getSampleGroup() {
        SampleGroup sampleGroup = new SampleGroup();
        ProteinDB proteinDB = new ProteinDB();
        proteinDB.setSpecies(dbType);
        //proteinSeq = new TreeMap<>();
        String proteinDBFile = "db/" + dbType + ".fasta";
        File dbFile = new File(proteinDBFile);
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(dbFile));

            String fline;
            String seq = "";
            String name = "";
            String id = "";
            while((fline = bufferedReader.readLine()) != null){
                if(fline.charAt(0) == '>'){
                    if(seq.equals("")){
                        String[] vsline = fline.split("\\|");
                        id = vsline[1];
                        String[] vsName = vsline[2].split("_");
                        name = vsName[0];
                    } else {
                        proteinDB.add(name, id, seq);
                        seq = "";
                        String[] vsline = fline.split("\\|");
                        id = vsline[1];
                        String[] vsName = vsline[2].split("_");
                        name = vsName[0];
                    }
                } else {
                    seq = seq + fline.replaceAll("\\s+", "");
                }
            }
            proteinDB.add(name, id, seq);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(sampleId == null){
            /*
            sampleInfoName = new ArrayList<>();
            sampleInfoType = new ArrayList<>();
            sampleInfo = new ArrayList<>();
            sampleId = new ArrayList<>();
            sampleId.add("Sample");

            int indexId = -1;
            int indexSequence = -1;
            int indexProtein = -1;
            int indexModification = -1;
            int indexAbundance = -1;
            int indexCharge = -1;
            ArrayList<String> otherInfo = new ArrayList<>();
            ArrayList<Integer> indexOther = new ArrayList<>();
            for(int i=0; i< proteomicsRawName.size(); i++){
                String infoTmp = proteomicsRawName.get(i).toLowerCase();
                switch (infoTmp){
                    case "id":
                        indexId = i;
                        break;
                    case "sequence":
                        indexSequence = i;
                        break;
                    case "protein":
                        indexProtein = i;
                        break;
                    case "modification":
                        indexModification = i;
                        break;
                    case "abundance":
                        indexAbundance = i;
                        break;
                    case "charge":
                        indexCharge = i;
                        break;
                    default:
                        otherInfo.add(proteomicsRawName.get(i));
                        indexOther.add(i);
                        break;
                }
            }

            Set<String> proteinNotFind = new TreeSet<>();
            ArrayList<String> pepNotMatch = new ArrayList<>();
            ArrayList<String> proteinNotMatch = new ArrayList<>();
            for(int i=0; i<proteomicsRaw.get(0).size(); i++){
                String id = proteomicsRaw.get(indexId).get(i);
                String sequence = proteomicsRaw.get(indexSequence).get(i);
                String protein = proteomicsRaw.get(indexProtein).get(i);
                if(protein.contains("_")){
                    protein = protein.substring(0, protein.indexOf("_"));
                }
                String modificationStr = proteomicsRaw.get(indexModification).get(i);
                String charge = proteomicsRaw.get(indexCharge).get(i);

                ArrayList<Modification> modifications = new ArrayList<>();

                String seq = proteinDB.getSeq(protein);
                if(seq == null){
                    proteinNotFind.add(protein);
                    continue;
                }

                if(!modificationStr.isEmpty()){
                    String[] modiTmp = modificationStr.split("\\|");
                    for(int j=0; j<modiTmp.length; j++){
                        Modification tmp = new Modification(modiTmp[j]);
                        modifications.add(tmp);
                    }
                }

                TreeMap<String, Double> doubleInfo = new TreeMap<>();
                TreeMap<String, String> strInfo = new TreeMap<>();

                for(int j=0; j<otherInfo.size(); j++){
                    if(proteomicsRawType.get(indexOther.get(j)).equals("Double")){
                        doubleInfo.put(otherInfo.get(j), tryDoubleParse(proteomicsRaw.get(indexOther.get(j)).get(i)));
                    } else {
                        strInfo.put(otherInfo.get(j), proteomicsRaw.get(indexOther.get(j)).get(i));
                    }
                }

                Double abTmp = tryDoubleParse(proteomicsRaw.get(indexAbundance).get(i));
                String spId = "Sample";

                Peptide ptTmp = new Peptide(id, sequence,  abTmp, Integer.parseInt(charge.trim()), doubleInfo, strInfo, modifications, 0);

                if(sampleGroup.addPeptide(spId, protein, seq, ptTmp)){
                    sampleGroup.addPepData(spId, id, abTmp);
                    sampleGroup.increaseProteinData(spId, protein, abTmp);
                } else {
                    pepNotMatch.add(sequence);
                    proteinNotMatch.add(protein);
                    continue;
                }

            }*/
            System.out.println("Open project with error!");
        } else {
            int indexId = -1;
            int indexSequence = -1;
            int indexProtein = -1;
            int indexModification = -1;
            int indexCharge = -1;
            ArrayList<Integer> indexAbundance = new ArrayList<>();
            ArrayList<String> otherInfo = new ArrayList<>();
            ArrayList<Integer> indexOther = new ArrayList<>();
            for(int i=0; i< sampleId.size(); i++){
                indexAbundance.add(-1);
            }
            for(int i=0; i< proteomicsRawName.size(); i++){
                String infoTmp = proteomicsRawName.get(i).toLowerCase();
                int idx = containsCaseInsensitive(sampleId, infoTmp);
                if(idx >=0){
                    indexAbundance.set(idx, i);
                    continue;
                }

                switch (infoTmp){
                    case "id":
                        indexId = i;
                        break;
                    case "sequence":
                        indexSequence = i;
                        break;
                    case "protein":
                        indexProtein = i;
                        break;
                    case "modification":
                        indexModification = i;
                        break;
                    case "modifications":
                        indexModification = i;
                        break;
                    case "charge":
                        indexCharge = i;
                        break;
                    default:
                        otherInfo.add(proteomicsRawName.get(i));
                        indexOther.add(i);
                        break;
                }
            }

            for(int i=0; i<sampleId.size();i++){
                for(int j=0; j<sampleInfoName.size(); j++){
                    if(sampleInfoName.get(j).toLowerCase().equals("sampleid")){
                        continue;
                    }
                    if(sampleInfoType.get(j).equals("Double")){
                        sampleGroup.addNumInfo(sampleId.get(i), sampleInfoName.get(j), tryDoubleParse(sampleInfo.get(j).get(i)));
                    } else {
                        sampleGroup.addStrInfo(sampleId.get(i), sampleInfoName.get(j), sampleInfo.get(j).get(i));
                    }
                }
            }
            Set<String> proteinNotFind = new TreeSet<>();
            ArrayList<String> pepNotMatch = new ArrayList<>();
            ArrayList<String> proteinNotMatch = new ArrayList<>();

            Map<String, ArrayList<String>> pepInMultipleProtein = new TreeMap<>();

            ArrayList<Boolean> idxAdd = new ArrayList<>();
            for(int i=0; i<proteomicsRaw.get(0).size();i++){
                idxAdd.add(false);
            }

            for(int i=0; i<proteomicsRaw.get(0).size(); i++){
                if(idxAdd.get(i)){
                    continue;
                }
                String id = proteomicsRaw.get(indexId).get(i);
                String sequence = proteomicsRaw.get(indexSequence).get(i);
                String protein = proteomicsRaw.get(indexProtein).get(i);
                String charge = proteomicsRaw.get(indexCharge).get(i);

                ArrayList<String> modiStrAll = new ArrayList<>();
                //String modificationStr = proteomicsRaw.get(indexModification).get(i);
                modiStrAll.add(proteomicsRaw.get(indexModification).get(i));
                idxAdd.set(i, true);

                boolean flagPepInMultiProtein = false;
                if(i<idxAdd.size()-1){
                    for(int j=i+1; j<idxAdd.size();j++){
                        if(idxAdd.get(j)){
                            continue;
                        }
                        if(proteomicsRaw.get(indexId).get(j).equals(id)){
                            if(proteomicsRaw.get(indexProtein).get(j).equals(protein)){
                                modiStrAll.add(proteomicsRaw.get(indexModification).get(j));
                            } else {
                                flagPepInMultiProtein = true;
                                if(pepInMultipleProtein.containsKey(id)){
                                    pepInMultipleProtein.get(id).add(proteomicsRaw.get(indexProtein).get(j));
                                } else {
                                    ArrayList<String> pTmp = new ArrayList<>();
                                    pTmp.add(protein);
                                    pTmp.add(proteomicsRaw.get(indexProtein).get(j));
                                    pepInMultipleProtein.put(id, pTmp);
                                }
                            }
                            idxAdd.set(j, true);
                        }
                    }
                }
                if(flagPepInMultiProtein){
                    continue;
                }

                if(protein.contains("_")){
                    protein = protein.substring(0, protein.indexOf("_"));
                }

                ArrayList<Modification> modifications = new ArrayList<>();

                String seq = proteinDB.getSeq(protein);
                if(seq == null){
                    proteinNotFind.add(protein);
                    continue;
                }

                if(modiStrAll.size()==1) {
                    String modificationStr = modiStrAll.get(0);
                    if(!modificationStr.isEmpty()){
                        //System.out.println(modificationStr);
                        String[] modiTmp = modificationStr.split("\\|");
                        for(int j=0; j<modiTmp.length; j++){
                            //System.out.println(modiTmp[j]);
                            Modification tmp = new Modification(modiTmp[j]);
                            modifications.add(tmp);
                        }
                    }
                } else {
                    Map<String, Map<Integer, Integer>> frequence = new TreeMap<>();
                    Map<Integer, String> resInfo = new TreeMap<>();
                    for(String str : modiStrAll){
                        if(str.isEmpty()){
                            continue;
                        }
                        //ArrayList<Modification> modiRawTmp = new ArrayList<>();
                        String[] modiTmp = str.split("\\|");
                        for(int j=0; j<modiTmp.length; j++){
                            //System.out.println(modiTmp[j]);
                            Modification tmp = new Modification(modiTmp[j]);


                            String typeTmp = tmp.getModificationType();
                            if(!frequence.containsKey(typeTmp)){
                                Map<Integer, Integer> fTmp = new TreeMap<>();
                                frequence.put(typeTmp, fTmp);
                            }

                            for(int k=0; k<tmp.getPos().size(); k++){
                                int posTmp = tmp.getPos().get(k);
                                resInfo.put(posTmp, tmp.getResidue().get(k));
                                if(frequence.get(typeTmp).containsKey(posTmp)){
                                    int ftmp = frequence.get(typeTmp).get(posTmp);
                                    frequence.get(typeTmp).put(posTmp, ftmp+1);
                                } else {
                                    frequence.get(typeTmp).put(posTmp, 1);
                                }
                            }
                        }
                    }

                    int numRecord = modiStrAll.size();

                    for(Map.Entry<String, Map<Integer, Integer>> entry : frequence.entrySet()){
                        ArrayList<Integer> posTmp = new ArrayList<>();
                        ArrayList<String> resTmp = new ArrayList<>();
                        ArrayList<Double> perTmp = new ArrayList<>();
                        for(Map.Entry<Integer, Integer> entry1 : entry.getValue().entrySet()){
                            Integer pos = entry1.getKey();
                            Integer num = entry1.getValue();
                            if(num < numRecord){
                                posTmp.add(pos);
                                resTmp.add(resInfo.get(pos));
                                perTmp.add(-1.0);
                            } else {
                                ArrayList<Integer> posTmp1 = new ArrayList<>();
                                ArrayList<String> resTmp1 = new ArrayList<>();
                                ArrayList<Double> perTmp1 = new ArrayList<>();
                                posTmp1.add(pos);
                                resTmp1.add(resInfo.get(pos));
                                perTmp1.add(100.0);
                                Modification m = new Modification(entry.getKey(), posTmp1, resTmp1, perTmp1);
                                modifications.add(m);
                            }
                        }
                        if(posTmp.size()>0){
                            Modification m = new Modification(entry.getKey(), posTmp, resTmp, perTmp);
                            modifications.add(m);
                        }
                    }
                }

                TreeMap<String, Double> doubleInfo = new TreeMap<>();
                TreeMap<String, String> strInfo = new TreeMap<>();

                for(int j=0; j<otherInfo.size(); j++){
                    if(proteomicsRawType.get(indexOther.get(j)).equals("Double")){
                        doubleInfo.put(otherInfo.get(j), tryDoubleParse(proteomicsRaw.get(indexOther.get(j)).get(i)));
                    } else {
                        strInfo.put(otherInfo.get(j), proteomicsRaw.get(indexOther.get(j)).get(i));
                    }
                }

                for(int j=0; j<sampleId.size(); j++){
                    Double abTmp = tryDoubleParse(proteomicsRaw.get(indexAbundance.get(j)).get(i));
                    String spId = sampleId.get(j);
                    Peptide ptTmp = new Peptide(id, sequence,  abTmp, Integer.parseInt(charge.trim()), doubleInfo, strInfo, modifications, 0);

                    if(sampleGroup.addPeptide(spId, protein, seq, ptTmp)){
                        sampleGroup.addPepData(spId, id, abTmp);
                        sampleGroup.increaseProteinData(spId, protein, abTmp);
                    } else {
                        if(j==0){
                            pepNotMatch.add(sequence);
                            proteinNotMatch.add(protein);
                        }
                        continue;
                    }
                }
            }
        }

        sampleGroup.initPepCutoff();
        sampleGroup.updatePepShow();
        sampleGroup.rawAbundance();
        sampleGroup.initModificationColor();
        return  sampleGroup;
    }
    //public void setSampleGroup(SampleGroup sampleGroup) { this.sampleGroup = sampleGroup; }

    public ProjectInfo(File projectFile, String projectName){
        this.projectFile = projectFile;
        this.projectName = projectName;
        dataImported = false;

        proteomicsRawName = null;
        proteomicsRaw = null;
        proteomicsRawType = null;

        sampleInfoName = null;
        sampleInfoType = null;
        sampleInfo = null;
        sampleId = null;

        dbType = null;
    }

    public static Double tryDoubleParse(String text){
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int containsCaseInsensitive(ArrayList<String> src, String tar){
        for(int i=0; i<src.size(); i++){
            if(src.get(i).equalsIgnoreCase(tar)){
                return i;
            }
        }

        return -1;
    }

}
