package com.shekhargulati.crawler;

//import c01s19.assgn3.filebuilder.FileBuilder;
import com.shekhargulati.database.Database;
import com.shekhargulati.filter.CrawlerFilter;
import com.shekhargulati.filter.Default_filter;
import com.shekhargulati.filter.Filter;
import com.shekhargulati.pack.Pack;
import com.shekhargulati.pack.WorkerResult;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Assignment3WebCrawler implements Crawler {
  private int maxThreads;
  private Integer maxLinks;
  private int maxSize;
  private ExecutorService executor;
  private String seed;
  private Filter filter;
  //private FileBuilder fb;

  private Set<String> allUrlSet;
  private Set<Pack> relevantSet;
  private Queue<String> urlQueue;
  private ArrayList<Thread> threadContainer;
  //private Map<String, Integer> depthLookasideTable;

  private int waitThreadCount;


  private static final Object signal = new Object();

  public Filter getFilter() { return filter; }
  public String getSeed() { return seed; }
  public int getMaxSize() {
  	return maxSize;
  }
  public int getMaxThreads() {
  	return maxThreads;
  }

  public Set<Pack> getRelevantSet() {
  	return relevantSet;
  }

	/**
   * Builder design pattern for WebCrawler.
   */
  public static class Builder {

    private final String seed;

    private int maxThreads = 10; // Default max thread number
    private final int maxLinks = 2500; // ** WARNING: do not change this number to a bigger number
    private int maxSize = 10; // Default max number of valid links to contain
    private Filter filter = Default_filter.get_filter();
    //private FileBuilder fb = new FileBuilder();

    public Builder(String seed) {
      this.seed = seed;
    }

    public Builder maxThreadNumber(int threadNumber) {
      this.maxThreads = threadNumber;
      return this;
    }
    
    public Builder maxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
      }

    public Builder addFilter(Filter filter2) {
      this.filter = filter2;
      return this;
    }

    public Assignment3WebCrawler build() {
      return new Assignment3WebCrawler(this);
    }
  }

  private Assignment3WebCrawler() {
    this.allUrlSet = new HashSet<String>();
    this.relevantSet = new HashSet<Pack>();
    this.urlQueue = new LinkedList<String>();
    this.threadContainer = new ArrayList<Thread>();
    //depthLookasideTable = new HashMap<String, Integer>();
  }

  private Assignment3WebCrawler(Builder builder) {
    this();
    this.seed = builder.seed;
    this.maxThreads = builder.maxThreads;
    this.maxLinks = builder.maxLinks;
    this.maxSize = builder.maxSize;
    this.executor = Executors.newFixedThreadPool(this.maxThreads);
    this.filter = builder.filter;
    //this.fb = builder.fb;
  }
  	
  /**
   * Pop a url from queue.
   *
   * @return a url in queue, null if queue is empty
   */
  private String popAUrl() {
    if (urlQueue.isEmpty()) {
      return null;
    }
    return urlQueue.poll();
  }
  
  	/**
  	 * Check if input url is already crawled. If no, then add into set and queue
  	 * and record its depth.
  	 *
  	 * @param url is input url.
  	 */
  	private void pushAUrl(String url) {
  		if (!this.allUrlSet.contains(url)) {
  			this.allUrlSet.add(url);
  			this.urlQueue.add(url);
  		}
  	}
  	
  	private boolean pushRelevant(Pack pack) {
  		if (pack != null) {
  			this.relevantSet.add(pack);
  			if (this.relevantSet.size() >= this.maxSize) {
  				if (this.relevantSet.size() > this.maxSize) {
  					this.relevantSet.remove(pack);
				}
  				return true;
  			}
  		}
  		return false;
  	}
  	
  	public class Worker implements Callable<WorkerResult> {

  		private final String url;
  		
  		Worker(String url) {
  			this.url = url;
  		}

		@Override
		public WorkerResult call() throws Exception {
			if (url == null) {
				return null;
			} else {
				return crawling(url);
			}
		}
  		
  	}

  	/**
  	 * Starting crawling the url.
  	 *
  	 * Get all of its contents, and all sub urls related to it. Then, try to add
  	 * sub urls into queue for next time crawling.
  	 *
  	 * @param url is a input url to crawl
  	 */
  	private WorkerResult crawling(String url) {
  		Pack pack;
		ArrayList<String> subUrls = new ArrayList<>();
			
		// Get the actual web document
		Document document;
		try {
			document = Jsoup.connect(url).timeout(10 * 1000).get();
		
		} catch (SocketTimeoutException e2) {
			System.out.println("time out");
			return null;
		} catch (IOException e1) {
			System.out.println("Invalid url being passed to crawling function");
			return null;
		} catch (IllegalArgumentException e3) {
			System.out.println("Bad URL provided to the crawling function");
			return null;
		}
		// Current website is relevant.
		if ((pack = this.filter.check_relevance(document)) != null) {
			System.out.println(" url: " + url + " successfully" + " size: " + this.relevantSet.size());
		}
		// Current website is irrelevant
		else {
			System.out.println(": crawling " + url + ", irrelevant website." + " size: " + this.relevantSet.size());
		}
	
		Elements elements = document.select("a[href]");
		String subUrl;
		for (Element element : elements) {
			String temp;
			if ((temp = element.attr("data-event")) != "") {
				System.out.println("Non-critical url: " + temp);
				continue;
			}
			subUrl = element.attr("abs:href");
			if (subUrl != null) {
				subUrls.add(subUrl);
			}
		}
		WorkerResult result = new WorkerResult(pack, subUrls);
		return result;
  	}


  	private Set<Worker> addWorker() {
  		Set<Worker> workers = new HashSet<Worker>();
  		for (int i = 0; i < this.maxThreads; i ++) {
  			String url = this.popAUrl();
  			if (url == null) {
  				break;
  			}
//  			System.out.println(url);
  			workers.add(new Worker(url));
  		}
  		return workers;
  	}
  	
  	/**
  	 * Start crawling and check if jobs are all finished.
  	 *	
  	 * If all jobs are done, then set the flag to true.
  	 */
	public Set<Pack> start() {
		this.pushAUrl(this.seed);
		boolean flag = false;
		//int currSize = 0;
		List<Future<WorkerResult>> futures;
		
		long start = System.currentTimeMillis();
		while (true) {
			// Breaking condition 1: no more url to crawl
			if (this.urlQueue.size() == 0) {
				break;
			}
//			if (this.allUrlSet.size() >= this.maxLinks) {
//				System.out.println("The system get overwhelmed by crawling too many links.");
//				break;
//			}
			try {
				//String url = this.popAUrl();
				futures = this.executor.invokeAll(this.addWorker());
				for (Future<WorkerResult> future : futures) {
					//WorkerResult workerResult = future.get();
					// Breaking condition 2: reach maxsize
					if (future != null && future.get() != null) {
						if (this.pushRelevant(future.get().getResult())) {
							flag = true;
							break;
						}
						// Add more suburl to the url queue
						for (String suburl : future.get().getSubUrl()) {
//						System.out.println(suburl);
							this.pushAUrl(suburl);
						}
					}

				}
				if (flag) {
					break;
				}
			}
			catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				System.out.println("Process in start function encounter a problem.");
				return null;
			}
		}
  		long end = System.currentTimeMillis();
		System.out.println("Total Web: " + this.relevantSet.size());
		System.out.println("Time Spent: " + (end - start) / 1000 + " sec");

		return this.relevantSet;
  	}

	// Debugging block
  	public static void main(String[] args) {
  		Crawler crawler = new Builder("https://www.kijiji.ca/b-apartments-condos/city-of-toronto/c37l1700273?enableSearchNavigationFlag=true&adRemoved=1445589348")
    		.maxThreadNumber(10)
    		.maxSize(50)
    		.addFilter(new CrawlerFilter())
    		.build();
    	Set<Pack> crawlResult = crawler.start();
//    	System.out.println(crawlResult.size());
//		System.exit(0);

		// Connect to database
    	String name = "Assignment3";
//		Database.createDatabase(name);
		Connection conn = Database.connect(name);
		// Recreate the table
		Database.clearTable(conn);
		Database.createTable(conn);
		// Add to the table
		for (Pack pack : crawlResult) {
			Database.putInfo(conn, pack);
		}
		System.out.println("Data has been put");
		// Get info back from database
		System.out.println(Database.getInfo(conn));

		//System.out.println(Database.getMaxVal(conn, "rent"));

		System.exit(0);
  	}


}
