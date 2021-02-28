/*
 *  Copyright (C) 2020 Claus Niesen
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

import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
