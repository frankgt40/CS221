/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.uci.ccai6.cs221;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpStatus;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.BufferedReader;

import java.io.FileReader;

/**
 * @author Cheng Cai
 */
public class CS221Crawler extends WebCrawler {
	private static final Pattern __Filter = Pattern
			.compile(".*(\\.(css|js|gif|jpg|png|mp3|pdf|ps|ppt|pptx|doc|docx|zip|gz|tgz))$");

	public boolean containsURL(String url) {
		return false;
	}

	public void countURL(String url) {

	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		// Ignore the url if it has an extension that matches our defined set of
		// image extensions.
		if (__Filter.matcher(href).matches()) {
			return false;
		}
		String domain = url.getDomain();
		if (domain.equals("uci.edu")) {
			String subDomain = url.getSubDomain();
			if (subDomain.endsWith(".ics")) {
				int code = referringPage.getStatusCode();
				if (code == 403 || code == 404) {
					CS221DB.insertLostURL(referringPage.getWebURL().getURL());
					return false;
				}
				if (href.contains("archive.ics.uci.edu")) {
					CS221DB.insertLostURL(href);
					return false;
				}
				return true;
			}
		}
		return false;
		// Only accept the url if it is in the "www.ics.uci.edu" domain and
		// protocol is "http".
		// return href.startsWith("http://www.frankgt40.com");
		// return href.startsWith("http://"+domain+"ics.uci.edu");
	}

	@Override
	public void visit(Page page) {
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();
		String anchor = page.getWebURL().getAnchor();
		if (url.contains("archive.ics.uci.edu")) {
			CS221DB.insertLostURL(url);
			return;
		}
		logger.info("URL: {}", url);
		HtmlParseData htmlParseData;
		String text = "";
		String html = "";
		String header = "";
		Set<WebURL> links = null;
		int outGoing = 0;
		int textLength = 0;
		int htmlLength = 0;
		if (page.getParseData() instanceof HtmlParseData) {
			htmlParseData = (HtmlParseData) page.getParseData();
			text = htmlParseData.getText();
			html = htmlParseData.getHtml();
			links = htmlParseData.getOutgoingUrls();

			outGoing = text.length();
			htmlLength = html.length();
			outGoing = links.size();

			// if (!containsURL(url))
			// __TextOut.print(text);
		}

		Header[] responseHeaders = page.getFetchResponseHeaders();
		if (responseHeaders != null) {
			logger.debug("Response headers:");
			for (Header headeri : responseHeaders) {
				header += headeri.getName() + ":" + headeri.getValue() + ", ";
			}
		}
		CS221DB.insertLog(docid, url, domain, subDomain, path, parentUrl, anchor, textLength, htmlLength, outGoing, header);
		CS221DB.insertUrl(url);
		CS221DB.insertHTMLPage(url, html);
		CS221DB.insertTxtPage(url, text);
	}
	
	@Override
	  protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {

	    if (statusCode != HttpStatus.SC_OK) {

	      if (statusCode == HttpStatus.SC_NOT_FOUND || statusCode == HttpStatus.SC_FORBIDDEN) {
	    	  CS221DB.insertLostURL(webUrl.getURL());
	      } 
	    } 
	    super.handlePageStatusCode(webUrl, statusCode, statusDescription);
	  }
}