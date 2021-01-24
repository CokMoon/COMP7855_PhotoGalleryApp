package com.example.photogallery;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.example.photogallery.ImageControl.SearchFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;


public class Main extends AppCompatActivity {
    static final int REQUEST_SEARCH_IMAGE = 0;
    static final int REQUEST_TAKE_PHOTO = 1;

    static Date startDate;
    static Date endDate;
    static String city;
    static String country;
    static String userKeyword;
    static String CurrentCity = "null";
    static String CurrentCountry = "null";

    private int PhotoIndex = 0;
    private ArrayList<String> photoGallery;
    private String currentPhotoPath;
    double longitude = 0;
    double latitude = 0;

    private File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
            "/Android/data/com.example.photogallery/files/Pictures");
    private SearchFilter Filtering = new SearchFilter(file);

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ask for camera & location permission
        requestPermissions(new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION},
            REQUEST_TAKE_PHOTO);

        photoGallery = Filtering.getSavedImages();
        currentPhotoPath = photoGallery.get(PhotoIndex);
        displayPhoto(currentPhotoPath);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, therefore ask permission
            // Acquire a reference to the system Location Manager
            LocationManager locationManager =
                    (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    ConvLonLatToName();
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };
            //Start listening for Location updates
            String locationProvider = LocationManager.GPS_PROVIDER;
            locationManager.requestLocationUpdates
                    (locationProvider, 0, 0, locationListener);
        }
    }

    /** Take lat & long info and converte it to city & country name */
    private void ConvLonLatToName (){
        //Convert long & lad data into city name
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses =
                    geocoder.getFromLocation(latitude, longitude, 2);
            CurrentCity = addresses.get(0).getLocality();
            CurrentCountry = addresses.get(0).getCountryName();
        } catch (IOException ex) {
            //Error occurred when getting address
        }
    }

    /** Display the Photo on the main activity  */
    private void displayPhoto(String path) {
        ImageView iv = findViewById(R.id.ThumbPic);

        //if there picture in folder display most recent picture else show default image
        if (!path.isEmpty())
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
        else
            iv.setImageResource(R.drawable.image);
    }

    /** Add keyword to image Exif */
    private void addKeyword(String CurrentImage, String Keyword) throws IOException {

        ExifInterface exif = new ExifInterface(CurrentImage);

        if((CurrentImage.length() != 0)&&(Keyword.length() != 0)) {
            exif.setAttribute(ExifInterface.TAG_USER_COMMENT, Keyword);
        }

        exif.saveAttributes();
        Log.d("CurrentImage", CurrentImage);
    }

    /** Called when the user taps LEFT or RIGHT
     *  Keep track of the photo indexes          */
    public void onClick(View v) throws IOException {
        switch(v.getId()){
            case R.id.btnLeft:
                --PhotoIndex;
                break;
            case R.id.btnRight:
                ++PhotoIndex;
                break;
            case R.id.confirmKeyword:
                EditText T = findViewById(R.id.editKeyword);
                String Keyword = T.getText().toString();
                addKeyword(photoGallery.get(PhotoIndex), Keyword);
                break;
            default:
                break;
        }

        if (PhotoIndex < 0)
            PhotoIndex = 0;
        if (PhotoIndex >= photoGallery.size())
            PhotoIndex = photoGallery.size() - 1;

        if(photoGallery.size() > 0) {
            currentPhotoPath = photoGallery.get(PhotoIndex);
            displayPhoto(currentPhotoPath);
        }
    }

    /** Called when the user taps FILTER
     *  Open the "Search" page            */
    public void SearchPage(View view) {
        Intent S = new Intent(this, Search.class);
        startActivityForResult(S, REQUEST_SEARCH_IMAGE);
    }

    /** Called when user taps SNAP
     *  Open the camera app         */
    public void TakePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.photogallery.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /** Saves the full size photo  */
    private File createImageFile() throws IOException {

        //Create timeStamp
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());

        // Create an image file name
        String imageFileName = timeStamp + "_" + CurrentCity + "_" + CurrentCountry + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /** Coming back from activities
     *  From Camera: update main screen thumbnail to show all picture
     *  From Search: update and filter base on date & location*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Coming back from camera and showing thumbnail on main screen
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            photoGallery = Filtering.getSavedImages();
            PhotoIndex = 0;

            currentPhotoPath = photoGallery.get(PhotoIndex);
            displayPhoto(currentPhotoPath);
        }

        // Coming back from search screen
        if (requestCode == REQUEST_SEARCH_IMAGE && resultCode == RESULT_OK) {

            //expected date format from user
            SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy/MM/dd");

            try {
                startDate = DateFormat.parse(data.getStringExtra("StartDate"));
                endDate = DateFormat.parse(data.getStringExtra("EndDate"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            city = data.getExtras().getString("CityName",null);
            country = data.getExtras().getString("CountryName",null);
            userKeyword = data.getExtras().getString("UserKeyword", null);

            photoGallery =
                    Filtering.filterImage(startDate, endDate, city, country, userKeyword);
            PhotoIndex = 0;

            currentPhotoPath = photoGallery.get(PhotoIndex);
            displayPhoto(currentPhotoPath);
        }
    }

    /** Upload is pressed */
    public void Uploading(View v) {
        Upload Upload = new Upload();
        Upload.execute( currentPhotoPath );
    }

    /** AsyncTask to connect to Tomcat Server & upload picture */
    private class Upload extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... Img) {

            final String Upload_URL = "https://10.0.2.2:8443/midp/hits";
            final File Image = new File(currentPhotoPath);
            final int BufferSize = 4096;
            HttpsURLConnection urlConnection;
            String serverResponse = "Connect Upload";

            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                InputStream caInput = getResources().openRawResource(R.raw.server);

                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    Log.d("Certificate","ca=" + ((X509Certificate) ca).getSubjectDN());
                } finally {
                    caInput.close();
                }

                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                if (keyStore.isCertificateEntry("ca"))
                    Log.d("Entry","Good");

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                // Create an SSLContext that uses our TrustManager
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);

                //Ignore hostname check
                HostnameVerifier nullHostnameVerifier =
                        new HostnameVerifier() {
                            @Override
                            public boolean verify(final String hostname, final SSLSession session) {
                                return true;
                            }
                        };

                URL url2 = new URL(Upload_URL);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpsURLConnection) url2.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setHostnameVerifier(nullHostnameVerifier);

                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                // sets file name & filesize AS A http header
                urlConnection.setRequestProperty("Filename", Image.getName());

//                urlConnection.setChunkedStreamingMode(0);

                //opens output stream of the Http connection for writing data
                OutputStream out = urlConnection.getOutputStream();

                // Open input stream of the fie ofr reading data
                FileInputStream InputFile = new FileInputStream(Image);

                byte[] buffer = new byte[(int) BufferSize];
                int bytesRead = -1;

                while((bytesRead = InputFile.read(buffer)) != -1){
                    out.write(buffer, 0, bytesRead);
                }

                out.close();
                InputFile.close();

                // always check HTTP response code from sever
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK){
                    //read server's response
                    BufferedReader Reader = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()
                    ));
                    serverResponse = Reader.readLine();
                    Log.d("Server Response",serverResponse);
                } else {
                    Log.d("Server Response","Server Response's non-okay: " + responseCode);
                    serverResponse = "upload failed, Code: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return serverResponse;
        }
        @Override
        protected void onPostExecute(String result) {
            TextView response = findViewById(R.id.UploadStatus);
            response.setText(result);
        }
    }
}



