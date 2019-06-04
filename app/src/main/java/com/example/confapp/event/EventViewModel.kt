package com.example.confapp.event

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.confapp.NotificationUtils
import com.example.confapp.model.CEvent
import com.example.confapp.R
import com.example.confapp.model.CComment
import com.example.confapp.model.CUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class EventViewModel: ViewModel() {

    companion object {
        const val EVENT_SUBSCRIBED = 0
        const val EVENT_UNSUBSCRIBED = 1
        const val EVENT_SUB_ERROR = -1
    }

    private var m_currentUser: CUser? = null

    private val fbUser = FirebaseAuth.getInstance().currentUser

    private var m_currentEvent = MutableLiveData<CEvent>()
    val currentEvent: LiveData<CEvent>
        get() = m_currentEvent

    private var m_evtName = MutableLiveData<String>()
    val evtName: LiveData<String>
        get() = m_evtName

    private var m_evtTypeIcon = MutableLiveData<Int>()
    val evtTypeIcon: LiveData<Int>
        get() = m_evtTypeIcon

    private var m_evtTypeText = MutableLiveData<String>()
    val evtTypeText: LiveData<String>
        get() = m_evtTypeText

    private var m_evtDateText = MutableLiveData<String>()
    val evtDateText: LiveData<String>
        get() = m_evtDateText

    private var m_evtTimeText = MutableLiveData<String>()
    val evtTimeText: LiveData<String>
        get() = m_evtTimeText

    private var m_evtLocationText = MutableLiveData<String>()
    val evtLocationText: LiveData<String>
        get() = m_evtLocationText

    private var m_evtAboutText = MutableLiveData<String>()
    val evtAboutText: LiveData<String>
        get() = m_evtAboutText

    private var m_currentFavoriteIcon = MutableLiveData<Int>()
    val currentFavoriteIcon: LiveData<Int>
        get() = m_currentFavoriteIcon

    private val m_comments = MutableLiveData<List<CComment>>()
    val comments: MutableLiveData<List<CComment>>
        get() = m_comments


    private lateinit var database: DatabaseReference


    private var m_currentFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    init{
        if(isUserLoggedIn()){
            val currentUserId: String = m_currentFirebaseUser!!.uid.toString()
            FirebaseDatabase.getInstance().reference.child("model/user/$currentUserId").ref.addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e("EventViewModel", "User can not be loaded.")
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    m_currentUser = dataSnapshot.getValue(CUser::class.java)!!

                    if(isUserSubscribed()){
                        m_currentFavoriteIcon.value = R.drawable.ic_hearth_red
                    }else{
                        m_currentFavoriteIcon.value = R.drawable.ic_hearth_lightest_blue
                    }
                }
            })
        }
        database = FirebaseDatabase.getInstance().reference

    }


    fun updateEventLiveData(){
        when(m_currentEvent.value!!.type){
            CEvent.EventType.WORKSHOP.value -> {
                m_evtTypeIcon.value = R.drawable.ic_tag_workshop
                m_evtTypeText.value = CEvent.EventType.WORKSHOP.value.capitalize()
            }
            CEvent.EventType.PRESENTATION.value -> {
                m_evtTypeIcon.value = R.drawable.ic_tag_presentation
                m_evtTypeText.value = CEvent.EventType.PRESENTATION.value.capitalize()
            }
            CEvent.EventType.QA.value -> {
                m_evtTypeIcon.value = R.drawable.ic_tag_qa
                m_evtTypeText.value = CEvent.EventType.PRESENTATION.value.capitalize()
            }
        }

        m_evtLocationText.value = "Hall: " + m_currentEvent.value!!.hall

        m_evtTimeText.value = m_currentEvent.value!!.getTimeString()

        m_evtDateText.value = m_currentEvent.value!!.getDateString()

        m_evtName.value = m_currentEvent.value!!.name

        m_evtAboutText.value = m_currentEvent.value!!.about


        if(m_currentUser != null){
            if(isUserSubscribed()){
                m_currentFavoriteIcon.value = R.drawable.ic_hearth_red
            }else{
                m_currentFavoriteIcon.value = R.drawable.ic_hearth_lightest_blue
            }
        }

    }

    fun updateCurrentEvent(evt: CEvent){
        m_currentEvent.value = evt
    }

    fun updateCurrentEvent(id: String) {
        getEventById(id)
    }

    private fun getEventById(id: String){

        FirebaseDatabase.getInstance().reference.child("Data/event/$id").ref.addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("EventViewModel", "Event can not be loaded via ID.")
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                m_currentEvent.value = dataSnapshot.getValue(CEvent::class.java)!!
            }
        })
    }

    fun getCommentsFromDatabase(id: String){//odi ponovno u scrolling activity  crasha mi se sada na t

        FirebaseDatabase.getInstance().reference.child("Data/event/$id/comment").ref.addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("EventViewModel", "Comment can not be loaded via ID.")
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val comments: MutableList<CComment> = mutableListOf()

                for(commentSnapshot in dataSnapshot.children){
                    val comment: CComment = commentSnapshot.getValue(CComment::class.java)!!

                    comments.add(comment)
                }//

                m_comments.value = comments

            }
        })
    }

    fun isUserLoggedIn(): Boolean{
        return m_currentFirebaseUser != null
    }

    fun toggleSubscribeToEvent(): Int{
        // Check if current user is subscribed to this event

        var ret = EVENT_SUB_ERROR

        // If yes, delete id from list, and update database
        if(isUserSubscribed()){
            val eventIdIndex = m_currentUser!!.subscribedEvents.indexOf(m_currentEvent.value!!.id)
            m_currentUser!!.subscribedEvents.removeAt(eventIdIndex)
            FirebaseDatabase.getInstance().reference.child("model/user/${m_currentUser!!.uid}").setValue(m_currentUser)
            m_currentFavoriteIcon.value = R.drawable.ic_hearth_lightest_blue

            ret = EVENT_UNSUBSCRIBED
        }else{
            // Add id to list and update database
            m_currentUser!!.subscribedEvents.add(m_currentEvent.value!!.id)
            FirebaseDatabase.getInstance().reference.child("model/user/${m_currentUser!!.uid}").setValue(m_currentUser)
            m_currentFavoriteIcon.value = R.drawable.ic_hearth_red

            ret = EVENT_SUBSCRIBED
        }

        return ret
    }

    private fun isUserSubscribed(): Boolean{
        var ret = false

        if(fbUser != null && m_currentEvent.value != null && m_currentUser!!.subscribedEvents.contains(m_currentEvent.value!!.id)){
            ret = true
        }

        return ret
    }

    fun getHourBeforeEventStart(): Long {
        var time = m_currentEvent.value!!.getDateTimeCalendar()

        time.add(Calendar.HOUR_OF_DAY, -1)

        return time.timeInMillis
    }

}
