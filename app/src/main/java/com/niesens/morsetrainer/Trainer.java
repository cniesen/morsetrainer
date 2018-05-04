package com.niesens.morsetrainer;

import android.app.Activity;

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

    Trainer(Activity activity) {
        this.activity = activity;
        this.wordList = loadWordList("words.txt");

        this.morsePlayer = new MorsePlayer();
        this.textSpeaker = new TextSpeaker(activity, this);
    }

    public void train() {
        int wordNumber = random.nextInt(wordList.size());
        morsePlayer.play(MorseTranslate.textToMorse(wordList.get(wordNumber).getMorseText()));

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        textSpeaker.speak(wordList.get(wordNumber).getSpeakText());
    }

    private List<Word> loadWordList(String fileName) {
        List<Word> wordList = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(activity.getAssets().open(fileName), "UTF-8"));

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
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return wordList;
    }

    public void destroy() {
        morsePlayer.destroy();
        textSpeaker.destroy();
    }
}
