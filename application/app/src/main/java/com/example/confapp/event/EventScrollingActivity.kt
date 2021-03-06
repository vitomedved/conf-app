package com.example.confapp.event

import android.annotation.SuppressLint
import android.app.Activity
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
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.example.confapp.exhibitors.ExhibitorsRecyclerAdapter
import com.example.confapp.model.CExhibitor


@Suppress("DEPRECATION")
class EventScrollingActivity : AppCompatActivity() {

    private lateinit var button_favorite: FloatingActionButton

    private var evtId = ""

    private lateinit var viewModel: EventViewModel


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
    private lateinit var recyclerViewExhibitors: RecyclerView



    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_scrolling)
        setSupportActionBar(toolbar)

        //event_scrolling_activity_progress_bar_header.visibility = View.VISIBLE
        event_scrolling_activity_progress_bar_content.visibility = View.VISIBLE


        button_favorite = findViewById(R.id.button_favorite)

        viewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)


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


        recyclerViewExhibitors = findViewById(R.id.recyclerView_eventExhibitors)
        recyclerViewExhibitors.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        val adapterExhibitors = ExhibitorsRecyclerAdapter()
        recyclerViewExhibitors.adapter = adapterExhibitors
        //viewModel.getExhibitorsFromDatabaseByEventId(evtId)
        viewModel.getExhibitorsFromDatabase(evtId)

        viewModel.getUsersFromDatabase()

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

        viewModel.exhibitors.observe(this, Observer { newExhibitors ->
            adapterExhibitors.setData(newExhibitors as MutableList<CExhibitor>)
        })

        viewModel.getCommentsFromDatabase(evtId)


        // oprosti Vito ako ovo nije prema pravilima, ali radi :)
        fun View.hideKeyboard() {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
            editText_comment.clearFocus();
        }

        editText_comment.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                v.hideKeyboard()
            }
        }

        if(viewModel.isUserLoggedIn()){
            button_favorite.setOnClickListener { view ->
                // Set current user to favorites
                if(!checkConnectivity()){
                    Toast.makeText(this, "Connect to the internet ", Toast.LENGTH_LONG).show()
                }else{
                    // Check if event is subscribed or not
                    val result = viewModel.toggleSubscribeToEvent()

                    if(result == EventViewModel.EVENT_SUBSCRIBED){
                        viewModel.getHourBeforeEventStart()
                        NotificationUtils().setNotification(Calendar.getInstance().timeInMillis + 5000, viewModel.currentEvent.value!!, this)
                        }
                }
            }

            button_sendComment.setOnClickListener {
                editText_comment.text.toString()

                if(!checkConnectivity()){
                    Toast.makeText(this, "Connect to the internet", Toast.LENGTH_LONG).show()
                }else{

                    if (viewModel.onSendCommentClick(evtId, stringDateTime, editText_comment.text.toString(), selectedPhotoUri)) {
                        Toast.makeText(this, "Comment added to database", Toast.LENGTH_SHORT).show()
                        viewModel.getCommentsFromDatabase(evtId)
                        editText_comment.text.clear()
                        editText_comment.clearFocus()
                        imageView_uploadedImage.setImageBitmap(null)
                        imageView_uploadedImage.layoutParams.height = -2
                    }else{
                        Toast.makeText(this, "Comment cannot be blank", Toast.LENGTH_LONG).show()
                    }
                }
            }

            button_uploadImage.setOnClickListener {

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/comments/*"
                startActivityForResult(intent, 0)
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

            button_uploadImage.setOnClickListener {
                makeAlert()
            }
        }


    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        super.onContextItemSelected(item)

        Toast.makeText(this, "Clicked ${item!!.groupId}", Toast.LENGTH_SHORT).show()
        if(!checkConnectivity()){
            Toast.makeText(this, "Connect to the internet", Toast.LENGTH_LONG).show()
        }else{
            if(viewModel.removeComment(evtId, item.groupId)){
                Toast.makeText(this, "Comment successfully removed", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Unable to delete comment", Toast.LENGTH_LONG).show()
            }
        }

        return true
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            Log.d("probica", selectedPhotoUri.toString())

            imageView_uploadedImage.layoutParams.height = 200
            imageView_uploadedImage.setImageBitmap(bitmap)

        }
    }

    // Returns false if device is not connected to internet, true if device is connected to internet
    private fun checkConnectivity(): Boolean {
        var ret = false

        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if(isConnected)
        {
            ret = true
        }
        return ret
    }


}
