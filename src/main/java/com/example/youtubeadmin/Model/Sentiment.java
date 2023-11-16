package com.example.youtubeadmin.Model;

import com.example.youtubeadmin.Reactions;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.*;

import static com.example.youtubeadmin.Comments.getService;
public class Sentiment  extends Task<Void> {
    private static  Map<String, Integer> sentimentCounts = new HashMap<>();
    public static void showSentiments() throws IOException {
        YouTube.CommentThreads.List comments = getService().commentThreads().list("snippet,replies");
        comments.setVideoId(Reactions.getVideoID());
        comments.setMaxResults(50L);
        List<String> comentarios = new ArrayList<>();
        CommentThreadListResponse commentThread = comments.execute();
        for(CommentThread commentThread1  : commentThread.getItems() ){
          comentarios.add(commentThread1.getSnippet().getTopLevelComment().getSnippet().getTextOriginal());
        }
        // Configura las propiedades para el procesamiento de texto
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        // props.setProperty("annotators", "tokenize,ssplit,pos,parse");
        props.setProperty("parse.model", String.valueOf(Sentiment.class.getResource("/englishPCFG (1).ser.gz")));
        props.setProperty("sentiment.model", String.valueOf(Sentiment.class.getResource("/sentiment.ser.gz")));

        // Crea un objeto de StanfordCoreNLP con las propiedades
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        sentimentCounts.put("Positive", 0);
        sentimentCounts.put("Neutral", 0);
        sentimentCounts.put("Negative", 0);
        sentimentCounts.put("Very Negative", 0);

        for (String text : comentarios) {
            Annotation document = new Annotation(text);
            pipeline.annotate(document);
            String sentiment = "";
            for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
                sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            }
            sentimentCounts.put(sentiment, sentimentCounts.get(sentiment) + 1);
        }

    }

    public static Map<String, Integer> getSentimentCounts() {
        return sentimentCounts;
    }

    @Override
    protected Void call() throws Exception {
        showSentiments();
        return null;
    }
}
