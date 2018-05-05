package com.niesens.morsetrainer.filepicker;

import java.io.File;
import java.io.FileFilter;

public class WordListFileFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        return pathname.isFile() && !pathname.isHidden() && pathname.getName().toLowerCase().endsWith(".txt");
    }
}
