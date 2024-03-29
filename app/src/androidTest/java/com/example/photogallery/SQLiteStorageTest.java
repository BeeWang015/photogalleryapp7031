package com.example.photogallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.test.InstrumentationRegistry;

import com.example.photogallery.db.SQLiteStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class SQLiteStorageTest {
    Context appContext; SQLiteStorage ss;
    @Before
    public void initialization() {
        appContext = InstrumentationRegistry.getTargetContext();
        ss = new SQLiteStorage();
        int status = ss.init(appContext, "App.db");

//        String filepath = "C:\\Users\\bkwan\\Desktop\\PhotoGallery\\app\\src\\main\\java\\com\\example\\photogallery\\image\\";
//        File imageFile = new File(filepath, "stock_person1.jpg");
//        FileInputStream fileInputStream = null;
//        try {
//            fileInputStream = new FileInputStream(imageFile);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        byte[] image = byteArrayOutputStream.toByteArray();
//
//        Log.d("imageArray", image.toString());
        String imageUrl = "https://img.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_1562-693.jpg";
        Bitmap bmImg;
        byte[] bitmapData2 = null;
        URL myFileURL = null;
        try {
            myFileURL = new URL(imageUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            conn.setDoInput(true);
            conn.connect();
            int length = conn.getContentLength();
            int[] bitmapData = new int[length];
            bitmapData2 = new byte[length];
            InputStream is = conn.getInputStream();

            bmImg = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }



        status = ss.addPhoto("cafe", "2018-08-25 13:20:21", 40.94829666666667, -73.90721333333333,"test_cafe_2018-08-25 13:20:21_40.94829666666667_-73.90721333333333_12345.jpg", bitmapData2);
        status = ss.addPhoto("cafe", "2017-08-25 13:20:21", 40.94829666666667, -73.90721333333333, "test_cafe_2017-08-25 13:20:21_40.94829666666667_-73.90721333333333_54321.jpg", bitmapData2);
    }
    @Test
    public void TestSQLiteStorage() throws Exception {
        //Test keywords based search
        //public ArrayList<String> findPhotos(String startTimestamp, String endTimestamp, String keyword, double latitude_start, double latitude_end, double longitude_start, double longitude_end)
        ArrayList<String> photos = ss.findPhotos("2017-08-25 00:00:00", "2018-08-26 00:00:00","cafe", 40, 42, -75, -70 );
        //Verify that two photos found with the specified keyword
//        Log.d("list_them", photos.get(0));
        assertEquals(2, photos.size());
        assertEquals(true, photos.get(0).contains("cafe"));
        assertEquals(true, photos.get(1).contains("cafe"));
        //Test time based search
        //Initialize a time window around the time a Photo was taken
        Date startTimestamp = null, endTimestamp = null;
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startTimestamp = format.parse("2018-08-25 13:20:21");
            calendar.setTime(startTimestamp);
            calendar.add(Calendar.MINUTE, 30);
            endTimestamp = calendar.getTime();
        } catch (Exception ex) { }
        //Call the method
        photos = ss.findPhotos(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTimestamp), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTimestamp), "cafe", 40, 42, -75, -70);
        //Verify that only one photo with the matching timestamp is found
        assertEquals(1, photos.size());
        assertEquals(true, photos.get(0).contains("2018-08-25 13:20:21"));
    }
    @After
    public void finalization() {
        int status = ss.deletePhoto("test_cafe_2018-08-25 13:20:21_40.94829666666667_-73.90721333333333_12345.jpg");
        status = ss.deletePhoto("test_cafe_2017-08-25 13:20:21_40.94829666666667_-73.90721333333333_54321.jpg");
    }

}
