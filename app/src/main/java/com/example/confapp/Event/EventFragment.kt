package com.example.confapp.Event

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.confapp.Data.CEvent
import com.example.confapp.R

class EventFragment: Fragment() {

    private lateinit var retView: View

    private var event: CEvent? = null

    private lateinit var viewModel: EventViewModel

    fun setCurrentEvent(evt: CEvent){
        event = evt
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retView = inflater.inflate(R.layout.fragment_event, container, false)

        viewModel  = ViewModelProviders.of(this).get(EventViewModel::class.java)

        // This will be called only once because: when android "restarts" app with this fragment currently on screen, event will be set to null, but viewModel will save the state of this event
        if(event != null)
        {
            viewModel.setEvent(event!!)
        }

        val txt = retView.findViewById<TextView>(R.id.textView_eventName)

        txt.text = viewModel.event.value?.name

        return retView
    }
}