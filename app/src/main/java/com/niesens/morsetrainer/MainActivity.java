/*
 *  Copyright (C) 2021–2026 Claus Niesen
 *
 *  This file is part of Claus' Morse Trainer.
 *
 *  Claus' Morse Trainer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Claus' Morse Trainer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Claus' Morse Trainer.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.niesens.morsetrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.niesens.morsetrainer.filepicker.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_PICKER_REQUEST_CODE = 1;

    Button button_startStop;
    Button button_trainingFile;
    NumberPicker numberPicker_wordTrainTimes;
    ToggleButton toggleButton_speakFirst;
    private MorsePlayer morsePlayer;
    private TextSpeaker textSpeaker;
    private List<Word> wordList;
    Trainer trainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if ("Yes".equals(SharedPreferencesHelper.getUiNightMode(sharedPreferences))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);

        morsePlayer = new MorsePlayer(sharedPreferences);
        textSpeaker = new TextSpeaker(this, sharedPreferences);

        createExternalStorageDirectory(this);

        setContentView(R.layout.activity_main);

        button_startStop = findViewById(R.id.startStop);
        button_startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getString(R.string.trainingStopText).contentEquals(button_startStop.getText())) {
                    stopTrainer();
                } else {
                    startTrainer();
                }
            }
        });

        button_trainingFile = findViewById(R.id.trainingFile);
        button_trainingFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FilePickerActivity.class);
                startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
            }
        });

        numberPicker_wordTrainTimes = findViewById(R.id.wordTrainTimes);
        numberPicker_wordTrainTimes.setMinValue(1);
        numberPicker_wordTrainTimes.setMaxValue(10);
        numberPicker_wordTrainTimes.setValue(SharedPreferencesHelper.getWordTrainTimes(sharedPreferences));
        numberPicker_wordTrainTimes.setWrapSelectorWheel(false);
        numberPicker_wordTrainTimes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                sharedPreferences.edit().putInt("word_train_times", newVal).apply();
            }
        });

        toggleButton_speakFirst = findViewById(R.id.speakFirst);
        toggleButton_speakFirst.setChecked(SharedPreferencesHelper.getSpeakFirst(sharedPreferences));
        toggleButton_speakFirst.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("speak_first", isChecked).apply();
            }
        });
    }

    private void startTrainer() {
        if (wordList == null || wordList.isEmpty()) {
            return;
        }
        button_startStop.setText(R.string.trainingStopText);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        trainer = new Trainer(
                morsePlayer,
                textSpeaker,
                wordList,
                sharedPreferences
        );
        trainer.execute();
    }

    private void stopTrainer() {
        button_startStop.setText(R.string.trainingStartText);
        if (trainer != null) {
            trainer.cancel(true);
            trainer.destroy();
            trainer = null;
            morsePlayer.stop();
            textSpeaker.stop();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            stopTrainer();
            button_trainingFile.setText(data.getStringExtra("wordListFileName"));
            wordList = createWordList(data.getStringExtra("wordListFilePath"));
            button_startStop.setEnabled(true);
        }
    }

    private List<Word> createWordList(String filePath) {
        List<Word> wordList = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(filePath)));

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
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return wordList;
    }

    private void createExternalStorageDirectory(Context context) {
        File dir = new File(context.getExternalFilesDir(null), "WordLists");
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                try {
                    String[] assetFiles = getAssets().list("");
                    for (String assetFile : assetFiles) {
                        if (assetFile.endsWith(".txt")) {
                            copyFile(assetFile, dir.getPath());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void copyFile(String fileName, String externalStoragePath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getAssets().open(fileName);
            out = new FileOutputStream(externalStoragePath + "/" + fileName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (itemId == R.id.menu_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        if (trainer != null) {
            trainer.cancel(true);
            trainer.destroy();
        }
        morsePlayer.destroy();
        textSpeaker.destroy();
        super.onDestroy();
    }

}