package test;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;

public class Main4 {
    public static void main(String[] args) {
        // Configura las propiedades para el procesamiento de texto
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
       // props.setProperty("annotators", "tokenize,ssplit,pos,parse");
        props.setProperty("parse.model", "src/englishPCFG (1).ser.gz");
        props.setProperty("sentiment.model", "src/sentiment.ser.gz");

        // Crea un objeto de StanfordCoreNLP con las propiedades
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Texto de ejemplo para analizar el sentimiento
        String text = "You're worthless, sometimes I think you're pretty useless!";
        // Análisis de sentimiento
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        // Obtiene la puntuación de sentimiento del documento
        String sentiment = "";
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
        }

        // Imprime el resultado del análisis de sentimiento
        System.out.println("Sentimiento: " + sentiment);

    }
}
