package com.example.photogallery.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class SQLiteStorage {
    SQLiteDatabase db = null;
    public int init(Context context, String dbName) {
        db = context.openOrCreateDatabase(dbName, 0, null);
        if(!db.isOpen()) { return -1; }
        db.beginTransaction();
        try{
            db.execSQL("CREATE TABLE IF NOT EXISTS Photos (timestamp TEXT, caption TEXT, latitude float, longitude float, fullpath TEXT);");
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
            query = "select fullpath from photos where caption like '%" + keyword + "%';";
        }
        else
            query = "select fullpath from photos where timestamp between '"+ startTimestamp + "' and '" + endTimestamp +
                    "' and caption like '%" + keyword + "%';";
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
    public int addPhoto(String caption, String timestamp, double latitude, double longitude, String fullpath) {
        if(!db.isOpen()) { return -1; }
        db.beginTransaction();
        try{
            String sqlStmt = "insert into photos (timestamp, caption, latitude, longitude, fullpath) values ('"
                    + timestamp + "','" + caption + "', " + latitude + ", " + longitude + ",'" + fullpath+"');";
            db.execSQL(sqlStmt);
            db.setTransactionSuccessful();
        } catch (Exception e) { return -1; }
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

