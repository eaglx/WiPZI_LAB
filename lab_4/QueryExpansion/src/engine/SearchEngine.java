package engine;

import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import search.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SearchEngine
{
    private PorterStemmer _stemmer;
    private TokenizerME _tokenizer;

    private ArrayList <Document> _documents;
    private Dictionary _dictionary;

    private String QUERY = "machine learning";
    private int K_BEST_RESULTS = 10;

    public static void main(String args[]) throws IOException
    {
        SearchEngine se = new SearchEngine();
        se.run();
    }

    public void run() throws IOException
    {
        System.out.println("Init and tokenizer and Porter stemmer...");

        TokenizerModel tokenizerModel = new TokenizerModel(new File("en-token.bin"));
        _tokenizer = new TokenizerME(tokenizerModel);
        _stemmer = new PorterStemmer();

        System.out.println("Loading the dictionary...");
        _dictionary = loadDictionary();
        System.out.println("Processing the dictionary...");
        _dictionary.doProcessing(_stemmer, false);

        System.out.println("Loading the documents...");
        _documents = loadDocuments();
        System.out.println("Processing the documents...");
        for (Document d : _documents)
            d.doProcessing(_dictionary, _tokenizer, _stemmer);

        System.out.println("Computing IDFs...");
        _dictionary.computeIDFs(_documents);
        //_dictionary.print();

        System.out.println("Computing vector representations...");
        for (Document d : _documents)
            d.computeVectorRepresentations(_dictionary, _tokenizer, _stemmer);

        System.out.println("Preparing query = " + QUERY);
        Document queryDocument = new Document("query", QUERY, 0);
        queryDocument.doProcessing(_dictionary, _tokenizer, _stemmer);
        queryDocument.computeVectorRepresentations(_dictionary, _tokenizer, _stemmer);

        System.out.println("------------------------------------");
        // TODO IMPLEMENT DIFFERENT METHODS FOR SEARCHING
        // uncomment/comment some lines to choose some searching method.
        //ISearch SEARCH_METHOD = new DummySimilarity(_documents);
        //ISearch SEARCH_METHOD = new CosineSimilarity_TF_IDF(_documents);
        //ISearch SEARCH_METHOD = new RelevanceFeedback(_documents, 0.5d, 0.7d, 0.3d,
         //                                  new int[] { 66, 29 }, new int[] { 7, 77 });
        //ISearch SEARCH_METHOD = new QueryExpansion(_dictionary, _documents, _tokenizer, _stemmer);
        ISearch SEARCH_METHOD = new WordNet(_dictionary, _documents);

        System.out.println("Searching using: " + SEARCH_METHOD.getName());

        ArrayList <Score> scores = SEARCH_METHOD.getSortedDocuments(queryDocument);
        for (int i = 0; i < K_BEST_RESULTS; i++)
        {
            System.out.println("");
            String sc = String.format("%.4f", scores.get(i)._score).replace(',', '.');
            System.out.println(String.format("%d : score= %s, document title= \"%s\" (ID = %d)",
                                             (i + 1), sc,
                                             scores.get(i)._document._title,
                                             scores.get(i)._document._ID));
            System.out.println(scores.get(i)._document._content);
        }
    }

    private ArrayList <Document> loadDocuments() throws FileNotFoundException
    {
        ArrayList <Document> documents = new ArrayList <>(1024);

        Scanner scanner = new Scanner(new File("documents.txt"));

        StringBuilder sb = null;
        String title = "";
        int id = -1;

        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if ("".equals(line))
            {
                assert sb != null;
                id++;
                documents.add(new Document(title, sb.toString(), id));
                sb = null;
            } else
            {
                if (sb == null)
                {
                    sb = new StringBuilder();
                    title = line;
                }
                sb.append(line).append("\n");
            }
        }
        assert sb != null;
        id++;
        documents.add(new Document(title, sb.toString(), id));
        scanner.close();

        return documents;
    }

    private Dictionary loadDictionary() throws FileNotFoundException
    {
        Dictionary dictionary = new Dictionary(1024);

        Scanner scanner = new Scanner(new File("keywords.txt"));

        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            dictionary._keywords.add(line);
        }

        return dictionary;
    }
}
