package com.example.confapp.event.favourite

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.confapp.R
import com.example.confapp.event.favourite.recycler.FavouriteEventAdapter
import kotlinx.android.synthetic.main.fragment_favourite_event.view.*

class FavouriteEventFragment : Fragment() {
    private lateinit var retView: View
    private lateinit var viewModel: FavouriteEventViewModel

    private val adapter = FavouriteEventAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retView = inflater.inflate(R.layout.fragment_favourite_event, container, false)

        viewModel = ViewModelProviders.of(this).get(FavouriteEventViewModel::class.java)

        retView.fragment_favourite_event_recycler.layoutManager = LinearLayoutManager(activity)

        viewModel.events.observe(this, Observer { newEvents ->
            // TODO: its bad to do this like this. Should create method in adapter setData(events) which will then notify for change of data
            adapter.m_events = newEvents!!
            retView.fragment_favourite_event_recycler.adapter = adapter
            retView.fragment_favourite_event_progress_bar.visibility = View.INVISIBLE
        })

        viewModel.isProgressBarLoading.observe(this, Observer { isLoading ->
            when (isLoading) {
                true -> {
                    retView.fragment_favourite_event_progress_bar.visibility = View.VISIBLE
                }

                false -> {
                    retView.fragment_favourite_event_progress_bar.visibility = View.INVISIBLE
                }
            }
        })

        return retView
    }

    override fun onResume() {
        super.onResume()
        viewModel.initViewModel()
    }
}