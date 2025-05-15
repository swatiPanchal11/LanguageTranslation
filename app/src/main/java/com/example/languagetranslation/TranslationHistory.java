package com.example.languagetranslation;

public class TranslationHistory {
    private String originalText;
    private String translatedText;

    // Empty constructor for Firebase
    public TranslationHistory() {
    }

    public TranslationHistory(String originalText, String translatedText) {
        this.originalText = originalText;
        this.translatedText = translatedText;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}