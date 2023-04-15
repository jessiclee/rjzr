package com.example.rjzr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainGameView extends AppCompatActivity {
    final String TAG = "Main GameView";
    private GameView gameView;

    //ensure he cannot be swiped off screen
    int swipeLane = 2;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

        //make man move lanes when swiping left and right
        gameView.setOnTouchListener(new OnSwipeTouchListener(ctx) {
            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeRight() {
                if(swipeLane < 3){
                    gameView.runningMan.x += 400;
                    swipeLane++;
                }
            }
            public void onSwipeLeft() {
                if(swipeLane > 1) {
                    gameView.runningMan.x -= 400;
                    swipeLane--;
                }
            }
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