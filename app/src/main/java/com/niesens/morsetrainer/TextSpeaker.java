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

package com.niesens.morsetrainer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME;

import androidx.annotation.Nullable;

public class TextSpeaker implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final SharedPreferences sharedPreferences;
    private final TextToSpeech textToSpeech;
    private final HashMap<String, String> textToSpeechParams;
    private final Activity activity;
    private Object trainer;
    private int beforeSpeakDelay;
    private int afterSpeakDelay;
    private boolean showToast;
    private boolean vocalize;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (key == null) return;
        switch (key) {
            case "delay_before_answer":
                if (SharedPreferencesHelper.getSpeakFirst(sharedPreferences)) {
                    setAfterSpeakDelay(SharedPreferencesHelper.getDelayBeforeAnswer(sharedPreferences));
                } else {
                    setBeforeSpeakDelay(SharedPreferencesHelper.getDelayBeforeAnswer(sharedPreferences));
                }
                break;
            case "delay_after_answer":
                if (SharedPreferencesHelper.getSpeakFirst(sharedPreferences)) {
                    setBeforeSpeakDelay(SharedPreferencesHelper.getDelayAfterAnswer(sharedPreferences));
                } else {
                    setAfterSpeakDelay(SharedPreferencesHelper.getDelayAfterAnswer(sharedPreferences));
                }
                break;
            case "answer_toast":
                setShowToast(SharedPreferencesHelper.getAnswerToast(sharedPreferences));
                break;
            case "answer_vocalize":
                setVocalize(SharedPreferencesHelper.getAnswerVocalize(sharedPreferences));
                break;
            case "speak_first":
                if (SharedPreferencesHelper.getSpeakFirst(sharedPreferences)) {
                    setAfterSpeakDelay(SharedPreferencesHelper.getDelayBeforeAnswer(sharedPreferences));
                    setBeforeSpeakDelay(SharedPreferencesHelper.getDelayAfterAnswer(sharedPreferences));
                } else {
                    setBeforeSpeakDelay(SharedPreferencesHelper.getDelayBeforeAnswer(sharedPreferences));
                    setAfterSpeakDelay(SharedPreferencesHelper.getDelayAfterAnswer(sharedPreferences));
                }
                break;
        }
    }

    TextSpeaker(Activity activity, SharedPreferences sharedPreferences) {
        this.activity = activity;
        this.sharedPreferences = sharedPreferences;
        if (SharedPreferencesHelper.getSpeakFirst(sharedPreferences)) {
            this.beforeSpeakDelay = SharedPreferencesHelper.getDelayAfterAnswer(sharedPreferences);
            this.afterSpeakDelay = SharedPreferencesHelper.getDelayBeforeAnswer(sharedPreferences);
        } else {
            this.beforeSpeakDelay = SharedPreferencesHelper.getDelayBeforeAnswer(sharedPreferences);
            this.afterSpeakDelay = SharedPreferencesHelper.getDelayAfterAnswer(sharedPreferences);
        }
        this.showToast = SharedPreferencesHelper.getAnswerToast(sharedPreferences);
        this.vocalize = SharedPreferencesHelper.getAnswerVocalize(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        textToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setOnUtteranceProgressListener(utteranceListener);
                }
            }
        });
        textToSpeechParams = new HashMap<>();
        textToSpeechParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, activity.getPackageName());
    }

    public void speak(final String text, final Object trainer) {
        try {
            Thread.sleep(beforeSpeakDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        this.trainer = trainer;
        if (showToast) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
        textToSpeechParams.put(KEY_PARAM_VOLUME, vocalize ? "1" : "0");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, textToSpeechParams);
    }

    public void stop() {
        textToSpeech.stop();
    }

    private final UtteranceProgressListener utteranceListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
        }

        @Override
        public void onDone(String utteranceId) {
            try {
                Thread.sleep(afterSpeakDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (trainer) {
                trainer.notify();
            }
        }

        @Override
        public void onError(String utteranceId) {
        }
    };

    public void destroy() {
        textToSpeech.stop();
        textToSpeech.shutdown();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public int getBeforeSpeakDelay() {
        return beforeSpeakDelay;
    }

    public void setBeforeSpeakDelay(int beforeSpeakDelay) {
        this.beforeSpeakDelay = beforeSpeakDelay;
    }

    public int getAfterSpeakDelay() {
        return afterSpeakDelay;
    }

    public void setAfterSpeakDelay(int afterSpeakDelay) {
        this.afterSpeakDelay = afterSpeakDelay;
    }

    public boolean isShowToast() {
        return showToast;
    }

    public void setShowToast(boolean showToast) {
        this.showToast = showToast;
    }

    public boolean isVocalize() {
        return vocalize;
    }

    public void setVocalize(boolean vocalize) {
        this.vocalize = vocalize;
    }
}
