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

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.niesens.morsetrainer.R

internal class PreferenceControllerDelegate(//controller stuff
    private val context: Context
) : OnSeekBarChangeListener, View.OnClickListener {
    private val TAG: String = javaClass.getSimpleName()

    private var maxValue = 0
    private var minValue = 0
    var interval: Int = 0
    private var currentValue = 0
    private var measurementUnit: String? = null

    private var valueView: TextView? = null
    private var seekBarView: SeekBar? = null
    private var measurementView: TextView? = null
    private var valueHolderView: LinearLayout? = null
    private var bottomLineView: FrameLayout? = null

    //view stuff
    var title: String? = null
    var summary: String? = null
    private var isEnabled = false

    private var viewStateListener: ViewStateListener? = null
    private var persistValueListener: PersistValueListener? = null
    private var changeValueListener: ChangeValueListener? = null

    internal interface ViewStateListener {
        fun isEnabled(): Boolean
        fun setEnabled(enabled: Boolean)
    }

    fun setPersistValueListener(persistValueListener: PersistValueListener?) {
        this.persistValueListener = persistValueListener
    }

    fun setViewStateListener(viewStateListener: ViewStateListener?) {
        this.viewStateListener = viewStateListener
    }

    fun setChangeValueListener(changeValueListener: ChangeValueListener?) {
        this.changeValueListener = changeValueListener
    }

    fun loadValuesFromXml(attrs: AttributeSet?) {
        if (attrs == null) {
            currentValue = DEFAULT_CURRENT_VALUE
            minValue = DEFAULT_MIN_VALUE
            maxValue = DEFAULT_MAX_VALUE
            interval = DEFAULT_INTERVAL

            isEnabled = DEFAULT_IS_ENABLED
        } else {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference)
            try {
                minValue = a.getInt(R.styleable.SeekBarPreference_msbp_minValue, DEFAULT_MIN_VALUE)
                interval = a.getInt(R.styleable.SeekBarPreference_msbp_interval, DEFAULT_INTERVAL)
                maxValue = a.getInt(R.styleable.SeekBarPreference_msbp_maxValue, DEFAULT_MAX_VALUE)

                measurementUnit = a.getString(R.styleable.SeekBarPreference_msbp_measurementUnit)
                currentValue = a.getResources().getInteger(
                    attrs.getAttributeResourceValue(
                        "http://schemas.android.com/apk/res/android",
                        "defaultValue",
                        DEFAULT_CURRENT_VALUE
                    )
                )
            } finally {
                a.recycle()
            }
        }
    }


    fun onBind(view: View) {
        view.setClickable(false)

        seekBarView = view.findViewById<SeekBar?>(R.id.seekbar)
        measurementView = view.findViewById<TextView?>(R.id.measurement_unit)
        valueView = view.findViewById<TextView>(R.id.seekbar_value)

        setMaxValue(maxValue)
        seekBarView!!.setOnSeekBarChangeListener(this)

        measurementView!!.setText(measurementUnit)

        setCurrentValue(currentValue)
        valueView!!.setText(currentValue.toString())

        bottomLineView = view.findViewById<FrameLayout>(R.id.bottom_line)
        valueHolderView = view.findViewById<LinearLayout>(R.id.value_holder)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val newValue = minValue + (progress * interval)

        if (changeValueListener != null) {
            if (!changeValueListener!!.onChange(newValue)) {
                return
            }
        }
        currentValue = newValue
        valueView!!.setText(newValue.toString())
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        setCurrentValue(currentValue)
    }

    override fun onClick(v: View?) {
    }

    fun isEnabled(): Boolean {
        return isEnabled
    }

    fun setEnabled(enabled: Boolean) {
        Log.d(TAG, "setEnabled = " + enabled)
        isEnabled = enabled

        if (viewStateListener != null) {
            viewStateListener!!.setEnabled(enabled)
        }

        if (seekBarView != null) { //theoretically might not always work
            Log.d(TAG, "view is disabled!")
            seekBarView!!.setEnabled(enabled)
            valueView!!.setEnabled(enabled)
            valueHolderView!!.setClickable(enabled)
            valueHolderView!!.setEnabled(enabled)

            measurementView!!.setEnabled(enabled)
            bottomLineView!!.setEnabled(enabled)
        }
    }


    fun getMaxValue(): Int {
        return maxValue
    }

    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue

        if (seekBarView != null) {
            seekBarView!!.setMax((maxValue - minValue) / interval)
        }
    }

    fun getMinValue(): Int {
        return minValue
    }

    fun setMinValue(minValue: Int) {
        this.minValue = minValue
        setMaxValue(maxValue)
    }

    fun getCurrentValue(): Int {
        return currentValue
    }

    fun setCurrentValue(value: Int) {
        var value = value
        if (value < minValue) value = minValue
        if (value > maxValue) value = maxValue

        if (changeValueListener != null) {
            if (!changeValueListener!!.onChange(value)) {
                return
            }
        }
        currentValue = value
        if (seekBarView != null) seekBarView!!.setProgress((currentValue - minValue) / interval)

        if (persistValueListener != null) {
            persistValueListener!!.persistInt(value)
        }
    }

    fun getMeasurementUnit(): String? {
        return measurementUnit
    }

    fun setMeasurementUnit(measurementUnit: String?) {
        this.measurementUnit = measurementUnit
        if (measurementView != null) {
            measurementView!!.setText(measurementUnit)
        }
    }

    companion object {
        private const val DEFAULT_CURRENT_VALUE = 50
        private const val DEFAULT_MIN_VALUE = 0
        private const val DEFAULT_MAX_VALUE = 100
        private const val DEFAULT_INTERVAL = 1
        private const val DEFAULT_IS_ENABLED = true
    }
}

