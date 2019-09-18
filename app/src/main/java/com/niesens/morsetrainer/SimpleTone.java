/*
 *  Copyright (C) 2019 Nathan Crapo
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
import android.util.Log;

public class SimpleTone extends Sound {
    private final int NUM_TAPER_CYCLES = 4;
    protected final int samplesPerCycle;
    protected final int rampLen;
    protected final int releaseStartSample;

    public SimpleTone(int freqHz, int durationInMs, int _sampleRate) {
        super(durationInMs, _sampleRate);

        if (freqHz == 0) {
            throw new IllegalStateException("Illegal input frequency of 0HZ");
        }

        // How does cycle of tone we're generating fit into samples buffer?
        samplesPerCycle = this.sampleRate / freqHz;
        if (samplesPerCycle < 2) {
            throw new IllegalArgumentException("Increase sample rate or lower frequency");
        }

        // Calculate attack and release params
        rampLen = (NUM_TAPER_CYCLES * samplesPerCycle);
        releaseStartSample = numSamples - rampLen;

        // Generate waveform
        for (int sampleNum = 0; sampleNum < this.numSamples; sampleNum++) {
            double angle = (2 * Math.PI) * ((double) sampleNum / (double) samplesPerCycle);
            double level = Math.sin(angle) * Short.MAX_VALUE;
            short sample;

            level = shapeAmplitude(sampleNum, level);
            sample = (short) level;
            samples[sampleNum] = sample;
        }
    }

    public SimpleTone(int freqHz, int durationInMs) {
        this(freqHz, durationInMs, defaultSampleRate);
    }

    // Ramp amplitude of first and last few complete cycles up and down to
    // create a more pleasant sound; "attack" and "release".
    protected double shapeAmplitude(int sampleNum, double input) {
        double output;

        if (isAttackCycle(sampleNum)) {
            output = input * getAttackGain(sampleNum);
        } else if (isReleaseCycle(sampleNum)) {
            output = input * getReleaseGain(sampleNum);
        } else {
            output = input;
        }

        return output;
    }

    protected boolean isAttackCycle(int sampleNum) {
        return (sampleNum < rampLen);
    }

    protected boolean isReleaseCycle(int sampleNum) {
        return (releaseStartSample <= sampleNum);
    }

    protected double getAttackGain(int sampleNum) {
        int rampPosition = sampleNum;

        if (rampPosition < 0) rampPosition = 0;
        if (rampPosition > rampLen) rampPosition = rampLen;
        double gain = (double) sampleNum / (double) rampLen;
        return gain;
    }

    protected double getReleaseGain(int sampleNum) {
        int rampPosition = numSamples - sampleNum;

        if (rampPosition < 0) rampPosition = 0;
        if (rampPosition > rampLen) rampPosition = rampLen;
        double gain = (double) rampPosition / (double) rampLen;
        return gain;
    }
}
