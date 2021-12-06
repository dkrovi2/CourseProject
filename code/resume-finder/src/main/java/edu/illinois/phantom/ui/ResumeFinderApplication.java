package edu.illinois.phantom.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ResumeFinderApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ResumeFinderApplication.class.getResource("Resume_Finder.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 600);

        stage.setTitle("Resume Finder");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}