/*
 *  Copyright (C) 2021–2026 Claus Niesen
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

package com.niesens.morsetrainer.seekbarpreference

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.preference.Preference
import android.util.AttributeSet
import android.view.View
import com.niesens.morsetrainer.R
import com.niesens.morsetrainer.seekbarpreference.PreferenceControllerDelegate.ViewStateListener


/**
 * Adapted from https://github.com/MrBIMC/MaterialSeekBarPreference
 */
class SeekBarPreference : Preference, View.OnClickListener, ViewStateListener, PersistValueListener,
    ChangeValueListener {
    private var controllerDelegate: PreferenceControllerDelegate? = null

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?) : super(context) {
        init(null)
    }

    private fun init(attrs: AttributeSet?) {
        setLayoutResource(R.layout.seekbar_view_layout)
        controllerDelegate = PreferenceControllerDelegate(getContext())

        controllerDelegate!!.setViewStateListener(this)
        controllerDelegate!!.setPersistValueListener(this)
        controllerDelegate!!.setChangeValueListener(this)

        controllerDelegate!!.loadValuesFromXml(attrs)
    }

    override fun onBindView(view: View) {
        super.onBindView(view)
        controllerDelegate!!.onBind(view)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        super.onSetInitialValue(restorePersistedValue, defaultValue)
        controllerDelegate!!.setCurrentValue(getPersistedInt(controllerDelegate!!.getCurrentValue()))
    }

    override fun persistInt(value: Int): Boolean {
        return super.persistInt(value)
    }

    override fun onChange(value: Int): Boolean {
        return callChangeListener(value)
    }

    override fun onClick(v: View?) {
        controllerDelegate!!.onClick(v)
    }

    var maxValue: Int
        get() = controllerDelegate!!.getMaxValue()
        set(maxValue) {
            controllerDelegate!!.setMaxValue(maxValue)
        }

    var minValue: Int
        get() = controllerDelegate!!.getMinValue()
        set(minValue) {
            controllerDelegate!!.setMinValue(minValue)
        }

    var interval: Int
        get() = controllerDelegate!!.interval
        set(interval) {
            controllerDelegate!!.interval = interval
        }

    var currentValue: Int
        get() = controllerDelegate!!.getCurrentValue()
        set(currentValue) {
            controllerDelegate!!.setCurrentValue(currentValue)
            persistInt(controllerDelegate!!.getCurrentValue())
        }

    var measurementUnit: String?
        get() = controllerDelegate!!.getMeasurementUnit()
        set(measurementUnit) {
            controllerDelegate!!.setMeasurementUnit(measurementUnit)
        }
}