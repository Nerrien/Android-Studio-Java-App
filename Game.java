package com.example.priestownjava;

import java.util.ArrayList;
import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

// The game

public class Game {

    // Screen info
    public static int screenWidth;
    public static int screenHeight;
    public static float screenDensity;
    private boolean gameOver;

    // Needed to draw background image full screen.
    private Paint paintForImages;

    // Images
    public static Bitmap crossNormalImage;
    public static Bitmap crossHeldImage;
    public static Bitmap villainImage;

    // List of all villains on a screen.
    private ArrayList<Villain> activeVillains;

    // Needed for new random coordinates.
    private Random random = new Random();

    public Cross cross;
    // Is it the start of the round?
    public boolean startOfRound;
    // Attack round is taking place
    public boolean attackRound;
    // Number of attack patterns
    private int maxAttackPatterns = 4;
    // Attack pattern for this round
    private int attackPattern;
    // Time since start of attack
    private long attackTime;
    private long timePassed;
    private int attack;
    // Attack timings
    private static long attackInterval = 3000;
    private int numOfAttacks = 2;
    // iFrames toggle prevents player taking immediate death damage on collision
    private boolean iFrame;
    private long iFrameTime;
    private static long iFrameTimeLimit = 3000;
    // Player health;
    public int pHealth;
    private Paint paintText;

    public Game(int screenWidth, int screenHeight, Resources resources){
        Game.screenWidth = screenWidth;
        Game.screenHeight = screenHeight;
        Game.screenDensity = resources.getDisplayMetrics().density;

        this.LoadContent(resources);
        // Paint for images
        paintForImages = new Paint();
        paintForImages.setFilterBitmap(true);
        // Text stats
        int textSize = 25;
        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(textSize);
        // Tracking active villain/attacks
        activeVillains = new ArrayList<Villain>();
        pHealth = 100;
        //cross = new Cross( screenWidth / 2, screenHeight / 2);
        this.ResetRound();
    }

    /**
     * Load files.
     */
    private void LoadContent(Resources resources){
        villainImage = BitmapFactory.decodeResource(resources, R.drawable.villain2);
        crossHeldImage = BitmapFactory.decodeResource(resources, R.drawable.cross_held2);
        crossNormalImage = BitmapFactory.decodeResource(resources, R.drawable.cross_normal2);
    }

    /**
     * For (re)setting some game variables before game can start.
     */
    private void ResetRound(){
        gameOver = false;
        activeVillains.clear();
        cross = new Cross( screenWidth / 2, screenHeight / 2);
        startOfRound = true;
    }

    /**
     * Game update method.
     * If game over, skip.
     * Update each villain's position.
     * If villain collides with cross, health goes down.
     * If currently
     * @param gameTime Elapsed game time in milliseconds.
     */
    public void Update(long gameTime) {
        if(gameOver){
            return;
        }
        // Check for collision
        for(int i=0; i < activeVillains.size(); i++) {
            activeVillains.get(i).update();
            if (activeVillains.get(i).collideWithCross(cross.crossRect)) {
                hitEnemy(gameTime);
            }
        }
        if(attackRound){
            if (startOfRound){
                attackTime = gameTime;
                startOfRound = false;
            }
            long timePassed = gameTime - attackTime;
            if (gameTime - attackTime > attackInterval){
                // Enemy attack cycle
                attack += 1;
                int attackChoice = random.nextInt(numOfAttacks) + 1;
                attackPatterns(attackPattern,attackChoice);
                attackTime = gameTime;
                if(attack == 3){
                    attackRound = false;
                    attack = 0;
                }
            }
        }
    }

    /**
     * Pattern governing position of spawns for villains during attack.
     * @param attackPattern
     * @param attack
     */
    public void attackPatterns(int attackPattern, int attack){
        switch (attackPattern){
            case 0: // enter from north, x = spread
                if (attack == 3){
                    addNewVillain((int)(screenWidth * 0.6),(int)(screenHeight * -0.5), 'n');
                    attackRound = false;
                } else if (attack == 2){
                    addNewVillain((int)(screenWidth * 0.3),(int)(screenHeight * -0.5), 'n');
                } else if (attack == 1){
                    addNewVillain((int)(screenWidth * 0),(int)(screenHeight * -0.5), 'n');
                }
                break;
            case 1: // enter from east, y = spread
                if (attack == 3){
                    addNewVillain((int)(screenWidth * 1.5),(int)(screenHeight * 0.6), 'e');
                    attackRound = false;
                } else if (attack == 2){
                    addNewVillain((int)(screenWidth * 1.5),(int)(screenHeight * 0.3), 'e');
                } else if (attack == 1){
                    addNewVillain((int)(screenWidth * 1.5),(int)(screenHeight * 0), 'e');
                }
                break;
            case 2: // enter from south, x = spread
                if (attack == 3){
                    addNewVillain((int)(screenWidth * 0.6),(int)(screenHeight * 1.5), 's');
                    attackRound = false;
                } else if (attack == 2){
                    addNewVillain((int)(screenWidth * 0.3),(int)(screenHeight * 1.5), 's');
                } else if (attack == 1){
                    addNewVillain((int)(screenWidth * 0),(int)(screenHeight * 1.5), 's');
                }
                break;
            case 3: // enter from west, y = spread
                if (attack == 3){
                    addNewVillain((int)(screenWidth * -0.5),(int)(screenHeight * 0.6), 'w');
                    attackRound = false;
                } else if (attack == 2){
                    addNewVillain((int)(screenWidth * -0.5),(int)(screenHeight * 0.3), 'w');
                } else if (attack == 1){
                    addNewVillain((int)(screenWidth * -0.5),(int)(screenHeight * 0), 'w');
                }
                break;
        }
    }
    /**
     * Draw the game to the screen.
     * @param canvas Canvas on which we will draw.
     */
    public void Draw(Canvas canvas) {
        // First we need to erase everything we draw before.
        canvas.drawColor(Color.WHITE);

        // Draw Villains
        for(int i=0; i < activeVillains.size(); i++){
            activeVillains.get(i).draw(canvas);
        }
        cross.draw(canvas);
        // Draw health
        canvas.drawText("Health: " + Integer.toString(pHealth), 8.0f, 25.0f,paintText);
    }

    /**
     *
     * @param gameTime
     */
    public void hitEnemy (long gameTime){
        if (!iFrame){
            iFrame = true;
            iFrameTime = gameTime;
            pHealth -= 10;
        } else if (iFrame){
            if (gameTime - iFrameTime > iFrameTimeLimit) { // 3000 milliseconds
                iFrame = false;
            }
        }
        return;
    }

    /**
     * When touch on screen is detected.
     *
     * @param event MotionEvent
     */
    public void touchEvent_actionDown(MotionEvent event){
        if (cross.beingHeld((int)event.getX(), (int)event.getY()) & startOfRound) {
            attackRound = true;
            // Selecting attack pattern
            attackPattern = random.nextInt(maxAttackPatterns);
        }
    }

    /**
     * When moving on screen is detected.
     *
     * @param event MotionEvent
     */
    public void touchEvent_actionMove(MotionEvent event){
        cross.moving((int)event.getRawX(),(int)event.getRawY());
    }

    /**
     * When touch on screen is released.
     *
     * @param event MotionEvent
     */
    public void touchEvent_actionUp(MotionEvent event){
        cross.dropped();
    }

    /** Creates new villain, adds to array */
    private void addNewVillain(int x, int y, char dir){
        this.activeVillains.add( new Villain( x,y,dir ) );
        int upwards = 1;
    }

}