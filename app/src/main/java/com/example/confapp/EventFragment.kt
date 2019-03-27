package com.example.confapp

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class EventFragment: Fragment() {

    private lateinit var retView: View

    private var event: CEvent = CEvent("", "", 35, "", -1, "", listOf(1, 2), "")

    private lateinit var viewModel: EventViewModel

    fun setCurrentEvent(evt: CEvent){
        event = evt
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retView = inflater.inflate(R.layout.fragment_event, container, false)

        viewModel  = ViewModelProviders.of(this).get(EventViewModel::class.java)

        viewModel.event.value = event
        val txt = retView.findViewById<TextView>(R.id.textView_eventName)
        txt.text = viewModel.event.toString()

        return retView
    }
}