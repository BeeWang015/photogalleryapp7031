package com.example.photogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        try {
            sharedPreferences = this.getSharedPreferences("searchKeyword", Context.MODE_PRIVATE);
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date now = calendar.getTime();
            String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now);
            Date today = format.parse((String) todayStr);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String tomorrowStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            Date tomorrow = format.parse((String) tomorrowStr);
            ((EditText) findViewById(R.id.etFromDateTime)).setText(new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(today));
            ((EditText) findViewById(R.id.etToDateTime)).setText(new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(tomorrow));

        } catch (Exception ex) {
            Log.d("catch1", "Error with dates");
        }
    }

    public void cancel(final View v) {
        finish();
    }

    public void go(final View v) {
        Intent i = new Intent();
        EditText from = (EditText) findViewById(R.id.etFromDateTime);
        EditText to = (EditText) findViewById(R.id.etToDateTime);
        EditText keywords = (EditText) findViewById(R.id.etKeywords);
        EditText longitudeStart = (EditText) findViewById(R.id.etLongitudeStart);
        EditText longitudeEnd = (EditText) findViewById(R.id.etLongitudeEnd);
        EditText latitudeStart = (EditText) findViewById(R.id.etLatitudeStart);
        EditText latitudeEnd = (EditText) findViewById(R.id.etLatitudeEnd);
        i.putExtra("STARTTIMESTAMP", from.getText() != null ? from.getText().toString() : "");
        i.putExtra("ENDTIMESTAMP", to.getText() != null ? to.getText().toString() : "");
        i.putExtra("KEYWORDS", keywords.getText() != null ? keywords.getText().toString() : "");
        Log.d("checking1", Double.parseDouble(longitudeStart.getText().toString()) + " This is the result");
        i.putExtra("LONGITUDESTART", from.getText() != null ? Double.parseDouble(longitudeStart.getText().toString()) : 0.0);
        i.putExtra("LONGITUDEEND", from.getText() != null ? Double.parseDouble(longitudeEnd.getText().toString()) : 0.0);
        i.putExtra("LATITUDESTART", from.getText() != null ? Double.parseDouble(latitudeStart.getText().toString()) : 0.0);
        i.putExtra("LATITUDEEND", from.getText() != null ? Double.parseDouble(latitudeEnd.getText().toString()) : 0.0);
        Log.d("catch2", "Before SharedPreferences");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d("catch3", "After SharedPreferences");
        editor.putString("keyword", keywords.getText().toString());
        editor.apply();
        Log.d("catch4", sharedPreferences.getString("keyword", null));
        Toast.makeText(this, "successfully added", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK, i);
        finish();
    }
}
