package edu.hanyang.BIR2018044220M1;


import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class HelloHolmes implements SimpleSE{

	public Directory createIndex(String[][] docs, Analyzer
			analyzer) throws IOException {
			// 1. Index
			Directory index = new RAMDirectory();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter w = new IndexWriter(index, config);
			for (String[] doc: docs) {
			addDoc(w, doc[0], doc[1]);
			}
			w.close();
			return index;
	}
	
	public String[][] search(Directory index, String querystr, Analyzer analyzer) throws ParseException, IOException{
		Query q = new QueryParser("linetext",analyzer).parse(querystr);
		
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		String[][] result = new String[hits.length][2];
		for(int i=0; i<hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			result[i][0] = d.get("linenumber");
			result[i][1] = d.get("isbn");
		}
		
		reader.close();
		
		return result;
	}
	
	public void addDoc(IndexWriter w, String linenumber, String linetext)throws IOException{
		Document doc = new Document();
		
		doc.add(new StringField("linenumber", linenumber, Field.Store.YES));
		doc.add(new TextField("linetext", linetext, Field.Store.YES));
		
		w.addDocument(doc);
	}
}
