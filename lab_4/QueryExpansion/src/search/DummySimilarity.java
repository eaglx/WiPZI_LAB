package search;

import engine.Document;

import java.util.ArrayList;

public class DummySimilarity implements ISearch
{
    private ArrayList<Document> _documents;

    public DummySimilarity(ArrayList<Document> documents)
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

        for (Document document : _documents) scores.add(new Score(document, 0.0d));

        return scores;
    }

    @Override
    public String getName()
    {
        return "Dummy similarity";
    }
}
