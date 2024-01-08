package test;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main3 {
    public static void main(String[] args) throws Exception {
        // Carga el modelo preentrenado
        String kerasModelPath = "C:\\Users\\Jean Franco\\Downloads\\model.h5";
        MultiLayerNetwork model = KerasModelImport.importKerasSequentialModelAndWeights(kerasModelPath);

        // Carga los vectores de palabras
        String wordVectorsPath = "C:\\Users\\Jean Franco\\OneDrive\\Escritorio\\SBW-vectors-300-min5.txt";
        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(wordVectorsPath));

        // Tokeniza la cadena de texto
        String text = "Estoy triste";
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        List<String> tokens = tokenizerFactory.create(text).getTokens();

        // Convierte los tokens a vectores
        INDArray features = Nd4j.create(1, wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length, tokens.size());
        for (int i = 0; i < tokens.size() && i < features.size(2); i++) {
            String token = tokens.get(i);
            if (wordVectors.hasWord(token)) {
                features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(i)},
                        Nd4j.create(wordVectors.getWordVector(token)));
            }
        }

        // Crea un DataSetIterator
        DataSet dataSet = new DataSet(features, null);
        List<DataSet> listDs = new ArrayList<>();
        listDs.add(dataSet);
            DataSetIterator dsIterator = new ListDataSetIterator<DataSet>(listDs);

        // Usa el modelo para predecir el estado de ánimo
        while (dsIterator.hasNext()) {
            DataSet ds = dsIterator.next();
            INDArray prediction = model.output(ds.getFeatures());
            System.out.println("Estado de ánimo: " + prediction);
        }
    }
}
