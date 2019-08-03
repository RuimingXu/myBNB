package com.shekhargulati.filter;

import org.jsoup.nodes.Document;

import com.shekhargulati.pack.Pack;

public interface Filter {

	Pack check_relevance(Document doc);

}
