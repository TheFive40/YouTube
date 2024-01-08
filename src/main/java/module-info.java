module com.example.youtubeadmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires google.api.services.youtube.v3.rev222;
    requires google.api.client;
    requires com.google.api.client;
    requires google.http.client.jackson2;
    requires com.jfoenix;
    requires commons.math3;
    requires JMathPlot;
    requires java.desktop;
    requires stanford.corenlp;
    requires org.apache.opennlp.tools;
    requires google.oauth.client;
    requires org.apache.poi.poi;
    requires deeplearning4j.nn;
    requires deeplearning4j.nlp;
    requires deeplearning4j.modelimport;
    requires nd4j.api;
    requires deeplearning4j.utility.iterators;
    requires javafx.graphics;
    opens com.example.youtubeadmin to javafx.fxml;
    exports com.example.youtubeadmin;
    exports com.example.youtubeadmin.Model;
    opens com.example.youtubeadmin.Model to javafx.base, javafx.fxml;

}