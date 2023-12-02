package com.example.photogallery.db;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;

public class FileStorage {
    private Context context;
    public FileStorage(Context context) {
        this.context = context;
    }
    public ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords, double latitude_start, double latitude_end, double longitude_start, double longitude_end) {
//        File folder = new File(Environment.getExternalStorageDirectory()
//                .getAbsolutePath(), "/Android/data/com.example.photogallery/files/Pictures");
//        ArrayList<String> photos = new ArrayList<String>();
//        File[] fList = folder.listFiles();
//        if (fList != null) {
//            for (File f : fList) {
//                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
//                        && f.lastModified() <= endTimestamp.getTime())
//                ) && (keywords == "" || f.getPath().contains(keywords)))
//                    photos.add(f.getPath());
//            }
//        }
//        return photos;

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
    public String updatePhotoA(String path, String caption) {
        String[] attr = path.split("_");
        String newPath = attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3] + "_" + attr[4] + attr[5] + "_" + attr[6] + "_";
        File to = new File(newPath);
        File from = new File(path);
        from.renameTo(to);
        return newPath;
    }
    private File createPhoto() throws IOException {
        File photoFile = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "_caption_" + timeStamp + "_";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            photoFile = File.createTempFile(imageFileName, ".jpg",storageDir);
        } catch (IOException ex) { }
        return photoFile;
    }
}

