package com.example.youtubeadmin;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
public class HelloApplication extends Application {
    //private static final String API_KEY = "AIzaSyAY7MRIY2o6lsvLA-9Cb6hufm3en-L_cUQ";
    public static FXMLLoader fxmlLoader;
    @Override
    public void start(Stage stage) throws IOException {
        fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        HelloController helloController = fxmlLoader.getController();
        scene.getStylesheets().add("styles.css");
        stage.getIcons().add(new Image("icon.png"));
        stage.setTitle("YouTube TrendMapper");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    public static FXMLLoader getLoader(){
        return fxmlLoader;
    }
    public static void main(String[] args) {
        launch();
    }
}