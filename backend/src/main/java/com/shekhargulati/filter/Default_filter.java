package com.shekhargulati.filter;

import org.jsoup.nodes.Document;

import com.shekhargulati.pack.Pack;

/*
 * Default mock Filter for Filter instance in WebCrawler
 */
public class Default_filter implements Filter {
	
	private static Default_filter instance = null;
	
	private Default_filter() {}
	
	public static Default_filter get_filter() {
		if (instance == null) {
			instance = new Default_filter();
		}
		return instance;
	}
	
	public Pack check_relevance(Document doc) {
		return null;
	}

}
