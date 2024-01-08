package com.example.youtubeadmin;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Comments implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField txtWord;
    @FXML
    private Button btnSearch, btnClean;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final ArrayList<String> listComments = new ArrayList<>();
    private static String apiKey = "AIzaSyDIkUDeVIhN33M-eviiWooPOZZ0MUsysGQ";
    private static String channelName;
    private static String Language;
    private static String Title;

    public void mostAudiency(String chart, String rg) throws GeneralSecurityException {
        try {
            YouTube.Videos.List videolist = getService().videos().list("snippet,statistics");

            videolist.setChart("mostPopular");

            videolist.setVideoCategoryId(DashboardContent.getCategoryAndId()
                    .get(DashboardContent.getSelectedCategory()));

            videolist.setRegionCode(rg);

            videolist.setMaxResults(20L);

            VideoListResponse videoResponse = videolist.execute();

            for (Video video : videoResponse.getItems()) {
                channelName = video.getSnippet().getChannelTitle();
                Language = video.getSnippet().getDefaultAudioLanguage();
                Title = video.getSnippet().getTitle();
                YouTube.CommentThreads.List comments = getService().commentThreads().list("snippet,replies");
                comments.setSearchTerms(txtWord.getText());
                comments.setVideoId(video.getId());
                comments.setMaxResults(20L);
                CommentThreadListResponse commentThreadListResponse = comments.execute();
                for (CommentThread commentThread : commentThreadListResponse.getItems()) {
                    textArea.appendText("\n " + commentThread.getSnippet().getTopLevelComment()
                            .getSnippet().getAuthorDisplayName() + " : " + commentThread.getSnippet()
                            .getTopLevelComment().getSnippet().getTextOriginal());

                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // Mostrar los comentarios en el TextFlow con palabras clave resaltadas

    }
    public static YouTube getService() {
        try {
            return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                    .setApplicationName("mi aplicacion")
                    .setYouTubeRequestInitializer(new YouTubeRequestInitializer(apiKey))
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSearch.setOnAction(e -> {
            try {
                mostAudiency(DashboardContent.getOptionSelected(), DashboardContent.getRegion());
            } catch (GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnClean.setOnAction(e -> {
            textArea.setText("");
        });

    }
}
