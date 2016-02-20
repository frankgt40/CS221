package edu.uci.ccai6.cs221;

import java.io.PrintWriter;

public class IFIDFItem {
	public int __docID = -1;
	public String __token = null;
	public int __count = -1;
	public IFIDFItem(int __docID, String __token, int __count) {
		super();
		this.__docID = __docID;
		this.__token = __token;
		this.__count = __count;
	}
	public void writeToFile(PrintWriter out) {
		out.println(__token + ","+__docID+","+__count);
	}
}
