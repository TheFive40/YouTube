package com.example.youtubeadmin;

import com.example.youtubeadmin.Model.TransmissionsModel;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
public class Transmissions implements Initializable {
    private static final List<String> broadcasts = new ArrayList<>();
    @FXML
    private Button btnPrint, btnSave;
    private static final List<Integer> totalViews = new ArrayList<>();
    private static final List<Integer> totalComments = new ArrayList<>();
    @FXML
    private TableView<TransmissionsModel> tableView;
    private static final List<Integer> totalLikes = new ArrayList<>();
    @FXML
    private TableColumn columnView, columnChannel, columnCategory, columnComments, columnLikes;

    @FXML
    private TextField txtViews, txtComments, txtLikes;
    private static Long startTime = 0L;

    private void liveBroad() throws IOException {
        Platform.runLater(() -> {


            int likes = 0;
            YouTube.Search.List videosYouTubeRequest = null;
            try {
                videosYouTubeRequest = Comments.getService().search().list("snippet");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            videosYouTubeRequest.setEventType("live");
            videosYouTubeRequest.setVideoCategoryId(DashboardContent.getCategoryAndId().get(DashboardContent.getSelectedCategory()));
            videosYouTubeRequest.setOrder(DashboardContent.getOptionSelected());
            videosYouTubeRequest.setType("video");
            videosYouTubeRequest.setMaxResults(10L);
            try {
                for (SearchResult search : videosYouTubeRequest.execute().getItems()) {
                    broadcasts.add(search.getId().getVideoId());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!broadcasts.isEmpty()) {
                YouTube.Videos.List videos = null;
                try {
                    videos = Comments.getService().videos().list("snippet,Statistics,LiveStreamingDetails");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (int i = 0; i < broadcasts.size(); i++) {
                    videos.setId(broadcasts.get(i));
                    VideoListResponse videoListResponse = null;
                    try {
                        videoListResponse = videos.execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    for (Video video : videoListResponse.getItems()) {
                        totalViews.add(video.getStatistics().getViewCount().intValue());
                        if (video.getStatistics().getLikeCount() != null) {
                            totalLikes.add(video.getStatistics().getLikeCount().intValue());
                            likes = video.getStatistics().getLikeCount().intValue();
                        }
                        if (video.getLiveStreamingDetails().getActiveLiveChatId() != null) {
                            YouTube.LiveChatMessages.List liveChat = null;
                            try {
                                liveChat = Comments.getService().liveChatMessages().list(
                                        video.getLiveStreamingDetails().getActiveLiveChatId(), "snippet,AuthorDetails");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            LiveChatMessageListResponse liveChatMessages = null;
                            try {
                                liveChatMessages = liveChat.execute();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            totalComments.add(liveChatMessages.getItems().size());
                            tableView.getItems().add(new TransmissionsModel(video.getStatistics().getViewCount().doubleValue(),
                                    liveChatMessages.getItems().size(), DashboardContent.getSelectedCategory(),
                                    video.getSnippet().getChannelTitle(), likes));
                        }
                    }
                }
            }
            AtomicInteger Likes = new AtomicInteger();
            AtomicInteger Comments = new AtomicInteger();
            AtomicInteger Views = new AtomicInteger();

            totalLikes.forEach(x -> {
                Likes.addAndGet(x);
            });
            totalComments.forEach(x -> {
                Comments.addAndGet(x);
            });
            totalViews.forEach(x -> {
                Views.addAndGet(x);
            });
            txtLikes.setText(" " + Likes.get());
            txtComments.setText(" " + Comments.get());
            txtViews.setText(" " + Views.get());
            tableView.refresh();
        });
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Long startTime = System.currentTimeMillis();
        initComponent();
        try {
            liveBroad();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Long endTime = System.currentTimeMillis();

        Long elapsedTimeMilliseconds = endTime - startTime;

        double elapsedTimeSeconds = (double) elapsedTimeMilliseconds / 1000.0;

        // Imprime el tiempo transcurrido en segundos
        System.out.println("Tiempo transcurrido: " + elapsedTimeSeconds + " segundos");
    }
    public void btnPrinter(Stage primaryStage){
        btnPrint.setOnAction(e->{
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            if (printerJob != null) {
                if (printerJob.showPrintDialog(primaryStage)) {
                    if (printerJob.printPage(tableView)) {
                        printerJob.endJob();
                    }
                }
            }
        });
    }
    public static void load() {
        Stage primaryStage = new Stage();

        primaryStage.setTitle("Cargando");

        ProgressBar progressBar = new ProgressBar(0);

        StackPane root = new StackPane();

        root.getChildren().add(progressBar);

        primaryStage.setScene(new Scene(root, 300, 150));

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    updateProgress(i, 100);
                    Thread.sleep(50); // Simular una tarea de carga
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(event -> {
            // Tarea de carga completada
            System.out.println("Carga completa");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            primaryStage.close();
        });

        new Thread(task).start();
        primaryStage.show();

    }
    protected void btnSaveListener(Stage primaryStage){
        btnSave.setOnAction(e->{
            FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Seleccionar un archivo");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivos de excel", "*.xls"),
                    new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
            );

            java.io.File file = fileChooser.showSaveDialog(primaryStage);

            String absolutePath = file.getAbsolutePath();

            try (Workbook workbook = new HSSFWorkbook()){
                // Crear una hoja de trabajo
                Sheet sheet = workbook.createSheet("Transmisiones");

                Row row = sheet.createRow(0);
                Cell rowChannel = row.createCell(0);
                Cell rowCategory = row.createCell(1);
                Cell rowViews = row.createCell(2);
                Cell rowComments = row.createCell(3);
                Cell rowLikes = row.createCell(4);

                rowChannel.setCellValue("Channel");
                rowCategory.setCellValue("Category");
                rowViews.setCellValue("Views");
                rowComments.setCellValue("Comments");
                rowLikes.setCellValue("Likes");

                for (int i = 1; i <tableView.getItems().size(); i++)
                {
                    Row row1 = sheet.createRow(i);
                    Cell cellChannel = row1.createCell(0);
                    Cell cellCategory = row1.createCell(1);
                    Cell cellViews = row1.createCell(2);
                    Cell cellComments = row1.createCell(3);
                    Cell cellLikes = row1.createCell(4);
                    cellCategory.setCellValue(tableView.getItems().get(i-1).getCategory());
                    cellChannel.setCellValue(tableView.getItems().get(i-1).getChannel());
                    cellViews.setCellValue(tableView.getItems().get(i-1).getViews());
                    cellComments.setCellValue(tableView.getItems().get(i-1).getComments());
                    cellLikes.setCellValue(tableView.getItems().get(i-1).getLikes());
                }
                FileOutputStream out = new FileOutputStream(absolutePath);

                workbook.write(out);

                out.close();
                System.out.println("Datos escritos con éxito en el archivo 'datos.xlsx'.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }
    public void initComponent() {
        columnChannel.setCellValueFactory(new PropertyValueFactory<TransmissionsModel, String>("channel"));
        columnCategory.setCellValueFactory(new PropertyValueFactory<TransmissionsModel, String>("category"));
        columnComments.setCellValueFactory(new PropertyValueFactory<TransmissionsModel, Integer>("comments"));
        columnLikes.setCellValueFactory(new PropertyValueFactory<TransmissionsModel, Integer>("likes"));
        columnView.setCellValueFactory(new PropertyValueFactory<TransmissionsModel, Double>("views"));
    }
}

