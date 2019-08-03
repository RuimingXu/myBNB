package com.shekhargulati.app;

import com.shekhargulati.crawler.Assignment3WebCrawler;
import com.shekhargulati.crawler.Crawler;
import com.shekhargulati.filter.CrawlerFilter;
import com.shekhargulati.pack.Pack;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class CrawlerTests {

    @Test
    public void testCrawlerWithZeroPack() {
        String temp = "test";
        Assignment3WebCrawler crawler = new Assignment3WebCrawler.Builder(temp).build();
        assertEquals(0,crawler.getRelevantSet().size());
    }

    @Test
    public void testCrawlerWithutBuild() {
        String temp = "https://www.kijiji.ca/222v-apartments-condos/oakville-halton-region/bachelor-studio-basement-apartment-for-rent-in-mississauga/1448802776?enableSearchNavigationFlag=true";
        CrawlerFilter expectFilter = mock(CrawlerFilter.class);
        Assignment3WebCrawler crawler = new Assignment3WebCrawler.Builder(temp).build();
        assertEquals(10, crawler.getMaxSize());
        assertEquals(10, crawler.getMaxThreads());
        assertEquals(temp, crawler.getSeed());
    }

    @Test
    public void testCrawlerBuildWithOutMaxThreadAndMaxSize() {
        String temp = "https://www.kijiji.ca/v-ap22artments-condos/oakville-halton-region/bachelor-studio-basement-apartment-for-rent-in-mississauga/1448802776?enableSearchNavigationFlag=true";
        CrawlerFilter expectFilter = mock(CrawlerFilter.class);
        Assignment3WebCrawler crawler = new Assignment3WebCrawler.Builder(temp)
                .addFilter(expectFilter)
                .build();
        assertEquals(expectFilter, crawler.getFilter());
        assertEquals(10, crawler.getMaxSize());
        assertEquals(10, crawler.getMaxThreads());
        assertEquals(temp, crawler.getSeed());
    }
    @Test
    public void testCrawlerBuildWithOutMaxSize() {
        String temp = "https://www.kijiji.ca/v-apartments-condos/oakville-halton-region/bachelor-studio-basement-apartment-for-rent-in-mississauga/1448802776?enableSearchNavigationFlag=true";
        CrawlerFilter expectFilter = mock(CrawlerFilter.class);
        Assignment3WebCrawler crawler = new Assignment3WebCrawler.Builder(temp)
                .maxThreadNumber(12)
                .addFilter(expectFilter)
                .build();
        assertEquals(expectFilter, crawler.getFilter());
        assertEquals(10, crawler.getMaxSize());
        assertEquals(12, crawler.getMaxThreads());
        assertEquals(temp, crawler.getSeed());
    }

    @Test
    public void testCrawlerFullInitialization() {
        String temp = "https://www.kijiji.ca/v-apartments-condos/oakville-halton-region/bachelor-studio-basement-apartment-for-rent-in-mississauga/1448802776?enableSearchNavigationFlag=true";
        CrawlerFilter expectFilter = mock(CrawlerFilter.class);
        Assignment3WebCrawler crawler = new Assignment3WebCrawler.Builder(temp)
                .maxThreadNumber(11)
                .maxSize(5)
                .addFilter(expectFilter)
                .build();
        assertEquals(expectFilter, crawler.getFilter());
        assertEquals(5, crawler.getMaxSize());
        assertEquals(11, crawler.getMaxThreads());
        assertEquals(temp, crawler.getSeed());
    }

    @Test
    public void testSizeWhenValidUrl() {
        Crawler crawler = new Assignment3WebCrawler.Builder("https://www.kijiji.ca/v-apartments-condos/oakville-halton-region/bachelor-studio-basement-apartment-for-rent-in-mississauga/1448802776?enableSearchNavigationFlag=true")
                .maxThreadNumber(10)
                .maxSize(3)
                .addFilter(new CrawlerFilter())
                .build();
        Set<Pack> crawlResult = crawler.start();
        assertEquals(3, crawlResult.size());
    }

    @Test
    public void testSizeWhenInvalidUrl() {
        Crawler crawler = new Assignment3WebCrawler.Builder("Some invalid  url")
                .maxThreadNumber(10)
                .maxSize(15)
                .addFilter(new CrawlerFilter())
                .build();
        Set<Pack> crawlResult = crawler.start();
        Set<Pack> expected = new HashSet<>();
        assertEquals(expected, crawlResult);
    }

    @Test
    public void testZeroSize() {
        Crawler crawler = new Assignment3WebCrawler.Builder("https://www.kijiji.ca/v-apartments-condos/oakville-halton-region/bachelor-studio-basement-apartment-for-rent-in-mississauga/1448802776?enableSearchNavigationFlag=true")
                .maxThreadNumber(10)
                .maxSize(0)
                .addFilter(new CrawlerFilter())
                .build();
        Set<Pack> crawlResult = crawler.start();
        assertEquals(0, crawlResult.size());
    }


}
