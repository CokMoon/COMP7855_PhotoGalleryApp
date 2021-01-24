package com.example.photogallery;

import com.example.photogallery.ImageControl.SearchFilter;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SearchFilterTest {

    @Test
    public void testGetSavedImages() {
        try {
            File file = new File("C:\\Users\\JellyBeans\\Desktop\\Image");

            SearchFilter underTest = new SearchFilter();
            ArrayList<String> photos = underTest.GetSavedImages(file);
            assertEquals(4, photos.size());
        } catch (NullPointerException e) { e.printStackTrace(); }
    }

    @Test
    public void DateFilterImages() {
        try {
            File file = new File("C:\\Users\\JellyBeans\\Desktop\\Image");

            Date minDate = new Date("2019/03/01");
            Date maxDate = new Date("2019/05/01");
            String city  = "";
            String country = "";
            String Keyword = "";

            SearchFilter underTest = new SearchFilter();

            ArrayList<String> photos = underTest.Filter(file, minDate, maxDate, city, country, Keyword);
            assertEquals( 2, photos.size());
        } catch (NullPointerException e) { e.printStackTrace(); }
    }

    @Test
    public void KeywordFilterImages() {
        try {
            File file = new File("C:\\Users\\JellyBeans\\Desktop\\Image");

            Date minDate = null;
            Date maxDate = null;
            String city  = "";
            String country = "";
            String Keyword = "Luck";

            SearchFilter underTest = new SearchFilter();

            ArrayList<String> photos = underTest.Filter(file, minDate, maxDate, city, country, Keyword);
            assertEquals( photos.size(), 2);
        } catch (NullPointerException e) { e.printStackTrace(); }
    }
}