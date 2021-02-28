/*
 *  Copyright (C) 2021 Nathan Crapo
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

abstract class Sound {
    protected final int sampleRate;
    protected short samples[];
    protected final int numSamples;

    protected Sound(int durationInMs, int sampleRate) {
        this.sampleRate = sampleRate;
        this.numSamples = durationInMs * sampleRate / 1000;
        this.samples = new short[numSamples];
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

}
