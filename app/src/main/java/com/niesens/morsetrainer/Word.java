package com.niesens.morsetrainer;

public class Word {
    private String morseText;
    private String speakText;

    Word(String morseText, String speakText) {
        this.morseText = morseText;
        this.speakText = speakText;
    }

    public String getMorseText() {
        return morseText;
    }

    public String getSpeakText() {
        return speakText;
    }
}
