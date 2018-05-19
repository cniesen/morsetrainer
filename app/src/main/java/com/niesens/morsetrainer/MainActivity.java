package com.niesens.morsetrainer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int FILE_PICKER_REQUEST_CODE = 1;

    Button button_startStop;
    Button button_trainingFile;
    private MorsePlayer morsePlayer;
    private TextSpeaker textSpeaker;
    private List<Word> wordList;
    Trainer trainer;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        morsePlayer = new MorsePlayer();
        textSpeaker = new TextSpeaker(this);
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

        button_startStop = findViewById(R.id.button);
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

    }

    private void startTrainer() {
        button_startStop.setText(R.string.trainingStopText);
        trainer = new Trainer(morsePlayer, textSpeaker, wordList);
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

    public void onDestroy(){
        if (trainer != null) {
            trainer.cancel(true);
        }
        morsePlayer.destroy();
        textSpeaker.destroy();
        super.onDestroy();
    }

}
