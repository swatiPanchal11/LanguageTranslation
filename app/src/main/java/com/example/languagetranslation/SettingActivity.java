package com.example.languagetranslation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    private Spinner sourceLanguageSpinner, targetLanguageSpinner;
    private RadioGroup translationModeGroup;
    private SeekBar speechSpeedSeekBar;
    private Button saveSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity_2);

        // Initialize UI components
        sourceLanguageSpinner = findViewById(R.id.sourceLanguageSpinner);
        targetLanguageSpinner = findViewById(R.id.targetLanguageSpinner);
        translationModeGroup = findViewById(R.id.translationModeGroup);
        speechSpeedSeekBar = findViewById(R.id.speechSpeedSeekBar);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);

        // Set up language spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceLanguageSpinner.setAdapter(adapter);
        targetLanguageSpinner.setAdapter(adapter);

        // Load saved preferences
        loadSettings();

        // Save settings when the button is clicked
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                passDataToMainActivity();
            }
        });
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("TranslatorSettings", MODE_PRIVATE);

        // Load and set saved language preferences
        int sourceLanguagePos = sharedPreferences.getInt("sourceLanguage", 0);
        int targetLanguagePos = sharedPreferences.getInt("targetLanguage", 0);
        sourceLanguageSpinner.setSelection(sourceLanguagePos);
        targetLanguageSpinner.setSelection(targetLanguagePos);

        // Load and set translation mode
        int mode = sharedPreferences.getInt("translationMode", R.id.modeTextToText);
        translationModeGroup.check(mode);

        // Load and set speech speed
        int speechSpeed = sharedPreferences.getInt("speechSpeed", 50);
        speechSpeedSeekBar.setProgress(speechSpeed);
    }

    private void saveSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("TranslatorSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save language preferences
        editor.putInt("sourceLanguage", sourceLanguageSpinner.getSelectedItemPosition());
        editor.putInt("targetLanguage", targetLanguageSpinner.getSelectedItemPosition());

        // Save translation mode
        int selectedMode = translationModeGroup.getCheckedRadioButtonId();
        editor.putInt("translationMode", selectedMode);

        // Save speech speed
        int speechSpeed = speechSpeedSeekBar.getProgress();
        editor.putInt("speechSpeed", speechSpeed);

        editor.apply();
    }

    // Pass the settings to MainActivity using Intent
    private void passDataToMainActivity() {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);

        // Add settings data to the Intent
        intent.putExtra("sourceLanguage", sourceLanguageSpinner.getSelectedItemPosition());
        intent.putExtra("targetLanguage", targetLanguageSpinner.getSelectedItemPosition());
        intent.putExtra("translationMode", translationModeGroup.getCheckedRadioButtonId());
        intent.putExtra("speechSpeed", speechSpeedSeekBar.getProgress());

        startActivity(intent);
    }
}