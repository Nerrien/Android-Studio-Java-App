package com.example.priestownjava;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.app.Activity;

public class MainActivity extends Activity {

    private boolean showingMainMenu;
    private GamePanel gamePanel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showingMainMenu = true;
    }

    @Override
    public void onBackPressed(){
        if(!showingMainMenu) {
            showingMainMenu = true;
            // Stop game loop
            gamePanel.surfaceDestroyed(null);
            setContentView(R.layout.activity_main);
        }else{
            // Quit
            super.onBackPressed();
        }
    }

    // Start game on click
    public void onClickStartGame(View v){
        showingMainMenu = false;

        // Start and show game
        gamePanel = new GamePanel(this);
        setContentView(gamePanel);
    }
}