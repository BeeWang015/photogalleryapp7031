package com.example.photogallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.speech.RecognizerIntent;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

import com.example.photogallery.Views.FramedImageView;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, SensorEventListener {
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_MIN_VELOCITY = 100;
    private static final int PERMISSION_REQUEST_CODE = 10;
    private LocationManager locationManager;
    private TextView locationTextView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    static final int SPEAK_ACTIVITY_REQUEST_CODE = 5;
    String mCurrentPhotoPath;
    private ArrayList<String> photos = null;
    private int index = 0;
    private float zRef = Float.MIN_VALUE;
    private GestureDetector gestures;
    private SensorManager sensorManager;
    private Sensor sensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Keep keyword typed
        SharedPreferences sharedPreferences = this.getSharedPreferences("searchKeyword", MODE_PRIVATE);
        String keyword = (sharedPreferences.getString("keyword", ""));
        Log.d("iFailure2", keyword + " Hi if you missed it");

        // Swipe left and right gestures
        gestures = new GestureDetector(this, this);

        // Sensor for Rotational gestures
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, sensor, 100);

        // Latitude and Longitude services
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationTextView = findViewById(R.id.locationTV);

        // Check for keyword search
        if (!keyword.equals("")) {
            Log.d("message3", keyword + " Hello I am find this");
            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), keyword, 0.0, 0.0, 0.0, 0.0);
            if (photos.size() == 0) {
                sharedPreferences.edit().remove("keyword").apply();
                displayPhoto(null);
            } else {
                displayPhoto(photos.get(index));
            }
        } else {
            Log.d("message4", "You've arrived here");
            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "", 0.0, 0.0, 0.0, 0.0);
            if (photos.size() == 0) {
                displayPhoto(null);
            } else {
                displayPhoto(photos.get(index));
            }
        }

        checkLocationPermission();

    }

    public void goRight() {
        index++;
        if(index >= photos.size()) {
            index = 0;
        }
        displayPhoto(photos.get(index));
//        ImageView image = (ImageView) findViewById(R.id.ivGallery);
        FramedImageView image = (FramedImageView) findViewById(R.id.fiv);

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 2);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 2);
        ObjectAnimator scaleAnimation =  ObjectAnimator.ofPropertyValuesHolder(image, pvhX, pvhY);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(image, View.TRANSLATION_X, 800);
        translateAnimation.setRepeatCount(1);
        translateAnimation.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(image ,
                "rotation", 0f, 360f);
        rotateAnimation.setDuration(1000); // miliseconds
        AnimatorSet setAnimation = new AnimatorSet();
        setAnimation.play(scaleAnimation).after(rotateAnimation).before(translateAnimation);
        setAnimation.start();
    }
    public void goLeft() {
        Animation animationXML = AnimationUtils.loadAnimation(this, R.anim.slideleft);
        Animation animation = AnimationUtils.makeOutAnimation(this, true);
        animation.setDuration(500);
//        ImageView image = findViewById(R.id.ivGallery);
        FramedImageView image = (FramedImageView) findViewById(R.id.fiv);
        image.startAnimation(animation);

        index--;
        if(index < 0){
            index = photos.size() - 1;
        }
        displayPhoto(photos.get(index));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestures.onTouchEvent(event);
    }

    @Override
    public boolean onDown(@NonNull MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {
                goLeft();
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {
                goRight();
            }
        } catch (Exception e) {    }
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float z = event.values[2];
            if (zRef == Float.MIN_VALUE) {
                zRef = z;
                return;
            } else {
                float zChange = zRef - z;
                if (zChange > 0.1f) {
                    goRight();
                } else if (zChange < -0.1f) {
                    goLeft();
                }
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void speakScroll(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        startActivityForResult(intent, SPEAK_ACTIVITY_REQUEST_CODE);
    }
    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.photogallery.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void scrollPhotos(View v) {
        updatePhoto(photos.get(index), ((EditText) findViewById(R.id.etCaption)).getText().toString());
        switch (v.getId()) {
            case R.id.btnPrev:
                if (index > 0) {
                    index--;
                }
                break;
            case R.id.btnNext:
                if (index < (photos.size() - 1)) {
                    index++;
                }
                break;
            default:
                break;
        }
        displayPhoto(photos.get(index));
    }

    private void displayPhoto(String path) {
        //ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        FramedImageView iv = (FramedImageView) findViewById(R.id.fiv);
        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
        EditText et = (EditText) findViewById(R.id.etCaption);
        if (path == null || path == "") {
            //iv.setImageResource(R.mipmap.ic_launcher);
            iv.setContent(null);
            et.setText("");
            tv.setText("");
        } else {
            // iv.setImageBitmap(BitmapFactory.decodeFile(path));
            iv.setContent(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et.setText(attr[1]);
            tv.setText(attr[2]);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
//        Log.d("Location1", "Longitude: " + longi + " Latitude: " + lat + "_");
        String imageFileName = "_caption_" + timeStamp + "_" + latitude + "_" + longitude + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Search Activity result
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startTimestamp, endTimestamp;
                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    assert from != null;
                    startTimestamp = format.parse(from);
                    assert to != null;
                    endTimestamp = format.parse(to);

                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                String keywords = (String) data.getStringExtra("KEYWORDS");
                double latitude_start = (Double) data.getDoubleExtra("LATITUDESTART", 0.0);
                double latitude_end = (Double) data.getDoubleExtra("LATITUDEEND", 0.0);
                double longitude_start = (Double) data.getDoubleExtra("LONGITUDESTART", 0.0);
                double longitude_end = (Double) data.getDoubleExtra("LONGTIUDEEND", 0.0);

                index = 0;
                photos = findPhotos(startTimestamp, endTimestamp, keywords, latitude_start, latitude_end, longitude_start, longitude_end);

                if (photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(photos.get(index));
                }
            }
        } // Photo snap result
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            ImageView mImageView = findViewById(R.id.ivGallery);
            FramedImageView mImageView = (FramedImageView) findViewById(R.id.fiv);
//            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            mImageView.setContent(BitmapFactory.decodeFile(mCurrentPhotoPath));
            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "", 0.0, 0.0, 0.0, 0.0);
        } // Microphone activity result
        else if (requestCode == SPEAK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                ArrayList<String> speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String word = speech.get(0);
                if(word.contains("right")) {
                    goRight();
                }
                else if(word.contains("left")) {
                    goLeft();
                }
            }
            catch (Exception e){ }
        }
    }

    private void updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 6) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3] + "_" + attr[4] + "_" + attr[5] + "_" + attr[6]);
            Log.d("updatePhotoName1", attr[0] + " should be path");
            Log.d("updatePhotoName1", attr[1] + " should be caption");
            Log.d("updatePhotoName2", attr[2] + " should be date");
            Log.d("updatePhotoName3", attr[3] + " should be time");
            Log.d("updatePhotoName4", attr[4] + " should be latitude");
            Log.d("updatePhotoName5", attr[5] + " should be longitude");
            Log.d("updatePhotoName6", attr[6] + " should be JPG");
            File from = new File(path);
            from.renameTo(to);
        }
    }

    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords, double latitude_start, double latitude_end, double longitude_start, double longitude_end) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photogallery/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : fList) {
                if (((startTimestamp == null && endTimestamp == null) ||
                        (f.lastModified() >= startTimestamp.getTime()
                                && f.lastModified() <= endTimestamp.getTime())
                ) && (keywords.equals("") || f.getPath().contains(keywords))) {
//                    Log.d("fileNames Long", f.toString().split("_")[4]);
//                    Log.d("fileNames Lat", f.toString().split("_")[5]);
                    if (((latitude_start == 0.0 &&
                            latitude_end == 0.0)) &&
                            (longitude_start == 0.0 &&
                                    longitude_end == 0.0)) {
                        photos.add(f.getPath());
                    } else if (((Double.compare(latitude_start, Double.parseDouble(f.toString().split("_")[4])) < 0) &&
                            (Double.compare(latitude_end, Double.parseDouble(f.toString().split("_")[4])) > 0)) &&
                            ((Double.compare(longitude_start, Double.parseDouble(f.toString().split("_")[5])) < 0) &&
                                    (Double.compare(longitude_end, Double.parseDouble(f.toString().split("_")[5])) > 0))) {
//                        Log.d("error5", (Double.compare(longitude_start, Double.parseDouble(f.toString().split("_")[4])) < 0) + " this is supposedly true");
//                        Log.d("error2", "you made it here");
                        photos.add(f.getPath());
                    }
//                    else {
//                        Log.d("error1", "youre here");
//                    }
                }
            }
        }
        return photos;
    }

    public void filter(View v) {
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        startActivityForResult(i, SEARCH_ACTIVITY_REQUEST_CODE);
    }

    private void requestLocationUpdates() {
        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Get the last known location
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateLocationText(lastKnownLocation);
            }

            // Set up a location listener to get updated location
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    updateLocationText(location);
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    // Handle provider disabled
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                    // Handle provider enabled
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // Handle status changed
                }
            };

            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private void updateLocationText(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String text = "Latitude: " + latitude + "\nLongitude: " + longitude;
            locationTextView.setText(text);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
           // Asking for permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted
            requestLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                requestLocationUpdates();
            } else {
                // Permission denied
            }
        }
    }
}