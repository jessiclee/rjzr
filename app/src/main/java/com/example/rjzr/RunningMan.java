package com.example.rjzr;

import static com.example.rjzr.GameView.screenRatioX;
import static com.example.rjzr.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class RunningMan {
    Bitmap running1, running2, running3;
    private GameView gameView;
    int width, height, runCounter = 1,x,y;
    int isRunning = 0;
    public RunningMan(GameView gameView, int screenX, int screenY, Resources res){
        this.gameView = gameView;
        running1 = BitmapFactory.decodeResource(res, R.drawable.leftfoot);
        running2 = BitmapFactory.decodeResource(res, R.drawable.stand);
        running3 = BitmapFactory.decodeResource(res, R.drawable.rightfoot);

        width = running1.getWidth();
        height = running1.getHeight();

        width /= 8;
        height /= 2;


        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        running1 = Bitmap.createScaledBitmap(running1, width, height, false);
        running2 = Bitmap.createScaledBitmap(running2, width, height, false);
        running3 = Bitmap.createScaledBitmap(running3, width, height, false);
        x = screenX/2 - width/2;
        y = (int) (screenY - height);
    }

    Bitmap getRun() throws InterruptedException {
        Thread.sleep(20);
        if(runCounter == 1){
            runCounter++;
            return running1;
        } else if(runCounter == 2){
            runCounter++;
            return running3;
        }
        runCounter = 1;
        return running2;
    }


}