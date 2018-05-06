package com.niesens.morsetrainer;

import android.app.Activity;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Trainer {
    private final Activity activity;
    private MorsePlayer morsePlayer;
    private TextSpeaker textSpeaker;
    private Random random = new Random();

    private List<Word> wordList;
    private boolean running;
    private String wordlistFile;

    Trainer(Activity activity) {
        this.activity = activity;
        this.running = false;
        this.wordList = null;

        this.morsePlayer = new MorsePlayer();
        this.textSpeaker = new TextSpeaker(activity, this);
    }

    public void setWordListFile(String wordlistFile) {
        this.wordlistFile = wordlistFile;
        loadWordList();
    }

    public String getWordlistFile() {
        return wordlistFile;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void train() {
        if (!running || TextUtils.isEmpty(wordlistFile)) {
            return;
        }

        int wordNumber = random.nextInt(wordList.size());
        morsePlayer.play(MorseTranslate.textToMorse(wordList.get(wordNumber).getMorseText()));

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        textSpeaker.speak(wordList.get(wordNumber).getSpeakText());
    }

    private void loadWordList() {
        wordList = new ArrayList<>();

        if (TextUtils.isEmpty(wordlistFile)) {
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(activity.getAssets().open(wordlistFile), "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineArray = line.split("\\|");
                if (lineArray.length > 1) {
                    wordList.add(new Word(lineArray[0], lineArray[1]));
                } else {
                    wordList.add(new Word(lineArray[0], lineArray[0]));
                }
            }
        } catch (IOException e) {
            //log the exception
            setWordListFile(null);
            setRunning(false);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }

    public void destroy() {
        morsePlayer.destroy();
        textSpeaker.destroy();
    }
}
