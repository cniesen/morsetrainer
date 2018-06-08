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
        }
    }
}
