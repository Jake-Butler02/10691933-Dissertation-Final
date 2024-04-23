package com.jake.dissertation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Button clearDataButton;
    private Button ReturnToScoreboard;
    private Button addPlayerPubButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        clearDataButton = findViewById(R.id.clear_data_button);
        ReturnToScoreboard = findViewById(R.id.return_to_score);
        addPlayerPubButton = findViewById(R.id.addPlayerPubButton);

        clearDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates an Intent to call the clear data void in scoreboard java code to clear data on the scoreboard
                Intent intent = new Intent(SettingsActivity.this, ScoreboardActivity.class);
                // Does this to prevent duplication
                intent.putExtra("CLEAR_DATA", true);
                startActivity(intent);
            }
        });

        ReturnToScoreboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ScoreboardActivity.class);
                startActivity(intent);
            }
        });



        addPlayerPubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, CreateArraysActivity.class);
                startActivity(intent);
            }
        });
    }
}




