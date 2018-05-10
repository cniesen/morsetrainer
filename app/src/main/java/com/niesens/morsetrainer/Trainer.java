package com.niesens.morsetrainer;

import android.os.AsyncTask;

import java.util.List;
import java.util.Random;

public class Trainer extends AsyncTask<Void, Void, Void> {
    private MorsePlayer morsePlayer;
    private TextSpeaker textSpeaker;
    private List<Word> wordList;
    private Random random = new Random();

    Trainer(MorsePlayer morsePlayer, TextSpeaker textSpeaker, List<Word> wordList) {
        this.morsePlayer = morsePlayer;
        this.textSpeaker = textSpeaker;
        this.wordList = wordList;
    }

    @Override
    protected Void doInBackground(Void... params) {

        synchronized (this) {
        while(!isCancelled()) {
            int wordNumber = random.nextInt(wordList.size());
            Word word = wordList.get(wordNumber);
            morsePlayer.play(MorseTranslate.textToMorse(word.getMorseText()));

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                return null;
            }
            textSpeaker.speak(word.getSpeakText(), this);
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
    }
        return null;
    }

    public void destroy() {
        morsePlayer.destroy();
        textSpeaker.destroy();
    }

}
