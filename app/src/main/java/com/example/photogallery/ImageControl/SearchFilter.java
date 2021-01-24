/**
 * @brief Provides search functionality
 *
 * Provide all functions to ist all images in a directory and offers filtering through the
 * following inputs: dates, location and keyword
 *
 * @Author Ben Yang
 */
package com.example.photogallery.ImageControl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class SearchFilter {
    //**********************************************************************************************
    // Constants
    //**********************************************************************************************

    //**********************************************************************************************
    // Fields
    //**********************************************************************************************

    private SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss"); ///<expected filename format "yyyy-MM-dd_HHmmss_City_Country_XXXXXXXXXX.jpg"
    private ArrayList<String> photoGallery; ///<a list of paths of the photos
    private File[] listofPhotos; ///<a list of photo file names in no particular order
    private File imageDir; ///<directory where the image is stored

    //**********************************************************************************************
    // Constructors
    //**********************************************************************************************

    /** @brief Constructor Gets the directory where the image is stored
     * @param storageDir The path to the images
     * @return N/A
     */
    public SearchFilter(File storageDir) {
        imageDir = storageDir; //initialize imageDir
    }

    //**********************************************************************************************
    // Private methods
    //**********************************************************************************************



    //**********************************************************************************************
    // Public methods
    //**********************************************************************************************

    /** @brief getSavedImages Create a path list to all the saved image in a single directory
     * @return ArrayList<String> or null
    */
    public ArrayList<String> getSavedImages() {

        ArrayList<String> photoGallery = new ArrayList<>();
        File[] listofPhotos = imageDir.listFiles();

        //if there are files in the directory, list the path; else return null
        if (listofPhotos != null) {
            for (File f : listofPhotos)
                photoGallery.add(f.getPath());
        } else {
            return null;
        }

        Collections.reverse(photoGallery);
        return photoGallery;
    }

    /** @brief filterImage Returns a filtered gallery base on the information given
     * @param minDate Minimum date range for the search
     * @param maxDate Maximum date range for the search
     * @param city City where the photo is taken  for the search
     * @param country Country where the photo is taken for the search
     * @param keyword Keyword contain in the photo for the search
     * @return ArrayList<String>
     */
    public ArrayList<String> filterImage ( Date minDate, Date maxDate, String city, String country,
                                           String keyword) {


        if((minDate != null) && (maxDate != null)){

        } else if (city != null) {

        } else if (country != null) {

        } else if (keyword != null) {

        } else {

        }

        //make sure photoGallery is not empty
        if(photoGallery.isEmpty()){ photoGallery.add(0, "null"); }

        return photoGallery;
    }

    /** @brief removeFilter Removes applied filters
     * @return ArrayList<String>
     */
    public ArrayList<String> removeFilter () {
        return photoGallery;
    }
}
