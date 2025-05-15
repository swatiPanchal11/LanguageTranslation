package com.example.languagetranslation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LanguageSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView selectedLanguagesList;
    private List<String> selectedLanguages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);



        recyclerView = findViewById(R.id.rv_language_list);
        selectedLanguagesList = findViewById(R.id.tv_selected_languages_list);

        // Dummy language data
        List<String> languages = Arrays.asList("English", "Spanish", "French", "German", "Chinese", "Japanese", "Korean", "Hindi");

        LanguageAdapter adapter = new LanguageAdapter(languages, language -> {
            if (!selectedLanguages.contains(language)) {
                selectedLanguages.add(language);
            }
            updateSelectedLanguages();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        EditText searchLanguage = findViewById(R.id.et_search_language);
        searchLanguage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              //  adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateSelectedLanguages() {
        StringBuilder languages = new StringBuilder();
        for (String lang : selectedLanguages) {
            languages.append(lang).append("\n");
        }
        selectedLanguagesList.setText(languages.toString().trim());
    }
}