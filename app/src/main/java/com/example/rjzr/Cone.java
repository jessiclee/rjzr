package com.example.rjzr;

import static com.example.rjzr.GameView.screenRatioX;
import static com.example.rjzr.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Cone {
    Bitmap myCone;
    int width, height = 1,x,y;
    int isOver = 0;


    public Cone(int screenX, int screenY, Resources res){
        this.myCone = BitmapFactory.decodeResource(res, R.drawable.cone);

        width = myCone.getWidth();
        height = myCone.getHeight();

        width /= 4;
        height /= 2;


        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        myCone = Bitmap.createScaledBitmap(myCone, width, height, false);

        x = screenX/2 - width/2;
        y = (int) (screenY - height);
    }

    Bitmap getMyCone() throws InterruptedException{

        return myCone;
    };

    Rect getCollisionShape () {
        return new Rect(x - 100, y + 200, x + width - 100, y + height - 100);
    }

}
