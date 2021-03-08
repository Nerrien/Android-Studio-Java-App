package com.example.priestownjava;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameLoopThread extends Thread {
    // Times per second the game is updated/drawn
    private final static int MAX_FPS = 60;
    private final static int MAX_FRAME_SKIPS = 5;
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;

    // Surface holder that can access the physical surface
    private SurfaceHolder surfaceHolder;
    // Initializing game class
    private Game game;
    // Elapsed game time in milliseconds
    private long gameTime;
    // Holds state of the game loop
    public boolean running;

    public GameLoopThread(SurfaceHolder surfaceHolder, Game game) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.game = game;
        this.gameTime = 0;
    }

    @Override
    public void run() {
        Canvas canvas;
        // time when the cycle has 'begun'?
        long beginTime;
        // time it took for the cycle to execute
        long timeDiff;
        // ms to sleep (<0 if we're behind)
        int sleepTime;
        // number of frames being skipped
        int framesSkipped;
        sleepTime = 0;
        while (running){
            canvas = null;
            try {
                // Try locking the canvas for exclusive pixel editing in surface
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    beginTime = System.currentTimeMillis();
                    // Resetting frames skipped
                    framesSkipped = 0;
                    this.game.Update(this.gameTime);
                    this.game.Draw(canvas);
                    // Calculate how long cycle took
                    timeDiff = System.currentTimeMillis() - beginTime;
                    // Calculate sleep time
                    sleepTime = (int)(FRAME_PERIOD - timeDiff);
                    if(sleepTime > 0) {
                        try {
                            // Send thread to sleep for short period
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                        // We need to catch up, so update without drawing game to screen
                        this.game.Update(this.gameTime);
                        // Add FRAME_PERIOD to check while condition again
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;
                    }
                    this.gameTime += System.currentTimeMillis() - beginTime;
                }
            }   catch(Exception e) {
                e.printStackTrace();
            } finally {
                // In case of exception surface is not left in inconsistent state
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
