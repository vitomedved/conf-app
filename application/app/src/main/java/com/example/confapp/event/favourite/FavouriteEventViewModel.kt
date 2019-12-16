package com.example.confapp.event.favourite

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.confapp.R
import com.example.confapp.model.CEvent
import com.example.confapp.model.CUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class FavouriteEventViewModel : ViewModel() {
    private var isLoading = MutableLiveData<Boolean>()
    val isProgressBarLoading: LiveData<Boolean>
        get() = isLoading

    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private var m_currentFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private var m_currentUser: CUser? = null

    private var m_events = MutableLiveData<List<CEvent>>()
    val events: LiveData<List<CEvent>>
    get() = m_events

    init {
        initViewModel()
    }

    fun initViewModel() {
        isLoading.value = true

        if (isUserLoggedIn()) {
            val currentUserId: String = m_currentFirebaseUser!!.uid.toString()
            FirebaseDatabase.getInstance().reference.child("model/user/$currentUserId")
                .ref.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e("EventViewModel", "User can not be loaded.")
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    m_currentUser = dataSnapshot.getValue(CUser::class.java)!!

                    getFavouriteEvents()
                }
            })
        }
    }

    fun getFavouriteEvents() {
        Log.d("FavouriteEventViewModel", "Current fav event ids: ${m_currentUser!!.subscribedEvents}")
        database.child("Data/event").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("ScheduleViewModel", "onCancelled called from database reference.")
            }

            override fun onDataChange(dataSnapshot: com.google.firebase.database.DataSnapshot) {
                val evt: MutableList<CEvent> = mutableListOf()

                for(eventSnapshot in dataSnapshot.children){
                    val event: CEvent = eventSnapshot.getValue(CEvent::class.java)!!

                    if(m_currentUser!!.subscribedEvents.contains(event.id))
                    {
                        evt.add(event)
                    }
                }
                evt.sortBy { it.date }
                m_events.value = evt
            }
        })

    }

    fun isUserLoggedIn(): Boolean {
        return m_currentFirebaseUser != null
    }
}