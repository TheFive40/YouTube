package com.example.youtubeadmin;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoCategoryListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Analytics implements Initializable {
    @FXML
    public HBox contentMain;
    @FXML
    private BarChart<String, Number> barChart;
    private HashMap<String, Double> popularCategories = new HashMap<>();
    @FXML
    private Label lblTime, lblComments, lblTags, lblTransmissions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HelloController.getWebView().getEngine().load(null);

        try {
            mostPopularCategories(barChart);
            lblTime.setOnMouseClicked(e -> {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AnalyticsTime.fxml"));
                try {
                    Stage stage = new Stage();
                    Parent parent = fxmlLoader.load();
                    Scene scene = new Scene(parent);
                    stage.setScene(scene);
                    stage.setTitle("Analytics Time");
                    stage.setResizable(false);
                    stage.show();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            lblComments.setOnMouseClicked(e->{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Comments.fxml"));
                Stage stage = new Stage();
                Parent parent = null;
                try {
                    parent = fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Scene scene = new Scene(parent);
                stage.setTitle("Comments");
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();


            });
            lblTags.setOnMouseClicked(e->{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MostTagsUsed.fxml"));
                Stage stage = new Stage();
                Parent parent = null;
                try {
                    parent = fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Scene scene = new Scene(parent);
                stage.setTitle("Tags");
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
            });
            lblTransmissions.setOnMouseClicked(e->{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Transmissions.fxml"));
                Stage stage = new Stage();
                Parent parent = null;
                try {
                    parent = fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Transmissions transmissions = fxmlLoader.getController();
                transmissions.btnPrinter(stage);
                transmissions.btnSaveListener(stage);
                Scene scene = new Scene(parent);
                stage.setTitle("Transmissions");
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
                Transmissions.load();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void mostPopularCategories(BarChart<String, Number> barChart) throws IOException {
        YouTube.Videos.List video = HelloController.getService().videos().list("snippet,id,statistics");

        video.setMaxResults(20L);

        video.setChart("mostPopular");

        String regionCode = HelloController.getRegionCode().equalsIgnoreCase("None") ? "CO" : HelloController.getRegionCode();

        video.setRegionCode(regionCode);

        VideoListResponse response = video.execute();

        fetchAndPopulateCategories(response);

        CategoryAxis xAxis = new CategoryAxis();

        NumberAxis yAxis = new NumberAxis();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<Map.Entry<String, Double>> sortedCategories = popularCategories.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(4) // Obtén las 5 categorías más populares
                .collect(Collectors.toList());

        for (Map.Entry<String, Double> entry : sortedCategories) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        this.barChart.getData().add(series);
    }

    private void fetchAndPopulateCategories(VideoListResponse videoListResponse) throws IOException {
        YouTube.VideoCategories.List videoCategoriesRequest = HelloController.getService().videoCategories().list("snippet");
        for (Video video : videoListResponse.getItems()) {

            String categoryId = video.getSnippet().getCategoryId();

            videoCategoriesRequest.setId(categoryId);

            VideoCategoryListResponse categoryResponse = videoCategoriesRequest.execute();

            if (popularCategories.containsKey(categoryResponse.getItems().getFirst().getSnippet().getTitle())) {
                popularCategories.put(categoryResponse.getItems().getFirst().getSnippet().getTitle(),
                        +video.getStatistics().getViewCount().doubleValue());
            } else {
                popularCategories.put(categoryResponse.getItems().getFirst().getSnippet().getTitle(), video.getStatistics().getViewCount().doubleValue());
            }
        }
    }
}