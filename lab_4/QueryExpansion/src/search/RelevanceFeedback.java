package search;

import engine.Document;

import java.util.ArrayList;
import java.util.Collections;

import static search.CosineSimilarity_TF_IDF.getCosineSimilarity;

public class RelevanceFeedback implements ISearch
{
    private ArrayList <Document> _documents;
    private double _alpha;
    private double _beta;
    private double _gamma;
    private int _relevantDocIDs[];
    private int _irrelevantDocIDs[];

    public RelevanceFeedback(ArrayList <Document> documents,
                             double alpha,
                             double beta,
                             double gamma,
                             int _relevantDocIDs[],
                             int _irrelevantDocIDs[])
    {
        this._documents = documents;
        this._alpha = alpha;
        this._beta = beta;
        this._gamma = gamma;
        this._relevantDocIDs = _relevantDocIDs;
        this._irrelevantDocIDs = _irrelevantDocIDs;
    }

    @Override
    public ArrayList <Score> getSortedDocuments(Document query)
    {
        return getSortedDocuments(query._tf_idf_representation);
    }

    @Override
    public ArrayList <Score> getSortedDocuments(double[] queryVector)
    {
        System.out.println("TF-IDF representation = ");
        for (double aQueryVector : queryVector)
            System.out.print(String.format("%.2f ", aQueryVector));
        System.out.println("");

        // DONE implement Rocchio method for relevance feedback
        // use _tf_idf_representation of documents,
        // alpha, betta, and gamma are the weights,
        // relevantDocIDs is the vector of IDs of relevant documents (id = index),
        // irrelevantDocIDs is the vector of IDs of irrelevant documents (id = index)
        //-----------------------------------------------------------
        double modifiedQuery[] = new double[queryVector.length];
        // -----------------------------------------------
        int length = queryVector.length;
        double relevantSum = 0;
        double irrelevantSum = 0;
        // -----------------------------------------------

        // DONE 2) update the modified query (relevant documents).
        // 1) iterate over the indexes of the relevant documents
        // 2) derive the vector representation (tf-idf) of a document
        // 3) update the modifiedQuery vector (add, beta weight, divide
        // by the number of relevant documents)

        // -----------------------------------------------
        //for (int relevantDocID : _relevantDocIDs)
        for(int i : _relevantDocIDs){
            double tmp[] = _documents.get(i)._tf_idf_representation;
            int len = tmp.length;
            for(int j = 0; j < len; j++) {
                relevantSum += tmp[i];
            }
        }
        // -----------------------------------------------


        // DONE 3)  update the modified query
        // 1) iterate over the indexes of the irrelevant documents
        // 2) derive the vector representation (tf-idf) of a document
        // 3) update the modifiedQuery vector (substract, gamma weight, divide
        // by the number of irrelevant documents)

        // -----------------------------------------------
        //for (int irrelevantDocID : _irrrelevantDocIDs)
        for(int i : _irrelevantDocIDs){
            double tmp[] = _documents.get(i)._tf_idf_representation;
            int len = tmp.length;
            for(int j = 0; j < len; j++) {
                irrelevantSum += tmp[i];
            }
        }

        for(int i = 0; i < length; i++) {
            modifiedQuery[i] = _alpha * queryVector[i] +
                    (_beta * relevantSum) / _relevantDocIDs.length  -
                    (_gamma * irrelevantSum) / _irrelevantDocIDs.length ;
        }

        // ---------------------------------------------------------

        System.out.println("Modified TF-IDF representation = ");
        for (double aQueryVector : modifiedQuery)
            System.out.print(String.format("%.2f ", aQueryVector));
        System.out.println("");

        ISearch search = new CosineSimilarity_TF_IDF(_documents);
        return search.getSortedDocuments(modifiedQuery);
    }

    @Override
    public String getName()
    {
        return "Cosine similarity + relevance feedback";
    }

}
