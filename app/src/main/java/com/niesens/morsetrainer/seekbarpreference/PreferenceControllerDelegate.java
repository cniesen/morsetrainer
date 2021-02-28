/*
 *  Copyright (C) 2021 Claus Niesen
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

package com.niesens.morsetrainer.seekbarpreference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.niesens.morsetrainer.R;


class PreferenceControllerDelegate implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private static final int DEFAULT_CURRENT_VALUE = 50;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_INTERVAL = 1;
    private static final boolean DEFAULT_IS_ENABLED = true;


    private int maxValue;
    private int minValue;
    private int interval;
    private int currentValue;
    private String measurementUnit;

    private TextView valueView;
    private SeekBar seekBarView;
    private TextView measurementView;
    private LinearLayout valueHolderView;
    private FrameLayout bottomLineView;

    //view stuff
    private String title;
    private String summary;
    private boolean isEnabled;

    //controller stuff
    private Context context;
    private ViewStateListener viewStateListener;
    private PersistValueListener persistValueListener;
    private ChangeValueListener changeValueListener;

    interface ViewStateListener {
        boolean isEnabled();
        void setEnabled(boolean enabled);
    }

    PreferenceControllerDelegate(Context context) {
        this.context = context;
    }

    void setPersistValueListener(PersistValueListener persistValueListener) {
        this.persistValueListener = persistValueListener;
    }

    void setViewStateListener(ViewStateListener viewStateListener) {
        this.viewStateListener = viewStateListener;
    }

    void setChangeValueListener(ChangeValueListener changeValueListener) {
        this.changeValueListener = changeValueListener;
    }

    void loadValuesFromXml(AttributeSet attrs) {
        if(attrs == null) {
            currentValue = DEFAULT_CURRENT_VALUE;
            minValue = DEFAULT_MIN_VALUE;
            maxValue = DEFAULT_MAX_VALUE;
            interval = DEFAULT_INTERVAL;

            isEnabled = DEFAULT_IS_ENABLED;
        }
        else {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);
            try {
                minValue = a.getInt(R.styleable.SeekBarPreference_msbp_minValue, DEFAULT_MIN_VALUE);
                interval = a.getInt(R.styleable.SeekBarPreference_msbp_interval, DEFAULT_INTERVAL);
                maxValue = a.getInt(R.styleable.SeekBarPreference_msbp_maxValue, DEFAULT_MAX_VALUE);

                measurementUnit = a.getString(R.styleable.SeekBarPreference_msbp_measurementUnit);
                currentValue = a.getResources().getInteger(attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "defaultValue", DEFAULT_CURRENT_VALUE));
            }
            finally {
                a.recycle();
            }
        }
    }


    void onBind(View view) {

        view.setClickable(false);

        seekBarView = view.findViewById(R.id.seekbar);
        measurementView = view.findViewById(R.id.measurement_unit);
        valueView = view.findViewById(R.id.seekbar_value);

        setMaxValue(maxValue);
        seekBarView.setOnSeekBarChangeListener(this);

        measurementView.setText(measurementUnit);

        setCurrentValue(currentValue);
        valueView.setText(String.valueOf(currentValue));

        bottomLineView = view.findViewById(R.id.bottom_line);
        valueHolderView = view.findViewById(R.id.value_holder);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int newValue = minValue + (progress * interval);

        if (changeValueListener != null) {
            if (!changeValueListener.onChange(newValue)) {
                return;
            }
        }
        currentValue = newValue;
        valueView.setText(String.valueOf(newValue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        setCurrentValue(currentValue);
    }

    @Override
    public void onClick(final View v) {

    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getSummary() {
        return summary;
    }

    void setSummary(String summary) {
        this.summary = summary;
    }

    boolean isEnabled() {
        return isEnabled;
    }

    void setEnabled(boolean enabled) {
        Log.d(TAG, "setEnabled = " + enabled);
        isEnabled = enabled;

        if(viewStateListener != null ) {
            viewStateListener.setEnabled(enabled);
        }

        if(seekBarView != null) { //theoretically might not always work
            Log.d(TAG, "view is disabled!");
            seekBarView.setEnabled(enabled);
            valueView.setEnabled(enabled);
            valueHolderView.setClickable(enabled);
            valueHolderView.setEnabled(enabled);

            measurementView.setEnabled(enabled);
            bottomLineView.setEnabled(enabled);
        }

    }


    int getMaxValue() {
        return maxValue;
    }

    void setMaxValue(int maxValue) {
        this.maxValue = maxValue;

        if (seekBarView != null) {
            seekBarView.setMax((maxValue - minValue) / interval);
        }
    }

    int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        setMaxValue(maxValue);
    }

    int getInterval() {
        return interval;
    }

    void setInterval(int interval) {
        this.interval = interval;
    }

    int getCurrentValue() {
        return currentValue;
    }

    void setCurrentValue(int value) {
        if(value < minValue) value = minValue;
        if(value > maxValue) value = maxValue;

        if (changeValueListener != null) {
            if (!changeValueListener.onChange(value)) {
                return;
            }
        }
        currentValue = value;
        if(seekBarView != null)
            seekBarView.setProgress((currentValue - minValue) / interval);

        if(persistValueListener != null) {
            persistValueListener.persistInt(value);
        }
    }

    String getMeasurementUnit() {
        return measurementUnit;
    }

    void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
        if(measurementView != null) {
            measurementView.setText(measurementUnit);
        }
    }

}

