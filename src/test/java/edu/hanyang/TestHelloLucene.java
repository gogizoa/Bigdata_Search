package edu.hanyang.BIR2018044220M1;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestHelloLucene {
	
	private static HelloLucene se = null;
	private static Directory index = null;
	private static Analyzer analyzer = null;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
    	String docs[][] = { 
    			{"Lucene in Action", "193398817"},
    			{"Lucene for Dummies", "55320055Z"},
    			{"Managing Gigabytes", "55063552A"},
    			{"The Art of Computer Science", "9900333X"}
    	};
    	
    	se = new HelloLucene();
	    
    	// 1. create index & add docs
	    analyzer = new StandardAnalyzer();
    	index = se.createIndex(docs, analyzer);
	}

	@Test
	public void test1() throws ParseException, IOException {
		String[][] hits = se.search(index, "lucene", analyzer);
        
		assertEquals(hits.length, 2);
		assertEquals(hits[0][1], "193398817");
		assertEquals(hits[1][1], "55320055Z");
	}

	@Test
	public void test2() throws ParseException, IOException {
		String[][] hits = se.search(index, "action", analyzer);
		assertEquals(hits.length, 1);
		assertEquals(hits[0][1], "193398817");
	}
	
	@Test
	public void test3() throws ParseException, IOException {
		String[][] hits = se.search(index, "computer", analyzer);
		assertEquals(hits.length, 1);
		assertEquals(hits[0][1], "9900333X");
	}
}
