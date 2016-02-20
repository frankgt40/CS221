package edu.uci.ccai6.cs221;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;

import cue.lang.Counter;
import cue.lang.NGramIterator;
import cue.lang.WordIterator;
import cue.lang.stop.StopWords;
import cue.lang.stop.*;

public class CS221DB {
	public static String url = "jdbc:mysql://localhost:3306/icsweb?useUnicode=true&characterEncoding=utf-8&useSSL=false";
	public static String user = "testuser";
	public static String password = "test623";
	private static Connection con = null;
	public static StopWords stopWords = StopWords.English;
	public static Pattern p = Pattern.compile("\\W");

	static {
		try {
			con = DriverManager.getConnection(url, user, password);
			// TRUNCATE TABLE tablename;
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(CS221DB.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		}
		// deleteData("logs_set");
	}

	public CS221DB() {

	}

	public static Connection getConn() {
		return con;
	}

	public static boolean deleteData(String table) {
		try {
			PreparedStatement pst;
			ResultSet rsl;
			int rsl2;
			// Insert the id and text
			pst = con.prepareStatement("TRUNCATE " + table);
			// pst.setString(1, table);
			rsl2 = pst.executeUpdate();
			if (rsl2 == 0)
				return false;
			else
				return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static void close() {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean insertLog(int docid, String url, String domain, String subDomain, String path,
			String parentUrl, String anchor, int textLength, int htmlLength, int outgoingLinks, String header) {

		try {
			PreparedStatement pst = con.prepareStatement("INSERT INTO logs_set (Docid, " + "URL, " + "Domain, "
					+ "Sub_domain," + "Path," + "Parent_page, " + "Ancho_text, " + "Text_length, " + "Html_length,"
					+ "Number_of_outgoing_links," + "header) " + "VALUES(?,?,?,?,?,?,?,?,?,?,?);");
			pst.setInt(1, docid);
			pst.setString(2, url);
			pst.setString(3, domain);
			pst.setString(4, subDomain);
			pst.setString(5, path);
			pst.setString(6, parentUrl);
			pst.setString(7, anchor);
			pst.setInt(8, textLength);
			pst.setInt(9, htmlLength);
			pst.setInt(10, outgoingLinks);
			pst.setString(11, header);
			int rsl = pst.executeUpdate();
			if (rsl == 0)
				return false;
			else
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insertLostURL(String url) {

		try {
			PreparedStatement pst = con.prepareStatement("INSERT INTO lost_url (URL) VALUES(?);");
			pst.setString(1, url);
			int rsl = pst.executeUpdate();
			if (rsl == 0)
				return false;
			else
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isAGoodToken(String token) {
		Matcher m = p.matcher(token);
		if (m.find())
			return false;
		if (stopWords.isStopWord(token))
			return false;
		if (NumberUtils.isNumber(token))
			return false;
		// if (token.contains("_"))
		// return false;
		// if (token.contains("-"))
		// return false;
		// if (token.contains("$"))
		// return false;
		// if (token.contains("@"))
		// return false;
		// if (token.contains("'"))
		// return false;
		// if (token.contains("?"))
		// return false;
		// if (token.contains("."))
		// return false;
		// if (token.contains("¡¯"))
		// return false;
		// if (token.contains(":"))
		// return false;
		// if (token.contains("/"))
		// return false;

		return true;
	}

	/*
	 * If limit equals -1, query unlimited number of result
	 */
	public static void buildPreIndexTable(int limit, Map<String, IFIDFValue> index, Map<String, IFIDFValue> positions) {
		// List<IFIDFItem> list = new ArrayList<IFIDFItem>();
		try {
			Statement stmt = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			//stmt.setFetchSize(Integer.MIN_VALUE);
			stmt.setFetchSize(4000);
			ResultSet resultSet;
			if (limit < 0) {
				resultSet = stmt.executeQuery("SELECT * FROM text_set;");
			} else {
				resultSet = stmt.executeQuery("SELECT * FROM text_set limit " + limit + ";");
			}

			Counter<String> words = new Counter<String>();
			while (resultSet.next()) {
				int urlID = resultSet.getInt(1);
				String text = resultSet.getString(2);
				int i = 1;
				for (String token : new WordIterator(text)) {
					// for (final String token : new NGramIterator(1,
					// resultSet.getString(1), Locale.ENGLISH,
					// StopWords.English)) {
					token = token.trim().toLowerCase();
					if (isAGoodToken(token)) {
						words.note(token);
						// Get positions
						if (positions.containsKey(token)) {
							positions.get(token).add(urlID, i);
						} else {
							IFIDFValue value = new IFIDFValue(urlID, i);
							positions.put(token, value);
						}
					}

					i++;
				}
				List<Entry<String, Integer>> wordFrequencies = words.getAllByFrequency(); // *cue.lang-getAllByFrequency
				for (Entry<String, Integer> item : wordFrequencies) {
					// Get if idf
					if (index.containsKey(item.getKey())) {
						index.get(item.getKey()).add(urlID, item.getValue());
					} else {
						IFIDFValue value = new IFIDFValue(urlID, item.getValue());
						index.put(item.getKey(), value);
					}

				}
				words.clear();
				System.out.println("Document " + urlID + " is finished!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return list;
	}

	public static List<String> getAllURL() {
		List<String> urlList = new ArrayList<String>();
		try {
			PreparedStatement pst = con.prepareStatement("SELECT URL FROM urlSet;");
			ResultSet rsl = pst.executeQuery();
			while (rsl.next()) {
				String url = rsl.getString(1);
				urlList.add(url);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlList;
	}

	public static void outputText() {
		try {
			PreparedStatement pst = con.prepareStatement("SELECT * FROM text_set INTO OUTFILE 'result/text.txt';");
			ResultSet rsl = pst.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean insertTxtPage(String url, String text) {
		try {
			PreparedStatement pst;
			ResultSet rsl;
			// Insert this url and get the urlID
			pst = con.prepareStatement("SELECT id_URL FROM urlset AS t WHERE t.URL = ?;");
			pst.setString(1, url);
			rsl = pst.executeQuery();
			int id = 0;
			if (!rsl.next()) {
				// if urlset does not have this url
				insertUrl(url);
				rsl = pst.executeQuery();
			}
			id = rsl.getInt(1); // Get the ID!

			// And search whether this text page exists
			pst = con.prepareStatement("SELECT id_URL FROM text_set AS t WHERE t.id_URL = ?;");
			pst.setInt(1, id);
			rsl = pst.executeQuery();
			if (rsl.next()) {
				// There already is a one!
				return false;
			} else {
				// There is no such text page, so insert it!
				int rsl2;
				// Insert the id and text
				pst = con.prepareStatement("INSERT INTO text_set (id_URL, text)  VALUES(?,?);");
				pst.setInt(1, id);
				pst.setString(2, text);
				rsl2 = pst.executeUpdate();
				if (rsl2 == 0)
					return false;
				else
					return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insertHTMLPage(String url, String htmlPage) {
		try {
			PreparedStatement pst;
			ResultSet rsl;
			// Insert this url and get the urlID
			pst = con.prepareStatement("SELECT id_URL FROM urlset AS t WHERE t.URL = ?;");
			pst.setString(1, url);
			rsl = pst.executeQuery();
			int id = 0;
			if (!rsl.next()) {
				// if urlset does not have this url
				insertUrl(url);
				rsl = pst.executeQuery();
			}
			id = rsl.getInt(1); // Get the ID!

			// And search whether this html page exists
			pst = con.prepareStatement("SELECT id_URL FROM html_set AS t WHERE t.id_URL = ?;");
			pst.setInt(1, id);
			rsl = pst.executeQuery();
			if (rsl.next()) {
				// There already is a one!
				return false;
			} else {
				// There is no such html page, so insert it!
				int rsl2;
				// Insert the id and html
				pst = con.prepareStatement("INSERT INTO html_set (id_URL, html)  VALUES(?,?);");
				pst.setInt(1, id);
				pst.setString(2, htmlPage);
				rsl2 = pst.executeUpdate();
				if (rsl2 == 0)
					return false;
				else
					return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean hasThisURL(String url) {
		PreparedStatement pst;
		ResultSet rsl;
		try {
			pst = con.prepareStatement("SELECT * FROM urlset AS t WHERE t.URL = ?;");
			pst.setString(1, url);
			rsl = pst.executeQuery();
			if (rsl.next()) {
				System.out.println("Omit one url: " + rsl.getString(2));
				return true;
			} else
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insertUrl(String url) {
		if (hasThisURL(url))
			return false;
		PreparedStatement pst;
		int rsl;
		try {
			pst = con.prepareStatement("INSERT INTO urlset (URL)  VALUES(?);");
			pst.setString(1, url);
			rsl = pst.executeUpdate();
			if (rsl == 0)
				return false;
			else
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}
}
