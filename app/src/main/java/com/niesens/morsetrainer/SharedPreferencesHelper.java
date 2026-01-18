/*
 *  Copyright (C) 2026 Claus Niesen
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

import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    public static class Defaults {
        public static final boolean MORSE_HIGH_WPM = false;
        public static final int MORSE_WPM = 20;
        public static final boolean MORSE_FARNSWORTH_ENABLED = true;
        public static final int MORSE_FARNSWORTH = 20;
        public static final int MORSE_PITCH = 600;
        public static final boolean MORSE_RANDOM_PITCH = false;
        public static final int DELAY_BEFORE_ANSWER = 1500;
        public static final int DELAY_AFTER_ANSWER = 1000;
        public static final boolean ANSWER_TOAST = true;
        public static final boolean ANSWER_VOCALIZE = true;
        public static final String UI_NIGHT_MODE = "No";
        public static final int WORD_TRAIN_TIMES = 1;
        public static final boolean SPEAK_FIRST = false;
    }
    public static boolean getMorseHighWpm(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("morse_high_wpm", Defaults.MORSE_HIGH_WPM);
    }

    public static int getMorseWpm(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("morse_wpm", Defaults.MORSE_WPM);
    }

    public static boolean getMorseFarnsworthEnabled(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("morse_farnsworth_enabled", Defaults.MORSE_FARNSWORTH_ENABLED);
    }

    public static int getMorseFarnsworth(SharedPreferences sharedPreferences) {
        if (getMorseFarnsworthEnabled(sharedPreferences)) {
            return sharedPreferences.getInt("morse_farnsworth", Defaults.MORSE_FARNSWORTH);
        } else {
            return sharedPreferences.getInt("morse_wpm", Defaults.MORSE_WPM);
        }
    }

    public static int getMorsePitch(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("morse_pitch", Defaults.MORSE_PITCH);
    }

    public static boolean getMorseRandomPitch(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("morse_random_pitch", Defaults.MORSE_RANDOM_PITCH);
    }

    public static int getDelayBeforeAnswer(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("delay_before_answer", Defaults.DELAY_BEFORE_ANSWER);
    }

    public static int getDelayAfterAnswer(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("delay_after_answer", Defaults.DELAY_AFTER_ANSWER);
    }

    public static boolean getAnswerToast(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("answer_toast", Defaults.ANSWER_TOAST);
    }

    public static boolean getAnswerVocalize(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("answer_vocalize", Defaults.ANSWER_VOCALIZE);
    }

    public static String getUiNightMode(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("ui_night_mode", Defaults.UI_NIGHT_MODE);
    }

    public static int getWordTrainTimes(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("word_train_times", Defaults.WORD_TRAIN_TIMES);
    }

    public static boolean getSpeakFirst(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("speak_first", Defaults.SPEAK_FIRST);
    }

}
