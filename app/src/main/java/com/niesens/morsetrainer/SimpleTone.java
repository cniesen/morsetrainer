package com.niesens.morsetrainer;

public class SimpleTone extends Sound {
    private final int NUM_TAPER_CYCLES = 4;
    protected final int numToneCycles;
    protected final double samplesPerToneCycle;

    public SimpleTone(int freqHz, int durationInMs, int _sampleRate) {
        super(durationInMs, _sampleRate);

        if (freqHz == 0) {
            throw new IllegalStateException("Illegal input frequency of 0HZ");
        }

        // How does cycle of tone we're generating fit into samples buffer?
        samplesPerToneCycle = (double) this.sampleRate / (double) freqHz;
        numToneCycles = numSamples / (int) samplesPerToneCycle;

        if (numToneCycles < (NUM_TAPER_CYCLES * 2)) {
            throw new IllegalStateException("Too few cycles at current sample rate");
        }

        if (samplesPerToneCycle < 2) {
            throw new IllegalArgumentException("Increase sample rate or lower frequency");
        }

        // Generate waveform
        for (int sampleNum = 0; sampleNum < this.numSamples; sampleNum++) {
            double angle = (2 * Math.PI) * ((double) sampleNum / samplesPerToneCycle);
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
        int cycleNum = getCycleNum(sampleNum);

        if (isAttackCycle(cycleNum)) {
            output = input * getAttackGain(cycleNum);
        } else if (isReleaseCycle(cycleNum)) {
            output = input * getReleaseGain(cycleNum);
        } else {
            output = input;
        }

        return output;
    }

    protected boolean isAttackCycle(int cycleNum) {
        return (cycleNum < NUM_TAPER_CYCLES);
    }

    protected boolean isReleaseCycle(int cycleNum) {
        int releaseStartCycle = numToneCycles - NUM_TAPER_CYCLES;
        return (releaseStartCycle <= cycleNum);
    }

    protected double getAttackGain(int cycleNum) {
        int rampPosition = cycleNum + 1;
        int rampLen = NUM_TAPER_CYCLES + 1;

        if (rampPosition < 0) rampPosition = 1;
        if (rampPosition > rampLen) rampPosition = rampLen;
        double gain = (double) rampPosition / (double) rampLen;
        return gain;
    }

    protected double getReleaseGain(int cycleNum) {
        int rampPosition = numToneCycles - cycleNum;
        int rampLen = NUM_TAPER_CYCLES + 1;

        if (rampPosition < 0) rampPosition = 1;
        if (rampPosition > rampLen) rampPosition = rampLen;
        double gain = (double) rampPosition / (double) rampLen;
        return gain;
    }

    // Cycle of the tone we're generating
    protected int getCycleNum(int sampleNum) {
        return sampleNum / (int) samplesPerToneCycle;
    }
}
