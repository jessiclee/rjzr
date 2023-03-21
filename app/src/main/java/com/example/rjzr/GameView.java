package com.example.rjzr;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceView;
import android.content.Context;

public class GameView extends SurfaceView implements Runnable {
    private Thread thread;
    private boolean isRunning;
    private Paint paint;
    private Background background1, background2;
    private int screenX, screenY = 0;
    public static float screenRatioX, screenRatioY;

    public GameView(MainGameView activity, int screenX, int screenY){
        super(activity);
        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 2560f / screenX;
        screenRatioY = 1440f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());


    }

    @Override
    public void run(){
        while(isRunning){
            toUpdate();
            toDraw();
            toSleep();
        }
    }

    private void toSleep() {
        try{
            Thread.sleep(10);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private void toDraw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void toUpdate() {
        background1.y += (40 * screenRatioY);
        if (background1.y > this.screenY){
            background1.y = 0;
        }
        if (background1.y > 0) {
            background2.y = background1.y - this.screenY;
        }
        else {
            background2.y = background1.y + this.screenY;
        }
    }

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
