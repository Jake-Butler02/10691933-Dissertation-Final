package com.jake.dissertation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppCompatButton map = findViewById(R.id.button2);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMap();
            }
        });

        AppCompatButton scoreboard = findViewById(R.id.button);
        scoreboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenScoreboard();
            }
        });
    }
    private void OpenMap() {
        startActivity(new Intent(MainActivity.this, MapActivity.class));
    }

    private void OpenScoreboard() {
        startActivity(new Intent(MainActivity.this, ScoreboardActivity.class));
    }
}