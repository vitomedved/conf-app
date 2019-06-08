package com.example.confapp.event

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import com.example.confapp.R
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.example.confapp.NotificationUtils
import com.example.confapp.login.LoginActivity
import com.example.confapp.model.CEvent
import com.example.confapp.schedule.ScheduleViewModel
import kotlinx.android.synthetic.main.activity_event_scrolling.*
import kotlinx.android.synthetic.main.content_event_scrolling.*
import java.util.*
import com.example.confapp.MainActivity
import com.example.confapp.event.comment.CommentsRecyclerAdapter
import com.example.confapp.model.CComment
import com.example.confapp.model.CUser
import java.text.SimpleDateFormat
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager


class EventScrollingActivity : AppCompatActivity() {

    private lateinit var button_favorite: FloatingActionButton

    private var evtId = ""

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


    private fun makeAlert(){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("User Profile")
            .setMessage("You must be logged in to post a comment, would you like to log in now?")
            .setPositiveButton("YES"){dialog, which ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            .setNeutralButton("Cancel"){dialog, which ->
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()

    }



    private lateinit var recyclerView: RecyclerView

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_scrolling)
        setSupportActionBar(toolbar)

        //event_scrolling_activity_progress_bar_header.visibility = View.VISIBLE
        event_scrolling_activity_progress_bar_content.visibility = View.VISIBLE


        button_favorite = findViewById(R.id.button_favorite)

        val viewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)


        val serializableEvt = intent.getSerializableExtra(ScheduleViewModel.KEY_CURRENT_EVENT)
        val currEvt: CEvent

        if(serializableEvt != null){
            currEvt = serializableEvt as CEvent
            viewModel.updateCurrentEvent(currEvt)
            evtId = currEvt.id
        }else{
            evtId = intent.getStringExtra("eventId")
            viewModel.updateCurrentEvent(evtId)
            this.title = viewModel.evtName.value
        }


        recyclerView = findViewById(R.id.recyclerView_comments)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        val adapter = CommentsRecyclerAdapter()
        recyclerView.adapter = adapter

        viewModel.getUsersFromDatabase()

        viewModel.comments.observe(this, Observer {
            newCommentsList ->
            adapter.setData(newCommentsList as MutableList<CComment>)

        })

        val currentDate  = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val stringDateTime = sdf.format(currentDate)



        viewModel.currentEvent.observe(this, Observer {
            viewModel.updateEventLiveData()
            //event_scrolling_activity_progress_bar_header.visibility = View.INVISIBLE
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

        viewModel.users.observe(this, Observer {users ->
            adapter.updateUsers(users as MutableList<CUser>)
            viewModel.getCommentsFromDatabase(evtId)
        })

        viewModel.comments.observe(this, Observer { newComments ->
            adapter.setData(newComments as MutableList<CComment>)
        })

        viewModel.getCommentsFromDatabase(evtId)

        // oprosti Vito ako ovo nije prema pravilima, ali radi :)
        fun View.hideKeyboard() {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }

        editText_comment.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                v.hideKeyboard()
            }
        }

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

            button_sendComment.setOnClickListener {
                editText_comment.text.toString()

                if (viewModel.onSendCommentClick(evtId, stringDateTime, editText_comment.text.toString())) {
                    Toast.makeText(this, "Comment added to database", Toast.LENGTH_SHORT).show()
                    viewModel.getCommentsFromDatabase(evtId)
                    editText_comment.text.clear()
                    editText_comment.clearFocus()
                }

            }
        }
        else{
            button_favorite.setOnClickListener { view ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

            button_sendComment.setOnClickListener {
                makeAlert()
            }
        }
    }
}

