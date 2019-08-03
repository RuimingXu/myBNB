package com.shekhargulati.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.*;
import java.net.URLEncoder;

import org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shekhargulati.crawler.Crawler;
import com.shekhargulati.crawler.Assignment3WebCrawler.Builder;
import com.shekhargulati.database.Database;
import com.shekhargulati.filter.CrawlerFilter;
import com.shekhargulati.pack.Pack;

@RestController
@RequestMapping("/control")
public class Controller {

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public Set<Pack> search(
			@RequestParam(value = "seed", defaultValue = "https://www.kijiji.ca/v-apartments-condos/oakville-halton-region/bachelor-studio-basement-apartment-for-rent-in-mississauga/1448802776?enableSearchNavigationFlag=true")String seed,
			@RequestParam(value = "size", defaultValue = "10") int size)
			throws ProcessingFailureException {
		Database.createDatabase("Assignment3");
		Connection conn = Database.connect("Assignment3");
		if (conn == null) {
			throw new ProcessingFailureException();
		}
		Database.clearTable(conn);
		Database.createTable(conn);
		Set<Pack> result = this.setGeo(this.doCrawlFromSeed(seed, size));
		if (result == null) {
			return new HashSet<>();
		}
		this.doAddData(conn, result);
		return result;
	}

	@RequestMapping(value = "/getMax", method = RequestMethod.GET)
	public Map<String, String> getMaxField(
			@RequestParam(value = "field", defaultValue = "rent") String field)
			throws ProcessingFailureException {
		Connection conn = Database.connect("Assignment3");
		if (conn == null) {
			throw new ProcessingFailureException();
		}
		Map<String, String> res = new HashMap<>();
		res.put("max", Database.getMaxVal(conn, field));
		return res;
	}

	@RequestMapping(value = "/initData", method = RequestMethod.GET)
	public Set<Pack> prepopulate() throws ProcessingFailureException {
		Connection conn = Database.connect("Assignment3");
		if (conn == null) {
			throw new ProcessingFailureException();
		}
		Set<Pack> initPacks = Database.getAll(conn);
		Set<Pack> result = this.setGeo(initPacks);
		if (result == null) {
			return new HashSet<>();
		}
		return result;
	}


	@RequestMapping(method = RequestMethod.GET)
	public Map<String, String> getKey() {
		Map<String, String> res = new HashMap<>();
		res.put("key", this.readKey());
		return res;
	}

	/**
	 * read all the input Scanner's content and combine to one String
	 * @param sc
	 * @return String of all the content in sc
	 */
	private String readData(Scanner sc) {
		String overall = "";
		while(sc.hasNext()) {
			overall = overall + sc.nextLine() ;
		}
		return overall;
	};

	/**
	 *  Fetch MapQuest API key for the frontend
	 * @return MapQuest API key for the frontend
	 */
	private String readKey() {
		String res = "";
		try {
			File file = new File(System.getProperty("user.dir") + File.separator + "secret.json");
			Scanner sc = new Scanner(new FileReader(file));
			String temp = this.readData(sc);
			sc.close();
			JSONParser parse = new JSONParser();
			JSONObject holder = (JSONObject)parse.parse(temp);
			res = holder.get("mapQuest").toString();
		} catch (IOException | ParseException e) {
			return res;
		}
		return res;
	}

	/**
	 * Set the Geo for the input pack set
	 * @param init
	 * @return update set of Pack
	 */
	private Set<Pack> setGeo(Set<Pack> init) {
//		System.out.println("Found GEO = " );
		String address = "http://www.mapquestapi.com/geocoding/v1/batch?key=qhgE0N5aBueikpkkAPu2GlhFH8tuuAKX";
		String params = "";
		Map<String, Pack> holder= new HashMap<>();
		try {
			for (Pack x : init) {
				String temp = x.getAddress();
				holder.put(temp, x);
				params = params + "&location=" + URLEncoder.encode(temp,"UTF-8");
			}
			if (params.length() == 1) {
				return init;
			}
			URL url = new URL(address+params);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			if (conn.getResponseCode() != 200) {
				throw new IOException();
			}
			String overall;
			Scanner sc = new Scanner(url.openStream());
			overall = this.readData(sc);
			sc.close();
			conn.disconnect();
			JSONParser parse = new JSONParser();
			JSONObject res = (JSONObject)parse.parse(overall);
			JSONArray results = (JSONArray)res.get("results");
			for (int i = 0; i < results.size(); i++)
			{
				JSONObject curr = (JSONObject) results.get(i);
				JSONObject link = (JSONObject) curr.get("providedLocation");
				JSONArray locate = (JSONArray) curr.get("locations");
				Pack pack = holder.get(link.get("location").toString());
				if (locate.size() != 0) {
					JSONObject tempJson = (JSONObject)((JSONObject) locate.get(0)).get("latLng");
					pack.setLatitude(tempJson.get("lat").toString());
					pack.setLongitude(tempJson.get("lng").toString());
				}
			}
		} catch (IOException | ParseException e){
			e.printStackTrace();
			return init;
		}
		return init;
	}



	/**
	 * Initialize database table for our application
	 * @return connection to the created database
	 */
//	private Connection doCreateTable() {
//		String name = "Assignment3";
//    	Database.createDatabase(name);
//    	Connection conn = Database.connect(name);
//    	Database.createTable(conn);
//    	return conn;
//	}

	/**
	 * Crawl from the seed provided from user with max number of links given
	 * @return
	 */
	private Set<Pack> doCrawlFromSeed(String seed, int maxSize) {
		// New crawler
		Crawler crawler = new Builder(seed)
				.maxThreadNumber(10)
				.maxSize(maxSize) // Crawl maxSize relevant websites
				.addFilter(new CrawlerFilter())
				.build();
		Set<Pack> result = crawler.start();
		return result;
	}

	/**
	 * Add the result from crawling process to the database
	 * @param conn: connection to the database
	 */
	private void doAddData(Connection conn, Set<Pack> result) {
//    	Iterator<Pack> iterator = result.iterator();
//    	while (iterator.hasNext()) {
//    		Database.putInfo(conn, iterator.next());
//    		//iterator.remove();
//    	}
		for (Pack pack : result) {
			Database.putInfo(conn, pack);
		}

	}
}