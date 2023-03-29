package com.example.rjzr;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

public class MainGameView extends AppCompatActivity {
    final String TAG = "Main GameView";
    private GameView gameView;
    MainActivity.OnSwipeTouchListener onSwipeTouchListener;
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

        setContentView(gameView);
        try {
            onSwipeTouchListener = new MainActivity.OnSwipeTouchListener(this, findViewById(R.id.main_game_view_relativeLayout));
        }catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
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