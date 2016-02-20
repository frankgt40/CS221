/*
 * Author: Bojun Wang
 * Referenced Libraries and code: 
 * 1.https://github.com/jdf/cue.language  
 * 2.http://rosettacode.org/wiki/Anagrams#Java
 */
package edu.uci.ccai6.cs221.project1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cue.lang.WordIterator;

public class Tokenizer {
	private String fileName;
	private BufferedReader reader;
	private List<String> tokenList;
	private String buffer = "";
	
	public String getFileName() {
		return fileName;
	}

	public List<String> getTokenList() {
		return tokenList;
	}

	public String getBuffer() {
		return buffer;
	}


	public Tokenizer(String name) {
		this.fileName = name;
		try {
			this.reader = new BufferedReader(new FileReader(this.fileName));
			String line = "";
			while ((line = this.reader.readLine()) != null) {
				this.buffer += line;
				this.buffer += "\n";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public List<String> tokenizeFile() {
		tokenList = new ArrayList<String>();
		for (String word : new WordIterator(this.buffer)) {  //*cue.lang-WordIterator
			if (!this.tokenList.contains(word.toLowerCase()))
				this.tokenList.add(word.toLowerCase());
		}
		Collections.sort(tokenList);
		return tokenList;
	}
	
	
	public void print(List<String> list) {
		System.out.println("Part A. Tokenizer: ");
		for (String word : list)
			System.out.println(word);
	}
	
}
