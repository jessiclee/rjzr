package com.example.rjzr;

import static com.example.rjzr.GameView.screenRatioX;
import static com.example.rjzr.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Ghost {
    int width, height = 1,x,y;
    Bitmap ghost;

    Ghost (int screenX, int screenY, Resources res) {

        ghost = BitmapFactory.decodeResource(res, R.drawable.ghost);
        width = ghost.getWidth();
        height = ghost.getHeight();

        width /= 4;
        height /= 2;


        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        ghost = Bitmap.createScaledBitmap(ghost, width, height, false);

        x = screenX/2 - width/2 + 10;
        y = (int) (screenY - height);
    }

}
