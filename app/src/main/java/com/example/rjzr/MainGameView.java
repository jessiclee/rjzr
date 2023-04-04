package com.example.rjzr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainGameView extends AppCompatActivity {
    private GameView gameView;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        gameView = new GameView(this, point.x, point.y);
        Context ctx = getApplicationContext();
        setContentView(gameView);
        gameView.setOnTouchListener(new OnSwipeTouchListener(ctx) {
//                public void onSwipeTop() {
//                    //Toast.makeText(ctx, "top", Toast.LENGTH_SHORT).show();
//                }
            public void onSwipeRight() {
                    //Toast.makeText(ctx, "right", Toast.LENGTH_SHORT).show();
                gameView.runningMan.x += 400;
            }
            public void onSwipeLeft() {
                    //Toast.makeText(ctx, "left", Toast.LENGTH_SHORT).show();
                gameView.runningMan.x -= 400;
            }
//                public void onSwipeBottom() {
//                    //Toast.makeText(ctx, "bottom", Toast.LENGTH_SHORT).show();
//
//            }

        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameView.resume();
    }

}