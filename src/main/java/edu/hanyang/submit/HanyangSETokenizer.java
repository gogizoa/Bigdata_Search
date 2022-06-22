package edu.hanyang.submit;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.ext.PorterStemmer;

import io.github.hyerica_bdml.indexer.Tokenizer;


public class HanyangSETokenizer implements Tokenizer {
	private Analyzer analyzer = null;
	private PorterStemmer s = null;
	public void setup() {
		analyzer = new SimpleAnalyzer();
		s = new PorterStemmer();
		}
	public List<String> split(String text) {
		List<String> result = new ArrayList<String>();
		try {
			TokenStream stream = analyzer.tokenStream(null, new StringReader(text));
			stream.reset();
			while (stream.incrementToken()) {
				result.add(stemString(
					stream.getAttribute(
					CharTermAttribute.class).toString()));
					}
				stream.close();
		} catch (IOException e) {
		throw new RuntimeException(e);
		}
		return result;
		}
	public void clean() { analyzer.close(); }
	private String stemString(String word) { s.setCurrent(word); s.stem(); return s.getCurrent(); }
}