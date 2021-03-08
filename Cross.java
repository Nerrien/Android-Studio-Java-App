package com.example.priestownjava;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Cross {

    // Position on screen
    public float x;
    public float y;
    public Rect crossRect;

    private boolean held;

    // Speed and direction
    private float velocity;

    public Cross(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean beingHeld(int touchX, int touchY){
        crossRect = new Rect((int)this.x, (int)this.y, (int)this.x + Game.crossNormalImage.getWidth(), (int)this.y + Game.crossNormalImage.getHeight());
        if (crossRect.contains(touchX,touchY)){
            held = true;
        }
        return held; //crossRect.contains(touchX, touchY);
    }
    public void moving(int x, int y){
        if (held == true){
            int widthOffset = (int)(Game.screenWidth * 0.1);
            this.x = x - widthOffset;
            int heightOffset = (int)(Game.screenHeight * 0.1);
            this.y = y - heightOffset;
        }

    }
    public void dropped(){
        held = false;
    }
    public void draw(Canvas canvas){
        if (held){
            canvas.drawBitmap(Game.crossHeldImage, x, y, null);
        } else if (!held){
            canvas.drawBitmap(Game.crossNormalImage, x, y, null);
        }

    }
}
