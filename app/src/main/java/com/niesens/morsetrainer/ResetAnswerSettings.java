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

package com.niesens.morsetrainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class ResetAnswerSettings extends DialogPreference {

    public ResetAnswerSettings(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPreferences.edit().putInt("delay_before_answer", getContext().getResources().getInteger(R.integer.default_delay_before_answer)).apply();
            sharedPreferences.edit().putInt("delay_after_answer", getContext().getResources().getInteger(R.integer.default_delay_after_answer)).apply();
            sharedPreferences.edit().putBoolean("answer_toast", getContext().getResources().getBoolean(R.bool.default_answer_toast)).apply();
            sharedPreferences.edit().putBoolean("answer_vocalize", getContext().getResources().getBoolean(R.bool.default_answer_vocalize)).apply();
        }
    }
}
