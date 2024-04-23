package com.jake.dissertation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import java.util.Random;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScoreboardActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private LinearLayout pubContainer;
    private TextView totalScoreTextView;
    private static final String PREFS_NAME = "ScoreboardPrefs";
    private static final String ENTRY_PREFS_KEY = "Entries"; //New
    private PopupWindow popupWindow;
    private ArrayList<Entry> entries;
    private TextView randomChallengeTextView;
    private Button generateChallengeButton;
    private Map<String, Integer> totalScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        randomChallengeTextView = findViewById(R.id.randomChallengeTextView);
        generateChallengeButton = findViewById(R.id.generateChallengeButton);
        Button addEntryButton = findViewById(R.id.addEntryButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        scrollView = findViewById(R.id.scrollView);
        pubContainer = findViewById(R.id.pubContainer);
        totalScoreTextView = findViewById(R.id.totalScoresTextView);
        totalScores = new HashMap<>();
        entries = new ArrayList<>();

        generateChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This will change the textview to a random challenge
                String randomChallenge = generateRandomChallenge();
                randomChallengeTextView.setText(randomChallenge);
            }
        });

        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve player and pub names from the loaded ArrayList
                Map<String, ArrayList<String>> loadedArrays = loadArrays();
                ArrayList<String> playerNames = loadedArrays.get("playerNames");
                ArrayList<String> pubNames = loadedArrays.get("pubNames");

                // Checks to see if player names exist and logs them, used for testing
                if (playerNames != null) {
                    Log.d("ScoreboardEntrySection", "Player Names: " + playerNames.toString());
                } else {
                    Log.e("ScoreboardEntrySection", "Player Names are null");
                }

                View popupView = LayoutInflater.from(ScoreboardActivity.this).inflate(R.layout.popup_add_entry, null);
                // Populate the spinners with pub names
                Spinner pubSpinner = popupView.findViewById(R.id.spinner_pub);
                if (pubSpinner != null && pubNames != null && !pubNames.isEmpty()) {
                    ArrayAdapter<String> pubAdapter = new ArrayAdapter<>(ScoreboardActivity.this, android.R.layout.simple_spinner_item, pubNames);
                    pubAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    pubSpinner.setAdapter(pubAdapter);
                } else {
                    //Logs data if failure for testing and tells the user to create the missing pub names
                    Log.e("ScoreboardActivity", "Pub names list is null or empty");
                    Toast.makeText(ScoreboardActivity.this, "No pub names available, Go to settings to add some", Toast.LENGTH_LONG).show();
                }

                // loads the popup page as a popupwindow
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.MATCH_PARENT;
                boolean focusable = true;
                popupWindow = new PopupWindow(popupView, width, height, focusable);

                // Populate the spinners with player names
                Spinner playerSpinner = popupView.findViewById(R.id.spinner_player_name);
                if (playerSpinner != null && playerNames != null && !playerNames.isEmpty()) {
                    ArrayAdapter<String> playerAdapter = new ArrayAdapter<>(ScoreboardActivity.this, android.R.layout.simple_spinner_item, playerNames);
                    playerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    playerSpinner.setAdapter(playerAdapter);
                } else {
                    Log.e("ScoreboardActivity", "Player names list is null or empty");
                    //Logs data if failure for testing and tells the user to create the player/team names
                    Toast.makeText(ScoreboardActivity.this, "No Player/Team names available, Go to settings to add some", Toast.LENGTH_LONG).show();
                }

                Spinner playerNameSpinner = popupView.findViewById(R.id.spinner_player_name);
                EditText scoreEditText = popupView.findViewById(R.id.et_score);
                Button confirmButton = popupView.findViewById(R.id.btn_confirm);
                Button clearDataButton = popupView.findViewById(R.id.clear_data_button);

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Gets the players names and scores from the text and spinners
                        String playerName = playerNameSpinner.getSelectedItem().toString();
                        String scoreText = scoreEditText.getText().toString();

                        if (!playerName.isEmpty() && !scoreText.isEmpty()) {
                            //This Section tests if the sections are empty before saving the data
                            int score = Integer.parseInt(scoreText);
                            String pubName = pubSpinner.getSelectedItem().toString();
                            addEntry(playerName, pubName, score);
                            saveData();
                            Toast.makeText(ScoreboardActivity.this, "New Score Added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ScoreboardActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                if (getIntent().getBooleanExtra("clearData", false)) {
                    // Settings page calls this to clear all the data
                    clearData();
                }

                clearDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ScoreboardActivity.this, ScoreboardActivity.class));
                    }
                });
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScoreboardActivity.this, SettingsActivity.class));
            }
        });
    }

    private Map<String, ArrayList<String>> loadArrays() {
        //This section loads the data when the scoreboard page is loaded
        SharedPreferences sharedPreferences = getSharedPreferences("ArraysPrefs", Context.MODE_PRIVATE);
        Set<String> defaultSet = new HashSet<>();
        ArrayList<String> playerNames = new ArrayList<>(sharedPreferences.getStringSet("playerNames", defaultSet));
        ArrayList<String> pubNames = new ArrayList<>(sharedPreferences.getStringSet("pubNames", defaultSet));
        Map<String, ArrayList<String>> loadedArrays = new HashMap<>();
        loadedArrays.put("playerNames", playerNames);
        loadedArrays.put("pubNames", pubNames);
        //Logs the loaded data for testing
        Log.d("ScoreboardActivity", "Loaded Player Names: " + playerNames);
        Log.d("ScoreboardActivity", "Loaded Pub Names: " + pubNames);
        return loadedArrays;
    }

    private String generateRandomChallenge() {
        // This is where i set what my challenges are
        String[] challenges = {
                "+3 Score: Everyone places their drink on their head first person to drop their drink loses.",
                "+1 Score for every failure: Take it in turns counting to 50 but every time the number has a multiple of 7 in it the person who's turn it is must say Bottles, if they forget they lose.",
                "+2 Score: Everyone who goes to the toilet during this pub loses this challenge.",
                "+1 Score: Everyone must drink with their left hand, everytime they don't they lose",
                "+2 Score: Everyone must talk in an foreign accent for the next 10 minutes, anyone who doesn't loses",
                "+2 Score: Everyone has to call everyone Chief for the next 30 minutes, anyone who doesn't loses ",
                "-2 Score: Get someone in the bar to dance with you",
                "+1 Score: Everyone says a number 1-10 anyone who says the same number as another must have a shot or take the score penalty",
                "+2 Score: Tequila shot for everyone, anyone who refuses takes the penalty",
                "+3 Score for loser -3 for Winner: Everyone must try to get a high five from people in the same pub, lowest score loses",
        };
        // This Randomises which challenge is chosen
        Random random = new Random();
        int index = random.nextInt(challenges.length);
        return challenges[index];
    }

    private void clearData() {
        // This clears all the entries data added to the scoreboard
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        entries.clear();
        // This updates the UI to remove the deleted Sections
        pubContainer.removeAllViews();
        totalScores.clear();
        totalScoreTextView.setText("");
    }

    private LinearLayout findOrCreatePubSection(String pubName) {
        // This creates new sections for the entry's added by the user to the scoreboard page
        for (int i = 0; i < pubContainer.getChildCount(); i++) {
            View childView = pubContainer.getChildAt(i);
            if (childView.getTag() != null && childView.getTag().equals(pubName)) {
                return (LinearLayout) childView;
            }
        }
        View pubSectionView = LayoutInflater.from(this).inflate(R.layout.pub_section_layout, null);
        TextView pubNameTextView = pubSectionView.findViewById(R.id.pubNameTextView);
        pubNameTextView.setPaintFlags(pubNameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        pubNameTextView.setText(pubName);
        pubContainer.addView(pubSectionView);
        pubSectionView.setTag(pubName);
        return (LinearLayout) pubSectionView;
    }

    private void addEntry(String playerName, String pubName, int score) {
        //This section takes the data inputed from the popup after the user presses confirm and places it in the sections in the add entry xml later used by create section
        Log.d("AddEntry", "Adding entry - Player: " + playerName + ", Pub: " + pubName + ", Score: " + score);

        // this Checks if an identical entry already exists to avoid duplication
        for (Entry entry : entries) {
            if (entry.getPlayerName().equals(playerName) && entry.getPubName().equals(pubName)) {
                // if exact copy exists it instead adds it to the players total score
                entry.setScore(entry.getScore() + score);
                //Logs the issue for testing
                Log.d("AddEntry", "Entry already exists. Updated score: " + entry.getScore());
                updateTotalScores();
                return;
            }
        }

        Log.d("AddEntry", "Creating new entry.");
        View entryView = LayoutInflater.from(this).inflate(R.layout.entry_layout, null);
        TextView playerNameTextView = entryView.findViewById(R.id.playerNameTextView);
        TextView pubNameTextView = entryView.findViewById(R.id.pubNameTextView);
        TextView scoreTextView = entryView.findViewById(R.id.scoreTextView);
        playerNameTextView.setText(playerName);
        pubNameTextView.setText(pubName);
        scoreTextView.setText(String.valueOf(score));
        // Adds the finished entry to the selected pub section
        LinearLayout pubSection = findOrCreatePubSection(pubName);
        if (pubSection != null) {
            pubSection.addView(entryView);
        }
        // Stores the entries data to be saved
        entries.add(new Entry(playerName, pubName, score));
        updateTotalScores();
    }

    private void updateTotalScores() {
        // This section manages the adding up of the users total scores
        totalScores.clear();
        for (Entry entry : entries) {
            String playerName = entry.getPlayerName();
            int score = entry.getScore();
            totalScores.put(playerName, totalScores.getOrDefault(playerName, 0) + score);
        }
        StringBuilder totalScoreText = new StringBuilder();
        for (Map.Entry<String, Integer> entry : totalScores.entrySet()) {
            totalScoreText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        totalScoreTextView.setText(totalScoreText.toString());
    }

    private void saveData() {
        //This section saves all the data so nothing is lost when leaving the page or closing the app
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("totalScoresSize", entries.size());

        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            editor.putString(ENTRY_PREFS_KEY + i, entry.toString());
        }
        editor.apply();
    }

    private void loadData() {
        //This section loads the data when the page is opened
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int entryCount = sharedPreferences.getInt("totalScoresSize", 0);
        if (entryCount == 0) {
            return;
        }
        for (int i = 0; i < entryCount; i++) {
            String entryString = sharedPreferences.getString(ENTRY_PREFS_KEY + i, "");
            if (!entryString.isEmpty()) {
                Entry entry = Entry.fromString(entryString);
                if (entry != null) {
                    addEntry(entry.getPlayerName(), entry.getPubName(), entry.getScore());
                }
            }
        }
    }

    static class Entry {
        //this section sorts out the details in the entries
        private String playerName;
        private String pubName;
        private int score;

        Entry(String playerName, String pubName, int score) {
            this.playerName = playerName;
            this.pubName = pubName;
            this.score = score;
        }

        String getPlayerName() {
            return playerName;
        }
        String getPubName() {
            return pubName;
        }
        int getScore() {
            return score;
        }

        void setScore(int score) {
            this.score = score;
        }
        // This converts the entries to string for saving
        @Override
        public String toString() {
            return playerName + "|" + pubName + "|" + score;
        }

        // This converts Strings to entries for creating the entries in the scoreboard
        static Entry fromString(String entryString) {
            String[] parts = entryString.split("\\|");
            if (parts.length == 3) {
                return new Entry(parts[0], parts[1], Integer.parseInt(parts[2]));
            }
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Saves the data when the activity is paused
        saveData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // This section loads the data when the page is opened or come back to it, doesnt load if clearing data due to a duplication bug. also logs for testing
        if (getIntent().getBooleanExtra("CLEAR_DATA", false)) {
            clearData();
            Log.d("ResumeSection", "Cleared Data");
            getIntent().removeExtra("CLEAR_DATA");
        } else {
            entries.clear();
            Log.d("ResumeSection", "Loaded Data");
            loadData();
        }
    }

}

