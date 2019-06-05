package com.example.confapp.event

//import android.app.DatePickerDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

import com.example.confapp.R
import com.example.confapp.schedule.ScheduleViewModel
import kotlinx.android.synthetic.main.fragment_new_event.view.*
import java.util.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class NewEventFragment : Fragment() {

    private lateinit var retView: View

    private lateinit var viewModel: NewEventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retView = inflater.inflate(R.layout.fragment_new_event, container, false)

        viewModel = ViewModelProviders.of(this).get(NewEventViewModel::class.java)

        viewModel.pickedDate.observe(this, android.arch.lifecycle.Observer { newDate ->
            retView.editText_eventDate.setText(newDate)
        })

        viewModel.pickedTime.observe(this, android.arch.lifecycle.Observer { newTime ->
            retView.editText_eventTime.setText(newTime)
        })


        retView.editText_eventName.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                v.hideKeyboard()
            }
        }

        retView.editText_eventDuration.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                v.hideKeyboard()
            }
        }

        retView.editText_eventHall.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                v.hideKeyboard()
            }
        }

        retView.editText_aboutEvent.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                v.hideKeyboard()
            }
        }

        retView.editText_eventDate.inputType = InputType.TYPE_NULL
        retView.editText_eventDate.setOnFocusChangeListener { _, _ ->
            showDateDialog()
        }

        retView.editText_eventDate.setOnClickListener {
            showDateDialog()
        }

        retView.editText_eventTime.inputType = InputType.TYPE_NULL
        retView.editText_eventTime.setOnFocusChangeListener { v, _ ->
            showTimeDialog()
        }

        retView.editText_eventTime.setOnClickListener {
            showTimeDialog()
        }

        retView.button_addEvent.setOnClickListener {
            if(checkConnectivity()){
                if(viewModel.onAddEventClick(
                    retView.editText_eventName.text.toString(),
                    retView.editText_aboutEvent.text.toString(),
                    retView.editText_eventDate.text.toString(),
                    retView.editText_eventTime.text.toString(),
                    retView.editText_eventHall.text.toString(),
                    retView.spinner_eventType.selectedItem.toString(),
                    retView.editText_eventDuration.text.toString(),
                    mutableListOf())){

                    Toast.makeText(context, "Event added to database", Toast.LENGTH_SHORT).show()
                    //activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
                    activity!!.finish()
                }else{
                    Toast.makeText(context, "Fill all required data to add an event", Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(context, "No internet connection, event not added", Toast.LENGTH_LONG).show()
            }
        }

        return retView
    }

    private fun showTimeDialog() {
        if(retView.editText_eventTime.hasFocus()){
            TimePickerDialog(context, viewModel, 0, 0, true).show()
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showDateDialog() {

        if(retView.editText_eventDate.hasFocus()){
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()

            val startTimeMillis = activity!!.intent.extras.get(ScheduleViewModel.KEY_START_DATE) as Long
            val endTimeMillis = activity!!.intent.extras.get(ScheduleViewModel.KEY_END_DATE) as Long

            start.timeInMillis = startTimeMillis
            end.timeInMillis = endTimeMillis

            val year = start.get(Calendar.YEAR)
            val month = start.get(Calendar.MONTH)
            val day = start.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(context, viewModel, year, month, day)
            datePickerDialog.datePicker.minDate = start.timeInMillis
            datePickerDialog.datePicker.maxDate = end.timeInMillis
            datePickerDialog.show()
        }
    }

    private fun checkConnectivity(): Boolean {
        var ret = false

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if(isConnected)
        {
            ret = true
        }
        return ret
    }

}
