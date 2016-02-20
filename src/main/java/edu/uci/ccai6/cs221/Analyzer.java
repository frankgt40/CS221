package edu.uci.ccai6.cs221;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import edu.uci.ccai6.cs221.project1.ThreeGramsProducer;
import edu.uci.ccai6.cs221.project1.TwoGramsProducer;
import edu.uci.ccai6.cs221.project1.WordFrequencies;

public class Analyzer {
	public static Map<String, Integer> domainPageCounter = new HashMap<String, Integer>();
	public static void q3() {
		List<String> urlList = CS221DB.getAllURL();
		for (String url : urlList) {
			String key = url.toLowerCase().trim();
			int beginIndex = 0;
			if (key.startsWith("https://"))
				beginIndex = "https://".length();
			else 
				beginIndex = "http://".length();
			int endIndex = key.indexOf(".ics.uci.edu") + 1;
			key = key.substring(beginIndex, endIndex);
			if (domainPageCounter.containsKey(key)) {
				Integer num = domainPageCounter.get(key);
				domainPageCounter.put(key, num+1);
			} else {
				domainPageCounter.put(key, 1);
			}
		}
		SortedSet<String> keys = new TreeSet<String>(domainPageCounter.keySet());
		for (String key : keys) { 
		   Integer value = domainPageCounter.get(key);
		   // do something
		   System.out.println("http://" + key + "ics.uci.edu, " + value);
		}

	}
	public static void main(String args[]) throws SQLException, IOException {
		//q3();
		//CS221DB.outputText();
		WordFrequencies wf = new WordFrequencies();
		List<Entry<String, Integer>> lf = wf.computeWordFrequencies();
		wf.print(lf);
		
		ThreeGramsProducer threeGram = new ThreeGramsProducer();
		List<Entry<String, Integer>> lf2 = threeGram.computeThreeGramFrequencies();
		threeGram.print(lf2);
		
		TwoGramsProducer twoGram = new TwoGramsProducer();
		List<Entry<String, Integer>> lf3 = twoGram.computeTwoGramFrequencies();
		twoGram.print(lf3);
		System.out.println("Finished!");
	}
}
