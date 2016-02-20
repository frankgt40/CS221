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
import java.util.Locale;
import java.util.Map.Entry;

import cue.lang.Counter;
import cue.lang.NGramIterator;
import cue.lang.stop.StopWords;
import edu.uci.ccai6.cs221.CS221DB;

public class TwoGramsProducer {

	public static PrintWriter __out;

	public TwoGramsProducer() throws IOException {
		__out = new PrintWriter(new BufferedWriter(new FileWriter("result/2Gram.txt", true)));
	}

	public List<Entry<String, Integer>> computeTwoGramFrequencies() throws SQLException {
		Connection conn = CS221DB.getConn();
		Statement stmt = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(100);
		ResultSet resultSet = stmt.executeQuery("SELECT text FROM text_set;");
		final Counter<String> ngrams = new Counter<String>();
		while (resultSet.next()) {
			for (final String ngram : new NGramIterator(2, resultSet.getString(1), Locale.ENGLISH, StopWords.English)) {
				ngrams.note(ngram.toLowerCase(Locale.ENGLISH));
			}
		}
		List<Entry<String, Integer>> rsl = ngrams.getAllByFrequency();
		return rsl;

	}

	public void print(List<Entry<String, Integer>> lsit) {
		for (Entry<String, Integer> each : lsit) {
			__out.println("2-Gram: \"" + each.getKey() + "\"\t Count: " + each.getValue() + ".");
		}
		__out.flush();
	}
}
