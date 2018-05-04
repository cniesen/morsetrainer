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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;

    Button b1;
    Button b2;
    Trainer trainer;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
        createExternalStorageDirectory();
        setContentView(R.layout.activity_main);

        trainer = new Trainer(this);

        b1= findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainer.train();
            }
        });

        b2= findViewById(R.id.trainingFile);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FilePickerActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

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
                Toast.makeText(this, "dir created", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "dir exists", Toast.LENGTH_SHORT).show();
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
        trainer.destroy();
        super.onDestroy();
    }

}
