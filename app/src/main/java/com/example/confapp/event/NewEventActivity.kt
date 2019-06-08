package com.example.confapp.event

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.confapp.R




class NewEventActivity: AppCompatActivity() {

    companion object {
        const val FRAGMENT_TAG = "NewEventFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event)

        supportFragmentManager.beginTransaction().replace(R.id.activity_newEvent, NewEventFragment(), FRAGMENT_TAG).commit()
    }
}