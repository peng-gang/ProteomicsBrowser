package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println("Proteomics Start!");
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
        primaryStage.setTitle("ProteomicsBrowser");
        primaryStage.setScene(new Scene(root, 1600, 1200));

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Sure to exit?");
            }
        });

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        //System.out.println(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
        System.out.println("Proteomics Init!");
        super.init();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Proteomics end!");
        super.stop();
    }
}
