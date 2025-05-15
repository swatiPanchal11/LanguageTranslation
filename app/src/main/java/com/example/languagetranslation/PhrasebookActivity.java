package com.example.languagetranslation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhrasebookActivity extends AppCompatActivity {

    private EditText searchPhrase;
    private ListView phraseListView;
    private Button addPhraseButton;
    private ArrayAdapter<String> phraseAdapter;
    private ArrayList<String> phraseList;

    // Firebase database reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrasebook);

        searchPhrase = findViewById(R.id.searchPhrase);
        phraseListView = findViewById(R.id.phraseListView);
        addPhraseButton = findViewById(R.id.addPhraseButton);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Phrases");

        // Initialize phrase list and adapter
        phraseList = new ArrayList<>();
        phraseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, phraseList);
        phraseListView.setAdapter(phraseAdapter);

        // Load saved phrases from Firebase
        loadPhrases();

        // Set up listeners
        setUpListeners();
    }

    private void loadPhrases() {
        // Retrieve the phrases from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phraseList.clear();
                for (DataSnapshot phraseSnapshot : snapshot.getChildren()) {
                    String phrase = phraseSnapshot.getValue(String.class);
                    phraseList.add(phrase);
                }
                phraseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PhrasebookActivity.this, "Failed to load phrases.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpListeners() {
        // Add phrase button listener
        addPhraseButton.setOnClickListener(v -> showAddPhraseDialog());

        // Search bar listener to filter phrases
        searchPhrase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phraseAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle phrase click to translate or play the phrase
        phraseListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPhrase = phraseAdapter.getItem(position);
            // Trigger translation or speech synthesis for selected phrase
            translateOrSpeakPhrase(selectedPhrase);
        });
    }

    private void showAddPhraseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Phrase");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newPhrase = input.getText().toString();
            if (!newPhrase.isEmpty()) {
                savePhraseToFirebase(newPhrase);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void savePhraseToFirebase(String newPhrase) {
        // Generate a unique key for each phrase
        String key = databaseReference.push().getKey();
        if (key != null) {
            Map<String, Object> phraseMap = new HashMap<>();
            phraseMap.put(key, newPhrase);
            databaseReference.updateChildren(phraseMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            phraseList.add(newPhrase);
                            phraseAdapter.notifyDataSetChanged();
                            Toast.makeText(PhrasebookActivity.this, "Phrase added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PhrasebookActivity.this, "Failed to add phrase", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(PhrasebookActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void translateOrSpeakPhrase(String phrase) {
        // Implement translation or speech synthesis here
        // Example: Use a translation API or Text-to-Speech (TTS) engine
    }
}