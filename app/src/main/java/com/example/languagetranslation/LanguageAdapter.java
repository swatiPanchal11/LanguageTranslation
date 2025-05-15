package com.example.languagetranslation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private List<String> languages;
    private OnLanguageClickListener listener;

    public LanguageAdapter(List<String> languages, OnLanguageClickListener listener) {
        this.languages = languages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        String language = languages.get(position);
        holder.languageName.setText(language);
        holder.itemView.setOnClickListener(v -> listener.onLanguageClick(language));
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public interface OnLanguageClickListener {
        void onLanguageClick(String language);
    }

    static class LanguageViewHolder extends RecyclerView.ViewHolder {
        TextView languageName;

        LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            languageName = itemView.findViewById(android.R.id.text1);
        }
    }
}