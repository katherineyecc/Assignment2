package com.example.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class SimpleDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "simpleDB";
    private static final String TABLE_NAME = "SimpleTable";

    public SimpleDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_TYPE = "type";

    @Override
    public void onCreate(SQLiteDatabase db){
        String createDb = "CREATE TABLE "+TABLE_NAME+" ("+
                KEY_ID+" INTEGER PRIMARY KEY,"+
                KEY_TIME+" TEXT,"+
                KEY_TYPE+" TEXT"+
                " )";
        db.execSQL(createDb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion >= newVersion)
            return;

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public long addActivity(Activity activity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_TIME, activity.getTime());
        v.put(KEY_TYPE, activity.getType());

        long ID = db.insert(TABLE_NAME, null, v);
        return ID;
    }

    public Activity getActivity(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] query = new String[] {KEY_ID,KEY_TIME,KEY_TYPE};
        Cursor cursor = db.query(TABLE_NAME,query,KEY_ID+"=?",
                new String[] {String.valueOf(id)},null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return new Activity(
                Long.parseLong(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2)
        );
    }

    public Activity getLastActivity(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME+" ORDER BY "+
                KEY_ID+" DESC";
        Cursor cursor = db.rawQuery(query, null);
        Activity activity = new Activity();
        if(cursor != null){
            if(cursor.moveToLast()){
                activity.setID(Long.parseLong(cursor.getString(0)));
                activity.setTime(cursor.getString(1));
                activity.setType(cursor.getString(2));
            }
        }
        return activity;
    }

    public List<Activity> getAllActivities(){
        List<Activity> allActivities = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE_NAME+" ORDER BY "+
                KEY_ID+" DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            while(cursor.moveToNext()){
                Activity activity = new Activity();
                activity.setID(Long.parseLong(cursor.getString(0)));
                activity.setTime(cursor.getString(1));
                activity.setType(cursor.getString(2));
                allActivities.add(activity);
            }
        }
        return allActivities;
    }
}
