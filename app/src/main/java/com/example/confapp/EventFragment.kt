package com.example.confapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class EventFragment: Fragment() {

    private lateinit var retView: View

    private lateinit var event: CEvent

    fun setCurrentEvent(evt: CEvent){
        event = evt
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retView = inflater.inflate(R.layout.fragment_event, container, false)

        val txt = retView.findViewById<TextView>(R.id.textView_eventName)
        txt.text = event.name

        return retView
    }
}