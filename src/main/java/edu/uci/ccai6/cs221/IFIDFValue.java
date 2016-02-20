package edu.uci.ccai6.cs221;

import java.io.PrintWriter;
import java.util.List;

public class IFIDFValue {
	// docID:count,docID2:count2,docID3:count3...
	public String __value;
	public IFIDFValue(int docID, int count) {
		__value = docID + ":" + count;
	}
	public void add(int docID, int count) {
		__value += "," + docID + ":" + count;
	}
	public boolean has(int docID) {
		String[] list = __value.split(",");
		for (String one : list) {
			String[] list2 = one.split(":");
			if (docID == Integer.parseInt(list2[0])) return true;
		}
		return false;
	}
	public int getDF() {
		return __value.split(",").length;
	}
}
