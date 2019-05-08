package com.niesens.morsetrainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class ResetUiSettings extends DialogPreference {

    public ResetUiSettings(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPreferences.edit().putString("ui_night_mode", getContext().getResources().getString(R.string.default_ui_night_mode)).apply();
        }
    }
}
