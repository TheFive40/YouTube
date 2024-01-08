package com.example.youtubeadmin;

import com.example.youtubeadmin.Model.RegresionLineal;
import com.example.youtubeadmin.Model.TimePrediction;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AnalyticsTime implements Initializable {
    @FXML
    private Button btnCalcular;
    @FXML
    private TableView<TimePrediction> tableView;
    @FXML
    private TextField txtMinutes, averageMinutes, averageSeconds;
    @FXML
    private TableColumn<TimePrediction, String> columnMinutes, columnMinutesPrediction;
    @FXML
    private Label txtCategory;

    private static double minutes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            txtCategory.setText(DashboardContent.getSelectedCategory());
            averageMinutes.setText(" " + HelloController.getAverageMinutes());
            averageSeconds.setText(" " + HelloController.getAverageSeconds());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        columnMinutes.setCellValueFactory(new PropertyValueFactory<>("x"));
        columnMinutesPrediction.setCellValueFactory(new PropertyValueFactory<>("y"));
        btnCalcular.setOnAction(e -> {
            try{
                minutes = Double.parseDouble(txtMinutes.getText());
            }catch(NumberFormatException ex){
                Errors.showFormatErrorWindow();
            }
            RegresionLineal regresionLineal = new RegresionLineal();
            tableView.getItems().addAll(new TimePrediction(txtMinutes.getText(), regresionLineal.linearRegresion()));
            tableView.refresh();
        });
    }

    public static double getMinutes() {
        return minutes;
    }
}
