package com.example.rjzr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
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
        //Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        StrictMode.enableDefaults();
        View decorView = getWindow().getDecorView();

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        //Connection to database
        mdb = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = mdb.getWritableDatabase();

        //Display High Score
        cursor = sqLiteDatabase.rawQuery(getHighScoreQuery, null);
        TextView mainHighscore = (TextView) findViewById(R.id.main_highscore);
        String actualHS = "";
        if (cursor.moveToFirst()) {
            do {
                actualHS = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        mainHighscore.setText("Highscore: " + actualHS ); //set text for text view
        cursor.close();

        //Start Game Button
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainGameView.class));
            }

        });

        //Play Music
        mediaPlayer = MediaPlayer.create(this, R.raw.musicc);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

    }
}



