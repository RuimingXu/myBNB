package com.shekhargulati.filter;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.shekhargulati.pack.Pack;

public class CrawlerFilter implements Filter {
	
	public final String houseID;
	public final String address;
	public final String rent;
	public final String postDate;
	public final String description;
	
	public CrawlerFilter() {
		this.houseID = "a[class^=adId]";
		this.address = "span.address-3617944557";
		this.rent = "span.currentPrice-441857624";
		this.postDate = "div.datePosted-319944123";
		this.description = "div[class^=descriptionContainer]";
	}
	
	/*
	 * Reading website is those that contain house information by looking for house ID
	 */
	public Pack check_relevance(Document doc) {
		Elements theID = doc.select(this.houseID);
		
		if (theID.size() == 1) {
			Elements theAddress = doc.select(this.address);
			Elements theRent = doc.select(this.rent);
			Elements theDate = doc.select(this.postDate);
			Elements theDescription = doc.select(this.description);
			Pack pack = new Pack.Builder(theID.text(), theAddress.text(), theRent.text())
					.setDate(theDate.text())
					.setDescription(theDescription.text())
					.build();
			return pack;
		}
		return null;
	}

}
