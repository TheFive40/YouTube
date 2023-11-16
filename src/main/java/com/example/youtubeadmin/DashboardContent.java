package com.example.youtubeadmin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DashboardContent implements Initializable {
    @FXML
    public HBox contentMain;
    @FXML
    private ListView<String> category;
    @FXML
    private TextField maxResults;
    @FXML
    private AnchorPane anchor;
    @FXML
    private ComboBox<String> regionCode, definitionsType;
    @FXML
    private ToggleGroup videoFilter;
    private static long results;
    private static String definition;
    private static String selectedCategory;

    @FXML
    private RadioButton viewCount, rating, relevance;

    private static String optionSelected, Region;
    private FXMLLoader finalLoader;
    private Parent parent;

    private static HashMap<String, String> categoryAndId = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComponent();
        HelloController.stopVideo();
        try {
            finalLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            parent = finalLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HelloController controller = finalLoader.getController();
        HelloController.getWebView().getEngine().load(null);
        maxResults.setOnAction(e -> {
            controller.setMaxSearchResult(Long.parseLong(maxResults.getText()));
        });
        regionCode.setOnAction(e -> {
            HelloController.setVideoRegionCode(regionCode.getSelectionModel().getSelectedItem());
            Region = regionCode.getSelectionModel().getSelectedItem();
        });
        viewCount.setOnAction(e -> {
            HelloController.setOrderVideo(viewCount.getId());
            optionSelected = viewCount.getText();

        });
        relevance.setOnAction(e -> {
            HelloController.setOrderVideo(relevance.getId());
            optionSelected = relevance.getText();
        });
        rating.setOnAction(e -> {
            optionSelected = rating.getText();
        });

        category.setOnMouseClicked(e -> {
            System.out.println(category.getSelectionModel().getSelectedItem());
            selectedCategory = category.getSelectionModel().getSelectedItem();
        });
        definitionsType.setOnAction(e -> {
            definition = definitionsType.getSelectionModel().getSelectedItem();
        });
        maxResults.setOnAction(e -> {
            try {
                results = Long.parseLong(maxResults.getText());
            } catch (NumberFormatException exception) {
                Errors.showFormatErrorWindow();
            }
        });
    }

    public static String getOptionSelected() {
        return optionSelected;
    }

    public static String getSelectedCategory() {
        return selectedCategory;
    }

    public static HashMap<String, String> getCategoryAndId() {
        return categoryAndId;
    }

    private void initComponent() {
        category.getItems().addAll("Entertainment", "Film & Animation", "Gaming", "Science & Technology"
                , "Autos & Vehicles", "Sports", "Anime/Animation", "Music", "Comedy");
        regionCode.getItems().addAll("US", "CA", "CO", "AR", "FR", "MX", "IT", "ES", "DE", "AU", "BR", "JP", "KR");
        definitionsType.getItems().addAll("any", "high", "standard");
        categoryAndId.put("Entertainment", "24");
        categoryAndId.put("Film & Animation", "1");
        categoryAndId.put("Gaming", "20");
        categoryAndId.put("Science & Technology", "28");
        categoryAndId.put("Autos & Vehicles", "2");
        categoryAndId.put("Sports", "17");
        categoryAndId.put("Anime/Animation", "31");
        categoryAndId.put("Music", "10");
        categoryAndId.put("Comedy", "23");
    }

    public HBox getContentMain() {
        return contentMain;
    }

    public ListView<String> getCategory() {
        return category;
    }

    public TextField getMaxResults() {
        return maxResults;
    }

    public AnchorPane getAnchor() {
        return anchor;
    }

    public ComboBox<String> getRegionCode() {
        return regionCode;
    }

    public ComboBox<String> getDefinitionsType() {
        return definitionsType;
    }

    public ToggleGroup getVideoFilter() {
        return videoFilter;
    }

    public static long getResults() {
        return results;
    }

    public static String getRegion() {
        return Region;
    }

    public RadioButton getViewCount() {
        return viewCount;
    }

    public RadioButton getRating() {
        return rating;
    }

    public RadioButton getRelevance() {
        return relevance;
    }

    public FXMLLoader getFinalLoader() {
        return finalLoader;
    }

    public Parent getParent() {
        return parent;
    }

    public static String getDefinition() {
        return definition;
    }
}
