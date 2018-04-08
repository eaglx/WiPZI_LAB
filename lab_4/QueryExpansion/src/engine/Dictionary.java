package engine;

import opennlp.tools.stemmer.PorterStemmer;

import java.util.*;

public class Dictionary
{
    public ArrayList <String> _keywords;
    public ArrayList <String> _terms;
    public HashMap <String, String> _termsToKeywords;
    public ArrayList <Double> _idf;

    public HashMap <String, Integer> _termID;

    public Dictionary(int capacity)
    {
        this._keywords = new ArrayList <>(capacity);
    }

    public void computeIDFs(ArrayList <Document> documents)
    {
        // DONE compute idfs values
        int size = _terms.size();
        _idf = new ArrayList <>(size);
        int counter = 0;
        // 1) iterate over the _terms
        for(String term : _terms) {
            for(Document document: documents) {
                // 2) check how many documents contain this term (document._terms.contains())
                if(document._terms.contains(term))
                    ++counter;
            }
            // 3) compute idf value (Math.log()) and update _idf vector (add idf value)
            if(counter > 0) {
                _idf.add(Math.log((double)size / (double)counter));
                //_idf.add(Math.log(counter/size)/Math.log(2));
            } else {
                _idf.add(0.0);
            }
                counter = 0;
        }
    }

    public void doProcessing(PorterStemmer stemmer, boolean log)
    {
        Set <String> stemmed = new HashSet <>();
        _termsToKeywords = new HashMap <>(_keywords.size());
        _terms = new ArrayList <>(_keywords.size());
        for (String s : _keywords)
        {
            String stem = stemmer.stem(s);
            stemmed.add(stem);
            _termsToKeywords.put(stem, s);
        }

        _terms.addAll(stemmed);
        Collections.sort(_terms);

        _termID = new HashMap <>();
        for (int i = 0; i < _terms.size(); i++)
            _termID.put(_terms.get(i), i);

        if (log)
        {
            for (String s : _terms)
                System.out.println(s);
            System.out.println(_terms.size() + " " + _keywords.size());
        }
    }

    void print()
    {
        for (int i = 0; i < _terms.size(); i++)
        {
            System.out.println(i + " : " + _terms.get(i) + "   idf = " + _idf.get(i));
        }
    }
}
