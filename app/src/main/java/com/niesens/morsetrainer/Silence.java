package com.niesens.morsetrainer;

public class Silence extends Sound {
    final int SILENCE_VAL = 0;

    public Silence(int durationInMs, int _sampleRate) {
        super(durationInMs, _sampleRate);

        for (int sampleNum = 0; sampleNum < this.numSamples; sampleNum++) {
            this.samples[sampleNum] = SILENCE_VAL;
        }
    }

    public Silence(int durationInMs) {
        this(durationInMs, defaultSampleRate);
    }
}
