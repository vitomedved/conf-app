package com.example.confapp.map

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.confapp.R

class MapFragment: Fragment() {

    private lateinit var retView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retView = inflater.inflate(R.layout.fragment_map, container, false)

        return retView
    }
}