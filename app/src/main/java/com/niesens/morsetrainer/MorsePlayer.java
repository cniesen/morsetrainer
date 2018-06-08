package com.niesens.morsetrainer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.Random;

public class MorsePlayer {
    private static final int SAMPLE_RATE_HZ = 48000;

    private int charPosition;
    private int wpm;
    private int farnsworth;
    private int pitch;
    private int currentPitch;
    private boolean randomPitch;
    private short[] ditAudioData;
    private short[] dahAudioData;
    private short[] componentBreakAudioData;
    private short[] characterBreakAudioData;
    private short[] wordBreakAudioData;
    private AudioTrack audioTrack;
    private Random random = new Random();


    MorsePlayer(int wpm, int farnsworth,int pitch, boolean randomPitch) {
        this.wpm = wpm;
        this.farnsworth = (farnsworth > getWpm()) ? getWpm() : farnsworth;
        this.pitch = pitch;
        setCurrentPitch(pitch);
        this.randomPitch = randomPitch;
        updateDitAudioData();
        updateDahAudioData();
        updateComponentBreakAudioData();
        updateCharacterBreakAudioData();
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
        updateDitAudioData();
        updateDahAudioData();
        updateComponentBreakAudioData();
    }

    public int getFarnsworth() {
        return farnsworth;
    }

    public void setFarnsworth(int farnsworth) {
        this.farnsworth = (farnsworth > getWpm()) ? getWpm() : farnsworth;
        updateCharacterBreakAudioData();
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
        updateDitAudioData();
        updateDahAudioData();
        updateComponentBreakAudioData();
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

    private void updateDitAudioData() {
        ditAudioData = new short[SAMPLE_RATE_HZ * (1200 / wpm) / 1000];
        for (int i = 0; i < ditAudioData.length; i++) {
            ditAudioData[i] = (short) (Math.sin((2.0 * Math.PI * i / (SAMPLE_RATE_HZ / currentPitch))) * Short.MAX_VALUE);
        }
    }

    private void updateDahAudioData() {
        dahAudioData = new short[SAMPLE_RATE_HZ * 3 * (1200 / wpm) / 1000];
        for (int i = 0; i < dahAudioData.length; i++) {
            dahAudioData[i] = (short) (Math.sin((2.0 * Math.PI * i / (SAMPLE_RATE_HZ / currentPitch))) * Short.MAX_VALUE);
        }
    }

    private void updateComponentBreakAudioData() {
        componentBreakAudioData = new short[SAMPLE_RATE_HZ * (1200 / wpm) / 1000];
        for (int i = 0; i < componentBreakAudioData.length; i++) {
            componentBreakAudioData[i] = 0;
        }
    }

    private void updateCharacterBreakAudioData() {
        characterBreakAudioData = new short[SAMPLE_RATE_HZ * 3 * (1200 / farnsworth) / 1000];
        for (int i = 0; i < characterBreakAudioData.length; i++) {
            characterBreakAudioData[i] = 0;
        }
    }

    private void updateWordBreakAudioData() {
        wordBreakAudioData = new short[SAMPLE_RATE_HZ * 7 * (1200 / farnsworth) / 1000];
        for (int i = 0; i < wordBreakAudioData.length; i++) {
            wordBreakAudioData[i] = 0;
        }
    }

    private void playDit() {
        audioTrack.write(ditAudioData, 0, ditAudioData.length);
        playComponentBreak();
    }

    private void playDah() {
        audioTrack.write(dahAudioData, 0, dahAudioData.length);
        playComponentBreak();
    }

    private void playComponentBreak() {
        audioTrack.write(componentBreakAudioData, 0, componentBreakAudioData.length);
    }

    private void playCharacterBreak() {
        audioTrack.write(characterBreakAudioData, 0, characterBreakAudioData.length);
    }

    private void playWordBreak() {
        audioTrack.write(wordBreakAudioData, 0, wordBreakAudioData.length);
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
                    playDit();
                    break;
                case '-':
                    playDah();
                    break;
                case ' ':
                    playCharacterBreak();
                    break;
                case '|':
                    playWordBreak();
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

