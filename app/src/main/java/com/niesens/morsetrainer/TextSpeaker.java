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
    private int beforeAnswerDelay;
    private int afterAnswerDelay;
    private boolean showToast;

    TextSpeaker(Activity activity, int beforeAnswerDelay, int afterAnswerDelay, boolean showToast) {
        this.activity = activity;
        this.beforeAnswerDelay = beforeAnswerDelay;
        this.afterAnswerDelay = afterAnswerDelay;
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

    public void speak(final String text, final Object trainer) {
        try {
            Thread.sleep(beforeAnswerDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.trainer = trainer;
        if (showToast) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, textToSpeechParams);
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
                Thread.sleep(afterAnswerDelay);
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

    public int getBeforeAnswerDelay() {
        return beforeAnswerDelay;
    }

    public void setBeforeAnswerDelay(int beforeAnswerDelay) {
        this.beforeAnswerDelay = beforeAnswerDelay;
    }

    public int getAfterAnswerDelay() {
        return afterAnswerDelay;
    }

    public void setAfterAnswerDelay(int afterAnswerDelay) {
        this.afterAnswerDelay = afterAnswerDelay;
    }

    public boolean isShowToast() {
        return showToast;
    }

    public void setShowToast(boolean showToast) {
        this.showToast = showToast;
    }
}
