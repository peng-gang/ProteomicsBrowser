package project;

import data.SampleGroup;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

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
    private SampleGroup sampleGroup;


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

    public SampleGroup getSampleGroup() {return  sampleGroup;}
    public void setSampleGroup(SampleGroup sampleGroup) { this.sampleGroup = sampleGroup; }

    public ProjectInfo(File projectFile, String projectName){
        this.projectFile = projectFile;
        this.projectName = projectName;
        dataImported = false;

        proteomicsRawName = null;
        proteomicsRaw = null;
        proteomicsRawType = null;
        sampleGroup = null;
    }

}
