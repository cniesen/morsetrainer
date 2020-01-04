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

package com.niesens.morsetrainer.seekbarpreference;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.niesens.morsetrainer.R;

/**
 * Adapted from https://github.com/MrBIMC/MaterialSeekBarPreference
 */
public class SeekBarPreference extends Preference implements View.OnClickListener, PreferenceControllerDelegate.ViewStateListener, PersistValueListener, ChangeValueListener {
    private PreferenceControllerDelegate controllerDelegate;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SeekBarPreference(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        setLayoutResource(R.layout.seekbar_view_layout);
        controllerDelegate = new PreferenceControllerDelegate(getContext());

        controllerDelegate.setViewStateListener(this);
        controllerDelegate.setPersistValueListener(this);
        controllerDelegate.setChangeValueListener(this);

        controllerDelegate.loadValuesFromXml(attrs);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        controllerDelegate.onBind(view);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        controllerDelegate.setCurrentValue(getPersistedInt(controllerDelegate.getCurrentValue()));
    }

    @Override
    public boolean persistInt(int value) {
        return super.persistInt(value);
    }

    @Override
    public boolean onChange(int value) {
        return callChangeListener(value);
    }

    @Override
    public void onClick(final View v) {
        controllerDelegate.onClick(v);
    }

    public int getMaxValue() {
        return controllerDelegate.getMaxValue();
    }

    public void setMaxValue(int maxValue) {
        controllerDelegate.setMaxValue(maxValue);
    }

    public int getMinValue() {
        return controllerDelegate.getMinValue();
    }

    public void setMinValue(int minValue) {
        controllerDelegate.setMinValue(minValue);
    }

    public int getInterval() {
        return controllerDelegate.getInterval();
    }

    public void setInterval(int interval) {
        controllerDelegate.setInterval(interval);
    }

    public int getCurrentValue() {
        return controllerDelegate.getCurrentValue();
    }

    public void setCurrentValue(int currentValue) {
        controllerDelegate.setCurrentValue(currentValue);
        persistInt(controllerDelegate.getCurrentValue());
    }

    public String getMeasurementUnit() {
        return controllerDelegate.getMeasurementUnit();
    }

    public void setMeasurementUnit(String measurementUnit) {
        controllerDelegate.setMeasurementUnit(measurementUnit);
    }
}