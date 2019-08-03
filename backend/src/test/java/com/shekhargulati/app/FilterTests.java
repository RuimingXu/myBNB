package com.shekhargulati.app;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import com.shekhargulati.filter.CrawlerFilter;
import com.shekhargulati.pack.Pack;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilterTests {

    @Test
    public void testValidURLDoc()  {
        CrawlerFilter myFilter = new CrawlerFilter();
        Document mockDoc = mock(Document.class);
        Elements mockElem1 = mock(Elements.class);
        Elements mockElem2 = mock(Elements.class);
        Elements mockElem3 = mock(Elements.class);
        Elements mockElem4 = mock(Elements.class);
        Elements mockElem5 = mock(Elements.class);

        when(mockElem1.text()).thenReturn("some id");
        when(mockElem2.text()).thenReturn("some address");
        when(mockElem3.text()).thenReturn("some price");
        when(mockElem4.text()).thenReturn("some date");
        when(mockElem5.text()).thenReturn("some description");

        when(mockElem1.size()).thenReturn(1);

        when(mockDoc.select(myFilter.houseID)).thenReturn(mockElem1);
        when(mockDoc.select(myFilter.address)).thenReturn(mockElem2);
        when(mockDoc.select(myFilter.rent)).thenReturn(mockElem3);
        when(mockDoc.select(myFilter.postDate)).thenReturn(mockElem4);
        when(mockDoc.select(myFilter.description)).thenReturn(mockElem5);

        Pack expected = new Pack.Builder("some id", "some address", "some price")
                .setDate("some date")
                .setDescription("some description")
                .build();
        Pack actual = myFilter.check_relevance(mockDoc);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidURLDocWithIncompleteInfo1()  {
        CrawlerFilter myFilter = new CrawlerFilter();
        Document mockDoc = mock(Document.class);
        Elements mockElem1 = mock(Elements.class);
        Elements mockElem2 = mock(Elements.class);
        Elements mockElem3 = mock(Elements.class);
        Elements mockElem4 = mock(Elements.class);
        Elements mockElem5 = mock(Elements.class);

        when(mockElem1.text()).thenReturn("some id");
        when(mockElem2.text()).thenReturn("some address");
        when(mockElem3.text()).thenReturn("some price");
        when(mockElem4.text()).thenReturn("some date");
        when(mockElem5.text()).thenReturn(null);

        when(mockElem1.size()).thenReturn(1);

        when(mockDoc.select(myFilter.houseID)).thenReturn(mockElem1);
        when(mockDoc.select(myFilter.address)).thenReturn(mockElem2);
        when(mockDoc.select(myFilter.rent)).thenReturn(mockElem3);
        when(mockDoc.select(myFilter.postDate)).thenReturn(mockElem4);
        when(mockDoc.select(myFilter.description)).thenReturn(mockElem5);

        Pack expected = new Pack.Builder("some id", "some address", "some price")
                .setDate("some date")
                .build();
        Pack actual = myFilter.check_relevance(mockDoc);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidURLDocWithIncompleteInfo2()  {
        CrawlerFilter myFilter = new CrawlerFilter();
        Document mockDoc = mock(Document.class);
        Elements mockElem1 = mock(Elements.class);
        Elements mockElem2 = mock(Elements.class);
        Elements mockElem3 = mock(Elements.class);
        Elements mockElem4 = mock(Elements.class);
        Elements mockElem5 = mock(Elements.class);

        when(mockElem1.text()).thenReturn("some id");
        when(mockElem2.text()).thenReturn(null);
        when(mockElem3.text()).thenReturn(null);
        when(mockElem4.text()).thenReturn(null);
        when(mockElem5.text()).thenReturn("some description");

        when(mockElem1.size()).thenReturn(1);

        when(mockDoc.select(myFilter.houseID)).thenReturn(mockElem1);
        when(mockDoc.select(myFilter.address)).thenReturn(mockElem2);
        when(mockDoc.select(myFilter.rent)).thenReturn(mockElem3);
        when(mockDoc.select(myFilter.postDate)).thenReturn(mockElem4);
        when(mockDoc.select(myFilter.description)).thenReturn(mockElem5);

        Pack expected = new Pack.Builder("some id", null, null)
                .setDescription("some description")
                .build();
        Pack actual = myFilter.check_relevance(mockDoc);
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidURLDoc()  {
        CrawlerFilter myFilter = new CrawlerFilter();
        Document mockDoc = mock(Document.class);
        Elements mockElem1 = mock(Elements.class);

        when(mockElem1.size()).thenReturn(0);
        when(mockDoc.select(myFilter.houseID)).thenReturn(mockElem1);

        Pack expected = null;
        Pack actual = myFilter.check_relevance(mockDoc);
        assertEquals(expected, actual);
    }

}
