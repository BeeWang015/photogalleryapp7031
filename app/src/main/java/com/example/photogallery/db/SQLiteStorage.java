package com.example.photogallery.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SQLiteStorage {
    SQLiteDatabase db = null;
    public int init(Context context, String dbName) {
        db = context.openOrCreateDatabase(dbName, 0, null);
        if(!db.isOpen()) { return -1; }
        db.beginTransaction();
        try{
            db.execSQL("CREATE TABLE IF NOT EXISTS Photos (timestamp TEXT, caption TEXT, latitude float, longitude float, fullpath TEXT, image BLOB);");
            db.setTransactionSuccessful();
        } catch (Exception e) { return -1; }
        finally {
            db.endTransaction();
        }
        return 0;
    }
    public ArrayList<String> findPhotos(String startTimestamp, String endTimestamp, String keyword, double latitude_start, double latitude_end, double longitude_start, double longitude_end) {
        ArrayList<String> photos = new ArrayList<String>();
        Cursor dbCursor = null;
        String query = null;
        if(!db.isOpen() || (startTimestamp .equals("") && endTimestamp.equals("") && keyword.equals("") && latitude_start == 0.0 && latitude_end == 0.0 && longitude_start == 0.0 && longitude_end == 0.0)) {
            return null;
        }
        if (keyword == "")
            query = "select fullpath from photos where timestamp between '"+ startTimestamp + "' and '" + endTimestamp + "';";
        else if (startTimestamp == "")
            query = "select fullpath from photos where caption like '%" + keyword + "%';";
        else if (latitude_start == 0.0 && longitude_start == 0.0) {
            query = "select fullpath from photos where timestamp between '"+ startTimestamp + "' and '" + endTimestamp +
                    "' and caption like '%" + keyword + "%';";
        }
        else
            query = "select fullpath from photos where timestamp between '"+ startTimestamp + "' and '" + endTimestamp +
                    "' and caption like '%" + keyword + "%' and latitude between " + latitude_start + " and " + latitude_end + " and longitude between " +  longitude_start + " and " + longitude_end + ";";
        try{
            dbCursor = db.rawQuery(query, null);
            int fullpathCol = dbCursor.getColumnIndex("fullpath");
            if (dbCursor != null) {
                dbCursor.moveToFirst();
                if (dbCursor.getCount() != 0) {
                    do {
                        photos.add(dbCursor.getString(fullpathCol));
                    } while (dbCursor.moveToNext());
                }
            }
        }
        catch (Exception e) {
            return null;
        }
        return  photos;
    }
    public int addPhoto(String caption, String timestamp, double latitude, double longitude, String fullpath, byte[] image) {
        if(!db.isOpen()) { return -1; }
        db.beginTransaction();
        try{
//            String sqlStmt = "insert into photos (timestamp, caption, latitude, longitude, fullpath, image) values ('"
//                    + timestamp + "','" + caption + "', " + latitude + ", " + longitude + ", '" + fullpath+"', " + image + ");";
//            db.execSQL(sqlStmt);
//            db.setTransactionSuccessful();

            String sqlStmt = "insert into photos (timestamp, caption, latitude, longitude, fullpath, image) values (?, ?, ?, ?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sqlStmt);

            statement.bindString(1, timestamp);
            statement.bindString(2, caption);
            statement.bindDouble(3, latitude);
            statement.bindDouble(4, longitude);
            statement.bindString(5, fullpath);
            statement.bindBlob(6, image);

            statement.executeInsert();
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            return -1; }
        finally {
            db.endTransaction();
        }
        return 0;
    }
    public int deletePhoto(String fullpath) {
        if(!db.isOpen()) { return -1; }
        db.beginTransaction();
        try{
            String sqlStmt = "delete from photos where fullpath ='"+ fullpath+ "';";
            db.execSQL(sqlStmt);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            return -1;
        } finally {
            db.endTransaction();
        }
        return 0;
    }

}

