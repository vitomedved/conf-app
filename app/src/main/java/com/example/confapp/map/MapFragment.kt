package com.example.confapp.map

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.confapp.R

class MapFragment: Fragment() {

    private lateinit var retView: View
    private lateinit var hallBtn1: ImageView
    private lateinit var textDisp: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retView = inflater.inflate(R.layout.fragment_map, container, false)

        hallBtn1 = retView.findViewById(R.id.imageButton_hall1_map)
        textDisp = retView.findViewById(R.id.textView_display_text_map)
        hallBtn1.setOnClickListener {
            textDisp.text = "Showing events in hall 1"
        }

        return retView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}