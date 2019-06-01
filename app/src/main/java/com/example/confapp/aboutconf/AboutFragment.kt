package com.example.confapp.aboutconf

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.confapp.R
import com.example.confapp.schedule.ScheduleViewModel
import com.firebase.client.Firebase
import kotlinx.android.synthetic.main.fragment_about.view.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*

@Suppress("DEPRECATION")
class AboutFragment : Fragment() {

    private lateinit var retView: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        retView = inflater.inflate(R.layout.fragment_about, container, false)


        return retView
    }



}