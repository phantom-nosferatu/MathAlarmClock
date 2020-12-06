package com.example.alarm_puzzle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_time_picker.*


class TimePickerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time_picker, container, false)
    }

    override fun onResume() {
        super.onResume()
        setTimePicker()
        setFormatterTimePicker()
    }

    private fun setTimePicker() {
        num_picker_hour.minValue = 0
        num_picker_hour.maxValue = 23
        num_picker_minute.minValue = 0
        num_picker_minute.maxValue = 59
        num_picker_hour.wrapSelectorWheel = true
        num_picker_minute.wrapSelectorWheel = true
        setFormatterTimePicker()
    }

    private fun setFormatterTimePicker() {
        num_picker_hour.setFormatter { i -> String.format("%02d", i) }
        num_picker_minute.setFormatter { i -> String.format("%02d", i) }
    }

}
