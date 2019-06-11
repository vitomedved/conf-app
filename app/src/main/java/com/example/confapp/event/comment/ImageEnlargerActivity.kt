package com.example.confapp.event.comment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.DrawableWrapper
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.confapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_enlarger.*

class ImageEnlargerActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_enlarger)
        supportActionBar?.hide()

        val dm : DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        var width : Int = dm.widthPixels
        var height : Int = dm.heightPixels

        width = (width * 0.8).toInt()
        height = (height * 0.6).toInt()

        val url = intent.getStringExtra("image")

        val myImage = findViewById(R.id.imageView_zoomImage) as ImageView

        Picasso.get().load(url).into(myImage)


        //getWindow().setLayout( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setLayout(width, height)
        window.decorView.background = ColorDrawable(Color.TRANSPARENT)
    }
}
