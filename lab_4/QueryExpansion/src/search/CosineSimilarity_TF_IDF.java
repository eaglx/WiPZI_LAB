package search;

import engine.Document;

import java.util.ArrayList;
import java.util.Collections;

public class CosineSimilarity_TF_IDF implements ISearch
{
    private ArrayList <Document> _documents;

    public CosineSimilarity_TF_IDF(ArrayList <Document> documents)
    {
        this._documents = documents;
    }

    @Override
    public ArrayList <Score> getSortedDocuments(Document query)
    {
        return getSortedDocuments(query._tf_idf_representation);
    }

    @Override
    public ArrayList <Score> getSortedDocuments(double[] queryVector)
    {
        ArrayList <Score> scores = new ArrayList <>(_documents.size());
        // TODO update scores: compute a similarity of each document to the query vector
        // use getCosineSimilarity() method
        // -----------------------------------------------
        for (Document document : _documents)
            scores.add(new Score(document, getCosineSimilarity(queryVector, document._tf_idf_representation)));
        // -----------------------------------------------

        Collections.sort(scores);
        return scores;
    }

    @Override
    public String getName()
    {
        return "Cosine similarity";
    }

    static double getCosineSimilarity(double v1[], double v2[])
    {
        double dot = getDotProduct(v1, v2);
        double l1 = getLength(v1);
        double l2 = getLength(v2);
        if (Double.compare(l1, 0.0d) == 0) return 0.0d;
        if (Double.compare(l2, 0.0d) == 0) return 0.0d;
        return dot / (l1 * l2);
    }

    private static double getDotProduct(double a[], double b[])
    {
        double sum = 0.0d;
        for (int i = 0; i < a.length; i++)
            sum += (a[i] * b[i]);
        return sum;
    }

    private static double getLength(double a[])
    {
        double sum = 0.0d;
        for (double anA : a) sum += (anA * anA);
        return Math.sqrt(sum);
    }

}
