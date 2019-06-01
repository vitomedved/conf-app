package com.example.confapp.user

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import com.example.confapp.R

class UserProfileActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.popupwindowuser)

        var dm : DisplayMetrics = DisplayMetrics()
        getWindowManager().defaultDisplay.getMetrics(dm)

        var width : Int = dm.widthPixels
        var height : Int = dm.heightPixels

        width = (width * 0.8).toInt()
        height = (height * 0.6).toInt()

        getWindow().setLayout(width, height)

    }
}