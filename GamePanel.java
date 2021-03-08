package com.example.priestownjava;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

// Class takes care of surface for drawing and touch

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private Game game;
    private GameLoopThread gameLoopThread;

    public GamePanel(Context context) {
        super(context);

        // Focus must be on GamePanel so that events can be handled
        this.setFocusable(true);
        // For intercepting events on the surface
        this.getHolder().addCallback(this);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    public void surfaceCreated(SurfaceHolder holder) {
        // Can now safely start the game loop
        startGame();
    }

    private void startGame(){
        // Creating instance of Game class
        game = new Game(getWidth(), getHeight(), getResources());

        gameLoopThread = new GameLoopThread(this.getHolder(), game);

        gameLoopThread.running = true;
        gameLoopThread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        gameLoopThread.running = false;

        // Shut down game loop thread cleanly
        boolean retry = true;
        while(retry) {
            try {
                gameLoopThread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if(action == MotionEvent.ACTION_DOWN){
            game.touchEvent_actionDown(event);
        }

        if(action == MotionEvent.ACTION_MOVE) {
            game.touchEvent_actionMove(event);
        }

        if(action == MotionEvent.ACTION_UP){
            game.touchEvent_actionUp(event);
        }

        return true;
    }
}
