package search;

import engine.Dictionary;
import engine.Document;

import java.util.ArrayList;

public interface ISearch
{
    ArrayList <Score> getSortedDocuments(Document query);
    ArrayList <Score> getSortedDocuments(double queryVector[]);

    String getName();
}
