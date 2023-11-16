package com.example.photogallery;
import com.example.photogallery.db.FileStorage;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FileStorageTests {
    @Before  /*Initialization */
    public void grantPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + InstrumentationRegistry.getTargetContext().getPackageName()
                            + " android.permission.READ_EXTERNAL_STORAGE");
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + InstrumentationRegistry.getTargetContext().getPackageName()
                            + " android.permission.WRITE_EXTERNAL_STORAGE");
        }
    }
    @Test /*Unit Test for findPhotos method */
    public void findPhotosTest() throws Exception {
        // Using the App Context create an instance of the FileStorage
        Context appContext = InstrumentationRegistry.getTargetContext();
        FileStorage fs = new FileStorage(appContext);

        //Test time based search
        //Initialize a time window around the time a Photo was taken
        Date startTimestamp = null, endTimestamp = null;
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            startTimestamp = format.parse("20220928_000000");
            endTimestamp = calendar.getTime();
            calendar.setTime(startTimestamp);
            calendar.add(Calendar.MINUTE, 1);

        } catch (Exception ex) {
            Log.d("photos1", "There are no photos in this");
        }

        Log.d("start time1", startTimestamp + "");
        Log.d("end time1", endTimestamp + "");
        //Call the method specifying the test time window.
        ArrayList<String> photos = fs.findPhotos(startTimestamp, endTimestamp, "");

        Log.d("photos list1", photos + "");

        //Verify that only one photo with the matching timestamp is found
        assertEquals(9, photos.size());
        Log.d("photos0", photos.get(0) + "");
        assertEquals(true, photos.get(0).contains("20230928_191746"));
    }

    @Test /*Unit Test for findPhotos method */
    public void findPhotosKeywordTest() throws Exception {
        // Using the App Context create an instance of the FileStorage
        Context appContext = InstrumentationRegistry.getTargetContext();
        FileStorage fs = new FileStorage(appContext);

        //Test time based search
        //Initialize a time window around the time a Photo was taken
        Date startTimestamp = null, endTimestamp = null;
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            startTimestamp = format.parse("20220928_000000");
            endTimestamp = calendar.getTime();
            calendar.setTime(startTimestamp);
            calendar.add(Calendar.MINUTE, 1);

        } catch (Exception ex) {
            Log.d("photos1", "There are no photos in this");
        }

        Log.d("start time1", startTimestamp + "");
        Log.d("end time1", endTimestamp + "");
        //Call the method specifying the test time window.
        ArrayList<String> photos = fs.findPhotos(startTimestamp, endTimestamp, "hellothere");

        Log.d("photos list1", photos + "");

        //Verify that only one photo with the matching timestamp is found
        assertEquals(1, photos.size());
        Log.d("photos0", photos.get(0) + "");
        assertEquals(true, photos.get(0).contains("hellothere"));
    }

    @Test /*Unit Test for findPhotos method */
    public void findMultiPhotosTest() throws Exception {
        // Using the App Context create an instance of the FileStorage
        Context appContext = InstrumentationRegistry.getTargetContext();
        FileStorage fs = new FileStorage(appContext);

        //Test time based search
        //Initialize a time window around the time a Photo was taken
        Date startTimestamp = null, endTimestamp = null;
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            startTimestamp = format.parse("20231001_000000");
            endTimestamp = calendar.getTime();
            calendar.setTime(startTimestamp);
            calendar.add(Calendar.MINUTE, 1);

        } catch (Exception ex) {
            Log.d("photos1", "There are no photos in this");
        }

        Log.d("start time1", startTimestamp + "");
        Log.d("end time1", endTimestamp + "");
        //Call the method specifying the test time window.
        ArrayList<String> photos = fs.findPhotos(startTimestamp, endTimestamp, "");

        Log.d("photos list1", photos + "");

        //Verify that multiple photos are found
        assertEquals(6, photos.size());
    }

    @Test /*Unit Test for findPhotos method */
    public void findMultiKeywordPhotosTest() throws Exception {
        // Using the App Context create an instance of the FileStorage
        Context appContext = InstrumentationRegistry.getTargetContext();
        FileStorage fs = new FileStorage(appContext);

        //Test time based search
        //Initialize a time window around the time a Photo was taken
        Date startTimestamp = null, endTimestamp = null;
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            startTimestamp = format.parse("20230928_000000");
            endTimestamp = calendar.getTime();
            calendar.setTime(startTimestamp);
            calendar.add(Calendar.MINUTE, 1);

        } catch (Exception ex) {
            Log.d("photos1", "There are no photos in this");
        }

        Log.d("start time1", startTimestamp + "");
        Log.d("end time1", endTimestamp + "");
        //Call the method specifying the test time window.
        ArrayList<String> photos = fs.findPhotos(startTimestamp, endTimestamp, "bye");

        Log.d("photos list1", photos + "");

        //Verify that only one photo with the matching timestamp is found
        assertEquals(3, photos.size());
        Log.d("photos0", photos.get(0) + "");
        assertEquals(true, photos.get(0).contains("bye"));
        assertEquals(true, photos.get(1).contains("bye"));
        assertEquals(true, photos.get(2).contains("bye"));
    }
}


