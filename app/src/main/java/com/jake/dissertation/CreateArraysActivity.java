package com.jake.dissertation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CreateArraysActivity extends AppCompatActivity {

    private ArrayList<String> playerNamesList;
    private ArrayList<String> pubNamesList;
    private ArrayAdapter<String> adapterPlayerNames;
    private ArrayAdapter<String> adapterPubNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_arrays);

        // Creates strings that use the arrays
        playerNamesList = new ArrayList<>();
        pubNamesList = new ArrayList<>();

        // Initializes the adapters for the lists
        adapterPlayerNames = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playerNamesList);
        adapterPubNames = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pubNamesList);

        // assigns the adapters to the Lists
        ListView playerNamesListView = findViewById(R.id.playerNamesListView);
        ListView pubNamesListView = findViewById(R.id.pubNamesListView);
        playerNamesListView.setAdapter(adapterPlayerNames);
        pubNamesListView.setAdapter(adapterPubNames);

        //Saves the inputed Player/Team names by the user to the list
        Button addPlayerNameButton = findViewById(R.id.addPlayerButton);
        addPlayerNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextPlayerName = findViewById(R.id.playerNameEditText);
                String playerName = editTextPlayerName.getText().toString();
                if (!playerName.isEmpty()) {
                    playerNamesList.add(playerName);
                    adapterPlayerNames.notifyDataSetChanged();
                    editTextPlayerName.setText("");
                } else {
                    Toast.makeText(CreateArraysActivity.this, "Please enter a player name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Saves the inputed Pub names by the user to the list
        Button addPubNameButton = findViewById(R.id.addPubButton);
        addPubNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextPubName = findViewById(R.id.pubNameEditText);
                String pubName = editTextPubName.getText().toString();
                if (!pubName.isEmpty()) {
                    pubNamesList.add(pubName);
                    adapterPubNames.notifyDataSetChanged();
                    editTextPubName.setText("");
                } else {
                    Toast.makeText(CreateArraysActivity.this, "Please enter a pub name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button saveButton = findViewById(R.id.saveArraysButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveArrays();
            }
        });

        // Button to delete all pub/player names
        Button deleteAllNamesButton = findViewById(R.id.deleteAllNamesButton);
        deleteAllNamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerNamesList.clear();
                pubNamesList.clear();

                adapterPlayerNames.notifyDataSetChanged();
                adapterPubNames.notifyDataSetChanged();

                SharedPreferences sharedPreferences = getSharedPreferences("ArraysPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("playerNames");
                editor.remove("pubNames");
                editor.apply();

                Toast.makeText(CreateArraysActivity.this, "All Pub/Player Names Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        // Load saved player names and pub names when page is loaded
        loadArrays();
    }

    private void saveArrays() {
        //Saves the data and sends the data to scoreboard to apply the new data
        SharedPreferences sharedPreferences = getSharedPreferences("ArraysPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("playerNames", new HashSet<>(playerNamesList));
        editor.putStringSet("pubNames", new HashSet<>(pubNamesList));
        editor.apply();
        Toast.makeText(this, "Arrays saved successfully", Toast.LENGTH_SHORT).show();

        // Log the data being saved for testing
        Log.d("SaveArrays", "Player Names: " + playerNamesList.toString());
        Log.d("SaveArrays", "Pub Names: " + pubNamesList.toString());

        sendDataToScoreboardActivity(playerNamesList, pubNamesList);
    }

    private void loadArrays() {
        //loads the data when page loads
        SharedPreferences sharedPreferences = getSharedPreferences("ArraysPrefs", Context.MODE_PRIVATE);
        Set<String> defaultSet = new HashSet<>();
        playerNamesList.addAll(sharedPreferences.getStringSet("playerNames", defaultSet));
        pubNamesList.addAll(sharedPreferences.getStringSet("pubNames", defaultSet));
        adapterPlayerNames.notifyDataSetChanged();
        adapterPubNames.notifyDataSetChanged();
    }

    public void sendDataToScoreboardActivity(ArrayList<String> playerNames, ArrayList<String> pubNames) {
        //sends data to scoreboard to add it to the spinners
        Intent intent = new Intent(this, ScoreboardActivity.class);
        intent.putStringArrayListExtra("playerNames", playerNames);
        intent.putStringArrayListExtra("pubNames", pubNames); // Add pub names list
        startActivity(intent);
    }

}





