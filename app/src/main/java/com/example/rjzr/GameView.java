package com.example.rjzr;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.content.Context;
import android.widget.TextView;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameView extends SurfaceView implements Runnable {
    private Thread thread;
    private boolean isRunning;
    private Paint paint;
    private Background background1, background2;

    static Lock slock = new ReentrantLock();
    static Lock srlock = new ReentrantLock();
    int score;
    private Cone cone;
    static volatile int screenX, screenY = 0;
    public static volatile float screenRatioX, screenRatioY;

    RunningMan runningMan;

    public GameView(MainGameView activity, int screenX, int screenY){
        super(activity);
        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 2560f / screenX;
        screenRatioY = 1440f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());
        cone = new Cone(this, screenX, screenY, getResources());
        runningMan = new RunningMan(this, screenX, screenY, getResources());
        TextView currTimer = findViewById(R.id.timer_text);


    }

    @Override
    public void run(){
        Thread t1 = new Thread(update);
        Thread t2 = new Thread(lane2);
        t2.start();
        t1.start();
        while(isRunning){
            try {
                toDraw();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            toSleep();
        }
    }

    // multithread for updating the background animation
    Runnable update = new Runnable() {
        @Override
        public void run() {
            while(isRunning) {
                background1.y += (40 * screenRatioY);
                if (background1.y >screenY) {
                    background1.y = 0;
                }
                if (background1.y > 0) {
                    background2.y = background1.y - screenY;
                } else {
                    background2.y = background1.y + screenY;
                }
            }
        }
    };

    Runnable lane2 = new Runnable() {
        @Override
        public void run() {
            while(isRunning) {

            }
        }
    };


    private void toSleep() {
        try{
            Thread.sleep(10);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    private void toDraw() throws InterruptedException {
        try {
            if (getHolder().getSurface().isValid()) {
                Canvas canvas = getHolder().lockCanvas();
                canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
                canvas.drawBitmap(background2.background, background2.x, background2.y, paint);
                canvas.drawBitmap(runningMan.getRun(), runningMan.x, runningMan.y, paint);
                //canvas.drawText(score + "", screenX / 2f, 164);
                canvas.drawBitmap(cone.getMyCone(), cone.x, cone.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
            }
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }

//    private void toUpdate() {
//        background1.y += (40 * screenRatioY);
//        if (background1.y > this.screenY){
//            background1.y = 0;
//        }
//        if (background1.y > 0) {
//            background2.y = background1.y - this.screenY;
//        }
//        else {
//            background2.y = background1.y + this.screenY;
//        }
//    }

    public void resume(){
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause(){
        try{
            isRunning = false;
            thread.join();
        } catch(InterruptedException e){
            e.printStackTrace();
        }

    }

}
