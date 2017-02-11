package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    @FXML
    private MenuBar menuBar;

    @FXML
    private TreeView treeView;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabProteomicsData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("MainController Initialization");
        //initTreeView();
        //try {
        //    tabRawData.setContent(FXMLLoader.load(getClass().getResource("../view/data/ProteomicsData.fxml")));
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
    }

    //Initialization functions
    private void initTreeView(){
        TreeItem<String> root = new TreeItem<>("Project");

        //Data
        TreeItem<String> data = new TreeItem<>("Data");
        TreeItem<String> proteomicsData = new TreeItem<>("Proteomics Data");
        TreeItem<String> sampleData = new TreeItem<>("Samples");
        data.getChildren().addAll(proteomicsData, sampleData);

        //Browser
        TreeItem<String> bw = new TreeItem<>("Browser");


        root.getChildren().addAll(data, bw);

        treeView.setRoot(root);
    }

}

