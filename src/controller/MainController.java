package controller;

import controller.apps.ImportDataController;
import controller.browser.PBrowserController;
import controller.data.ProteomicsDataController;
import controller.data.SamplePepDataController;
import controller.data.SampleProteinDataController;
import controller.dataselect.CorScatterDataSelectController;
import controller.dataselect.TTestDataSelectController;
import controller.result.CorScatterController;
import controller.result.PValueTableController;
import data.*;
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
import javafx.scene.input.SwipeEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.math3.stat.inference.TestUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable{

    //Menu
    @FXML private MenuBar menuBar;
    //File
    @FXML private Menu menuFile;
    @FXML private MenuItem menuNewProj;
    @FXML private MenuItem menuImportData;
    @FXML private MenuItem menuClose;
    @FXML private MenuItem menuOpenProj;
    @FXML private MenuItem menuSaveProj;

    //Analyze
    @FXML private Menu menuAnalyze;


    //TreeView
    @FXML private TreeView treeView;

    //TabPane
    @FXML private TabPane tabPane;
    @FXML private Tab tabProteomicsData;

    private Tab tabSamplePepData = null;
    private Tab tabSampleProteinData = null;
    private Tab tabBrowser = null;

    private SingleSelectionModel<Tab> selectionModel;

    private TreeItem<String> treeRoot;


    private File ptFile;
    private File spFile;


    //Controller
    private ProteomicsDataController proteomicsDataController;
    private SamplePepDataController samplePepDataController;
    private SampleProteinDataController sampleProteinDataController;
    private PBrowserController pBrowserController;

    ///Data
    //DB
    //private String proteinDBFile = "src/protein.info.csv";
    private TreeMap<String, String> proteinSeq;


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
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
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


        treeRoot = new TreeItem<>(projName);

        treeView.setRoot(treeRoot);
        menuImportData.setDisable(false);
        menuSaveProj.setDisable(false);
    }

    @FXML private void openProj(ActionEvent event){

    }

    @FXML private void saveProj(ActionEvent event){

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

        String type = controller.getType();
        System.out.println(type);
        System.out.println(ptFile);
        System.out.println(spFile);

        if(ptFile == null){
            return;
        }

        initTreeView();
        loadData(type);
        initProteomicsDataTab();
        //initSamplePepDataTab();
        //initSampleProteinDataTab();
        menuImportData.setDisable(true);
    }

    @FXML private void correlation(ActionEvent event){
        System.out.println("correlation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/dataselect/CorScatterDataSelect.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage corScatterDataSelectStage = new Stage();
        corScatterDataSelectStage.setTitle("Data Select (Correlation)");
        corScatterDataSelectStage.setScene(new Scene(root, 400, 300));

        corScatterDataSelectStage.initModality(Modality.WINDOW_MODAL);
        corScatterDataSelectStage.initOwner(menuBar.getScene().getWindow());

        CorScatterDataSelectController controller = loader.getController();
        controller.set(sampleGroup);

        String tabSel = tabPane.getSelectionModel().getSelectedItem().getText();
        if(tabSel.equals("Peptide")){
            controller.init("Peptide");
        } else if(tabSel.equals("Protein")){
            controller.init("Protein");
        } else {
            System.err.println("Wrong Tab");
        }

        corScatterDataSelectStage.showAndWait();

        String dataId1 = controller.getData1Id();
        String dataId2 = controller.getData2Id();

        ArrayList<Double> d1;
        ArrayList<Double> d2;

        if(tabSel.equals("Peptide")){
            d1 = sampleGroup.getPepData(dataId1);
            d2 = sampleGroup.getPepData(dataId2);
        } else {
            d1 = sampleGroup.getProteinData(dataId1);
            d2 = sampleGroup.getProteinData(dataId2);
        }


        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("../view/result/CorScatter.fxml"));
        Parent root2 = null;
        try {
            root2 = loader2.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage scStage = new Stage();
        scStage.setTitle("Correlation Scatter Plot");
        scStage.setScene(new Scene(root2, 600, 400));

        scStage.initModality(Modality.WINDOW_MODAL);
        scStage.initOwner(menuBar.getScene().getWindow());

        CorScatterController controller2 = loader2.getController();
        controller2.set(dataId1, dataId2, d1, d2);
        controller2.init();
        scStage.showAndWait();
    }

    @FXML private void tTest(ActionEvent event){
        System.out.println("t-test");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/dataselect/TTestDataSelect.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage tTestDataSelectStage = new Stage();
        tTestDataSelectStage.setTitle("Data Select (T-Test)");
        tTestDataSelectStage.setScene(new Scene(root, 400, 300));

        tTestDataSelectStage.initModality(Modality.WINDOW_MODAL);
        tTestDataSelectStage.initOwner(menuBar.getScene().getWindow());

        TTestDataSelectController controller = loader.getController();
        controller.set(sampleGroup);
        String tabSel = tabPane.getSelectionModel().getSelectedItem().getText();
        if(tabSel.equals("Peptide")){
            controller.init("Peptide");
        } else if(tabSel.equals("Protein")){
            controller.init("Protein");
        } else {
            System.err.println("Wrong Tab");
        }

        tTestDataSelectStage.showAndWait();

        Double low = controller.getLow();
        Double high = controller.getHigh();
        String groupId= controller.getGroupId();
        String dataId = controller.getDataId();

        //T-test
        ArrayList<String> sampleId = new ArrayList<>(sampleGroup.getSampleId());
        ArrayList<String> g1 = new ArrayList<>();
        ArrayList<String> g2 = new ArrayList<>();
        if(low == null){
            String g1Str = sampleGroup.getStrInfo(sampleId.get(0), groupId);
            for(String sp : sampleId){
                if(sampleGroup.getStrInfo(sp, groupId).equals(g1Str)){
                    g1.add(sp);
                } else {
                    g2.add(sp);
                }
            }
        } else {
            for(String sp: sampleId){
                if(sampleGroup.getNumInfo(sp, groupId) < low){
                    g1.add(sp);
                }

                if(sampleGroup.getNumInfo(sp, groupId) > high){
                    g2.add(sp);
                }
            }
        }

        ArrayList<String> testId = new ArrayList<>();
        ArrayList<Double> pv = new ArrayList<>();
        if(dataId.equals("ALL")){
            double[] t1 = new double[g1.size()];
            double[] t2 = new double[g2.size()];

            if(tabSel.equals("Peptide")){
                for(String pepId : sampleGroup.getPepId()){
                    testId.add(pepId);
                    for(int i=0; i<g1.size(); i++){
                        t1[i] = sampleGroup.getPepData(g1.get(i), pepId);
                    }

                    for(int i=0; i<g2.size(); i++){
                        t2[i] = sampleGroup.getPepData(g2.get(i), pepId);
                    }

                    double pvalue = TestUtils.tTest(t1, t2);
                    pv.add(pvalue);
                }
            } else {
                for(String proteinId : sampleGroup.getProteinId()){
                    testId.add(proteinId);
                    for(int i=0; i<g1.size();i++){
                        t1[i] = sampleGroup.getProteinData(g1.get(i), proteinId);
                    }

                    for(int i =0; i<g2.size();i++){
                        t2[i] = sampleGroup.getProteinData(g2.get(i), proteinId);
                    }

                    double pvalue = TestUtils.tTest(t1, t2);
                    pv.add(pvalue);
                }
            }
        } else {
            double[] t1 = new double[g1.size()];
            double[] t2 = new double[g2.size()];

            testId.add(dataId);
            if(tabSel.equals("Peptide")){
                for(int i=0; i<g1.size(); i++){
                    t1[i] = sampleGroup.getPepData(g1.get(i), dataId);
                }

                for(int i=0; i<g2.size(); i++){
                    t2[i] = sampleGroup.getPepData(g2.get(i), dataId);
                }
            } else if(tabSel.equals("Protein")){
                for(int i=0; i<g1.size(); i++){
                    t1[i] = sampleGroup.getProteinData(g1.get(i), dataId);
                }

                for(int i=0; i<g2.size(); i++){
                    t2[i] = sampleGroup.getProteinData(g2.get(i), dataId);
                }
            } else {
                System.err.println("Wrong Tab");
            }

            double pvalue = TestUtils.tTest(t1, t2);
            pv.add(pvalue);
        }


        //show result
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("../view/result/PValueTable.fxml"));
        Parent root2 = null;
        try {
            root2 = loader2.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage pvStage = new Stage();
        pvStage.setTitle("P Value of T-Test");
        pvStage.setScene(new Scene(root2, 300, 400));

        pvStage.initModality(Modality.WINDOW_MODAL);
        pvStage.initOwner(menuBar.getScene().getWindow());

        PValueTableController controller2 = loader2.getController();
        controller2.set(testId, pv);
        controller2.init();
        pvStage.showAndWait();
    }

    @FXML private void boxPlot(ActionEvent event){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("MainController Initialization");

        selectionModel = tabPane.getSelectionModel();
        //initTreeView();

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                String selectedValue = newValue.getText();
                switch (selectedValue){
                    case "Proteomics":
                        menuAnalyze.setVisible(false);
                        break;
                    case "Peptide":
                        menuAnalyze.setVisible(true);
                        break;
                    case "Protein":
                        menuAnalyze.setVisible(true);
                        break;
                    case "Browser":
                        menuAnalyze.setVisible(false);
                        break;
                    default:
                        System.err.println("Cannot find " + selectedValue);
                        break;
                }
            }
        });
    }

    //Initialization functions
    private void initTreeView(){

        //Data
        TreeItem<String> data = new TreeItem<>("Data");
        TreeItem<String> proteomicsData = new TreeItem<>("Proteomics Data");
        TreeItem<String> samplePepData = new TreeItem<>("Peptide Data");
        TreeItem<String> sampleProteinData = new TreeItem<>("Protein Data");
        data.getChildren().addAll(proteomicsData, samplePepData, sampleProteinData);


        //Browser
        TreeItem<String> bw = new TreeItem<>("Browser");


        treeRoot.getChildren().addAll(data, bw);


        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;

                String selectedValue = selectedItem.getValue();

                switch (selectedValue){
                    case "Proteomics Data":
                        System.out.println("Select Proteomics Data Tab");
                        selectionModel.select(tabProteomicsData);
                        break;
                    case "Peptide Data":
                        System.out.println("Select Peptide Data");
                        if(tabSamplePepData == null){
                            initSamplePepDataTab();
                            tabPane.getTabs().add(tabSamplePepData);
                        }
                        selectionModel.select(tabSamplePepData);
                        menuAnalyze.setVisible(true);
                        break;
                    case "Protein Data":
                        System.out.println("Select Protein Data");
                        if(tabSampleProteinData == null){
                            initSampleProteinDataTab();
                            tabPane.getTabs().add(tabSampleProteinData);
                        }
                        selectionModel.select(tabSampleProteinData);
                        menuAnalyze.setVisible(true);
                        break;
                    case "Browser":
                        System.out.println("Select Browser");
                        if(tabBrowser == null){
                            initBrowserTab();
                            tabPane.getTabs().add(tabBrowser);
                        }
                        selectionModel.select(tabBrowser);
                        menuAnalyze.setVisible(false);
                        break;
                    default:
                        System.err.println("Wrong Selected tree item " + selectedValue);
                        break;
                }
            }
        });
    }



    private void initProteomicsDataTab(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/data/ProteomicsData.fxml"));
            tabProteomicsData.setContent(loader.load());
            tabProteomicsData.setText("Proteomics");
            proteomicsDataController = loader.getController();
            proteomicsDataController.setProteomicsData(proteomicsRawName, proteomicsRawType,  proteomicsRaw);
            proteomicsDataController.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initSamplePepDataTab(){
        tabSamplePepData = new Tab();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/data/SamplePepData.fxml"));
        try {
            tabSamplePepData.setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        tabSamplePepData.setText("Peptide");
        samplePepDataController = loader.getController();
        samplePepDataController.setData(sampleGroup);
        samplePepDataController.show();
    }

    private void initSampleProteinDataTab(){
        tabSampleProteinData = new Tab();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/data/SampleProteinData.fxml"));
        try {
            tabSampleProteinData.setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        tabSampleProteinData.setText("Protein");
        sampleProteinDataController = loader.getController();
        sampleProteinDataController.setData(sampleGroup);
        sampleProteinDataController.show();

    }

    private void initBrowserTab(){
        tabBrowser = new Tab();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/browser/PBrowser.fxml"));
        try {
            tabBrowser.setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        tabBrowser.setText("Browser");
        pBrowserController = loader.getController();
        pBrowserController.setData(sampleGroup);
        pBrowserController.show();
    }

    private void loadData(String type){
        readSampleDataFile();
        readProteomicsDataFile();
        loadProteinSeqDB(type);
        arrangeData();
    }

    private void loadProteinSeqDB(String type){
        proteinSeq = new TreeMap<>();
        String proteinDBFile = "db/" + type + "/protein.info.csv";
        File dbFile = new File(proteinDBFile);
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(dbFile));

            String fline;
            while((fline = bufferedReader.readLine()) != null){
                String[] vslines = fline.split(",");
                proteinSeq.put(vslines[0], vslines[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            String id = proteomicsRaw.get(indexId).get(i);
            String sequence = proteomicsRaw.get(indexSequence).get(i);
            Integer charge = tryIntegerParse(proteomicsRaw.get(indexCharge).get(i));
            Double mz = tryDoubleParse(proteomicsRaw.get(indexMz).get(i));
            Double score = tryDoubleParse(proteomicsRaw.get(indexScore).get(i));
            String protein = proteomicsRaw.get(indexProtein).get(i);
            String modificationStr = proteomicsRaw.get(indexModification).get(i);
            ArrayList<Modification> modifications = new ArrayList<>();
            if(!modificationStr.isEmpty()){
                String[] modiTmp = modificationStr.split(";");
                for(int j=0; j<modiTmp.length; j++){
                    Modification tmp = new Modification(modiTmp[j]);
                    modifications.add(tmp);
                }
            }

            for(int j=0; j<sampleId.size(); j++){
                Double abTmp = tryDoubleParse(proteomicsRaw.get(indexAbundance.get(j)).get(i));
                String spId = sampleId.get(j);
                Peptide ptTmp = new Peptide(id, sequence, charge, mz, score, abTmp, modifications);

                sampleGroup.addPepData(spId, id, abTmp);
                sampleGroup.addPeptide(spId, protein, proteinSeq.get(protein), ptTmp);
                sampleGroup.increaseProteinData(spId, protein, abTmp);
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
    }


    public static Integer tryIntegerParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double tryDoubleParse(String text){
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}

