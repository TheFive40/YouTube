package com.example.youtubeadmin;

import com.example.youtubeadmin.Model.Sentiment;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Reactions implements Initializable {
    @FXML
    public HBox contentMain;
    @FXML
    private PieChart pieChart;
    @FXML
    private TextField videoId;
    @FXML
    private Button btnSubmit;

    private static String videoID;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Sentiment sentiment = new Sentiment();
        sentiment.setOnSucceeded(ex -> {
            for (Map.Entry<String, Integer> entry : Sentiment.getSentimentCounts().entrySet()) {
                PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
                pieChart.getData().add(slice);
                pieChart.setTitle("Sentiment Analysis");
            }
        });
        ExecutorService executorService = Executors.newCachedThreadPool();
        btnSubmit.setOnAction(e->{
            System.out.println("Se invoco el evento");
            videoID = videoId.getText();
            executorService.submit(sentiment);
        });
    }

    public static String getVideoID() {
        return videoID;
    }
}
