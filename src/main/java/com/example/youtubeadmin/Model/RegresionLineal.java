package com.example.youtubeadmin.Model;

import com.example.youtubeadmin.AnalyticsTime;
import com.example.youtubeadmin.HelloController;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.List;

public class RegresionLineal {
    public int linearRegresion(){
        // Datos de entrenamiento
        List<Integer> listMinutes = HelloController.listMinutes;
        List<Double> viewCounts = HelloController.viewCounts;

        // Convertir listas en arreglos
        double[] xData = listMinutes.stream().mapToDouble(Integer::intValue).toArray();
        double[] yData = viewCounts.stream().mapToDouble(Double::intValue).toArray();
        // Crear un modelo de regresión lineal simple
        SimpleRegression regression = new SimpleRegression();
        // Agregar los datos de entrenamiento al modelo
        for (int i = 0; i < xData.length; i++) {
            regression.addData(xData[i], yData[i]);
        }
        // Obtener los coeficientes de la regresión
        double intercept = regression.getIntercept();
        double slope = regression.getSlope();
        // Realizar una predicción para una nueva duración (ejemplo: 5 minutos)
        double newDuration = AnalyticsTime.getMinutes();

        return (int) regression.predict(newDuration);

    }
}
