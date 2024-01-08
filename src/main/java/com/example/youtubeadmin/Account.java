package com.example.youtubeadmin;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.ResourceBundle;

public class Account implements Initializable {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @FXML
    private WebView webView;
    @FXML
    private Button btnConfirm;
    @FXML
    public HBox contentMain;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            playList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
    public void playList() throws IOException, GeneralSecurityException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId("359182186870-1u52ljj2hrj7gr86bo1fa71cpgf74cdm.apps.googleusercontent.com");
        details.setClientSecret("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");
        details.setRedirectUris(Collections.singletonList("http://localhost"));
        details.setTokenUri("https://oauth2.googleapis.com/token");
        details.setAuthUri("https://accounts.google.com/o/oauth2/auth");
        clientSecrets.setInstalled(details);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(YouTubeScopes.YOUTUBE_READONLY))
                .setAccessType("offline")
                .build();
        flow.newAuthorizationUrl().setRedirectUri("http://localhost:8080/");
        // Aquí es donde deberías redirigir al usuario para obtener su autorización
        String url = flow.newAuthorizationUrl().setRedirectUri(clientSecrets.getDetails().getRedirectUris().get(0)).build();

        webView.getEngine().load(url);
        StringBuilder code =  new StringBuilder();

        btnConfirm.setOnAction(e->{
          String location =   webView.getEngine().getLocation();

          int init = location.indexOf('=')+1;
          int end = location.indexOf('&');

          for(int i = init;i<end;i++){
              code.append(location.charAt(i));
          }
          try {
              TokenResponse response = flow.newTokenRequest(code.toString()).setRedirectUri(clientSecrets.getDetails().getRedirectUris().get(0)).execute();

              YouTube.Playlists.List playList = HelloController.getService().playlists().list("snippet");

              Metadata.setAccessToken(response.getAccessToken());

          }catch(IOException exception){
              exception.printStackTrace();
          }

        });


    }
}
