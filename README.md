# WiPZI_LAB
Search and processing of information resources lab

## Content ##

### lab_1 - WebCrawling ###

Web crawling is a process of graph exploration and collecting data. Information which was gathered may be used then to:

* build a search engine (Google, etc.),
* analyse users behaviour (e.g., which pages are visited frequently, how users move
through the web?),
* business intelligence (what are the current actions of my opponents or partners?),
* collecting e-mail addresses (phishing, sending spam). 

### lab_2 - ApacheTika ###

Apache Tika: toolkit for detecting and extracting metadata and text from over
a thousand different file types (such as DOC, PPT, XLS and PDF; http://tika.apache.org):

* Apache Tika provides methods for automatic file type detection.

* Tika provides support for the most popular metadata formats and tools for defining own
ones.

### lab_3 - Text processing + Apache OpenNLP ###

Apache Open NLP: open source Java library for Natural Language Processing (NLP). It 
supports the most common NLP tasks such as:

* tokenization,

* sentence segmentation,

* part-of-speech tagging,

* named entity extraction,

* chunking,

* parsing,

* language detection,

* coreference resolution.

### lab_4 - Query Expansion ###

The performance of every search engine strongly depends on the query provided by the user. By expressing his or her question differently, different results, e.g., rankings of the most relevant pages, may be obtained. Thus, a good search engine should aid the user in (re)formulating a query. This involves, e.g., suggesting new words like synonyms, various morphological forms, or words that frequently co-occur with the words of the provided query. The search engine may also fix spelling errors (“neighborhood” -> “neighbourhood”). Some techniques are more decision-aiding oriented. For instance, the search engine may allow adjusting weights of the words of the query. Such adjustment may be based on the selection of (ir)relevant documents (user’s feedback). The goal of such techniques is to improve the retrieval process and help the user to find answers that are the most relevant to him or her.

### lab_5 - HITS + Page Rank + Trust Rank ###

Web structure mining is focused on discovering
a topology of the web. In particular, a structure that is represented by hyperlinks (edges) that connect
some pages (nodes). Links and web pages constitute a directed graph. The goal of web structure
mining is to generate a structural summary of this graph. This can be, e.g.,:

* finding similarities or relations between web sites,

* categorizing web sites: for crawling policy, for ranking purposes (search engines),

* revealing the structure of a web site (navigation purposes).
