package controller;

import controller.apps.ImportDataController;
import controller.browser.PBrowserController;
import controller.data.ProteomicsDataController;
import controller.data.SamplePepDataController;
import controller.data.SampleProteinDataController;
import controller.dataselect.BoxPlotDataSelectController;
import controller.dataselect.CorScatterDataSelectController;
import controller.dataselect.TTestDataSelectController;
import controller.filter.PepFilterController;
import controller.parameter.ParaExportModificationInfoController;
import controller.parameter.ParaModifiedCysController;
import controller.parameter.ParaModifiedCysFigureController;
import controller.result.CorScatterController;
import controller.result.CysResidualFigureController;
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
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.SwipeEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.math3.ode.events.Action;
import org.apache.commons.math3.stat.inference.TestUtils;
import project.ProjectInfo;
import project.PublicInfo;

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


    //Data
    @FXML private Menu menuData;
    @FXML private RadioMenuItem menuProteinRaw;
    @FXML private RadioMenuItem menuProteiniBAQ;

    @FXML private RadioMenuItem menuNonnormalization;
    @FXML private RadioMenuItem menuMedianNormalization;

    //Analyze
    @FXML private Menu menuAnalyze;

    @FXML private Menu menuDataClean;

    //View
    @FXML private Menu menuView;
    @FXML private RadioMenuItem menuScaleRegular;
    @FXML private RadioMenuItem menuScaleLog2;
    @FXML private RadioMenuItem menuScaleLog10;

    //Export
    @FXML private Menu menuExport;
    @FXML private MenuItem menuExportModificationInfo;
    @FXML private Menu menuExportModiCys;

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
    //private TreeMap<String, String> proteinSeq;
    private ProteinDB proteinDB;



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


    //project information
    private ProjectInfo projectInfo = null;



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
                projectInfo = new ProjectInfo(file, projName);
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(projectInfo);
                oos.close();
                fos.close();

                //FileWriter fileWriter = new FileWriter(file);
                //fileWriter.write(projName);
                //fileWriter.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            return;
        }


        treeRoot = new TreeItem<>(projName);

        treeView.setRoot(treeRoot);
        menuImportData.setDisable(false);
        menuSaveProj.setDisable(false);
        menuNewProj.setDisable(true);
        menuOpenProj.setDisable(true);
    }

    @FXML private void openProj(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Project");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ProteomicsBrowser Proj", "*.pbproj")
        );
        Stage stage = (Stage) menuBar.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                if(projectInfo == null){
                    try {
                        projectInfo = (ProjectInfo) ois.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    treeRoot = new TreeItem<>(projectInfo.getProjectName());
                    treeView.setRoot(treeRoot);
                    if(!projectInfo.getDataImported()){
                        menuImportData.setDisable(false);
                        menuSaveProj.setDisable(false);
                        menuNewProj.setDisable(true);
                        menuOpenProj.setDisable(true);
                        return;
                    }

                    initTreeView();

                    proteomicsRawName = projectInfo.getProteomicsRawName();
                    proteomicsRaw = projectInfo.getProteomicsRaw();
                    proteomicsRawType = projectInfo.getProteomicsRawType();
                    sampleGroup = projectInfo.getSampleGroup();

                    initProteomicsDataTab();
                    menuImportData.setDisable(true);
                    menuNewProj.setDisable(true);
                    menuOpenProj.setDisable(true);
                    //Do something more?
                } else {
                    //Do something first
                    try {
                        projectInfo = (ProjectInfo) ois.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                ois.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            return;
        }
    }

    @FXML private void saveProj(ActionEvent event){
        File file = projectInfo.getPorjectFile();
        if(menuImportData.isDisable()){
            projectInfo.setDataImported(true);
            projectInfo.setProteomicsRaw(proteomicsRaw);
            projectInfo.setProteomicsRawName(proteomicsRawName);
            projectInfo.setProteomicsRawType(proteomicsRawType);
            projectInfo.setSampleGroup(sampleGroup);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(projectInfo);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML private void importData(ActionEvent event) throws IOException {
        System.out.println("Import Data");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/apps/ImportData.fxml"));
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

    //Menu Analyze
    @FXML private void correlation(ActionEvent event){
        System.out.println("correlation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dataselect/CorScatterDataSelect.fxml"));
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


        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/view/result/CorScatter.fxml"));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dataselect/TTestDataSelect.fxml"));
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
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/view/result/PValueTable.fxml"));
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
        System.out.println("BoxPlots");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dataselect/BoxPlotDataSelect.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage boxPlotDataSelectStage = new Stage();
        boxPlotDataSelectStage.setTitle("Data Select (BoxPlot)");
        boxPlotDataSelectStage.setScene(new Scene(root, 400, 300));

        boxPlotDataSelectStage.initModality(Modality.WINDOW_MODAL);
        boxPlotDataSelectStage.initOwner(menuBar.getScene().getWindow());

        BoxPlotDataSelectController controller = loader.getController();
        controller.set(sampleGroup);
        String tabSel = tabPane.getSelectionModel().getSelectedItem().getText();
        if(tabSel.equals("Peptide")){
            controller.init("Peptide");
        } else if(tabSel.equals("Protein")){
            controller.init("Protein");
        } else {
            System.err.println("Wrong Tab");
        }

        boxPlotDataSelectStage.showAndWait();

        Double low = controller.getLow();
        Double high = controller.getHigh();
        String groupId= controller.getGroupId();
        String dataId = controller.getDataId();

        //set into different groups
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

        ArrayList<Double> t1 = new ArrayList<>();
        ArrayList<Double> t2 = new ArrayList<>();

        if(tabSel.equals("Peptide")){
            for(int i=0; i<g1.size(); i++){
                t1.add(sampleGroup.getPepData(g1.get(i), dataId));
            }

            for(int i=0; i<g2.size(); i++){
                t2.add(sampleGroup.getPepData(g2.get(i), dataId));
            }
        } else if(tabSel.equals("Protein")){
            for(int i=0; i<g1.size(); i++){
                t1.add(sampleGroup.getProteinData(g1.get(i), dataId));
            }

            for(int i=0; i<g2.size(); i++){
                t2.add(sampleGroup.getProteinData(g2.get(i), dataId));
            }
        } else {
            System.err.println("Wrong Tab");
        }

        ArrayList<ArrayList<Double>> dataAll = new ArrayList<>();
        dataAll.add(t1);
        dataAll.add(t2);

        //Box Plot
        Stage boxPlotStage = new Stage();
        boxPlotStage.setTitle("BoxPlot " + dataId);
        boxPlotStage.setScene(new Scene(boxPlot(dataAll, 250, 200, 25, 25), 300, 250, Color.WHITE));


        boxPlotStage.initModality(Modality.WINDOW_MODAL);
        boxPlotStage.initOwner(menuBar.getScene().getWindow());

        boxPlotStage.showAndWait();
    }

    //Menu Data
    @FXML private void rawData(ActionEvent event){
        String selTab = tabPane.getSelectionModel().getSelectedItem().getText();
        if(selTab.equals("Protein")){
            //set back to non-normalization
            menuNonnormalization.setSelected(true);
            sampleGroup.setProteinIntegrationType(PublicInfo.ProteinIntegrationType.Raw);
            sampleGroup.setProteinStatus(PublicInfo.ProteinStatus.Unnormalized);
        } else {
            System.out.println("Wrong Selection: " + selTab);
        }
        sampleGroup.updateProtein();
        sampleProteinDataController.show();
    }

    @FXML private void iBAQ(ActionEvent event){
        String selTab = tabPane.getSelectionModel().getSelectedItem().getText();
        if(selTab.equals("Protein")){
            menuNonnormalization.setSelected(true);
            sampleGroup.setProteinIntegrationType(PublicInfo.ProteinIntegrationType.iBAQ);
            sampleGroup.setProteinStatus(PublicInfo.ProteinStatus.Unnormalized);
        } else {
            System.out.println("Wrong Selection: " + selTab);
        }
        sampleGroup.updateProtein();
        sampleProteinDataController.show();
    }


    @FXML private void nonNormalization(ActionEvent event){
        String selTab = tabPane.getSelectionModel().getSelectedItem().getText();
        if(selTab.equals("Protein")){
            sampleGroup.setProteinStatus(PublicInfo.ProteinStatus.Unnormalized);
        } else {
            System.out.println("Wrong Selection: " + selTab);
        }
        sampleGroup.updateProtein();
        sampleProteinDataController.show();
    }

    @FXML private void medianNormalization(ActionEvent event){
        String selTab = tabPane.getSelectionModel().getSelectedItem().getText();
        if(selTab.equals("Protein")){
            sampleGroup.setProteinStatus(PublicInfo.ProteinStatus.Median);
        } else {
            System.out.println("Wrong Selection: " + selTab);
        }
        sampleGroup.updateProtein();
        sampleProteinDataController.show();
    }

    //Menu View
    @FXML private void scaleRegular(ActionEvent event){
        String selTab = tabPane.getSelectionModel().getSelectedItem().getText();
        switch (selTab){
            case "Proteomics":
                System.err.println("Wrong Selection: " + selTab);
                break;
            case "Peptide":
                samplePepDataController.setScaleType(PublicInfo.ScaleType.Regular);
                break;
            case "Protein":
                sampleProteinDataController.setScaleType(PublicInfo.ScaleType.Regular);
                break;
            case "Browser":
                System.err.println("Wrong Selection: " + selTab);
                break;
            default:
                System.err.println("Cannot find " + selTab);
                break;
        }
    }

    @FXML private void scaleLog2(ActionEvent event){
        String selTab = tabPane.getSelectionModel().getSelectedItem().getText();
        switch (selTab){
            case "Proteomics":
                System.err.println("Wrong Selection: " + selTab);
                break;
            case "Peptide":
                samplePepDataController.setScaleType(PublicInfo.ScaleType.Log2);
                break;
            case "Protein":
                sampleProteinDataController.setScaleType(PublicInfo.ScaleType.Log2);
                break;
            case "Browser":
                System.err.println("Wrong Selection: " + selTab);
                break;
            default:
                System.err.println("Cannot find " + selTab);
                break;
        }
    }

    @FXML private void scaleLog10(ActionEvent event){
        String selTab = tabPane.getSelectionModel().getSelectedItem().getText();
        switch (selTab){
            case "Proteomics":
                System.err.println("Wrong Selection: " + selTab);
                break;
            case "Peptide":
                samplePepDataController.setScaleType(PublicInfo.ScaleType.Log10);
                break;
            case "Protein":
                sampleProteinDataController.setScaleType(PublicInfo.ScaleType.Log10);
                break;
            case "Browser":
                System.err.println("Wrong Selection: " + selTab);
                break;
            default:
                System.err.println("Cannot find " + selTab);
                break;
        }
    }

    @FXML private  void exportModificationInfo(ActionEvent event){
        System.out.println("Export Modification Information");
        Protein protein = pBrowserController.getProtein();
        if(protein==null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modification Export");
            alert.setHeaderText(null);
            alert.setContentText("Please select Proteins in Browser");
            alert.showAndWait();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/parameter/ParaExportModificationInfo.fxml"));
        try {
            Parent root = loader.load();

            Stage exportModiStage = new Stage();
            exportModiStage.setTitle("Export Modification Information");
            exportModiStage.setScene(new Scene(root, 400, 250));
            exportModiStage.initModality(Modality.WINDOW_MODAL);
            exportModiStage.initOwner(menuBar.getScene().getWindow());

            ParaExportModificationInfoController controller = loader.getController();
            controller.setProtein(protein);

            exportModiStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void exportModifiedCysText(ActionEvent event){
        System.out.println("Export modified Cys Info Text");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/parameter/ParaModifiedCys.fxml"));
        try{
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Export modified Cys information in text file");
            stage.setScene(new Scene(root, 600, 400));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuBar.getScene().getWindow());

            ParaModifiedCysController controller = loader.getController();
            controller.init(sampleGroup);

            stage.showAndWait();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML private void showModifiedCysFigure(ActionEvent event){
        System.out.println("Show modified Cys Info Figure");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/parameter/ParaModifiedCysFigure.fxml"));
        ArrayList<Map<Character, Double>> residualFreq = null;
        try{
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Parameter for Modified Cys information Figure");
            stage.setScene(new Scene(root, 600, 300));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuBar.getScene().getWindow());

            ParaModifiedCysFigureController controller = loader.getController();
            controller.init(sampleGroup);
            stage.showAndWait();

            residualFreq = controller.getResidualFreq();
        } catch (IOException e){
            e.printStackTrace();
        }

        if(residualFreq == null){
            return;
        }

        FXMLLoader figLoader = new FXMLLoader(getClass().getResource("/view/result/CysResidualFigure.fxml"));
        try{
            Parent root = figLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Cys Residual Frequency");
            stage.setScene(new Scene(root, 600, 400));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuBar.getScene().getWindow());

            CysResidualFigureController controller = figLoader.getController();
            controller.init(residualFreq);
            stage.showAndWait();


        } catch (IOException e){
            e.printStackTrace();
        }


    }


    @FXML private void about(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ProteomicsBrowser");
        alert.setHeaderText(null);
        alert.setContentText("ProteomicsBrowser: Alpha \n Gang Peng, Yishuo Tang \n");
        alert.showAndWait();
        return;
    }

    @FXML private void pepFilter(ActionEvent event){
        System.out.println("pepFilter");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/filter/PepFilter.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage pepFilterStage = new Stage();
        pepFilterStage.setTitle("Peptide Filter");
        pepFilterStage.setScene(new Scene(root, 600, 400));

        pepFilterStage.initModality(Modality.WINDOW_MODAL);
        pepFilterStage.initOwner(menuBar.getScene().getWindow());

        PepFilterController controller = loader.getController();
        controller.init(pBrowserController.getProtein());

        pepFilterStage.showAndWait();

        pBrowserController.getProtein().updateShow();
        pBrowserController.updatePep();
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
                        menuData.setVisible(false);
                        menuView.setVisible(false);
                        menuExport.setVisible(false);
                        menuDataClean.setVisible(false);
                        treeView.getSelectionModel().select(2);
                        break;
                    case "Peptide":
                        menuAnalyze.setVisible(true);
                        menuData.setVisible(false);
                        menuView.setVisible(true);
                        menuExport.setVisible(false);
                        menuDataClean.setVisible(false);
                        treeView.getSelectionModel().select(3);
                        break;
                    case "Protein":
                        menuAnalyze.setVisible(true);
                        menuData.setVisible(true);
                        menuView.setVisible(true);
                        menuExport.setVisible(true);
                        menuExportModificationInfo.setDisable(true);
                        menuExportModiCys.setDisable(false);
                        menuDataClean.setVisible(false);
                        treeView.getSelectionModel().select(4);
                        break;
                    case "Browser":
                        menuAnalyze.setVisible(false);
                        menuData.setVisible(false);
                        menuView.setVisible(false);
                        menuExport.setVisible(true);
                        menuExportModificationInfo.setDisable(false);
                        menuExportModiCys.setDisable(true);
                        menuDataClean.setVisible(true);
                        treeView.getSelectionModel().select(5);
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
                //System.out.println(treeView.getSelectionModel().getSelectedIndex());
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;

                String selectedValue = selectedItem.getValue();

                switch (selectedValue){
                    case "Proteomics Data":
                        System.out.println("Select Proteomics Data Tab");
                        selectionModel.select(tabProteomicsData);
                        menuAnalyze.setVisible(false);
                        menuData.setVisible(false);
                        menuView.setVisible(false);
                        menuExport.setVisible(false);
                        menuDataClean.setVisible(false);
                        break;

                    case "Peptide Data":
                        System.out.println("Select Peptide Data");
                        if(tabSamplePepData == null){
                            initSamplePepDataTab();
                            tabPane.getTabs().add(tabSamplePepData);
                        }
                        selectionModel.select(tabSamplePepData);
                        menuAnalyze.setVisible(true);
                        menuData.setVisible(false);
                        menuView.setVisible(true);
                        menuExport.setVisible(false);
                        menuDataClean.setVisible(false);

                        switch (samplePepDataController.getScaleType()){
                            case Regular:
                                menuScaleRegular.setSelected(true);
                                break;
                            case Log2:
                                menuScaleLog2.setSelected(true);
                                break;
                            case Log10:
                                menuScaleLog10.setSelected(true);
                                break;
                            default:
                                System.out.println("Wrong Type!");
                                break;
                        }

                        break;
                    case "Protein Data":
                        System.out.println("Select Protein Data");
                        if(tabSampleProteinData == null){
                            initSampleProteinDataTab();
                            tabPane.getTabs().add(tabSampleProteinData);
                        }
                        selectionModel.select(tabSampleProteinData);
                        menuAnalyze.setVisible(true);
                        menuData.setVisible(true);
                        menuView.setVisible(true);
                        menuExport.setVisible(true);
                        menuExportModiCys.setDisable(false);
                        menuExportModificationInfo.setDisable(true);
                        menuDataClean.setVisible(false);

                        switch (sampleProteinDataController.getScaleType()){
                            case Regular:
                                menuScaleRegular.setSelected(true);
                                break;
                            case Log2:
                                menuScaleLog2.setSelected(true);
                                break;
                            case Log10:
                                menuScaleLog10.setSelected(true);
                                break;
                            default:
                                System.out.println("Wrong Type!");
                                break;
                        }


                        break;
                    case "Browser":
                        System.out.println("Select Browser");
                        if(tabBrowser == null){
                            initBrowserTab();
                            tabPane.getTabs().add(tabBrowser);
                        }
                        sampleGroup.setAbundanceRange();
                        selectionModel.select(tabBrowser);
                        menuAnalyze.setVisible(false);
                        menuData.setVisible(false);
                        menuView.setVisible(false);
                        menuExport.setVisible(true);
                        menuExportModiCys.setDisable(true);
                        menuExportModificationInfo.setDisable(false);
                        menuDataClean.setVisible(true);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/data/ProteomicsData.fxml"));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/data/SamplePepData.fxml"));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/data/SampleProteinData.fxml"));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/browser/PBrowser.fxml"));
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
        proteinDB = new ProteinDB();
        proteinDB.setSpecies(type);
        //proteinSeq = new TreeMap<>();
        String proteinDBFile = "db/" + type + ".txt";
        File dbFile = new File(proteinDBFile);
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(dbFile));

            String fline;
            while((fline = bufferedReader.readLine()) != null){
                String[] vslines = fline.split("\t");
                //proteinSeq.put(vslines[0], vslines[1]);
                if(vslines.length != 3){
                    System.out.println(fline);
                    for(int i =0; i< vslines.length;i++){
                        System.out.println(vslines[i]);
                    }
                }
                proteinDB.add(vslines[1], vslines[0], vslines[2]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
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
        */
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

            bufferedReader.close();

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

        Set<String> proteinNotFind = new TreeSet<>();
        for(int i=0; i<proteomicsRaw.get(0).size(); i++){
            String id = proteomicsRaw.get(indexId).get(i);
            String sequence = proteomicsRaw.get(indexSequence).get(i);
            Integer charge = tryIntegerParse(proteomicsRaw.get(indexCharge).get(i));
            Double mz = tryDoubleParse(proteomicsRaw.get(indexMz).get(i));
            Double score = tryDoubleParse(proteomicsRaw.get(indexScore).get(i));
            String protein = proteomicsRaw.get(indexProtein).get(i);
            String modificationStr = proteomicsRaw.get(indexModification).get(i);
            ArrayList<Modification> modifications = new ArrayList<>();

            String seq = proteinDB.getSeq(protein);
            if(seq == null){
                proteinNotFind.add(protein);
                continue;
            }

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
                sampleGroup.addPeptide(spId, protein, seq, ptTmp);
                sampleGroup.increaseProteinData(spId, protein, abTmp);
            }
        }

        if(proteinNotFind.size() > 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Import Data");
            alert.setHeaderText(null);
            String msg = "The following proteins are not found in database:\n";
            for(String s : proteinNotFind){
                msg = msg + s + ",";
            }
            alert.setContentText(msg);
            alert.showAndWait();
            return;
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

        //set raw abundance

        sampleGroup.initPepCutoff();
        sampleGroup.updatePepShow();
        sampleGroup.rawAbundance();

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

    private Group boxPlot(ArrayList<ArrayList<Double>> dataAll, double width, double height, double top, double left){
        int numGroup = dataAll.size();

        Group root = new Group();

        double cellWidth = width / numGroup;

        ArrayList<Double> maxEach = new ArrayList<>();
        ArrayList<Double> minEach = new ArrayList<>();
        for(int i =0; i < numGroup; i++){
            maxEach.add(Collections.max(dataAll.get(i)));
            minEach.add(Collections.min(dataAll.get(i)));
        }

        double maxAll = Collections.max(maxEach);
        double minAll = Collections.min(minEach);

        double range = maxAll - minAll;

        double unit = height/range;

        double cellLeft = cellWidth/8;
        double cellRight = cellWidth/8;
        if(cellLeft < 2){
            cellLeft = 2;
        }

        if(cellRight < 2){
            cellRight = 2;
        }

        double paintWidth = cellWidth- cellLeft - cellRight;
        double widthFence = paintWidth/3;


        for(int i= 0; i < numGroup; i++){
            ArrayList<Double> data = dataAll.get(i);
            Collections.sort(data);

            int idx = data.size()/4;
            double q1 = data.get(idx);
            double q2 = data.get(idx*2);
            double q3 = data.get(idx*3);
            double IQR = q3 - q1;
            double innerFenceLow = q1 - IQR * 1.58;
            double innerFenceHigh = q3 + IQR * 1.58;
            double min = data.get(0);
            double max = data.get(data.size()-1);

            if(innerFenceHigh > max){
                innerFenceHigh = max;
            }

            if(innerFenceLow < min){
                innerFenceLow = min;
            }

            double st = left + i*cellWidth + cellLeft;

            Rectangle r = new Rectangle();
            r.setX(st);
            r.setY(transferY(q3, top, height, minAll, maxAll));
            r.setWidth(paintWidth);
            r.setHeight(unit*IQR);
            r.setFill(Color.TRANSPARENT);
            r.setStroke(Color.BLACK);


            Line md = new Line();
            md.setStartX(st);
            md.setStartY(transferY(q2, top, height, minAll, maxAll));
            md.setEndX(st+paintWidth);
            md.setEndY(transferY(q2, top, height, minAll, maxAll));

            Line h1 = new Line();
            h1.setStartX(st + (paintWidth-widthFence)/2);
            h1.setStartY(transferY(innerFenceLow, top, height, minAll, maxAll));
            h1.setEndX(st + (paintWidth+widthFence)/2);
            h1.setEndY(transferY(innerFenceLow, top, height, minAll, maxAll));

            Line h2 = new Line();
            h2.setStartX(st + (paintWidth-widthFence)/2);
            h2.setStartY(transferY(innerFenceHigh, top, height, minAll, maxAll));
            h2.setEndX(st + (paintWidth+widthFence)/2);
            h2.setEndY(transferY(innerFenceHigh, top, height, minAll, maxAll));

            Line v1 = new Line();
            v1.setStartX(st + paintWidth/2);
            v1.setStartY(transferY(innerFenceLow, top, height, minAll, maxAll));
            v1.setEndX(st + paintWidth/2);
            v1.setEndY(transferY(q1, top, height, minAll, maxAll));

            Line v2 = new Line();
            v2.setStartX(st + paintWidth/2);
            v2.setStartY(transferY(innerFenceHigh, top, height, minAll, maxAll));
            v2.setEndX(st + paintWidth/2);
            v2.setEndY(transferY(q3, top, height, minAll, maxAll));

            root.getChildren().addAll(r,md, h1, h2, v1, v2);

            for(int j=data.size()/4;j>=0;j--){
                if(data.get(j) < innerFenceLow){
                    Circle pt = new Circle();
                    pt.setCenterX(st + paintWidth/2);
                    pt.setCenterY(transferY(data.get(j), top, height, minAll, maxAll));
                    pt.setRadius(1);

                    root.getChildren().add(pt);
                }
            }

            for(int j=data.size()*3/4;j< data.size();j++){
                if(data.get(j) > innerFenceHigh){
                    Circle pt = new Circle();
                    pt.setCenterX(st + paintWidth/2);
                    pt.setCenterY(transferY(data.get(j), top, height, minAll, maxAll));
                    pt.setRadius(1);

                    root.getChildren().add(pt);
                }
            }
        }
        return root;
    }

    private double transferY(double y, double top, double height, double min, double max){
        return (top + (max-y)*height/(max-min));
    }

}

