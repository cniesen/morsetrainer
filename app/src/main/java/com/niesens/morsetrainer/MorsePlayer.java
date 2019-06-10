/*
 *  Copyright (C) 2019 Claus Niesen
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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.Random;

// See https://morsecode.scphillips.com/timing.html for info about morse code;
// including WPM calculations, terminilogy, and others.
public class MorsePlayer {
    private static final int SAMPLE_RATE_HZ = 48000;

    private int charPosition;
    private int wpm;
    private int farnsworth;
    private int wpmDitLenMs;
    private int farnsworthDitLenMs;
    private int pitch;
    private int currentPitch;
    private boolean randomPitch;
    private Sound ditSound;
    private Sound dahSound;
    private Sound intraCharSpace; // Space between dit and dah in each character
    private Sound interCharSpace; // Space between characters
    private Sound interWordSpace;
    private AudioTrack audioTrack;
    private Random random = new Random();


    MorsePlayer(int wpm, int farnsworth,int pitch, boolean randomPitch) {
        this.wpm = wpm;
        this.farnsworth = (farnsworth > getWpm()) ? getWpm() : farnsworth;
        this.wpmDitLenMs = ditLenMsFromWpm(getWpm());
        this.farnsworthDitLenMs = ditLenMsFromWpm(getFarnsworth());
        this.pitch = pitch;
        setCurrentPitch(pitch);
        this.randomPitch = randomPitch;
        updateDitSound();
        updateDahSound();
        updateIntraCharSpace();
        updateInterCharSpace();
        updateWordBreakAudioData();

        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_HZ, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE_HZ, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
        audioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
    }

    public int getWpm() {
        return wpm;
    }

    public void setWpm(int wpm) {
        this.wpm = wpm;
        this.wpmDitLenMs = ditLenMsFromWpm(getWpm());
        updateDitSound();
        updateDahSound();
        updateIntraCharSpace();
    }

    public int getFarnsworth() {
        return farnsworth;
    }

    public void setFarnsworth(int farnsworth) {
        this.farnsworth = (farnsworth > getWpm()) ? getWpm() : farnsworth;
        this.farnsworthDitLenMs = ditLenMsFromWpm(getFarnsworth());
        updateInterCharSpace();
        updateWordBreakAudioData();
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
        setCurrentPitch(pitch);
    }

    private void setCurrentPitch(int pitch) {
        this.currentPitch = pitch;
        updateDitSound();
        updateDahSound();
        updateIntraCharSpace();
    }

    public boolean isRandomPitch() {
        return randomPitch;
    }

    public void setRandomPitch(boolean randomPitch) {
        this.randomPitch = randomPitch;
        if (!randomPitch) {
            //reset random pitch to user setting
            setCurrentPitch(pitch);
        }
    }

    private int ditLenMsFromWpm(int wpm) {
        // See website referenced at top of file for the following formula:
        // t = 60 / (wpm * 50)
        //
        // multiply top and bottom by 20 - want 1000 on bottom
        // t = 1200 / (wpm * 1000)
        //
        // drop 1000 from bottom - convert from seconds to milliseconds
        return 1200 / wpm;
    }

    private void updateDitSound() {
        ditSound = new SimpleTone(currentPitch, wpmDitLenMs, SAMPLE_RATE_HZ);
    }

    private void updateDahSound() {
        dahSound = new SimpleTone(currentPitch, wpmDitLenMs * 3, SAMPLE_RATE_HZ);
    }

    private void updateIntraCharSpace() {
        intraCharSpace = new Silence(wpmDitLenMs, SAMPLE_RATE_HZ);
    }

    private void updateInterCharSpace() {
        interCharSpace = new Silence(farnsworthDitLenMs * 3, SAMPLE_RATE_HZ);
    }

    private void updateWordBreakAudioData() {
        interWordSpace = new Silence(farnsworthDitLenMs * 7, SAMPLE_RATE_HZ);
    }

    private void playSound(Sound s) {
        audioTrack.write(s.getSamples(), 0, s.getNumSamples());
    }

    private boolean doesDitOrDahFollow(String morseCode, int current) {
        // Avoid invalid deref
        if ((current < 0) || (current >= morseCode.length())) {
            return false;
        }

        if (morseCode.charAt(current + 1) == '-' ||
            morseCode.charAt(current + 1) == '.') {
            return true;
        }

        return false;
    }

    public void play(String morseCode) {
        if (randomPitch) {
            // random pitch between 200 and 1200
            setCurrentPitch((random.nextInt(20) + 5) * 50);
        }

        audioTrack.play();

        for (charPosition = 0; charPosition < morseCode.length(); charPosition++) {
            switch(morseCode.charAt(charPosition)) {
                case '.':
                    playSound(ditSound);
                    if (doesDitOrDahFollow(morseCode, charPosition)) playSound(intraCharSpace);
                    break;
                case '-':
                    playSound(dahSound);
                    if (doesDitOrDahFollow(morseCode, charPosition)) playSound(intraCharSpace);
                    break;
                case ' ':
                    playSound(interCharSpace);
                    break;
                case '|':
                    playSound(interWordSpace);
                    break;
            }
        }
        audioTrack.stop();
    }

    public void stop() {
        charPosition = Integer.MAX_VALUE;
    }

    public void destroy() {
        audioTrack.stop();
        audioTrack.release();
    }
}

