package data;

import javafx.util.Pair;
import project.PublicInfo;

import java.io.*;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * sample groups storing sample information including numeric, string information and peptide and protein data for each sample
 * Created by gpeng on 2/13/17.
 */
public class SampleGroup implements Serializable {
    private TreeMap<String, Sample> samples;
    private HashSet<String> numInfoName;
    private HashSet<String> strInfoName;
    private HashSet<String> pepId;

    // they are the same for different ways to use
    private HashSet<String> proteinId; //for abundance value
    private HashSet<String> proteinName; //for modification

    private PublicInfo.ProteinIntegrationType proteinIntegrationType;
    private PublicInfo.ProteinStatus proteinStatus;
    //selected protein for normalization
    private String selProteinNorm;

    private boolean flagRawAbundance = false;
    private boolean flagRawMedian = false;

    private boolean flagiBAQ = false;
    private boolean flagiBAQMedianNorm = false;


    private boolean flagAbundanceRange = false;


    public SampleGroup(){
        samples = new TreeMap<>();
        numInfoName = new HashSet<>();
        strInfoName = new HashSet<>();
        pepId = new HashSet<>();
        proteinId = new HashSet<>();
        proteinName = new HashSet<>();

        proteinIntegrationType = PublicInfo.ProteinIntegrationType.Raw;
        proteinStatus = PublicInfo.ProteinStatus.Unnormalized;
        selProteinNorm = null;
    }


