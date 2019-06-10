package com.example.confapp.event.comment

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.confapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_enlarger.*

class ImageEnlargerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_enlarger)
        supportActionBar?.hide()

        //image_enlarger_loading_animation.visibility = View.VISIBLE


        val url = getIntent().getStringExtra("image");
        val myImage = findViewById(R.id.imageView_zoomImage) as ImageView

        Picasso.get().load(url).into(myImage)
        //image_enlarger_loading_animation.visibility = View.INVISIBLE

    }
}
