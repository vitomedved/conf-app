package com.example.confapp.schedule

import com.example.confapp.R
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.View
import com.example.confapp.model.CConference
import com.example.confapp.model.CEvent
import com.example.confapp.model.CUser
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class ScheduleViewModel : ViewModel() {

    companion object {
        const val KEY_CURRENT_EVENT = "CURRENT_EVENT"
        const val KEY_START_DATE = "StartDate"
        const val KEY_END_DATE = "EndDate"
    }

    private lateinit var database: DatabaseReference

    private var m_startDate: Calendar = Calendar.getInstance()
    val startDate: Calendar
        get() = m_startDate

    private var m_endDate: Calendar = Calendar.getInstance()
    val endDate: Calendar
        get() = m_endDate

    private var m_currentDate: Calendar = Calendar.getInstance()

    private val m_shortWeekNames = DateFormatSymbols.getInstance().shortWeekdays
    private val m_shortMonthNames = DateFormatSymbols.getInstance().shortMonths

    private var m_currentDateString = MutableLiveData<String>()
    val currentDateString: LiveData<String>
        get() = m_currentDateString

    private var m_currentMonthString = MutableLiveData<String>()
    val currentMonthString: LiveData<String>
        get() = m_currentMonthString

    private var m_currentWeekdayString = MutableLiveData<String>()
    val currentWeekdayString: LiveData<String>
        get() = m_currentWeekdayString


    private val m_events = MutableLiveData<List<CEvent>>()
    val events: LiveData<List<CEvent>>
        get() = m_events

    private var m_isFirstDay = MutableLiveData<Boolean>()
    private var m_isLastDay = MutableLiveData<Boolean>()

    private var m_user: CUser? = null

    val currentLeftArrowIcon = MutableLiveData<Int>()
    val currentRightArrowIcon = MutableLiveData<Int>()


    init {
        database = FirebaseDatabase.getInstance().reference
        initUserFromDatabase()
        initDatesFromDatabase()
        initEventsFromDatabase()
    }

    private fun initEventsFromDatabase() {
        database.child("Data/event").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("ScheduleViewModel", "onCancelled called from database reference.")
            }

            override fun onDataChange(dataSnapshot: com.google.firebase.database.DataSnapshot) {
                val evt: MutableList<CEvent> = mutableListOf()

                val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val eventDate = Calendar.getInstance()


                for (eventSnapshot in dataSnapshot.children) {
                    val event: CEvent = eventSnapshot.getValue(CEvent::class.java)!!

                    eventDate.time = dateFormat.parse(event.date)

                    if (eventDate.get(Calendar.DAY_OF_YEAR) == m_currentDate.get(Calendar.DAY_OF_YEAR)) {
                        evt.add(event)
                    }
                }

                m_events.value = evt
            }
        })
    }

    fun updateEvents() {
        getEventsFromDatabase()
    }

    private fun getEventsFromDatabase() {
        database.child("Data/event").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("ScheduleViewModel", "onCancelled called from database reference.")
            }

            override fun onDataChange(dataSnapshot: com.google.firebase.database.DataSnapshot) {
                val evt: MutableList<CEvent> = mutableListOf()

                val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val eventDate = Calendar.getInstance()


                for (eventSnapshot in dataSnapshot.children) {
                    val event: CEvent = eventSnapshot.getValue(CEvent::class.java)!!

                    eventDate.time = dateFormat.parse(event.date)

                    if (eventDate.get(Calendar.DAY_OF_YEAR) == m_currentDate.get(Calendar.DAY_OF_YEAR)) {
                        evt.add(event)
                    }
                }
                evt.sortBy { it.date }
                m_events.value = evt
            }
        })
    }

    private fun initDatesFromDatabase() {
        database.child("Data/conference").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("ScheduleViewModel", "Date is never")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (conferenceSnapshot in dataSnapshot.children) {
                    val conference: CConference = conferenceSnapshot.getValue(CConference::class.java)!!

                    val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                    m_startDate.time = dateFormat.parse(conference.startDate)
                    m_endDate.time = dateFormat.parse(conference.endDate)
                    Log.d("ScheduleViewModel", "Date updated")
                    m_currentDate.time = m_startDate.time

                    updateDates()
                }
            }
        })
    }

    private fun updateDates() {
        m_currentDateString.value = m_currentDate.get(Calendar.DAY_OF_MONTH).toString()
        m_currentMonthString.value = m_shortMonthNames[m_currentDate.get(Calendar.MONTH)]
        m_currentWeekdayString.value = m_shortWeekNames[m_currentDate.get(Calendar.DAY_OF_WEEK)]

        if (m_currentDate.compareTo(m_startDate) == 0) {
            currentLeftArrowIcon.value = R.drawable.ic_arrow_left_grayed
            m_isFirstDay.value = true
        } else {
            currentLeftArrowIcon.value = R.drawable.ic_arrow_left
            m_isFirstDay.value = false
        }
        if (m_currentDate.compareTo(m_endDate) == 0) {
            currentRightArrowIcon.value = R.drawable.ic_arrow_right_grayed
            m_isLastDay.value = true
        } else {
            currentRightArrowIcon.value = R.drawable.ic_arrow_right
            m_isLastDay.value = false
        }

        getEventsFromDatabase()
    }

    fun onPrevDayClicked() {
        if (m_isFirstDay.value != true) {
            m_currentDate.add(Calendar.DAY_OF_YEAR, -1)
            updateDates()
        }
    }

    fun onNextDayClicked() {
        if (m_isLastDay.value != true) {
            m_currentDate.add(Calendar.DAY_OF_YEAR, 1)
            updateDates()
        }
    }

    fun removeEvent(index: Int): Boolean {
        //Log.d("asd", m_events.value!![index].name)
        if (m_user == null || m_user?.level == 0) {

            return false
        }
        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference

        val currentEventId = m_events.value!![index].id

        ref.child("Data/event/$currentEventId").setValue(null)

        return true
    }

    fun initUserFromDatabase() {
        val logged_uid = FirebaseAuth.getInstance().uid

        database.child("model/user/$logged_uid").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("ScheduleViewModel", "onCancelled called from database reference.")
            }

            override fun onDataChange(dataSnapshot: com.google.firebase.database.DataSnapshot) {
                val usr: CUser? = dataSnapshot.getValue(CUser::class.java)

                m_user = usr
            }
        })
    }

}