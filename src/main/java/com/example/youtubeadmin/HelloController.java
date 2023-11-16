package com.example.youtubeadmin;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloController implements Initializable {
    private static final String API_KEY = "AIzaSyCBLbjuluw4UVqlhGnwG7Jy42KJhcgiRb4"; //Llave  que sirve para conectarse a la API de YouTube

    //
    //AIzaSyAY7MRIY2o6lsvLA-9Cb6hufm3en-L_cUQ

    private static final JsonFactory jsonfactory = JacksonFactory.getDefaultInstance(); //Clase, que convierte las respuestas de la API
    // YouTube en Clases con atributos de JAVA

    @FXML //Relaciona el componente con el archivo hello-view.fxml
    private Label digitalClock, LblAbout;

    @FXML
    private Label LblDashboard, LblAcount, LblAnalytics, LblReactions, LblExit, LblStaff;

    @FXML
    public HBox contentMain, afterContentMain = new HBox();
    @FXML
    private Label lblMetadata;
    @FXML
    private TilePane contentVideo;
    private static WebView webView;
    public static final List<Integer> listMinutes = new ArrayList<>(), listSeconds = new ArrayList<>();
    public static final ArrayList<Double> viewCounts = new ArrayList<>();
    @FXML
    public AnchorPane anchorMain;
    private long MaxResults = 1L;
    private static Video videoMetaData;
    private static String regionCode = "None";
    private static String order = "relevance";
    private static FXMLLoader fxmlLoader;

    public void setMaxSearchResult(long result) {
        if (result < 100) {
            MaxResults = result;
        }
    }

    public static String getRegionCode() {
        return regionCode;
    }


    public static void setOrderVideo(String order2) {
        order = order2;
    }

    private static String showVideoMostPopular(String... filter) throws IOException {
        if (filter[0].equalsIgnoreCase("None")) {
            YouTube.Videos.List videoList = getService().videos().list("snippet,Statistics, contentDetails");
            videoList.setId("YbJOTdZBX1g");
            videoMetaData = videoList.execute().getItems().getFirst();
            return "YbJOTdZBX1g";
        } else {
            Date now = new Date();
            now.setMonth(now.getMonth()-1);
            YouTube.Search.List list = getService().search().list("snippet, id");
            if (DashboardContent.getSelectedCategory() != null)
                list.setVideoCategoryId(DashboardContent.getCategoryAndId().get(DashboardContent.getSelectedCategory()));
            list.setRegionCode(filter[0]);
            list.setOrder(filter[1]);
            list.setType("video");
            list.setPublishedAfter(new DateTime(now));
            if (DashboardContent.getDefinition() != null) list.setVideoDefinition(DashboardContent.getDefinition());
            list.setMaxResults(50L);
            List<SearchResult> resultList = list.execute().getItems();
            YouTube.Videos.List videoList = getService().videos().list("snippet,Statistics, contentDetails");
            videoList.setId(resultList.getFirst().getId().getVideoId());
            videoMetaData = videoList.execute().getItems().getFirst();
            return resultList.getFirst().getId().getVideoId();
        }
    }

    public static void setVideoRegionCode(String code) {
        regionCode = code;
    }

    private void initComponent() throws IOException {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateClock()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        if (webView != null) webView.getEngine().load(null);
        webView = new WebView();
        webView.setMaxHeight(265);
        webView.setMaxWidth(460);
        webView.getEngine().load("https://www.youtube.com/watch?v=" + showVideoMostPopular(getRegionCode(), getOrder()));
        contentVideo.getChildren().add(getWebView());
        setLblMetadataListener();
    }

    public static WebView getWebView() {
        return webView;
    }

    public static void stopVideo() {
        webView.getEngine().load(null);
    }

    public void setReplayVideo(WebView webView) {
        try {
            System.out.println(getOrder());
            webView.getEngine().load("https://www.youtube.com/watch?v=" + showVideoMostPopular(regionCode, getOrder()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getOrder() {
        return order;
    }

    private void updateClock() {
        // Obtener la hora actual
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        // Actualizar el texto del reloj digital
        digitalClock.setText(sdf.format(now));
    }

    public static YouTube getService() {
        try {
            return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonfactory, null)
                    .setApplicationName("mi aplicacion")
                    .setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY))
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initComponent();
            mouseClickedOnMenu();
            setViewCounts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void setLblMetadataListener() {
        lblMetadata.setOnMouseClicked(e -> {
            FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("Metadata.fxml"));
            try {
                showVideoMostPopular(regionCode, getOrder());
                Parent parent = fxmlLoader1.load();
                Metadata metadata = fxmlLoader1.getController();
                metadata.setMeta(videoMetaData);
                Stage stage = new Stage();
                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.setTitle("Metadata");
                stage.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void setViewCounts() throws IOException {
        YouTube.Videos.List videos = getService().videos().list("Statistics");
        videos.setRegionCode(DashboardContent.getRegion());
        videos.setMaxResults((DashboardContent.getResults() == 0L) ? 30L : DashboardContent.getResults());
        videos.setVideoCategoryId((DashboardContent.getSelectedCategory() != null) ? DashboardContent.getCategoryAndId()
                .get(DashboardContent.getSelectedCategory()) : "24");
        if(DashboardContent.getSelectedCategory() != null && !DashboardContent.getSelectedCategory().equalsIgnoreCase("Anime/Animation")){
            videos.setChart("mostPopular");
        }else if (DashboardContent.getSelectedCategory() != null && DashboardContent.getSelectedCategory().equalsIgnoreCase("Anime/Animation")){
            videos.setId("P_S7WOZtx9Y");
        }else{
            videos.setChart("mostPopular");
        }
        VideoListResponse videoListResponse = videos.execute();
        for (int i = 0; i < videoListResponse.getItems().size(); i++) {
            viewCounts.add(videoListResponse.getItems().get(i).getStatistics().getViewCount().doubleValue());
        }
    }

    private void mouseClickedOnMenu() throws IOException {
        webView.getEngine().load(null);
        LblDashboard.setOnMouseClicked(e -> {
            if (HelloApplication.getLoader() != null && HelloApplication.getLoader().getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("DashboardContent.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                DashboardContent content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            }
        });
        LblReactions.setOnMouseClicked(e -> {
            if (fxmlLoader != null && fxmlLoader.getController() instanceof DashboardContent) {
                DashboardContent content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Reactions.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Reactions reactions = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(reactions.contentMain);

            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Analytics) {
                Analytics content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Reactions.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                   ex.printStackTrace();
                }
                Reactions suscribers = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(suscribers.contentMain);


            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Reactions.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Reactions content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (HelloApplication.getLoader() != null && HelloApplication.getLoader().getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Reactions.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Reactions content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Staff) {
                Staff content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Reactions.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                   ex.printStackTrace();
                }
                Reactions suscribers = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(suscribers.contentMain);


            }
        });
        LblAcount.setOnMouseClicked(e -> {
            if (HelloApplication.getLoader() != null && HelloApplication.getLoader().getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof DashboardContent) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Reactions) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Analytics) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Staff) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            }
        });
        LblAbout.setOnMouseClicked(e -> {
            if (fxmlLoader != null && fxmlLoader.getController() instanceof DashboardContent) {

                ((DashboardContent) fxmlLoader.getController()).contentMain.getChildren().clear();

                ((DashboardContent) fxmlLoader.getController()).contentMain.getChildren().addAll(afterContentMain.getChildren());
                if (tilePane == null) {
                    AnchorPane anchorPane = (AnchorPane) ((DashboardContent) fxmlLoader.getController()).contentMain.getChildren().getFirst();
                    for (int i = 0; i < anchorPane.getChildren().size(); i++) {
                        if (anchorPane.getChildren().get(i) instanceof TilePane) {
                            tilePane = (TilePane) anchorPane.getChildren().get(i);
                        }
                    }
                }
                webView.getEngine().load(null);

                tilePane.getChildren().clear();

                tilePane.getChildren().addAll(webView);

                setReplayVideo(webView);

                fxmlLoader = HelloApplication.getLoader();
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Analytics) {

                ((Analytics) fxmlLoader.getController()).contentMain.getChildren().clear();

                ((Analytics) fxmlLoader.getController()).contentMain.getChildren().addAll(afterContentMain.getChildren());
                if (tilePane == null) {
                    AnchorPane anchorPane = (AnchorPane) ((Analytics) fxmlLoader.getController()).contentMain.getChildren().getFirst();
                    for (int i = 0; i < anchorPane.getChildren().size(); i++) {
                        if (anchorPane.getChildren().get(i) instanceof TilePane) {
                            tilePane = (TilePane) anchorPane.getChildren().get(i);
                        }
                    }
                }
                webView.getEngine().load(null);

                tilePane.getChildren().clear();

                tilePane.getChildren().addAll(webView);

                setReplayVideo(webView);

                fxmlLoader = HelloApplication.getLoader();
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Reactions) {

                ((Reactions) fxmlLoader.getController()).contentMain.getChildren().clear();

                ((Reactions) fxmlLoader.getController()).contentMain.getChildren().addAll(afterContentMain.getChildren());
                if (tilePane == null) {
                    AnchorPane anchorPane = (AnchorPane) ((Reactions) fxmlLoader.getController()).contentMain.getChildren().getFirst();
                    for (int i = 0; i < anchorPane.getChildren().size(); i++) {
                        if (anchorPane.getChildren().get(i) instanceof TilePane) {
                            tilePane = (TilePane) anchorPane.getChildren().get(i);
                        }
                    }
                }
                webView.getEngine().load(null);

                tilePane.getChildren().clear();

                tilePane.getChildren().addAll(webView);

                setReplayVideo(webView);

                fxmlLoader = HelloApplication.getLoader();
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Account) {

                ((Account) fxmlLoader.getController()).contentMain.getChildren().clear();

                ((Account) fxmlLoader.getController()).contentMain.getChildren().addAll(afterContentMain.getChildren());
                if (tilePane == null) {
                    AnchorPane anchorPane = (AnchorPane) ((Account) fxmlLoader.getController()).contentMain.getChildren().getFirst();
                    for (int i = 0; i < anchorPane.getChildren().size(); i++) {
                        if (anchorPane.getChildren().get(i) instanceof TilePane) {
                            tilePane = (TilePane) anchorPane.getChildren().get(i);
                        }
                    }
                }
                webView.getEngine().load(null);

                tilePane.getChildren().clear();

                tilePane.getChildren().addAll(webView);

                setReplayVideo(webView);

                fxmlLoader = HelloApplication.getLoader();
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Staff) {

                ((Staff) fxmlLoader.getController()).contentMain.getChildren().clear();

                ((Staff) fxmlLoader.getController()).contentMain.getChildren().addAll(afterContentMain.getChildren());
                if (tilePane == null) {
                    AnchorPane anchorPane = (AnchorPane) ((Staff) fxmlLoader.getController()).contentMain.getChildren().getFirst();
                    for (int i = 0; i < anchorPane.getChildren().size(); i++) {
                        if (anchorPane.getChildren().get(i) instanceof TilePane) {
                            tilePane = (TilePane) anchorPane.getChildren().get(i);
                        }
                    }
                }
                webView.getEngine().load(null);

                tilePane.getChildren().clear();

                tilePane.getChildren().addAll(webView);

                setReplayVideo(webView);

                fxmlLoader = HelloApplication.getLoader();
            }
        });


        LblExit.setOnMouseClicked(e -> {
            System.exit(0);
        });
        LblAnalytics.setOnMouseClicked(e -> {
            if (fxmlLoader != null && fxmlLoader.getController() instanceof DashboardContent) {
                DashboardContent content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Analytics.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("BAD REQUEST 404");
                    alert.setHeaderText("Ha ocurrido un error inesperado");
                    alert.setContentText("Al parecer la categoria de videos seleccionada no se encuentra disponible" +
                            "en tu region por favor intenta mas tarde");
                    alert.show();
                }
                Analytics analytics = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(analytics.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Analytics.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Analytics content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (HelloApplication.getLoader() != null && HelloApplication.getLoader().getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Analytics.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Analytics content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Staff) {
                Staff content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Analytics.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("BAD REQUEST 404");
                    alert.setHeaderText("Ha ocurrido un error inesperado");
                    alert.setContentText("Al parecer la categoria de videos seleccionada no se encuentra disponible" +
                            "en tu region por favor intenta mas tarde");
                    alert.show();
                }
                Analytics analytics = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(analytics.contentMain);
            }
        });
        LblStaff.setOnMouseClicked(ex -> {
            if (fxmlLoader != null && fxmlLoader.getController() instanceof DashboardContent) {
                DashboardContent content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Staff.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException exception) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("BAD REQUEST 404");
                    alert.setHeaderText("Ha ocurrido un error inesperado");
                    alert.setContentText("Al parecer la categoria de videos seleccionada no se encuentra disponible" +
                            "en tu region por favor intenta mas tarde");
                    alert.show();
                }
                Staff staff = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(staff.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Staff.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Staff content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (HelloApplication.getLoader() != null && HelloApplication.getLoader().getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Staff.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Staff content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            }
        });

    }

    TilePane tilePane = null;

    public static void formatMinutes(String duration) {
        if (!duration.contains("H")) {
            int Minutes = duration.indexOf('T') + 1;
            int limitMinutes = duration.indexOf('M');
            StringBuilder formatMinutes = new StringBuilder("0");
            for (int j = Minutes; j < limitMinutes; j++) {
                formatMinutes.append(duration.charAt(j));
                //formatMinutes = new StringBuilder(formatMinutes.toString().trim());
            }
            listMinutes.add(Integer.parseInt(formatMinutes.toString()));
        }
    }

    public static void formatSeconds(String duration) {
        if (!duration.contains("H")) {
            int Seconds = duration.indexOf('M') + 1;
            int limitSeconds = duration.indexOf('S');
            StringBuilder formatSeconds = new StringBuilder("0");
            for (int j = Seconds; j < limitSeconds; j++) {
                formatSeconds.append(duration.charAt(j));
                formatSeconds = new StringBuilder(formatSeconds.toString().trim());
            }
            try {
                listSeconds.add(Integer.parseInt(formatSeconds.toString()));
            } catch (NumberFormatException ignored) {
            }
        }
    }
    public static int getAverageMinutes() throws IOException {
        YouTube.Videos.List videos = getService().videos().list("ContentDetails");
        if (DashboardContent.getSelectedCategory() != null)
            videos.setVideoCategoryId(DashboardContent.getCategoryAndId().get(DashboardContent.getSelectedCategory()));
        videos.setMaxResults((DashboardContent.getResults() == 0L) ? 30L : DashboardContent.getResults());
        videos.setChart("mostPopular");
        videos.setRegionCode(DashboardContent.getRegion());
        VideoListResponse response = videos.execute();

        for (int i = 0; i < response.getItems().size(); i++) {
            String duration = response.getItems().get(i).getContentDetails().getDuration();
            formatMinutes(duration);
        }
        AtomicInteger sum = new AtomicInteger();
        listMinutes.forEach(sum::addAndGet);
        return sum.get() / listMinutes.size();
    }

    public static int getAverageSeconds() throws IOException {
        YouTube.Videos.List videos = getService().videos().list("ContentDetails");
        if (DashboardContent.getSelectedCategory() != null)
            videos.setVideoCategoryId(DashboardContent.getCategoryAndId().get(DashboardContent.getSelectedCategory()));
        videos.setMaxResults((DashboardContent.getResults() == 0L) ? 30L : DashboardContent.getResults());
        videos.setChart("mostPopular");
        videos.setRegionCode(DashboardContent.getRegion());

        VideoListResponse response = videos.execute();
        for (int j = 0; j < response.getItems().size(); j++) {
            String duration = response.getItems().get(j).getContentDetails().getDuration();
            formatSeconds(duration);
        }

        AtomicInteger sum = new AtomicInteger();

        listSeconds.forEach(sum::addAndGet);

        return sum.get() / listMinutes.size();
    }
}