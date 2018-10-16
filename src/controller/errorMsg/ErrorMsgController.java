package controller.errorMsg;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.PublicInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by gpeng on 4/27/18.
 */
public class ErrorMsgController {
    @FXML private Label lblMsg;

    private PublicInfo.ErrorMsgType type;
    private ArrayList<String> proteinNotFind = null;
    private ArrayList<String> pepNotMatch = null;
    private ArrayList<String> proteinNotMatch = null;
    private Map<String, ArrayList<String>> pepInMultipleProtein = null;

    public void setType(PublicInfo.ErrorMsgType type) {this.type = type;}
    public void setProteinNotFind(ArrayList<String> proteinNotFind) {this.proteinNotFind = proteinNotFind; }
    public void setPepNotMatch(ArrayList<String> pepNotMatch) {this.pepNotMatch = pepNotMatch;}
    public void setProteinNotMatch(ArrayList<String> proteinNotMatch) {this.proteinNotMatch = proteinNotMatch; }
    public void setPepInMultipleProtein(Map<String, ArrayList<String>> pepInMultipleProtein) {this.pepInMultipleProtein = pepInMultipleProtein;}

    public void init(boolean includePepMultiProtein){
        String msg;
        switch (type){
            case ProteinNotFound:
                if(proteinNotFind == null){
                    lblMsg.setText("All proteins are found in database.");
                    break;
                }

                if(proteinNotFind.size()==0){
                    lblMsg.setText("All proteins are found in database.");
                    break;
                }

                msg = "The following proteins that were not found in the database have been removed from the analyses.\n\n";
                msg += proteinNotFind.get(0);

                for(int i=1; i<proteinNotFind.size(); i++){
                    msg += "," + proteinNotFind.get(i);
                }
                lblMsg.setText(msg);
                break;
            case PepNotMatch:
                if(pepNotMatch == null || pepNotMatch.size()==0){
                    lblMsg.setText("All peptides can be matched to proteins.");
                    break;
                }
                msg = "The following peptide sequence that was not found in the specified protein has been removed from the analysis.\n\n(sequence\tprotein)\n";
                for(int k = 0; k<pepNotMatch.size(); k++){
                    msg = msg + pepNotMatch.get(k) + "\t" + proteinNotMatch.get(k) + "\n";
                }
                lblMsg.setText(msg);
                break;
            case PepInMultipleProtein:
                if(pepInMultipleProtein==null || pepInMultipleProtein.size() == 0){
                    lblMsg.setText("No peptides can be mapped to more than one protein.");
                    break;
                }

                if(includePepMultiProtein){
                    msg = "The following peptides (" + pepInMultipleProtein.size() + ") that mapped to multiple proteins are INCLUDED in the ProteomicsBrowser.\n\n(peptide id\tproteins)\n";

                } else{
                    msg = "The following peptides (" + pepInMultipleProtein.size() + ") that mapped to multiple proteins have been REMOVED from the ProteomicsBrowser.\n\n(peptide id\tproteins)\n";

                }
                for(Map.Entry<String, ArrayList<String>> entry : pepInMultipleProtein.entrySet()){
                    msg += entry.getKey() + "\t";
                    for(String strTmp : entry.getValue()){
                        msg += strTmp + " ";
                    }
                    msg += "\n";
                }
                lblMsg.setText(msg);
                break;
        }
    }

    @FXML private void ok(ActionEvent event){
        Stage stage = (Stage) lblMsg.getScene().getWindow();
        stage.close();
    }

    @FXML private void export(ActionEvent event){
        FileChooser fileChooser;
        Stage stage;
        File file;
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        switch (type){
            case ProteinNotFound:
                fileChooser = new FileChooser();
                fileChooser.setTitle("Save Message");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Proteins not found in database", "*.txt")
                );

                stage = (Stage) lblMsg.getScene().getWindow();
                file = fileChooser.showSaveDialog(stage);

                if(file == null){
                    return;
                }


                fileWriter = null;
                try {
                    fileWriter = new FileWriter(file);
                } catch (IOException e){
                    e.printStackTrace();
                }

                bufferedWriter = new BufferedWriter(fileWriter);

                try {
                    for(String s : proteinNotFind){
                        bufferedWriter.write(s + "\n");
                    }
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    try {
                        bufferedWriter.close();
                        fileWriter.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;
            case PepNotMatch:
                fileChooser = new FileChooser();
                fileChooser.setTitle("Save Message");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Peptide sequence do not match protein", "*.csv")
                );

                stage = (Stage) lblMsg.getScene().getWindow();
                file = fileChooser.showSaveDialog(stage);

                if(file == null){
                    return;
                }


                fileWriter = null;
                try {
                    fileWriter = new FileWriter(file);
                } catch (IOException e){
                    e.printStackTrace();
                }

                bufferedWriter = new BufferedWriter(fileWriter);

                try {
                    for(int k = 0; k<pepNotMatch.size(); k++){
                        bufferedWriter.write(pepNotMatch.get(k) + "," + proteinNotMatch.get(k) + "\n");
                    }
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    try {
                        bufferedWriter.close();
                        fileWriter.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;
            case PepInMultipleProtein:
                fileChooser = new FileChooser();
                fileChooser.setTitle("Save Message");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Peptide sequence matches multiple proteins", "*.csv")
                );

                stage = (Stage) lblMsg.getScene().getWindow();
                file = fileChooser.showSaveDialog(stage);

                if(file == null){
                    return;
                }


                fileWriter = null;
                try {
                    fileWriter = new FileWriter(file);
                } catch (IOException e){
                    e.printStackTrace();
                }

                bufferedWriter = new BufferedWriter(fileWriter);

                try {

                    for(Map.Entry<String, ArrayList<String>> entry : pepInMultipleProtein.entrySet()){
                        String fline = entry.getKey() + ",";
                        for(String strTmp : entry.getValue()){
                            fline += strTmp + " ";
                        }
                        fline += "\n";
                        bufferedWriter.write(fline);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    try {
                        bufferedWriter.close();
                        fileWriter.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


}
