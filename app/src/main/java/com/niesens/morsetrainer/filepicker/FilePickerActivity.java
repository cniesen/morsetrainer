package com.niesens.morsetrainer.filepicker;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.niesens.morsetrainer.R;

import java.io.File;
import java.util.Arrays;

public class FilePickerActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filepicker);
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        String externalStoragePath = Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name);
        File wordListDirectory = new File(externalStoragePath);
        File[] wordListFiles = wordListDirectory.listFiles(new WordListFileFilter());
        Arrays.sort(wordListFiles);
        RecyclerView.Adapter mAdapter = new FilePickerAdapter(Arrays.asList(wordListFiles));
        recyclerView.setAdapter(mAdapter);
    }


}
