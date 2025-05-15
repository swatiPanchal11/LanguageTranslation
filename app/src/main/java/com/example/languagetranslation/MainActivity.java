package com.example.languagetranslation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private EditText inputText;
    private TextView translatedText;
    private Spinner languageSpinner;
    private FirebaseTranslator translatorHindi, translatorKorean, translatorGujarati,translatorMarathi, translatorGerman,translatorChinese,translatorSpanish,translatorFrench,translatorJapanese;
    private boolean booleanHindi = false, booleanKorean = false, booleanGujarati = false,booleanMarathi=false, booleanGerman = false, booleanChinese=false, booleanSpanish=false, booleanFrench=false, booleanJapanese=false;
    private TextToSpeech textToSpeech;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar supportActionBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        languageSpinner = findViewById(R.id.languageSpinner);

        textToSpeech = new TextToSpeech(this, this);

            // Retrieve settings from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("TranslatorSettings", MODE_PRIVATE);

            int sourceLanguagePos = sharedPreferences.getInt("sourceLanguage", 0);
            int targetLanguagePos = sharedPreferences.getInt("targetLanguage", 0);
            int translationMode = sharedPreferences.getInt("translationMode", R.id.modeTextToText);
            int speechSpeed = sharedPreferences.getInt("speechSpeed", 50);

            // Apply the settings
            languageSpinner.setSelection(targetLanguagePos);
            // Set the translation mode logic if needed
            textToSpeech.setSpeechRate(speechSpeed / 100.0f);

        // Find the Toolbar and set it as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Create the ActionBarDrawerToggle and add it to the DrawerLayout
        toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar, R.string.open, R.string.close);

        // Add the toggle as a listener to the DrawerLayout
        drawerLayout.addDrawerListener(toggle);

        // Sync the toggle state
        toggle.syncState();

        // Navigation item selection
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.profile){
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                } else if(item.getItemId()==R.id.nav_home){
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                } else if (item.getItemId()==R.id.nav_history) {
                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                }else if (item.getItemId()==R.id.nav_phrasebook) {
                    startActivity(new Intent(MainActivity.this, PhrasebookActivity.class));
                } else if (item.getItemId()==R.id.nav_language_selection) {
                    startActivity(new Intent(MainActivity.this, LanguageSelectionActivity.class));
                }else if (item.getItemId()==R.id.nav_settings) {
                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
                }else if(item.getItemId()==R.id.nav_logout){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }else{
                    return false;
                }
                // Close drawer after item is clicked
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        inputText = findViewById(R.id.inputText);
        ImageButton microPhoneButton = findViewById(R.id.microPhoneButton);
        ImageButton microPhoneButton2=findViewById(R.id.microPhoneButton2);
        translatedText = findViewById(R.id.translatedText);
        Button translateButton = findViewById(R.id.translateButton);
        Button downloadModelButton = findViewById(R.id.downloadModel);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
        initializeTranslators();
        downloadModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadModel();
            }
        });
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areModelsDownloaded()) {
                    translateText();
                } else {
                    Toast.makeText(MainActivity.this, "Please download the required models first.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        microPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                texttoSpeak();
            }
        });
        microPhoneButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                read();
            }
        });
    }
    private void initializeTranslators() {
        try {
            FirebaseTranslatorOptions optionsHindi = new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                    .build();

            FirebaseTranslatorOptions optionsKorean = new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                    .setTargetLanguage(FirebaseTranslateLanguage.KO)
                    .build();

            FirebaseTranslatorOptions optionsGujarati = new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                    .setTargetLanguage(FirebaseTranslateLanguage.GU)
                    .build();

            FirebaseTranslatorOptions optionsMarathi = new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                    .setTargetLanguage(FirebaseTranslateLanguage.MR)
                    .build();

            FirebaseTranslatorOptions optionsGerman = new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                    .setTargetLanguage(FirebaseTranslateLanguage.DE)
                    .build();
            FirebaseTranslatorOptions optionsChinese = new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                    .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                    .build();

            FirebaseTranslatorOptions optionsSpanish = new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                    .setTargetLanguage(FirebaseTranslateLanguage.ES)
                    .build();

            FirebaseTranslatorOptions optionsFrench = new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                    .setTargetLanguage(FirebaseTranslateLanguage.FR)
                    .build();

            FirebaseTranslatorOptions optionsJapanese = new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                    .setTargetLanguage(FirebaseTranslateLanguage.JA)
                    .build();
            translatorHindi = FirebaseNaturalLanguage.getInstance().getTranslator(optionsHindi);
            translatorKorean = FirebaseNaturalLanguage.getInstance().getTranslator(optionsKorean);
            translatorGujarati = FirebaseNaturalLanguage.getInstance().getTranslator(optionsGujarati);
            translatorMarathi=FirebaseNaturalLanguage.getInstance().getTranslator(optionsMarathi);
            translatorGerman = FirebaseNaturalLanguage.getInstance().getTranslator(optionsGerman);
            translatorChinese = FirebaseNaturalLanguage.getInstance().getTranslator(optionsChinese);
            translatorSpanish = FirebaseNaturalLanguage.getInstance().getTranslator(optionsSpanish);
            translatorFrench = FirebaseNaturalLanguage.getInstance().getTranslator(optionsFrench);
            translatorJapanese = FirebaseNaturalLanguage.getInstance().getTranslator(optionsJapanese);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void downloadModel() {
        try {
            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                    .requireCharging()
                    .build();

            downloadFirebaseModel(translatorHindi, "Hindi", conditions);
            downloadFirebaseModel(translatorKorean, "Korean", conditions);
            downloadFirebaseModel(translatorGujarati, "Gujarati", conditions);
            downloadFirebaseModel(translatorMarathi,"Marathi",conditions);
            downloadFirebaseModel(translatorGerman, "German", conditions);
            downloadFirebaseModel(translatorChinese, "Chinese", conditions);
            downloadFirebaseModel(translatorSpanish, "Spanish", conditions);
            downloadFirebaseModel(translatorFrench, "French", conditions);
            downloadFirebaseModel(translatorJapanese, "Japanese", conditions);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void downloadFirebaseModel(FirebaseTranslator translator, String language, FirebaseModelDownloadConditions conditions) {
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(aVoid -> {
                    setLanguageModelDownloaded(language);  // Update the boolean flag
                    Toast.makeText(MainActivity.this, language + " model downloaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Log the error message
                    Toast.makeText(MainActivity.this, "Failed to download " + language + " model: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setLanguageModelDownloaded(String language) {
        switch (language) {
            case "Hindi":
                booleanHindi = true;
                break;
            case "Korean":
                booleanKorean = true;
                break;
            case "Gujarati":
                booleanGujarati = true;
                break;
            case "Marathi":
                booleanMarathi = true;
                break;
            case "German":
                booleanGerman = true;
                break;
            case "Chinese":
                booleanChinese = true;
                break;
            case "Spanish":
                booleanSpanish = true;
                break;
            case "French":
                booleanFrench = true;
                break;
            case "Japanese":
                booleanJapanese = true;
                break;
        }
    }

    private boolean areModelsDownloaded() {
        return booleanHindi && booleanKorean && booleanGujarati && booleanMarathi && booleanGerman && booleanChinese && booleanSpanish && booleanFrench && booleanJapanese; // Make sure all are downloaded
    }

    private void translateText() {
        String lang = languageSpinner.getSelectedItem().toString();
        String input = inputText.getText().toString();
        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!areModelsDownloaded()) {
            Toast.makeText(MainActivity.this, "Please download all required models first.", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (lang) {
            case "Hindi":
                translateUsingFirebaseTranslator(translatorHindi, input);
                break;
            case "Korean":
                translateUsingFirebaseTranslator(translatorKorean, input);
                break;
            case "Gujarati":
                translateUsingFirebaseTranslator(translatorGujarati, input);
                break;
            case "Marathi":
                translateUsingFirebaseTranslator(translatorMarathi, input);
                break;
            case "German":
                translateUsingFirebaseTranslator(translatorGerman, input);
                break;
            case "Chinese":
                translateUsingFirebaseTranslator(translatorChinese, input);
                break;
            case "Spanish":
                translateUsingFirebaseTranslator(translatorSpanish, input);
                break;
            case "French":
                translateUsingFirebaseTranslator(translatorFrench, input);
                break;
            case "Japanese":
                translateUsingFirebaseTranslator(translatorJapanese, input);
                break;
            default:
                Toast.makeText(this, "Language not supported.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void translateUsingFirebaseTranslator(FirebaseTranslator translator, String text) {
        translator.translate(text)
                .addOnSuccessListener(translatedText -> {
                    MainActivity.this.translatedText.setText(translatedText);

                    // Save translation to Firebase
                    saveTranslationToFirebase(text, translatedText);
                })
                .addOnFailureListener(e -> {
                    MainActivity.this.translatedText.setText("Translation failed: " + e.getMessage());
                });
    }

    private void saveTranslationToFirebase(String originalText, String translatedText) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("translations");

        // Create a unique key for each translation
        String key = databaseReference.push().getKey();

        // Create a map for translation data
        HashMap<String, String> translationData = new HashMap<>();
        translationData.put("originalText", originalText);
        translationData.put("translatedText", translatedText);

        // Save the translation under the generated key
        databaseReference.child(key).setValue(translationData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Translation saved to history", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to save translation: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
    private void texttoSpeak() {
        String text = inputText.getText().toString();
        if (text.isEmpty()) {
            text = "Please enter some text to speak.";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    private void read() {
        String text = translatedText.getText().toString();
        if (text.isEmpty()) {
            text = "Translated text will apear here.";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    @Override
    public void onInit(int status) {
        // Handle TextToSpeech initialization status here if needed.
    }
    @Override
    public void onBackPressed() {
        // Handle back press to close drawer if open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setSupportActionBar(Toolbar supportActionBar) {
        this.supportActionBar = supportActionBar;
    }
}
