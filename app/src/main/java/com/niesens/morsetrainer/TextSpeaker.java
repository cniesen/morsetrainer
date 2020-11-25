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

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

public class TextSpeaker  {
    private TextToSpeech textToSpeech;
    private HashMap<String, String> textToSpeechParams;
    private Activity activity;
    private Object trainer;
    private int beforeSpeakDelay;
    private int afterSpeakDelay;
    private boolean showToast;

    TextSpeaker(Activity activity, int beforeSpeakDelay, int afterSpeakDelay, boolean showToast) {
        this.activity = activity;
        this.beforeSpeakDelay = beforeSpeakDelay;
        this.afterSpeakDelay = afterSpeakDelay;
        this.showToast = showToast;
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

    public void speak(final String text, final boolean vocalize, final Object trainer) {
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
        final String speakText = (vocalize ? text : "");
        textToSpeech.speak(speakText, TextToSpeech.QUEUE_FLUSH, textToSpeechParams);
    }

    public void stop() {
        textToSpeech.stop();
    }

    private UtteranceProgressListener utteranceListener = new UtteranceProgressListener() {
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
}
