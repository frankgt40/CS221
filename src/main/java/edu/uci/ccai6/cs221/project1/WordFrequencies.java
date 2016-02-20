package edu.uci.ccai6.cs221.project1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map.Entry;

import cue.lang.Counter;
import cue.lang.WordIterator;
import edu.uci.ccai6.cs221.CS221DB;

public class WordFrequencies {
	private List<Entry<String, Integer>> wordFrequencies;
	public static PrintWriter __out;
	public WordFrequencies() throws IOException {
		__out = new PrintWriter(new BufferedWriter(new FileWriter("result/wordFrequencies.txt", true)));
	}
	public List<Entry<String, Integer>> computeWordFrequencies() throws SQLException {
		Connection conn = CS221DB.getConn();
		Statement stmt = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(100);
		ResultSet resultSet = stmt.executeQuery("SELECT text FROM text_set;");
		Counter<String> words = new Counter<String>();
		while (resultSet.next()) {
			for (final String token : new WordIterator(resultSet.getString(1))) {
				words.note(token.trim().toLowerCase());
			}
		}
		wordFrequencies = words.getAllByFrequency(); // *cue.lang-getAllByFrequency
		return wordFrequencies;
	}

	public void print(List<Entry<String, Integer>> list) {
		for (Entry<String, Integer> entry : list) {
			__out.println("Token: \"" + entry.getKey() + "\";\tCount: " + entry.getValue());
		}
		__out.flush();
	}
}
