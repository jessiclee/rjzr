package com.example.rjzr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    DatabaseHelper mdb;

    String getHighScoreQuery = "SELECT * FROM score_entries WHERE _id=1";
    Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        mdb = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = mdb.getWritableDatabase();

        cursor = sqLiteDatabase.rawQuery(getHighScoreQuery, null);

        TextView mainHighscore = (TextView) findViewById(R.id.main_highscore);

        String actualHS = "test";
        if (cursor.moveToFirst()) {
            do {
                actualHS = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        mainHighscore.setText("Highscore: " + actualHS ); //set text for text view

        cursor.close();

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainGameView.class));
            }

        });

        mediaPlayer = MediaPlayer.create(this, R.raw.musicc);
        mediaPlayer.start();

        mediaPlayer.setLooping(true);




    }
}



