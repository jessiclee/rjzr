package com.example.rjzr;

import static com.example.rjzr.DatabaseHelper.*;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.content.Context;
import android.view.View;
import android.content.Intent;

public class GameView extends SurfaceView implements Runnable {
    private Thread thread;
    private boolean isRunning;
    private boolean gameOver;
    private Paint paint;
    private Background background1, background2;
    private Cone cones[] = new Cone[6];
    static volatile int screenX, screenY = 0;
    public static volatile float screenRatioX, screenRatioY;
    RunningMan runningMan;
    Ghost ghost;

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
    private MainGameView activity;
    private final Object mutex = new Object();

    public GameView(MainGameView activity, int screenX, int screenY){
        super(activity);
        this.activity = activity;
        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 2560f / screenX;
        screenRatioY = 1440f / screenY;
        numLives = 3;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());
        runningMan = new RunningMan(this, screenX, screenY, getResources());
        ghost = new Ghost(screenX, screenY, getResources());

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.BLACK);

        int[] conePosY = {0, -750, -1300, -2100, -2600, -1300};
        int[] conePosX = {screenX, screenX-800, screenX+800 , screenX, screenX-800, screenX+800};

        for(int i = 0; i < 6; i++){
            cones[i] = new Cone(conePosX[i], conePosY[i], getResources());
        }
    }

    @Override
    public void run(){
        //Multithreading; start multiple threads for collision, timer, regeneration of lives and
        Thread t1 = new Thread(update);
        Thread t2 = new Thread(checkCollision);
        Thread t3 = new Thread(addLives);
        Thread t4 = new Thread(timerThread);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        while(true){
            try {
                toDraw();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            toSleep();
        }
    }

    //Thread used for score through a timer
    Runnable timerThread = new Runnable() {
        @Override
        public void run() {

            while(isRunning){
                try {
                    Thread.sleep(1000);
                    gameTime += 1;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };

    // multi threading for updating the background animation and cones
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

    //Regeneration of lives
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

    //CheckCollisions thread, if there are no more lives: stops all other threads, update score and return to main menu
    Runnable checkCollision = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                for (Cone c : cones) {
                    if (Rect.intersects(c.getCollisionShape(), runningMan.getCollisionShape())) {
                            if (numLives > 1) {
                                Thread counter = new Thread(invincible_Counter);
                                counter.start();
                                try {
                                    counter.join();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                isRunning = false;
                                gameOver = true;
                                if (cursor.moveToFirst()) {
                                    do {
                                        dbScore = Integer.parseInt(cursor.getString(1));
                                    } while (cursor.moveToNext());
                                }

                                if(dbScore < gameTime){
                                    dbScore = gameTime;
                                    values.put(SCORE, gameTime);
                                    int rows = sqLiteDatabase.update(SCORE_TABLE, values, ID + "=?",new String[] {"1"});
                                }

                                cursor.close();
                                exitGame();
                                return;
                            }
                    }
                }
            }
        }
        public void exitGame(){
            try {
                Thread.sleep(10000);
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.finish();
                        activity.startActivity(new Intent(activity, MainGameView.class));
                    }

                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    };

    // Invincibility thread, shares numLives through a mutex
    Runnable invincible_Counter = new Runnable() {
        @Override
        public void run() {
            try {
                isInv = true;
                synchronized (mutex){
                    numLives--;
                }
                    for(int i = 0; i < 5; i ++){
                        inv_timer ++;
                        Thread.sleep(1000);
                    }
                    inv_timer = 0;
                    isInv = false;

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

    //Draws all the graphics needed on the Canvas
    private void toDraw() throws InterruptedException {
        try {
            if (getHolder().getSurface().isValid()) {
                Canvas canvas = getHolder().lockCanvas();
                    canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
                    canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

                if(isRunning) {
                    canvas.drawBitmap(runningMan.getRun(), runningMan.x, runningMan.y, paint);
                    for (Cone c : cones) {
                        canvas.drawBitmap(c.getMyCone(), c.x, c.y, paint);
                    }
                    canvas.drawText("Lives Left:" + numLives + "", screenX / 2f, 164, paint);
                    if (isInv) {
                        canvas.drawText("Invincibility:" + (5 - inv_timer) + " s", screenX / 4f, 950, paint);
                    }
                    canvas.drawText("Score: " + gameTime + "", screenX / 2f, 330, paint);

                }
                if(gameOver){
                    paint.setTextSize(150);
                    canvas.drawText("CAUGHT BY", 300, 400, paint);
                    canvas.drawText("CASPER THE", 300, 550, paint);
                    canvas.drawText("FRIENDLY GHOST", 120, 700, paint);
                    canvas.drawText("HighScore: " + dbScore + "", 280, 950, paint);
                    canvas.drawText("Score: " + gameTime + "", 450, 1100, paint);
                    canvas.drawBitmap(ghost.ghost, ghost.x, ghost.y, paint);
                    paint.setTextSize(90);
                    canvas.drawText("Exiting game....", 350, 1400, paint);

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