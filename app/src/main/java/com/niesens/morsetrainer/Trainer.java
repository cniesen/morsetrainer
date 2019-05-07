package com.niesens.morsetrainer;

import android.os.AsyncTask;

import java.util.List;
import java.util.Random;

public class Trainer extends AsyncTask<Void, Void, Void> {
    private MorsePlayer morsePlayer;
    private TextSpeaker textSpeaker;
    private List<Word> wordList;
    private int wordTrainTimes;
    private boolean speakFirst;
    private Random random = new Random();

    Trainer(MorsePlayer morsePlayer, TextSpeaker textSpeaker, List<Word> wordList, int wordTrainTimes, boolean speakFirst) {
        this.morsePlayer = morsePlayer;
        this.textSpeaker = textSpeaker;
        this.wordList = wordList;
        this.wordTrainTimes = wordTrainTimes;
        this.speakFirst = speakFirst;
    }

    public void setWordTrainTimes(int wordTrainTimes) {
        this.wordTrainTimes = wordTrainTimes;
    }

    public void setSpeakFirst(boolean speakFirst) {
        this.speakFirst = speakFirst;
    }

    @Override
    protected Void doInBackground(Void... params) {

        synchronized (this) {
            Word word = null;
            int wordTrainedCount = 0;
            while(!isCancelled()) {
                if (word == null || wordTrainedCount >= wordTrainTimes) {
                    int wordNumber = random.nextInt(wordList.size());
                    word = wordList.get(wordNumber);
                    wordTrainedCount = 0;
                }
                boolean speakAfter = true;
                if (speakFirst) {
                    speakAfter = false;
                    textSpeaker.speak(word.getSpeakText(), this);
                    try {
                        wait();  // wait for text-to-speech to finish
                    } catch (InterruptedException e) {
                        return null;
                    }
                }
                morsePlayer.play(MorseTranslate.textToMorse(word.getMorseText()));
                if (speakAfter) {
                    textSpeaker.speak(word.getSpeakText(), this);
                    try {
                        wait(); // wait for text-to-speech to finish
                    } catch (InterruptedException e) {
                        return null;
                    }
                }
                wordTrainedCount++;
            }
        }
        return null;
    }

}
