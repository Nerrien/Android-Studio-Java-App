package com.example.priestownjava;

import android.graphics.Canvas;
import android.graphics.Rect;
import java.lang.Object;

public class Villain {

    public static char entry; // Which side is Villain entering from? n e s w
    public static float speed = 10; // +8 for vertical due to extra space

    public float x;
    public float y;
    public Rect villainRect;

    private float velocity;

    public Villain(int x, int y, char entry){
        this.x = x;
        this.y = y;
        this.entry = entry;
    }
    public void update(){
        if (this.entry == 'n'){
            this.y += speed + 8;
        } else if (this.entry == 'e'){
            this.x += speed * -1;
        } else if (this.entry == 's'){
            this.y += (speed * -1) - 8;
        } else if (this.entry == 'w'){
            this.x += speed;
        }
        villainRect = new Rect((int)this.x, (int)this.y, (int)this.x + Game.villainImage.getWidth(), (int)this.y + Game.villainImage.getHeight());

    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(Game.villainImage, x, y, null);
    }

    // MAKE COLLISION DETECTION THING HERE?
    public boolean collideWithCross(Rect crossRect){
        if (Rect.intersects(crossRect, villainRect)){
            return(true);
        } else {
            return(false);
        }
    }
}
