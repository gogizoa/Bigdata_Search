package edu.hanyang.BIR2018044220M1;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestHelloHolmes {
	
	private static HelloHolmes se = null;
	private static Directory index = null;
	private static Analyzer analyzer = null;
	
	private static String[][] read_docs () throws IOException {
    	ClassLoader classLoader = TestHelloHolmes.class.getClassLoader();
    	File path = new File(classLoader.getResource("244-8.txt").getFile());
		
		List<String> linelist = Files.readAllLines(path.toPath(), StandardCharsets.ISO_8859_1);
		String[][] lines = new String[linelist.size()][2];
		
		int idx = 0;
		for (String line: linelist) {
			lines[idx][0] = Integer.toString(idx);
			lines[idx][1] = line;
			idx++;
		}
		
		return lines;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
    	String docs[][] = read_docs();
    	
    	se = new HelloHolmes();
	    
    	// 1. create index & add docs
	    analyzer = new StandardAnalyzer();
    	index = se.createIndex(docs, analyzer);
	}

	@Test
	public void test1() throws ParseException, IOException {
		String[][] hits = se.search(index, "holmes", analyzer);
        
		assertEquals(hits[0][0], "2226");
		assertEquals(hits[1][0], "1708");
	}

	@Test
	public void test2() throws ParseException, IOException {
		String[][] hits = se.search(index, "watson", analyzer);

		assertEquals(hits[0][0], "4090");
		assertEquals(hits[1][0], "138");
	}
	
	@Test
	public void test3() throws ParseException, IOException {
		String[][] hits = se.search(index, "murder", analyzer);
		
		assertEquals(hits[0][0], "4590");
		assertEquals(hits[1][0], "4397");
	}
}
