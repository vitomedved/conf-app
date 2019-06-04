package com.example.confapp.event

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import com.example.confapp.R
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.example.confapp.NotificationUtils
import com.example.confapp.login.LoginActivity
import com.example.confapp.model.CEvent
import com.example.confapp.schedule.ScheduleViewModel
import kotlinx.android.synthetic.main.activity_event_scrolling.*
import kotlinx.android.synthetic.main.content_event_scrolling.*
import java.util.*
import com.example.confapp.MainActivity
import com.example.confapp.event.comment.CommentsRecyclerAdapter
import com.example.confapp.exhibitors.ExhibitorsRecyclerAdapter
import com.example.confapp.model.CComment


class EventScrollingActivity : AppCompatActivity() {

    private lateinit var button_favorite: FloatingActionButton
    override fun onBackPressed() {
        var launchedFromNotification = false

        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        if(intent.extras.containsKey(NotificationUtils.TAG_CLICKED_FROM_NOTIFICATION)){
            launchedFromNotification = intent.extras.getBoolean(NotificationUtils.TAG_CLICKED_FROM_NOTIFICATION)
        }

        if(launchedFromNotification){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        }else{
            super.onBackPressed()
        }
    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_scrolling)
        setSupportActionBar(toolbar)

        event_scrolling_activity_progress_bar_header.visibility = View.VISIBLE
        event_scrolling_activity_progress_bar_content.visibility = View.VISIBLE

        button_favorite = findViewById(R.id.button_favorite)

        val viewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)

        recyclerView = findViewById(R.id.recyclerView_comments)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        val adapter = CommentsRecyclerAdapter()
        recyclerView.adapter = adapter

        viewModel.comments.observe(this, Observer {
            newCommentsList ->
            adapter.setData(newCommentsList as MutableList<CComment>)

        })


        viewModel.currentEvent.observe(this, Observer {
            viewModel.updateEventLiveData()
            event_scrolling_activity_progress_bar_header.visibility = View.INVISIBLE
            event_scrolling_activity_progress_bar_content.visibility = View.INVISIBLE
        })

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



        val serializableEvt = intent.getSerializableExtra(ScheduleViewModel.KEY_CURRENT_EVENT)
        val currEvt: CEvent

        var evtId = ""

        if(serializableEvt != null){
            currEvt = serializableEvt as CEvent
            viewModel.updateCurrentEvent(currEvt)
            evtId = currEvt.id
        }else{
            evtId = intent.getStringExtra("eventId")
            viewModel.updateCurrentEvent(evtId)
            this.title = viewModel.evtName.value
        }
// da odem na event?da
        viewModel.getCommentsFromDatabase(evtId)

        if(viewModel.isUserLoggedIn()){
            button_favorite.setOnClickListener { view ->
                // Set current user to favorites

                // Check if event is subscribed or not
                val result = viewModel.toggleSubscribeToEvent()

                if(result == EventViewModel.EVENT_SUBSCRIBED){
                    val time: Long = viewModel.getHourBeforeEventStart()
                    NotificationUtils().setNotification(Calendar.getInstance().timeInMillis + 5000, viewModel.currentEvent.value!!, this)
                }
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
