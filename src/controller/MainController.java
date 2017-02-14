package controller;

import controller.apps.ImportDataController;
import controller.data.ProteomicsDataController;
import data.Peptide;
import data.PeptideInfo;
import data.SampleGroup;
import data.SampleInfo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.SplittableRandom;
import java.util.StringJoiner;

public class MainController implements Initializable{

    //Menu
    @FXML private MenuBar menuBar;
    //File
    @FXML private Menu menuFile;
    @FXML private MenuItem menuNewProj;
    @FXML private MenuItem menuImportData;
    @FXML private MenuItem menuClose;


    //TreeView
    @FXML private TreeView treeView;

    //TabPane
    @FXML private TabPane tabPane;
    @FXML private Tab tabProteomicsData;


    private TreeItem<String> treeRoot;


    private File ptFile;
    private File spFile;


    //Controller
    ProteomicsDataController proteomicsDataController;

    ///Data
    //proteomics data
    private ArrayList<ArrayList<String>> proteomicsRaw;
    private ArrayList<String> proteomicsRawName;
    private ArrayList<String> proteomicsRawType;

    //sample information data
    private ArrayList<String> sampleInfoName;
    private ArrayList<String> sampleInfoType;
    private ArrayList<ArrayList<String>> sampleInfo;
    private ArrayList<String> sampleId;

    //
    private SampleGroup sampleGroup;


    //Menu functions
    @FXML private void close(ActionEvent event){
        System.out.println("Menu Close");
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }

    @FXML private void newProject(ActionEvent event) throws IOException {
        System.out.println("new project");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("New ProteomicsBrowser Project");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ProteomicsBrowser Proj", "*.pbproj")
        );

        Stage stage = (Stage) menuBar.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        String fileName = file.getName();
        String projName = fileName.substring(0, fileName.length()-7);

