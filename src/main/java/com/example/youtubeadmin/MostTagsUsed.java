package com.example.youtubeadmin;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MostTagsUsed implements Initializable {

    @FXML
    private TextField txtNumber, txtVisualization, txtChannels;
    @FXML
    private ListView<String> listTags;
    @FXML
    private Label lblCategory;
    @FXML
    private ComboBox<String> category;

    private int count = 0;

    private static HashMap<String, String> categoryAndId = new HashMap<>();
    private final List<String> tags = new ArrayList<>();

    public void initComponent() {
        category.getItems().addAll("Entertainment", "Film & Animation", "Gaming", "Science & Technology"
                , "Autos & Vehicles", "Sports", "Anime/Animation", "Music", "Comedy");
        categoryAndId.put("Entertainment", "24");
        categoryAndId.put("Film & Animation", "1");
        categoryAndId.put("Gaming", "20");
        categoryAndId.put("Science & Technology", "28");
        categoryAndId.put("Autos & Vehicles", "2");
        categoryAndId.put("Sports", "17");
        categoryAndId.put("Anime/Animation", "31");
        categoryAndId.put("Music", "10");
        categoryAndId.put("Comedy", "23");
        txtNumber.setText("Not Found");
        txtVisualization.setText("Not Found");
        txtChannels.setText("Not Found");
    }

    private String mostPopularCategoryTags() {
        try {
            YouTube.Videos.List videoList = Comments.getService().videos().list("snippet, id");
            videoList.setChart("mostPopular");
            videoList.setMaxResults((DashboardContent.getResults() == 0L ) ? 50L : DashboardContent.getResults());
            videoList.setVideoCategoryId(categoryAndId.get(category.getSelectionModel().getSelectedItem()));
            videoList.setRegionCode(DashboardContent.getRegion());
            HashMap<Integer, String> mostPopular = new HashMap<>();
            List<Video> list;
            List<String> tagsList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                videoList.setPageToken(videoList.execute().getNextPageToken());
                list = videoList.execute().getItems();
                for (Video video : list) {
                    List<String> tags = video.getSnippet().getTags();
                    if (tags != null) {
                        tagsList.addAll(tags);
                    }
                }
            }
            for (int i = 0; i < tagsList.size(); i++) {
                count = 0;
                for (int j = i + 1; j < tagsList.size(); j++) {
                    if (tagsList.get(i).equalsIgnoreCase(tagsList.get(j)) ||
                            tagsList.get(i).contentEquals(tagsList.get(j))) count++;
                    if (count >= 5) mostPopular.put(count, tagsList.get(i));
                }
            }
            Set<Map.Entry<Integer, String>> set = mostPopular.entrySet();
            List<Integer> integerList = new ArrayList<>();
            Iterator<Map.Entry<Integer, String>> iterator = set.iterator();
            while (iterator.hasNext()) {
                integerList.add(iterator.next().getKey());
            }
            integerList.sort(Integer::compare);
            return mostPopular.get(integerList.getLast());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void mostPopularTags() {
        try {
            YouTube.Videos.List videoList = Comments.getService().videos().list("snippet, id");
            videoList.setChart("mostPopular");
            videoList.setMaxResults(50L);
            List<Video> list;
            HashSet<String> keyword = new HashSet<>();
            ArrayList<String> tagsList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                videoList.setPageToken(videoList.execute().getNextPageToken());
                list = videoList.execute().getItems();
                for (Video video : list) {
                    List<String> tags = video.getSnippet().getTags();
                    if (tags != null) {
                        tagsList.addAll(tags);
                    }
                }
            }

            for (int i = 0; i < tagsList.size(); i++) {
                count = 0;
                for (int j = i + 1; j < tagsList.size(); j++) {
                    if (tagsList.get(i).equals(tagsList.get(j))) count++;
                    if (count >= 10) keyword.add(tagsList.get(i));
                }
            }
            listTags.getItems().addAll(keyword);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComponent();
        mostPopularTags();
        category.setOnAction(e -> {
            String tag = mostPopularCategoryTags();
            lblCategory.setText(tag);
            try {
                numberOfVideos(tag);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void numberOfVideos(String tag) throws IOException {

        int numberOfvideos = 0;
        String ChannelName = " ";
        int Views = 0;
        YouTube.Videos.List video = Comments.getService().videos().list("snippet,Statistics");

        video.setChart("mostPopular");

        video.setRegionCode(DashboardContent.getRegion());

        video.setMaxResults(50L);

        for (int i = 0; i < 3; i++) {
            VideoListResponse videoListResponse = video.setPageToken(video.execute().getNextPageToken()).execute();
            for (Video video1 : videoListResponse.getItems()) {
                List<String> etiquetas = video1.getSnippet().getTags();
                if (etiquetas != null) {
                    if (video1.getSnippet().getTags().contains(tag)) {
                        numberOfvideos++;
                        ChannelName =  video1.getSnippet().getChannelTitle();
                        Views += video1.getStatistics().getViewCount().intValue();
                    }
                }
            }
        }
        txtChannels.setText(ChannelName);
        txtVisualization.setText(" " + Views);
        txtNumber.setText(" " + numberOfvideos);
    }
}
