package com.example.photogallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Search extends AppCompatActivity {

    private EditText fromDate;
    private EditText toDate;
    private EditText city;
    private EditText country;
    private EditText keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fromDate = (EditText) findViewById(R.id.search_fromDate);
        toDate   = (EditText) findViewById(R.id.search_toDate);
        city = (EditText) findViewById(R.id.search_city);
        country = (EditText) findViewById(R.id.search_Country);
        keyword = (EditText) findViewById(R.id.search_Keyword);
    }

    /** Call when user taps CANCEL
     *  Kill the current activity   */
    public void Cancel(final View v) {
        finish();
    }

    /** Call when user taps Search
     *  save From Date & To Date strings  */
    public void Search(final View v) {
        Intent SearchKey = new Intent();
        SearchKey.putExtra("StartDate", fromDate.getText().toString());
        SearchKey.putExtra("EndDate", toDate.getText().toString());
        SearchKey.putExtra("CityName", city.getText().toString());
        SearchKey.putExtra("CountryName", country.getText().toString());
        SearchKey.putExtra("UserKeyword", keyword.getText().toString());
        setResult(RESULT_OK, SearchKey);
        finish();
    }
}
