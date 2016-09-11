package org.bs.jnonogram.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

public class JnonogramGui extends Application {

    public JnonogramGui() {
        threadPool = Executors.newFixedThreadPool(10);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private final ExecutorService threadPool;

    public void scheduleTask(Runnable task) {
        threadPool.submit(task);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("Jnonogram");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.getScene().getStylesheets().add("themes/default.css");
        primaryStage.show();
    }

    public Preferences getPreferences() {
        return Preferences.userNodeForPackage(JnonogramGui.class);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        threadPool.shutdown();
    }

    private static JnonogramGui instance;

    public static JnonogramGui getInstance() {
        return instance;
    }
}
