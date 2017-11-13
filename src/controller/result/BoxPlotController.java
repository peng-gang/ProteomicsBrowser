package controller.result;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by gpeng on 11/2/17.
 */
public class BoxPlotController {
    @FXML private AnchorPane pane;

    private ArrayList<ArrayList<Double> > dataAll;
    private ArrayList<String> name;
    private  double width;
    private double height;
    private double top;
    private double left;

    @FXML private void save(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Output Boxplot");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Output Figure", "*.png")
        );

        Stage stage = (Stage) pane.getScene().getWindow();
        File figureFile = fileChooser.showSaveDialog(stage);

        double pixelScale = 3.0;
        WritableImage image = new WritableImage((int)(pane.getWidth()*pixelScale), (int)(pane.getHeight()*pixelScale));
        SnapshotParameters sp = new SnapshotParameters();
        sp.setTransform(Transform.scale(pixelScale, pixelScale));
        image = pane.snapshot(sp, image);

        try{
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", figureFile);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @FXML private void exit(ActionEvent event){
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }


    public void init(ArrayList<ArrayList<Double> > dataAll, ArrayList<String> name){
        this.dataAll = dataAll;
        this.name = name;
        width = 250;
        height = 200;
        top = 25;
        left = 25;

        pane.getChildren().add(boxPlot());
    }

    public void init(ArrayList<ArrayList<Double> > dataAll, ArrayList<String> name, double width, double height, double top, double left){
        this.dataAll = dataAll;
        this.name = name;
        this.width = width;
        this.height = height;
        this.top = top;
        this.left = left;

        pane.getChildren().add(boxPlot());
    }

    private Group boxPlot(){
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

            Text t = new Text(st + paintWidth/4, height + 50, name.get(i));
            t.setTextAlignment(TextAlignment.CENTER);

            root.getChildren().addAll(t);
        }
        return root;
    }

    private double transferY(double y, double top, double height, double min, double max){
        return (top + (max-y)*height/(max-min));
    }
}
