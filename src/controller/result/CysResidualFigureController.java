package controller.result;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by gpeng on 9/12/17.
 */
public class CysResidualFigureController {
    @FXML private ComboBox<Character> cmbColor;
    @FXML private ColorPicker colorPicker;
    @FXML private HBox hBox;

    private double width = 600.0;
    private double height = 300.0;
    private double hCutoff = 0.001;

    private double hSpacing = 2.0;
    private double vSpacing = 1.0;

    private ArrayList<Map<Character, Double>> residualFreq;

    @FXML private void output(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Modified Cys");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Output Figure", "*.png")
        );

        Stage stage = (Stage) hBox.getScene().getWindow();
        File figureFile = fileChooser.showSaveDialog(stage);
        if(figureFile==null){
            return;
        }


        //WritableImage image = hBox.snapshot(new SnapshotParameters(),null);
        double pixelScale = 3.0;
        WritableImage image = new WritableImage((int)(hBox.getWidth()*pixelScale), (int)(hBox.getHeight()*pixelScale));
        SnapshotParameters sp = new SnapshotParameters();
        sp.setTransform(Transform.scale(pixelScale, pixelScale));
        image = hBox.snapshot(sp, image);

        try{
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", figureFile);
        } catch (IOException e){
            e.printStackTrace();
        }

        //stage.close();
    }

    @FXML private void setColor(ActionEvent event){
        Character charSel = cmbColor.getSelectionModel().getSelectedItem().charValue();
        for(Node node : hBox.getChildren()){
            for(Node n : Pane.class.cast(node).getChildren()){
                Text text = Text.class.cast(n);
                if((text.getText().charAt(0)) == charSel){
                    text.setFill(colorPicker.getValue());
                }
            }
        }
    }

    public void init(ArrayList<Map<Character, Double>> residualFreq){
        hBox.setPrefWidth(width);
        hBox.setPrefHeight(height);
        hBox.setSpacing(hSpacing);
        this.residualFreq = residualFreq;

        ArrayList<Color> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.BLACK);
        colors.add(Color.ORANGE);
        colors.add(Color.CYAN);
        colors.add(Color.PURPLE);

        double paneWidth = (width - (residualFreq.size() - 1) * hSpacing) / residualFreq.size();
        TreeSet<Character> aminoAcidAll = new TreeSet<>();
        //ArrayList<Text> textAll = new ArrayList<>();
        for(Map<Character, Double> rf : residualFreq){
            int numChar = 0;
            for(Map.Entry<Character, Double> entry : rf.entrySet()){
                if(entry.getValue() < hCutoff){
                    break;
                }
                numChar++;
            }

            Pane pane = new Pane();
            pane.setPrefWidth(paneWidth);
            pane.setMinWidth(paneWidth);
            pane.setMaxWidth(paneWidth);
            pane.setPrefHeight(height);
            pane.setMaxHeight(height);
            pane.setMinHeight(height);


            double drawHeight = height - (numChar + 1) *vSpacing;

            int idxRow = 0;
            double layoutY = 0;
            for(Map.Entry<Character, Double> entry : rf.entrySet()){
                if(idxRow==numChar){
                    break;
                }
                idxRow++;
                aminoAcidAll.add(entry.getKey());

                Text text = new Text(String.valueOf(entry.getKey()));
                text.setTextAlignment(TextAlignment.CENTER);
                text.setTextOrigin(VPos.TOP);
                text.setBoundsType(TextBoundsType.VISUAL);
                text.setFont(Font.font("Consolas", 48));
                double fontHeight = text.getBoundsInLocal().getHeight();
                double fontWidth = text.getBoundsInLocal().getWidth();
                double realHeight = drawHeight * entry.getValue();

                double scaleX = paneWidth/fontWidth;
                double scaleY = realHeight/fontHeight;

                text.setScaleX(scaleX);
                text.setScaleY(scaleY);
                text.setLayoutX(0);

                if(layoutY == 0){
                    //layoutY = text.getLayoutY() + (scaleY-1.0)*fontHeight/2;
                    layoutY = vSpacing + fontHeight*(scaleY-1)/2.0;
                    text.setLayoutY(layoutY);
                    layoutY = fontHeight*scaleY + 2*vSpacing;
                } else {
                    double layoutYTmp = layoutY + fontHeight * (scaleY-1)/2.0;
                    text.setLayoutY(layoutYTmp);
                    layoutY = layoutY + fontHeight*scaleY + vSpacing;
                }


                pane.getChildren().add(text);
            }
            hBox.getChildren().add(pane);
        }

        ArrayList<Character> aaCopy = new ArrayList<>(aminoAcidAll);
        for(Node node : hBox.getChildren()){
            for(Node n : Pane.class.cast(node).getChildren()){
                Text text = Text.class.cast(n);
                int idxTmp = aaCopy.indexOf(text.getText().charAt(0)) % colors.size();
                text.setFill(colors.get(idxTmp));
            }
        }

        cmbColor.getItems().addAll(aaCopy);
        cmbColor.getSelectionModel().select(0);
        int idxTmp = aaCopy.indexOf(aaCopy.get(0)) % colors.size();
        colorPicker.setValue(colors.get(idxTmp));

        /*
        hBox.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                height = newValue.doubleValue();
                for(int i=0; i<hBox.getChildren().size(); i++){
                    Pane pane = Pane.class.cast(hBox.getChildren().get(i));
                    double drawHeight = height - (pane.getChildren().size() + 1) *vSpacing;
                    double layoutY = 0;
                    for(int j=0; j<pane.getChildren().size(); j++){
                        Text text = Text.class.cast(pane.getChildren().get(j));
                        Character charTmp = text.getText().charAt(0);
                        double fontHeight = text.getBoundsInLocal().getHeight();
                        double realHeight = drawHeight * (residualFreq.get(i).get(charTmp)).doubleValue();

                        double scaleY = realHeight/fontHeight;
                        text.setScaleY(scaleY);
                        if(layoutY == 0){
                            //layoutY = text.getLayoutY() + (scaleY-1.0)*fontHeight/2;
                            layoutY = vSpacing + fontHeight*(scaleY-1)/2.0;
                            text.setLayoutY(layoutY);
                            layoutY = fontHeight*scaleY + 2*vSpacing;
                        } else {
                            double layoutYTmp = layoutY + fontHeight * (scaleY-1)/2.0;
                            text.setLayoutY(layoutYTmp);
                            layoutY = layoutY + fontHeight*scaleY + vSpacing;
                        }
                    }
                }
            }
        });

        hBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                width = newValue.doubleValue();
                double paneWidth = (width - (residualFreq.size() - 1) * hSpacing) / residualFreq.size();
                for(int i=0; i<hBox.getChildren().size(); i++){
                    Pane pane = Pane.class.cast(hBox.getChildren().get(i));
                    pane.setPrefWidth(paneWidth);
                    double drawHeight = height - (pane.getChildren().size() + 1) *vSpacing;
                    double layoutY = 0;
                    for(int j=0; j<pane.getChildren().size(); j++){
                        Text text = Text.class.cast(pane.getChildren().get(j));
                        double fontWidth = text.getBoundsInLocal().getWidth();
                        double scaleX = paneWidth/fontWidth;

                        text.setScaleX(scaleX);
                        text.setLayoutX(0);
                    }
                }
            }
        });
        */
    }


}
