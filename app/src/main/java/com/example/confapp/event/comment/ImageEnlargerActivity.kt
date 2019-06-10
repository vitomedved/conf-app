package com.example.confapp.event.comment

import android.graphics.drawable.DrawableWrapper
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.confapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_enlarger.*

class ImageEnlargerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_enlarger)
        supportActionBar?.hide()


        val url = getIntent().getStringExtra("image");
        val myImage = findViewById(R.id.imageView_zoomImage) as ImageView

        Picasso.get().load(url).into(myImage)


        var dm : DisplayMetrics = DisplayMetrics()
        getWindowManager().defaultDisplay.getMetrics(dm)

        var width : Int = dm.widthPixels
        var height : Int = dm.heightPixels

        width = (width * 0.8).toInt()
        height = (height * 0.6).toInt()

        //getWindow().setLayout( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        getWindow().setLayout(width, height)
    }
}