        if(file != null){
            try{
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(projName);
                fileWriter.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        menuImportData.setDisable(false);
        treeRoot = new TreeItem<>(projName);

        treeView.setRoot(treeRoot);
    }

    @FXML private void importData(ActionEvent event) throws IOException {
        System.out.println("Import Data");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/apps/ImportData.fxml"));
        Parent root = loader.load();
        Stage importDataStage = new Stage();
        importDataStage.setTitle("Import Data");
        importDataStage.setScene(new Scene(root, 400, 300));

        importDataStage.initModality(Modality.WINDOW_MODAL);
        importDataStage.initOwner(menuBar.getScene().getWindow());

        ImportDataController controller = loader.getController();
        importDataStage.showAndWait();

        ptFile = controller.getPtFile();
        spFile = controller.getSpFile();

        System.out.println(ptFile);
        System.out.println(spFile);

        if(ptFile == null){
            return;
        }

        initTreeView();
        loadData();
        initProteomicsDataTab();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("MainController Initialization");
        //initTreeView();
    }

    //Initialization functions
    private void initTreeView(){

        //Data
        TreeItem<String> data = new TreeItem<>("Data");
        TreeItem<String> proteomicsData = new TreeItem<>("Proteomics Data");
        TreeItem<String> samplePepData = new TreeItem<>("Samples and Peptide");
        TreeItem<String> sampleProteinData = new TreeItem<>("Samples and Protein");
        data.getChildren().addAll(proteomicsData, samplePepData, sampleProteinData);


        //Browser
        TreeItem<String> bw = new TreeItem<>("Browser");


        treeRoot.getChildren().addAll(data, bw);



        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;

                if(selectedItem.getValue().equals("Proteomics Data")){
                    System.out.println("lala");
                }
            }
        });
    }

    private void initProteomicsDataTab(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/data/ProteomicsData.fxml"));
            tabProteomicsData.setContent(loader.load());
            proteomicsDataController = loader.getController();
            System.out.println(proteomicsDataController);
            proteomicsDataController.setProteomicsData(proteomicsRawName, proteomicsRawType,  proteomicsRaw);
            proteomicsDataController.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData(){
        readSampleDataFile();
        readProteomicsDataFile();
        arrangeData();
    }

    private void readProteomicsDataFile() {
        System.out.println("Read Proteomics Data");
        //If some variable doesn't exit, check later
        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new FileReader(ptFile));


            String header = bufferedReader.readLine();
            String[] vsheader = header.split(",");

            proteomicsRawName = new ArrayList<>();
            proteomicsRaw = new ArrayList<>();
            proteomicsRawType = new ArrayList<>();
            for(String strTmp : vsheader){
                proteomicsRawName.add(strTmp);
                ArrayList<String> tmp = new ArrayList<>();
                proteomicsRaw.add(tmp);
                proteomicsRawType.add("Double");
            }


            String fline;
            while((fline=bufferedReader.readLine()) != null){
                String[] vslines = fline.split(",");
                for(int i = 0; i < vslines.length; i++) {
                    if(proteomicsRawType.get(i).equals("Double") && !isNumeric(vslines[i])){
                        proteomicsRawType.set(i, "String");
                    }
                    proteomicsRaw.get(i).add(vslines[i]);
                }

            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void readSampleDataFile(){
        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new FileReader(spFile));

            String header = bufferedReader.readLine();
            String[] vsheader = header.split(",");

            sampleInfoName = new ArrayList<>();
            sampleInfoType = new ArrayList<>();
            sampleInfo = new ArrayList<>();
            sampleId = new ArrayList<>();
            for(String strTmp : vsheader){
                sampleInfoName.add(strTmp);
                sampleInfoType.add("Double");
                ArrayList<String> tmp = new ArrayList<>();
                sampleInfo.add(tmp);
            }

            int indexSampleId = -1;
            for(int i=0; i<vsheader.length; i++){
                if(vsheader[i].toLowerCase().equals("sampleid")){
                    indexSampleId = i;
                    break;
                }
            }

            String fline;
            while((fline=bufferedReader.readLine()) != null){
                String[] vslines = fline.split(",");
                for(int i = 0; i < vslines.length; i++){
                    if(sampleInfoType.get(i).equals("Double") && !isNumeric(vslines[i])){
                        sampleInfoType.set(i, "String");
                    }
                    sampleInfo.get(i).add(vslines[i]);
                }
                sampleId.add(vslines[indexSampleId]);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Read Sample Data");
    }


    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static int containsCaseInsensitive(ArrayList<String> src, String tar){
        for(int i=0; i<src.size(); i++){
            if(src.get(i).equalsIgnoreCase(tar)){
                return i;
            }
        }

        return -1;
    }


    private void arrangeData(){
        System.out.println("arrange data!");
        sampleGroup = new SampleGroup();

        int indexId = -1;
        int indexSequence = -1;
        int indexCharge = -1;
        int indexMz = -1;
        int indexScore = -1;
        int indexProtein = -1;
        int indexModification = -1;
        ArrayList<Integer> indexAbundance = new ArrayList<>();
        for(int i=0; i<sampleId.size();i++){
            indexAbundance.add(-1);
        }
        for(int i=0; i<proteomicsRawName.size(); i++){
            String tmp = proteomicsRawName.get(i).toLowerCase();
            int idx = containsCaseInsensitive(sampleId, tmp);
            if(idx >= 0){
                indexAbundance.set(idx, i);
                continue;
            }

            switch (tmp) {
                case "id":
                    indexId = i;
                    break;
                case "sequence":
                    indexSequence = i;
                    break;
                case "charge":
                    indexCharge = i;
                    break;
                case "mz":
                    indexMz = i;
                    break;
                case "score":
                    indexScore = i;
                    break;
                case "protein":
                    indexProtein = i;
                    break;
                case "modification":
                    indexModification = i;
                    break;
            }
        }

        //check find
        if(indexId == -1){
            System.err.println("Cannot find id");
        }

        if(indexSequence == -1){
            System.err.println("Cannot find sequence");
        }

        if(indexCharge == -1){
            System.err.println("Cannot find charge");
        }

        if(indexMz == -1){
            System.err.println("Cannot find mz");
        }

        if(indexScore == -1) {
            System.err.println("Cannot find score");
        }

        if(indexProtein == -1){
            System.err.println("Cannot find protein");
        }

        if(indexModification == -1){
            System.err.println("Cannot find modification");
        }

        for(int i=0; i<indexAbundance.size();i++){
            if(indexAbundance.get(i) == -1){
                System.err.println("Cannot find " + sampleId.get(i));
            }
        }


        for(int i=0; i<proteomicsRaw.get(0).size(); i++){

        }

    }

}

