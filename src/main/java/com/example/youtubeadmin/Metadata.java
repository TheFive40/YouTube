package com.example.youtubeadmin;

import com.example.youtubeadmin.Model.contentVideo;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Metadata implements Initializable {
    @FXML
    private ListView<String> listTagsVideo, playList;
    @FXML
    private TextField Language, txtComment;
    @FXML
    private TableView<contentVideo> tableContent;
    @FXML
    private TableColumn<contentVideo,String> columnDimension, columnDefinition, columnCaption;
    @FXML
    private TableColumn<contentVideo,Boolean> columnLicensed;
    private static String accessToken;

    public void setMeta(Video video) throws IOException {
        initComponent();
        if(video !=null){
            tableContent.getItems().addAll(new contentVideo(video.getContentDetails().getDimension(), video.getContentDetails()
                    .getDefinition(), video.getContentDetails().getCaption(), video.getContentDetails().getLicensedContent()));

           if(video.getSnippet().getTags()!= null) listTagsVideo.getItems().addAll(video.getSnippet().getTags());

            YouTube.Playlists.List listaReproduccion = HelloController.getService().playlists().list("snippet");

            Language.setText(video.getSnippet().getDefaultAudioLanguage());

            txtComment.setText(" " +video.getStatistics().getCommentCount().doubleValue());

            listaReproduccion.setChannelId(video.getSnippet().getChannelId());
            for(Playlist playlist : listaReproduccion.execute().getItems()){
                playList.getItems().add(playlist.getSnippet().getTitle());
                System.out.println(playlist.getSnippet().getPublishedAt());
            }
        }
    }
    public static void setAccessToken(String code){
        accessToken = code;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void initComponent(){
        columnDimension.setCellValueFactory(new PropertyValueFactory<>("dimension"));

        columnDefinition.setCellValueFactory(new PropertyValueFactory<>("definition"));

        columnCaption.setCellValueFactory(new PropertyValueFactory<>("caption"));

        columnLicensed.setCellValueFactory(new PropertyValueFactory<>("licensed"));


    }
}
