package com.example.youtubeadmin;

import javafx.scene.control.Alert;

public class Errors {
    public static void showFormatErrorWindow(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("¡ERROR DE FORMATO!");
        alert.setTitle("Number Format Exception");
        alert.setContentText("¡Por favor evita usar cadenas de caracteres o texto en "
                + " lugares donde no estan permitidos!");
        alert.showAndWait();
    }
}
