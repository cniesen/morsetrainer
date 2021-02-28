/*
 *  Copyright (C) 2021 Claus Niesen
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
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class ResetMorseSettings extends DialogPreference {

    public ResetMorseSettings(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            sharedPreferences.edit()
                    .putBoolean("morse_high_wpm", getContext().getResources().getBoolean(R.bool.default_morse_high_wpm))
                    .putInt("morse_wpm", getContext().getResources().getInteger(R.integer.default_morse_wpm))
                    .putBoolean("morse_farnsworth_enabled", getContext().getResources().getBoolean(R.bool.default_morse_farnsworth_enabled))
                    .putInt("morse_farnsworth", getContext().getResources().getInteger(R.integer.default_morse_farnsworth))
                    .putInt("morse_pitch", getContext().getResources().getInteger(R.integer.default_morse_pitch))
                    .putBoolean("morse_random_pitch", getContext().getResources().getBoolean(R.bool.default_morse_random_pitch))
                    .apply();
        }
    }
}
