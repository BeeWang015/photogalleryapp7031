package com.example.photogallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 10;
    private LocationManager locationManager;
    private TextView locationTextView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    String mCurrentPhotoPath;
    private ArrayList<String> photos = null;
    private int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = this.getSharedPreferences("searchKeyword", MODE_PRIVATE);
        String keyword = (sharedPreferences.getString("keyword", ""));
        Log.d("iFailure2", keyword + " Hi if you missed it");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationTextView = findViewById(R.id.locationTV);

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
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
        EditText et = (EditText) findViewById(R.id.etCaption);
        if (path == null || path == "") {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
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
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = findViewById(R.id.ivGallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "", 0.0, 0.0, 0.0, 0.0);
        }
    }

    private void updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 6) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[1] + "_" + attr[2] + "_" + attr[3] + "_" + attr[4] + "_" + attr[5]);
            Log.d("updatePhotoName1", attr[4] + " should be latitude");
            Log.d("updatePhotoName2", attr[5] + " should be longitude");
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