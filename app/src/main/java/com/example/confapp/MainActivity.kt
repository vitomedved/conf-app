package com.example.confapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hehe: String = "asdasd"

        Toast.makeText(this, "lalala", Toast.LENGTH_LONG)
    }
}
