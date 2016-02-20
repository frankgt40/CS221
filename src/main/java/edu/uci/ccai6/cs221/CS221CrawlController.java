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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * @author Cheng Cai
 */
public class CS221CrawlController {
	private static final Logger logger = LoggerFactory.getLogger(CS221CrawlController.class);
	public static long startTime = 0;// System.currentTimeMillis();
	public static long finalTime = 0;
	public static long elapsedTime = 0;
	// public static PrintWriter __out;
	public static PrintWriter __Q1to2Out;
	public static PrintWriter __out;
	public static PrintWriter __Q3Out;
	public static PrintWriter __URLOut;
	public static PrintWriter __TextOut;
	public static BufferedReader __urlBr;

	public static void main(String[] args) throws Exception {
		try {
			// __out = new PrintWriter(new BufferedWriter(new
			// FileWriter("result/logs.txt", true)));
			__Q1to2Out = new PrintWriter(new BufferedWriter(new FileWriter("result/q1to2.txt", true)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * crawlStorageFolder is a folder where intermediate crawl data is
		 * stored.
		 */
		String crawlStorageFolder = "result";// args[0];

		/*
		 * numberOfCrawlers shows the number of concurrent threads that should
		 * be initiated for crawling.
		 */
		int numberOfCrawlers = 10;// Integer.parseInt(args[1]);

		CrawlConfig config = new CrawlConfig();

		config.setCrawlStorageFolder(crawlStorageFolder);

		/*
		 * Be polite: Make sure that we don't send more than 1 request per
		 * second (1000 milliseconds between requests).
		 */
		config.setPolitenessDelay(1000);

		/*
		 * You can set the maximum crawl depth here. The default value is -1 for
		 * unlimited depth
		 */
		config.setMaxDepthOfCrawling(-1);
		/*
		 * You can set the maximum number of pages to crawl. The default value
		 * is -1 for unlimited number of pages
		 */
		config.setMaxPagesToFetch(-1);

		/**
		 * Do you want crawler4j to crawl also binary data ? example: the
		 * contents of pdf, or the metadata of images etc
		 */
		config.setIncludeBinaryContentInCrawling(false);

		/*
		 * Do you need to set a proxy? If so, you can use:
		 * config.setProxyHost("proxyserver.example.com");
		 * config.setProxyPort(8080);
		 *
		 * If your proxy also needs authentication:
		 * config.setProxyUsername(username); config.getProxyPassword(password);
		 */

		/*
		 * This config parameter can be used to set your crawl to be resumable
		 * (meaning that you can resume the crawl from a previously
		 * interrupted/crashed crawl). Note: if you enable resuming feature and
		 * want to start a fresh crawl, you need to delete the contents of
		 * rootFolder manually.
		 */
		config.setResumableCrawling(true);
		config.setSocketTimeout(300000);
		config.setConnectionTimeout(300000);
		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		controller.addSeed("http://www.ics.uci.edu");
		// controller.addSeed("http://www.frankgt40.com");
		// controller.addSeed("http://www.ics.uci.edu/~welling/");
		/*
		 * Set up agent string
		 */

		config.setUserAgentString("IR W16 WebCrawler 95164901 58203736 30842617");
		// String uuid = UUID.randomUUID().toString();
		// config.setUserAgentString(uuid);
		// System.out.println("This time we play: " + uuid);
		startTime = System.currentTimeMillis();
		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		controller.start(CS221Crawler.class, numberOfCrawlers);
		finalTime = System.currentTimeMillis();
		elapsedTime = finalTime - startTime;
		System.out.println("Time for crawl: " + (double) elapsedTime / 1000.0 + "s");

		__Q1to2Out.println("Total time for crawling: " + (double) elapsedTime / 1000.0 + "s.");
		__Q1to2Out.flush();
		__Q1to2Out.close();
		CS221DB.close();
	}
}