    // add data functions
    public void addNumInfo(String sample, String name, double value){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addNumInfo(name, value);
            samples.put(sample, tmp);
            numInfoName.add(name);
        } else {
            samples.get(sample).addNumInfo(name, value);
            numInfoName.add(name);
        }
    }

    public void addStrInfo(String sample, String name, String value){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addStrInfo(name, value);
            samples.put(sample, tmp);
            strInfoName.add(name);
        } else {
            samples.get(sample).addStrInfo(name, value);
            strInfoName.add(name);
        }
    }

    public void addPepData(String sample, String pepId, double value){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addPepData(pepId, value);
            samples.put(sample, tmp);
            this.pepId.add(pepId);
        } else {
            samples.get(sample).addPepData(pepId, value);
            this.pepId.add(pepId);
        }
    }

    public void increaseProteinData(String sample, String proteinId, double value){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.increaseProteinData(proteinId, value);
            samples.put(sample, tmp);
            this.proteinId.add(proteinId);
        } else {
            samples.get(sample).increaseProteinData(proteinId, value);
            this.proteinId.add(proteinId);
        }
    }

    public void addProteinData(String sample, String proteinId, double value){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addProteinData(proteinId, value);
            samples.put(sample, tmp);
            this.proteinId.add(proteinId);
        } else {
            samples.get(sample).addProteinData(proteinId, value);
            this.proteinId.add(proteinId);
        }
    }

    public void addProtein(String sample, Protein pt){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addProtein(pt);
            samples.put(sample, tmp);
            this.proteinName.add(pt.getName());
        } else {
            samples.get(sample).addProtein(pt);
            this.proteinName.add(pt.getName());
        }
    }

    public void addPeptide(String sample, String proteinName, String proteinSequence, Peptide pep){
        if(samples.get(sample) == null){
            Sample tmp = new Sample(sample);
            tmp.addPeptide(proteinName, proteinSequence, pep);
            samples.put(sample, tmp);
            this.proteinName.add(proteinName);
        } else {
            samples.get(sample).addPeptide(proteinName, proteinSequence, pep);
            this.proteinName.add(proteinName);
        }
    }

    public void setProteinIntegrationType(PublicInfo.ProteinIntegrationType proteinIntegrationType){
        this.proteinIntegrationType = proteinIntegrationType;
    }

    public void setProteinStatus(PublicInfo.ProteinStatus proteinStatus){
        this.proteinStatus = proteinStatus;
    }

    public void setProteinStatus(PublicInfo.ProteinStatus proteinStatus, String selProteinNorm) {
        this.proteinStatus = proteinStatus;
        this.selProteinNorm = selProteinNorm;
    }

    public void updatePepShow() {
        //update peptide show information
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            entry.getValue().updatePepShow();
        }
    }

    public void initPepCutoff(){
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            entry.getValue().initPepCutoff();
        }
    }

    public void updateProtein(){
        switch (proteinIntegrationType){
            case Raw:
                switch (proteinStatus){
                    case Unnormalized:
                        if(!flagRawAbundance){
                            rawAbundance();
                            flagRawAbundance = true;
                        }
                        // raw data
                        return;
                    case Median:
                        //median normalized raw data
                        if(!flagRawMedian){
                            rawAbundanceMedianNorm();
                            flagRawMedian = true;
                        }
                        return;
                    case SelProtein:
                        rawAbundanceSelProteinNorm();
                        return;
                    default:
                        return;
                }
            case iBAQ:
                switch (proteinStatus) {
                    case Unnormalized:
                        if(!flagiBAQ){
                            iBAQ();
                            flagiBAQ = true;
                        }

                        return;
                    case Median:
                        if(!flagiBAQMedianNorm){
                            iBAQMedianNorm();
                            flagiBAQMedianNorm = true;
                        }
                        return;
                    case SelProtein:
                        iBAQSelProteinNorm();
                        return;
                    default:
                        return;
                }
            default:
                return;
        }
    }


    //get data functions
    public Set<String> getSampleId(){
        return samples.keySet();
    }

    public HashSet<String> getNumInfoName(){
        return numInfoName;
    }

    public HashSet<String> getStrInfoName(){
        return strInfoName;
    }

    public HashSet<String> getPepId(){
        return pepId;
    }

    public HashSet<String> getProteinId(){
        return proteinId;
    }

    public HashSet<String> getProteinName(){
        return proteinName;
    }

    public ArrayList<Double> getNumInfo(String numInfoName){
        if(!this.numInfoName.contains(numInfoName)){
            return null;
        }

        ArrayList<Double> rlt = new ArrayList<>();
        for(String spName : samples.keySet()){
            rlt.add(samples.get(spName).getNumInfo(numInfoName));
        }

        return rlt;
    }

    public Double getNumInfo(String sample, String numInfoName){
        if((samples.keySet()).contains(sample)){
            return samples.get(sample).getNumInfo(numInfoName);
        } else {
            return null;
        }
    }

    public ArrayList<String> getStrInfo(String strInfoName){
        if(!this.strInfoName.contains(strInfoName)){
            return null;
        }

        ArrayList<String> rlt = new ArrayList<>();
        for(String spName : samples.keySet()){
            rlt.add(samples.get(spName).getStrInfo(strInfoName));
        }
        return rlt;
    }

    public String getStrInfo(String sample, String strInfoName){
        if((samples.keySet()).contains(sample)){
            return samples.get(sample).getStrInfo(strInfoName);
        } else {
            return null;
        }
    }

    public ArrayList<Double> getPepData(String pepId){
        if(!this.pepId.contains(pepId)){
            return null;
        }

        ArrayList<Double> rlt = new ArrayList<>();
        for(String spName : samples.keySet()){
            rlt.add(samples.get(spName).getPepData(pepId));
        }
        return rlt;
    }

    public Double getPepData(String sample, String pepId){
        if(samples.keySet().contains(sample)){
            return samples.get(sample).getPepData(pepId);
        } else {
            return null;
        }
    }

    public ArrayList<Double> getProteinData(String proteinId){
        if(!this.proteinId.contains(proteinId)){
            return null;
        }

        ArrayList<Double> rlt = new ArrayList<>();
        for(String spName : samples.keySet()){
            //rlt.add(samples.get(spName).getProteinData(proteinId));
            rlt.add(samples.get(spName).getProteinData(proteinId,proteinIntegrationType, proteinStatus));
        }
        return rlt;
    }

    public Double getProteinData(String sample, String proteinId){
        if(samples.keySet().contains(sample)){
            //return samples.get(sample).getProteinData(proteinId);
            return samples.get(sample).getProteinData(proteinId,proteinIntegrationType, proteinStatus);
        } else {
            return null;
        }
    }

    public ArrayList<Protein> getProtein(String proteinName){
        if(!this.proteinName.contains(proteinName)){
            return null;
        }

        ArrayList<Protein> rlt = new ArrayList<>();
        for(String spName : samples.keySet()){
            rlt.add(samples.get(spName).getProtein(proteinName));
        }
        return rlt;
    }

    public Protein getProtein(String sample, String proteinName){
        if(samples.keySet().contains(sample)){
            return samples.get(sample).getProtein(proteinName);
        } else {
            return null;
        }
    }


    public Collection<Protein> getSampleProtein(String sample){
        if(samples.keySet().contains(sample)){
            return samples.get(sample).getProteins();
        } else {
            return null;
        }
    }


    /**
     * iBAQ for each sample
     */
    public void iBAQ(){
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            entry.getValue().iBAQ();
        }
    }

    /**
     * raw abundance for each sample
     */
    public void rawAbundance(){
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            entry.getValue().rawAbundance();
        }
    }


    public void rawAbundanceMedianNorm(){
        ArrayList<Double> medianEach = new ArrayList<>();
        ArrayList<Double> dataAll = new ArrayList<>();
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            ArrayList<Double> tmp = entry.getValue().getRawAbundance();
            dataAll.addAll(tmp);
            Collections.sort(tmp);
            int idx = tmp.size()/2;
            if(tmp.size() % 2 == 0){
                double md = (tmp.get(idx-1) + tmp.get(idx))/2;
                medianEach.add(md);
            } else {
                medianEach.add(tmp.get(idx));
            }
        }

        Collections.sort(dataAll);
        int idx = dataAll.size()/2;
        double mdAll = 0;
        if(dataAll.size() %2 == 0){
            mdAll = (dataAll.get(idx-1) + dataAll.get(idx))/2;
        } else {
            mdAll = dataAll.get(idx);
        }

        idx = 0;
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            entry.getValue().rawAbundanceMedianNorm(mdAll-medianEach.get(idx));
            idx++;
        }

        return;
    }

    public void iBAQMedianNorm(){
        ArrayList<Double> medianEach = new ArrayList<>();
        ArrayList<Double> dataAll = new ArrayList<>();
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            ArrayList<Double> tmp = entry.getValue().getiBAQAbundance();
            dataAll.addAll(tmp);
            Collections.sort(tmp);
            int idx = tmp.size()/2;
            if(tmp.size() % 2 == 0){
                double md = (tmp.get(idx-1) + tmp.get(idx))/2;
                medianEach.add(md);
            } else {
                medianEach.add(tmp.get(idx));
            }
        }

        Collections.sort(dataAll);
        int idx = dataAll.size()/2;
        double mdAll = 0;
        if(dataAll.size() %2 == 0){
            mdAll = (dataAll.get(idx-1) + dataAll.get(idx))/2;
        } else {
            mdAll = dataAll.get(idx);
        }

        idx = 0;
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            entry.getValue().iBAQMedianNorm(mdAll-medianEach.get(idx));
            idx++;
        }
        return;
    }

    public void rawAbundanceSelProteinNorm(){
        if(selProteinNorm == null){
            return;
        }

        ArrayList<Double> rawAbundance = new ArrayList<>();
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            rawAbundance.add(entry.getValue().getRawAbundance(selProteinNorm));
        }

        ArrayList<Double> cp = new ArrayList<>(rawAbundance);
        Collections.sort(cp);
        int idx = cp.size()/2;
        double md;
        if(cp.size()%2 == 0){
            md = (cp.get(idx-1) + cp.get(idx))/2;
        } else {
            md = cp.get(idx);
        }

        idx = 0;
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            entry.getValue().rawAbundanceSelProteinNorm(md - rawAbundance.get(idx));
            idx++;
        }
    }

    public void iBAQSelProteinNorm(){
        if(selProteinNorm == null){
            return;
        }

        ArrayList<Double> rawAbundance = new ArrayList<>();
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            rawAbundance.add(entry.getValue().getiBAQAbundance(selProteinNorm));
        }

        ArrayList<Double> cp = new ArrayList<>(rawAbundance);
        Collections.sort(cp);
        int idx = cp.size()/2;
        double md;
        if(cp.size()%2 == 0){
            md = (cp.get(idx-1) + cp.get(idx))/2;
        } else {
            md = cp.get(idx);
        }

        idx = 0;
        for(Map.Entry<String, Sample> entry : samples.entrySet()){
            entry.getValue().iBAQSelProteinNorm(md - cp.get(idx));
            idx++;
        }
    }

    public void setAbundanceRange(){
        if(flagAbundanceRange){
            return;
        }
        for(Sample sp : samples.values()){
            sp.setAbundanceRange();
        }
        flagAbundanceRange = true;
    }


    public void outputModiResText(File textFile,  String sample, ArrayList<Modification.ModificationType> modiSelected, int numResidual){

        FileWriter fileWriter= null;
        try {
            fileWriter = new FileWriter(textFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for(Protein protein: samples.get(sample).getProteins()){
            ArrayList<String> info = protein.getModiResInfo(modiSelected, numResidual);
            for(String infoTmp : info){
                try{
                    bufferedWriter.write(infoTmp);
                    bufferedWriter.write("\n");
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Map<Character, Double>> outputModiResFreq(String sample, ArrayList<Modification.ModificationType> modiSelected, int numResidual){
        ArrayList<Pair<String, Integer> > modiRes = new ArrayList<>();
        for(Protein protein : samples.get(sample).getProteins()){
            modiRes.addAll(protein.getModiRes(modiSelected, numResidual));
        }

        ArrayList<HashMap<Character, Double> > count = new ArrayList<>();
        for(int i=0; i<(2*numResidual+1); i++){
            count.add(new HashMap<>());
        }

        for(Pair<String, Integer> modiResTmp : modiRes){
            String seq = modiResTmp.getKey();
            int pos = modiResTmp.getValue();
            for(int i=0; i<seq.length();i++){
                Double d = (count.get((numResidual-pos+i))).get(seq.charAt(i));
                if(d==null){
                    (count.get((numResidual-pos+i))).put(seq.charAt(i), 1.0);
                } else {
                    (count.get((numResidual-pos+i))).put(seq.charAt(i), d + 1.0);
                }
            }
        }

        ArrayList<Map<Character, Double>> rlt = new ArrayList<>();
        for(HashMap<Character, Double> ct : count){
            double total = 0;
            for(Double d : ct.values()){
                total += d;
            }

            for(Map.Entry<Character, Double> entry : ct.entrySet()){
                entry.setValue(entry.getValue()/total);
            }

            Map<Character, Double> sorted = (ct.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            rlt.add(sorted);
        }

        return rlt;
    }

}
