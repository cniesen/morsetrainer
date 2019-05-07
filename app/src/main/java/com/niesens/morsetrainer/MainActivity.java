package com.niesens.morsetrainer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int REQUEST_WRITE_STORAGE = 112;
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
        switch (getUiNightModePreference(sharedPreferences)) {
            case "Yes":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "No" :
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            default :
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
        super.onCreate(savedInstanceState);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        morsePlayer = new MorsePlayer(getMorseWpmPreference(sharedPreferences), getMorseFarnsworthPreference(sharedPreferences), getMorsePitchPreference(sharedPreferences), getMorseRandomPitchPreference(sharedPreferences));
        if (getSpeakFirstPreference(sharedPreferences)) {
            textSpeaker = new TextSpeaker(this, getDelayAfterAnswerPreference(sharedPreferences), getDelayBeforeAnswerPreference(sharedPreferences), getAnswerToastPreference(sharedPreferences));
        } else {
            textSpeaker = new TextSpeaker(this, getDelayBeforeAnswerPreference(sharedPreferences), getDelayAfterAnswerPreference(sharedPreferences), getAnswerToastPreference(sharedPreferences));
        }

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (hasPermission) {
            createExternalStorageDirectory();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
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
                startActivityForResult(intent, 1);
            }
        });

        numberPicker_wordTrainTimes = findViewById(R.id.wordTrainTimes);
        numberPicker_wordTrainTimes.setMinValue(1);
        numberPicker_wordTrainTimes.setMaxValue(10);
        numberPicker_wordTrainTimes.setValue(getWordTrainTimesPreference(sharedPreferences));
        numberPicker_wordTrainTimes.setWrapSelectorWheel(false);
        numberPicker_wordTrainTimes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                sharedPreferences.edit().putInt("word_train_times", newVal).apply();
            }
        });

        toggleButton_speakFirst = findViewById(R.id.speakFirst);
        toggleButton_speakFirst.setChecked(getSpeakFirstPreference(sharedPreferences));
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
        trainer = new Trainer(morsePlayer, textSpeaker, wordList, getWordTrainTimesPreference(sharedPreferences), toggleButton_speakFirst.isChecked());
        trainer.execute();
    }

    private void stopTrainer() {
        button_startStop.setText(R.string.trainingStartText);
        if (trainer != null) {
            trainer.cancel(true);
            trainer = null;
            morsePlayer.stop();
            textSpeaker.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createExternalStorageDirectory();
                } else {
                    Toast.makeText(this, getString(R.string.app_name) + " closed.\nStorage read/write permission is required.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            stopTrainer();
            button_trainingFile.setText(data.getStringExtra("wordListFileName"));
            wordList = createWordList(data.getStringExtra("wordListFileName"));
            button_startStop.setEnabled(true);
        }
    }

    private List<Word> createWordList(String fileName) {
        List<Word> wordList = new ArrayList<>();

        BufferedReader reader = null;
        try {
            String externalStoragePath = Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name);
            reader = new BufferedReader(new FileReader(new File(externalStoragePath, fileName)));

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

    private void createExternalStorageDirectory() {
        String externalStoragePath = Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name);
        File file = new File(externalStoragePath);
        if (!file.exists()) {
            if (file.mkdirs()) {
                try {
                    String[] assetFiles = getAssets().list("");
                    for (String assetFile : assetFiles) {
                        if (assetFile.endsWith(".txt")) {
                            copyFile(assetFile, externalStoragePath);
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
            while((read = in.read(buffer)) != -1) {
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
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.menu_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        if (trainer != null) {
            trainer.cancel(true);
        }
        morsePlayer.destroy();
        textSpeaker.destroy();
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "morse_wpm" :
                morsePlayer.setWpm(getMorseWpmPreference(sharedPreferences));
                break;
            case "morse_farnsworth" :
                morsePlayer.setFarnsworth(getMorseFarnsworthPreference(sharedPreferences));
                break;
            case "morse_pitch" :
                morsePlayer.setPitch(getMorsePitchPreference(sharedPreferences));
                break;
            case "morse_random_pitch" :
                morsePlayer.setRandomPitch(getMorseRandomPitchPreference(sharedPreferences));
                break;
            case "delay_before_answer" :
                if (getSpeakFirstPreference(sharedPreferences)) {
                    textSpeaker.setAfterSpeakDelay(getDelayBeforeAnswerPreference(sharedPreferences));
                } else {
                    textSpeaker.setBeforeSpeakDelay(getDelayBeforeAnswerPreference(sharedPreferences));
                }
                break;
            case "delay_after_answer" :
                if (getSpeakFirstPreference(sharedPreferences)) {
                    textSpeaker.setBeforeSpeakDelay(getDelayAfterAnswerPreference(sharedPreferences));
                } else {
                    textSpeaker.setAfterSpeakDelay(getDelayAfterAnswerPreference(sharedPreferences));
                }
                break;
            case "answer_toast" :
                textSpeaker.setShowToast(getAnswerToastPreference(sharedPreferences));
                break;
            case "word_train_times" :
                if (trainer != null) {
                    trainer.setWordTrainTimes(getWordTrainTimesPreference(sharedPreferences));
                }
                break;
            case "speak_first" :
                if (trainer != null) {
                    trainer.setSpeakFirst(getSpeakFirstPreference(sharedPreferences));
                }
                if (getSpeakFirstPreference(sharedPreferences)) {
                    textSpeaker.setAfterSpeakDelay(getDelayBeforeAnswerPreference(sharedPreferences));
                    textSpeaker.setBeforeSpeakDelay(getDelayAfterAnswerPreference(sharedPreferences));
                } else {
                    textSpeaker.setBeforeSpeakDelay(getDelayBeforeAnswerPreference(sharedPreferences));
                    textSpeaker.setAfterSpeakDelay(getDelayAfterAnswerPreference(sharedPreferences));
                }
                break;

        }

    }

    private int getMorseWpmPreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("morse_wpm", getResources().getInteger(R.integer.default_morse_wpm));
    }

    private int getMorseFarnsworthPreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("morse_farnsworth", getResources().getInteger(R.integer.default_morse_farnsworth));
    }

    private int getMorsePitchPreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("morse_pitch", getResources().getInteger(R.integer.default_morse_pitch));
    }

    private boolean getMorseRandomPitchPreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("morse_random_pitch", getResources().getBoolean(R.bool.default_morse_random_pitch));
    }

    private int getDelayBeforeAnswerPreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("delay_before_answer", getResources().getInteger(R.integer.default_delay_before_answer));
    }

    private int getDelayAfterAnswerPreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("delay_after_answer", getResources().getInteger(R.integer.default_delay_after_answer));
    }

    private boolean getAnswerToastPreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("answer_toast", getResources().getBoolean(R.bool.default_answer_toast));
    }

    private String getUiNightModePreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("ui_night_mode", getResources().getString(R.string.default_ui_night_mode));
    }

    private int getWordTrainTimesPreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("word_train_times", 1);
    }

    private boolean getSpeakFirstPreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("speak_first", false);
    }
}
