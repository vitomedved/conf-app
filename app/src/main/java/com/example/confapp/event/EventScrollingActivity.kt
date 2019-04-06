package com.example.confapp.event

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import com.example.confapp.R
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.example.confapp.login.LoginActivity
import com.example.confapp.model.CEvent
import com.example.confapp.schedule.ScheduleViewModel
import kotlinx.android.synthetic.main.activity_event_scrolling.*
import kotlinx.android.synthetic.main.content_event_scrolling.*

class EventScrollingActivity : AppCompatActivity() {

    //private lateinit var imageView_eventTypeIcon: ImageView
    //private lateinit var textView_eventType: TextView
    //private lateinit var textView_eventDate: TextView
    //private lateinit var textView_eventTime: TextView
    //private lateinit var textView_eventLocation: TextView
    //private lateinit var textView_aboutEvent: TextView

    private lateinit var button_favorite: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_scrolling)
        setSupportActionBar(toolbar)
        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/


        //imageView_eventTypeIcon = findViewById(R.id.imageView_eventType)
        //textView_eventType = findViewById(R.id.textView_eventType)
        //textView_eventDate = findViewById(R.id.textView_eventDate)
        //textView_eventTime = findViewById(R.id.textView_eventTime)
        //textView_eventLocation = findViewById(R.id.textView_eventLocation)
        //textView_aboutEvent = findViewById(R.id.textView_aboutEvent)
        button_favorite = findViewById(R.id.button_favorite)

        val viewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)

        viewModel.evtTypeIcon.observe(this, Observer { evtTypeIcon ->
            imageView_eventType.setImageResource(evtTypeIcon!!)
        })

        viewModel.evtTypeText.observe(this, Observer { evtTypeText ->
            textView_eventType.text = evtTypeText
        })

        viewModel.evtTimeText.observe(this, Observer { evtTimeText ->
            textView_eventTime.text = evtTimeText
        })

        viewModel.evtDateText.observe(this, Observer { evtDateText ->
            textView_eventDate.text = evtDateText
        })

        viewModel.evtLocationText.observe(this, Observer { evtLocationText ->
            textView_eventLocation.text = evtLocationText
        })

        viewModel.evtName.observe(this, Observer { evtName ->
            this.title = evtName
        })

        viewModel.evtAboutText.observe(this, Observer { aboutTxt ->
            textView_aboutEvent.text = aboutTxt
        })

        viewModel.currentFavoriteIcon.observe(this, Observer { favIcon ->
            button_favorite.setImageResource(favIcon!!)
        })

        val currEvt: CEvent = intent.getSerializableExtra(ScheduleViewModel.KEY_CURRENT_EVENT) as CEvent

        if(currEvt != null){
            viewModel.updateCurrentEvent(currEvt)
        }

        if(viewModel.isUserLoggedIn()){
            button_favorite.setOnClickListener { view ->
                // Set current user to favorites
                viewModel.toggleFavoriteEvent()
            }
        }
        else{
            button_favorite.setOnClickListener { view ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
