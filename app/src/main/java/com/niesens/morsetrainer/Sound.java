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

import static android.media.AudioFormat.ENCODING_PCM_16BIT;

public class Sound {
    protected static final int defaultSampleRate = 8000;
    protected final int sampleRate;
    protected short samples[];
    protected final int numSamples;

    public Sound(int durationInMs, int _sampleRate) {
        sampleRate = _sampleRate;
        numSamples = durationInMs * sampleRate / 1000;
        samples = new short[numSamples];
    }

    protected Sound(int _numSamples) {
        numSamples = _numSamples;
        sampleRate = defaultSampleRate;
        samples = new short[numSamples];
    }

    public Sound() {
        numSamples = 0;
        sampleRate = defaultSampleRate;
    }

    public short[] getSamples() {
        return samples;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getByteLength() {
        return numSamples * 2;
    }

    public int getEncoding() {
        return ENCODING_PCM_16BIT;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public Sound append(Sound a) {
        Sound resultingSound = new Sound(this.getNumSamples() + a.getNumSamples());
        arrayCopy(resultingSound.samples, 0, this.samples, 0, this.getNumSamples());
        arrayCopy(resultingSound.samples, this.getNumSamples(), a.samples, 0, a.getNumSamples());
        return resultingSound;
    }

    protected void arrayCopy(short[] dest, int destPos, short[] src, int srcPos, int len) {
        for (int i = 0; i < len; i++) {
            dest[destPos + i] = src[srcPos + i];
        }
    }
}
