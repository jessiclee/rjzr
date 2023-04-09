package com.example.rjzr;

import static com.example.rjzr.DatabaseHelper.*;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameView extends SurfaceView implements Runnable {
    private Thread thread;
    private boolean isRunning;
    private Paint paint;
    private Background background1, background2;

    static Lock slock = new ReentrantLock();
    static Lock srlock = new ReentrantLock();

    private Cone cones[] = new Cone[6];
    static volatile int screenX, screenY = 0;
    public static volatile float screenRatioX, screenRatioY;
    RunningMan runningMan;

    int gameTime = 0;
    ContentValues values = new ContentValues();

    Context ctx = getContext();
    DatabaseHelper mdb = new DatabaseHelper(ctx);
    SQLiteDatabase sqLiteDatabase = mdb.getWritableDatabase();

    String getHighScoreQuery = "SELECT * FROM score_entries WHERE _id=1";
    Cursor cursor = sqLiteDatabase.rawQuery(getHighScoreQuery, null);

    int dbScore = 0;

    private int numLives;
    private boolean isInv;
    private int inv_timer;

    private final Object mutex = new Object();

    public GameView(MainGameView activity, int screenX, int screenY){
        super(activity);
        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 2560f / screenX;
        screenRatioY = 1440f / screenY;
        numLives = 3;
        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());
        runningMan = new RunningMan(this, screenX, screenY, getResources());

        int[] conePosY = {0, -750, -1300, -2100, -2600, -1300};
        int[] conePosX = {screenX, screenX-800, screenX+800 , screenX, screenX-800, screenX+800};

        for(int i = 0; i < 6; i++){
            cones[i] = new Cone(this, conePosX[i], conePosY[i], getResources());
        }
    }

    @Override
    public void run(){

        Thread t1 = new Thread(update);
        Thread t2 = new Thread(checkCollision);
        Thread t3 = new Thread(addLives);
        Thread t4 = new Thread(timerThread);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        while(isRunning){
            try {
                toDraw();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            toSleep();
        }
    }

    Runnable timerThread = new Runnable() {
        @Override
        public void run() {

            while(isRunning){
                try {
                    //System.out.println("Current score:" + gameTime);
                    Thread.sleep(1000);
                    gameTime += 1;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };

    // multithread for updating the background animation
    Runnable update = new Runnable() {
        @Override
        public void run() {
            while(isRunning) {
                try {
                    Thread.sleep(70);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                background1.y += (30 * screenRatioY);
                if (background1.y >screenY) {
                    background1.y = 0;
                }
                if (background1.y > 0) {
                    background2.y = background1.y - screenY;
                } else {
                    background2.y = background1.y + screenY;
                }

                for(Cone c: cones){
                    if(c.y > screenY){
                        c.y = 0;
                    } else{
                        c.y += 20;
                    }
                }

            }
        }
    };

    Runnable addLives = new Runnable() {
        @Override
        public void run() {
            while(isRunning){
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (mutex){
                    if(numLives < 3){
                        numLives++;
                    }

                }
            }
        }
    };

    Runnable checkCollision = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                for (Cone c : cones) {
                    if (Rect.intersects(c.getCollisionShape(), runningMan.getCollisionShape())) {
                            System.out.println("I hit something");
                            if (numLives > 1) {
                                System.out.println("initiate counter thread");
                                Thread counter = new Thread(invincible_Counter);
                                counter.start();
                                try {
                                    counter.join();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                isRunning = false;

                                if (cursor.moveToFirst()) {
                                    do {
                                        dbScore = Integer.parseInt(cursor.getString(1));
                                    } while (cursor.moveToNext());
                                }
//                                System.out.println("gamescore " + gameTime);
//                                System.out.println("dbscore " + dbScore);
//                                System.out.println(dbScore < gameTime);

                                if(dbScore < gameTime){
                                    values.put(SCORE, gameTime);
                                    int rows = sqLiteDatabase.update(SCORE_TABLE, values, ID + "=?",new String[] {"1"});
                                    System.out.println("no of rows affected" + rows);
                                }

                                cursor.close();

                                // if yall wanna reset ur score uncomment out this
//                                values.put(SCORE, 0);
//                                int rows = sqLiteDatabase.update(SCORE_TABLE, values, ID + "=?",new String[] {"1"});
//                                System.out.println("no of rows affected" + rows);

                                return;
                            }

                    }
                }
            }
        }
    };

    Runnable invincible_Counter = new Runnable() {
        @Override
        public void run() {
            try {
                isInv = true;
                synchronized (mutex){
                    numLives--;
                }
                    System.out.println("Number of Lives: " +numLives);
                    for(int i = 0; i < 5; i ++){
                        inv_timer ++;
                        Thread.sleep(1000);
                    }
                    inv_timer = 0;
                    isInv = false;
                    return;

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

//                canvas.drawText("Lives Left:" + numLives + "", screenX , 164, paint);
//                if(isInv){
//                    canvas.drawText("Invisibility:" + inv_timer + " s", screenX , 164, paint);
//                }


                for(Cone c: cones){
                    canvas.drawBitmap(c.getMyCone(), c.x, c.y, paint);
                }

                getHolder().unlockCanvasAndPost(canvas);
            }
        } catch(InterruptedException e){
            e.printStackTrace();
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