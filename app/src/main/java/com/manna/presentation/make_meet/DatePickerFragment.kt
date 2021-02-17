package com.manna.presentation.make_meet

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentDatePickerBinding
import java.text.SimpleDateFormat
import java.util.*


class DatePickerFragment :
    BaseFragment<FragmentDatePickerBinding>(R.layout.fragment_date_picker) {

    private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd E요일", Locale.KOREA)
    private val timeFormat: SimpleDateFormat = SimpleDateFormat("a hh:mm", Locale.KOREA)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    private fun setupView() {
        with(binding) {
            initDate()

            timePicker.setOnTimeChangedListener { _, hour, minute ->
                setTimeResult(hour, minute)
            }

            okButton.setOnClickListener {
                handleNextButton()
            }

            btnBack.setOnClickListener {
                finish()
            }

            resultDate.setOnClickListener {
                datePicker.isVisible = true
                timePicker.isGone = true
            }

            resultTime.setOnClickListener {
                datePicker.isGone = true
                timePicker.isVisible = true
            }

            root.setOnTouchListener { _, _ -> true }
        }
    }

    private fun initDate() {
        val initDate = Calendar.getInstance().apply {
            time = arguments?.getSerializable(DATE_TIME) as? Date ?: Date()
        }

        setDateResult(
            initDate.get(Calendar.YEAR),
            initDate.get(Calendar.MONTH),
            initDate.get(Calendar.DAY_OF_MONTH)
        )
        setTimeResult(
            initDate.get(Calendar.HOUR_OF_DAY),
            initDate.get(Calendar.MINUTE)
        )

        binding.datePicker.init(
            initDate.get(Calendar.YEAR),
            initDate.get(Calendar.MONTH),
            initDate.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, dayOfMonth ->

            setDateResult(year, month, dayOfMonth)
        }
    }

    private fun setDateResult(year: Int, month: Int, dayOfMonth: Int) {
        val date = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
        binding.resultDate.text = dateFormat.format(date.time)
    }

    private fun setTimeResult(hour: Int, minute: Int) {
        with(binding) {
            val date = Calendar.getInstance().apply {
                set(
                    datePicker.year,
                    datePicker.month,
                    datePicker.dayOfMonth, hour, minute
                )
            }

            resultTime.text = timeFormat.format(date.time)
        }
    }

    private fun handleNextButton() {
        with(binding) {
            when {
                datePicker.isVisible -> {
                    datePicker.isGone = true
                    timePicker.isVisible = true
                }

                timePicker.isVisible -> {
                    val date = Calendar.getInstance().apply {
                        set(
                            datePicker.year,
                            datePicker.month,
                            datePicker.dayOfMonth,
                            timePicker.currentHour,
                            timePicker.currentMinute
                        )
                    }

                    val data = Bundle().apply { putSerializable(DATE_TIME, date.time) }
                    setFragmentResult(this@DatePickerFragment::class.java.simpleName, data)

                    finish()
                }
            }
        }
    }

    private fun finish() {
        parentFragmentManager.commit {
            remove(this@DatePickerFragment)
        }
    }

    companion object {
        const val DATE_TIME = "date_time"

        fun newInstance(date: Date? = null) =
            DatePickerFragment().apply {
                arguments = Bundle().apply {
                    if (date != null) {
                        putSerializable(DATE_TIME, date)
                    }
                }
            }
    }
}