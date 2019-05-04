package com.niesens.morsetrainer;

import android.os.AsyncTask;

import java.util.List;
import java.util.Random;

public class Trainer extends AsyncTask<Void, Void, Void> {
    private MorsePlayer morsePlayer;
    private TextSpeaker textSpeaker;
    private List<Word> wordList;
    private int wordTrainTimes;
    private Random random = new Random();

    Trainer(MorsePlayer morsePlayer, TextSpeaker textSpeaker, List<Word> wordList, Integer wordTrainTimes) {
        this.morsePlayer = morsePlayer;
        this.textSpeaker = textSpeaker;
        this.wordList = wordList;
        this.wordTrainTimes = wordTrainTimes;
    }

    public void setWordTrainTimes(int wordTrainTimes) {
        this.wordTrainTimes = wordTrainTimes;
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
                morsePlayer.play(MorseTranslate.textToMorse(word.getMorseText()));
                textSpeaker.speak(word.getSpeakText(), this);
                wordTrainedCount++;
                try {
                    wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }
        }
        return null;
    }

}
