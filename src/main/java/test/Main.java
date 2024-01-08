package test;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            FileInputStream modelIn = new FileInputStream(new File("C:\\Users\\Jean Franco\\Downloads\\langdetect-183.bin"));
            LanguageDetectorModel model = new LanguageDetectorModel(modelIn);
            LanguageDetector languageDetector = new LanguageDetectorME(model);

            String text = "Ich bin";
            Language[] languages = languageDetector.predictLanguages(text);

            // Variables para rastrear el idioma principal y la mayor confianza
            Language mainLanguage = null;
            double maxConfidence = 0.0;

            // Itera a través de las detecciones para encontrar el idioma principal
            for (Language lang : languages) {
                if (lang.getConfidence() > maxConfidence) {
                    maxConfidence = lang.getConfidence();
                    mainLanguage = lang;
                }
            }

            if (mainLanguage != null) {
                System.out.println("Idioma principal: " + mainLanguage.getLang() + " con confianza: " + maxConfidence);
            } else {
                System.out.println("No se pudo detectar el idioma principal.");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
