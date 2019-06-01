package com.example.confapp.event.favourite

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.confapp.R
import kotlinx.android.synthetic.main.fragment_favourite_event.view.*

class FavouriteEventFragment : Fragment() {
    private lateinit var retView: View
    private lateinit var viewModel: FavouriteEventViewModel

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retView = inflater.inflate(R.layout.fragment_favourite_event, container, false)

        viewModel = ViewModelProviders.of(this).get(FavouriteEventViewModel::class.java)

        viewModel.isProgressBarLoading.observe(this, Observer { isLoading ->
            when(isLoading){
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
}