package search;

import engine.Dictionary;
import engine.Document;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.TokenizerME;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class QueryExpansion implements ISearch
{
    private RealMatrix _correlationMatrix;
    private double CORRELATION_THRESHOLD = 0.3d;

    private Dictionary _dictionary;
    private ArrayList <Document> _documents;
    private TokenizerME _tokenizer;
    private PorterStemmer _stemmer;

    public QueryExpansion(Dictionary dictionary,
                          ArrayList <Document> documents,
                          TokenizerME tokenizer,
                          PorterStemmer stemmer)
    {
        this._dictionary = dictionary;
        this._documents = documents;
        this._tokenizer = tokenizer;
        this._stemmer = stemmer;

        RealMatrix A = MatrixUtils.createRealMatrix(dictionary._terms.size(), documents.size());

        // DONE compute _correlationMatrix
        // DONE 1) construct matrix A
        // Notice that a bag-of-words representation of a document
        // is a column of A;
        // derive subsequent bow representations
        // and use setColumnVector() method of A to set the columns.

        // -----------------------------------------------------
        for(int i = 0; i < documents.size(); i++){
            A.setColumn(i,documents.get(i)._bow_representation);
        }
        // -----------------------------------------------------

        // DONE 2) normalize matrix A
        // Iterate over rows. Use row.getNorm() method which returns a length of a vector.
        // Divide each element of a vector by the length ( newRow = oldRow.mapDivide( length ).
        // update A (setRowVector)

        // -----------------------------------------------------
        for(int i=0; i < A.getData().length; i++){
            double length = A.getRowVector(i).getNorm();
            RealVector newRow = A.getRowVector(i).mapDivide( length );
            A.setRowVector(i, newRow);
        }
        // -----------------------------------------------------

        // DONE 3) obtain AT (transposed matrix)
        RealMatrix AT = A.transpose();
        // DONE 4) set _correlationMatrix = A x AT (multiply)
        _correlationMatrix = A.multiply(AT);
    }

    @Override
    public ArrayList <Score> getSortedDocuments(Document query)
    {
        // -----------------------------------------------------
        double data[][] = _correlationMatrix.getData();

        // DONE 1) Build a set of unique indexes of terms of a query
        // You can iterate over dictionary._terms and use bow representation
        // of a query to verify if a term occurs in the query or not
        // if occurs, add the index of the term to uniqueTerms_Query
        Set <Integer> uniqueTerms_Query = new HashSet <>(_dictionary._terms.size());
        // --------------------------------------------------------
        for(String term : _dictionary._terms){
            int id = _dictionary._termID.get(term);
            if(query._bow_representation[id] > 0){
                uniqueTerms_Query.add(id);
            }
        }
        //-------------------------------------------------------

        System.out.print("Original terms: ");
        for (Integer i : uniqueTerms_Query)
            System.out.print(_dictionary._terms.get(i) + " ");
        System.out.println("");
        System.out.println("Added terms: ");

        // DONE 2) Build a set of indexes of unique terms which are
        // correlated with the terms of the query
        // Algorithm:
        Set <Integer> uniqueTerms_ModifiedQuery = new HashSet <>(_dictionary._terms.size());
        // 1) For each index (term) in uniqueTerms_query:
        for (Integer i : uniqueTerms_Query)
        {
            // 2) Look for a new term in dictionary._terms such that:
            //      - new term is different from the original term
            //      - new term does not occur in uniqueTerms_Query nor in uniqueTerms_ModifiedQuery
            //      - the correlation with the original term is greater than CORRELATION_THRESHOLD
            //      - choose the term with the greatest correlation with the original term
            double maxCorrelation = CORRELATION_THRESHOLD;//-1.0d;
            int index = -1;

            //-------------------------------------------------------
            String uniqueTerm = _dictionary._terms.get(i);
            for(String term : _dictionary._terms){
                int id = _dictionary._termID.get(term);
                if(!uniqueTerm.equals(term) && !uniqueTerms_Query.contains(id) &&
                        !uniqueTerms_ModifiedQuery.contains(id) && _correlationMatrix.getEntry(i,id) > maxCorrelation){
                    maxCorrelation = _correlationMatrix.getEntry(i,id);
                    index = id;
                }
            }
            //-------------------------------------------------------

            // 3) add the index of the chosen term to uniqueTerms_ModifiedQuery (DONE)
            if (index > -1)
            {
                System.out.println(String.format("   %s -> %s   correlation = %.2f",
                                                 _dictionary._terms.get(i),
                                                 _dictionary._terms.get(index),
                                                 data[i][index]));
                uniqueTerms_ModifiedQuery.add(index);
            }
        }

        // -----------------------------------------------------
        StringBuilder content = new StringBuilder();
        // DONE 4) Build a new query: construct content string.
        // You need to use keywords instead of terms: use _dicitonary._termsToKeywords map.
        // Build a string content which consists of keywords which match terms
        // (indexes) of uniqueTerms_Query and uniqueTerms_ModifiedQuery.
        // The order of keywords does not matter.
        //-------------------------------------------------------
        for(int id : uniqueTerms_Query){
            content.append(_dictionary._termsToKeywords.get(_dictionary._terms.get(id))+" ");
        }
        for(int id : uniqueTerms_ModifiedQuery){
            content.append(_dictionary._termsToKeywords.get(_dictionary._terms.get(id))+" ");
        }
        //-------------------------------------------------------

        System.out.println("Content for the modified query = " + content);

        Document modifiedQuery = new Document("modified query", content.toString(), 0);
        modifiedQuery.doProcessing(_dictionary, _tokenizer, _stemmer);
        modifiedQuery.computeVectorRepresentations(_dictionary, _tokenizer, _stemmer);

        ISearch search = new CosineSimilarity_TF_IDF(_documents);
        return search.getSortedDocuments(modifiedQuery);
    }

    @Override
    public ArrayList <Score> getSortedDocuments(double[] queryVector)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return "Cosine similarity + correlation matrix";
    }

}
