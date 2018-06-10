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
            sharedPreferences.edit().putInt("morse_wpm", getContext().getResources().getInteger(R.integer.default_morse_wpm)).apply();
            sharedPreferences.edit().putInt("morse_farnsworth", getContext().getResources().getInteger(R.integer.default_morse_farnsworth)).apply();
            sharedPreferences.edit().putInt("morse_pitch", getContext().getResources().getInteger(R.integer.default_morse_pitch)).apply();
            sharedPreferences.edit().putBoolean("morse_random_pitch", getContext().getResources().getBoolean(R.bool.default_morse_random_pitch)).apply();
        }
    }
}
