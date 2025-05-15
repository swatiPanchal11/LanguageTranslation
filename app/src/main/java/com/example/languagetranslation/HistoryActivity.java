package com.example.languagetranslation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private List<TranslationHistory> historyList;
    private SearchView searchView;
    private Button buttonClearHistory;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize components
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        searchView = findViewById(R.id.searchView);
        buttonClearHistory = findViewById(R.id.buttonClearHistory);

        // Set up RecyclerView
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyList);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistory.setAdapter(historyAdapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("translations");

        // Fetch translation history from Firebase
        fetchTranslationHistory();

        // Handle Clear History Button
        buttonClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });
    }

    private void fetchTranslationHistory() {
        // Add Firebase event listener to retrieve translation history
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the history list before adding new data to avoid duplicates
                historyList.clear();

                // Loop through the retrieved data and add each item to the list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TranslationHistory history = snapshot.getValue(TranslationHistory.class);
                    historyList.add(history);
                }

                // Notify the adapter to update the RecyclerView with the new data
                historyAdapter.notifyDataSetChanged();

                // Log the retrieved data for debugging
                Log.d("History", "Data Retrieved: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("History", "Error retrieving data: " + databaseError.getMessage());
            }
        });
    }
    // Method to clear history from Firebase
    private void clearHistory() {
        databaseReference.removeValue().addOnSuccessListener(aVoid -> {
            historyList.clear();
            historyAdapter.notifyDataSetChanged();
            Toast.makeText(HistoryActivity.this, "History cleared", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(HistoryActivity.this, "Failed to clear history: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}