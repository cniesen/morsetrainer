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

package com.niesens.morsetrainer.filepicker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.niesens.morsetrainer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilePickerActivity extends AppCompatActivity {

    private File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filepicker);

        File baseDir = new File(getExternalFilesDir(null), "WordLists");
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        files = baseDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (files == null) {
            files = new File[0];
        }

        Arrays.sort(files);

        List<String> names = new ArrayList<>();
        for (File f : files) {
            names.add(f.getName());
        }

        ListView listView = findViewById(R.id.fileListView);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            File selected = files[position];
            Intent result = new Intent();
            result.putExtra("wordListFileName", selected.getName());
            result.putExtra("wordListFilePath", selected.getAbsolutePath());
            setResult(RESULT_OK, result);
            finish();
        });
    }
}
