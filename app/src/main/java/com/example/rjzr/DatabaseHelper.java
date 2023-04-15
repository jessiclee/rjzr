package com.example.rjzr;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db";

    public static final String SCORE_TABLE = "score_entries";

    //Column names
    public static final String ID = "_id";
    public static final String SCORE = "score";

    //String array of columns
    private static final String[] COLUMNS = {ID, SCORE};

    private static final String CREATE_TABLE =
            "CREATE TABLE " + SCORE_TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY, " +
                    // will auto-increment if no value passed
                    SCORE + " TEXT );";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE);

        ContentValues values = new ContentValues();
        values.put(SCORE, 0);
        db.insert(SCORE_TABLE, null, values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldEntry, int newEntry) {
        db.execSQL("DROP TABLE IF EXISTS " + SCORE_TABLE);
        onCreate(db);
    }

}