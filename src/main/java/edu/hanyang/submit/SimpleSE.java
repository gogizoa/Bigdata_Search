package edu.hanyang.BIR2018044220M1;

import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;

public interface SimpleSE 
{
	Directory createIndex(String[][] docs, Analyzer analyzer) throws IOException;
	
	String[][] search(Directory index, String querystr, Analyzer analyzer) throws ParseException, IOException;
}
