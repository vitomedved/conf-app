package com.example.confapp.event.favourite

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.confapp.R
import com.example.confapp.event.favourite.recycler.FavouriteEventAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_favourite_event.view.*

class FavouriteEventFragment : Fragment() {
    private lateinit var retView: View
    private lateinit var viewModel: FavouriteEventViewModel

    private val adapter = FavouriteEventAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retView = inflater.inflate(R.layout.fragment_favourite_event, container, false)

        val isConnected = checkConnectivity()

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        if (!isConnected) {
            retView = inflater.inflate(R.layout.fragment_no_connection, container, false)
            return retView
        }

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
                    if (isLoggedIn) {
                        retView.fragment_favourite_event_progress_bar.visibility = View.VISIBLE
                    }
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
        viewModel = ViewModelProviders.of(this).get(FavouriteEventViewModel::class.java)
        viewModel.initViewModel()

        if(!adapter.isDataEmpty()){
            retView.fragment_favourite_event_progress_bar.visibility = View.INVISIBLE
        }
    }

    private fun checkConnectivity(): Boolean {
        var ret = false

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            ret = true
        }
        return ret
    }
}