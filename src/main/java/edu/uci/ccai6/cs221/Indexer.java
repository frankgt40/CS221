package edu.uci.ccai6.cs221;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Indexer {
	public static PrintWriter __preIndexOut;
	public static PrintWriter __positionsOut;
	private static Map<String, IFIDFValue> __index = new HashMap<String, IFIDFValue>();
	private static Map<String, IFIDFValue> __positions = new HashMap<String, IFIDFValue>();
	public static void initializer() {
		try {
			// __out = new PrintWriter(new BufferedWriter(new
			// FileWriter("result/logs.txt", true)));
			__preIndexOut = new PrintWriter(new BufferedWriter(new FileWriter("result/preIndexOut.txt", false)));
			__positionsOut = new PrintWriter(new BufferedWriter(new FileWriter("result/positionsOut.txt", false)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void outputResult(Map<String, IFIDFValue> rsl, PrintWriter writer) {
		Set<String> tokenSet = rsl.keySet();
		List<String> tokenList = new ArrayList<String>(tokenSet);
		Collections.sort(tokenList);
		for (String token : tokenList) {
			writer.println(token+"$$$"+rsl.get(token).__value);
		}
		writer.flush();
	}
	public static void main(String args[]){
		initializer();
		CS221DB.buildPreIndexTable(130000, __index, __positions);
		outputResult(__index, __preIndexOut);
		outputResult(__positions, __positionsOut);
		__preIndexOut.close();
		__positionsOut.close();
	}
}
