package com.example.confapp.event

//import android.app.DatePickerDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import com.example.confapp.model.CComment
import com.example.confapp.model.CEvent
import com.example.confapp.model.CExhibitor
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class NewEventViewModel : ViewModel(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var m_pickedDateTime = Calendar.getInstance()

    private var m_pickedDateString = MutableLiveData<String>()
    val pickedDate: LiveData<String>
        get() = m_pickedDateString

    lateinit var selectedPresentersArray: BooleanArray

    private var m_pickedTimeString = MutableLiveData<String>()
    val pickedTime: LiveData<String>
        get() = m_pickedTimeString

    private var m_exhibitors = MutableLiveData<List<CExhibitor>>()
    val exhibitors: LiveData<List<CExhibitor>>
        get() = m_exhibitors

    private lateinit var database: DatabaseReference

    //var boolArr = BooleanArray(m_exhibitors.value!!.size)
    fun initBoolArr() {
        selectedPresentersArray = BooleanArray(m_exhibitors.value!!.size)
        for (i in 0 until m_exhibitors.value!!.size) {
            selectedPresentersArray[i] = false
        }
    }

    fun getPresenters() {
        database = FirebaseDatabase.getInstance().reference

        database.child("Data/exhibitor").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("NewEventViewModel", "onCancelled called from database reference.")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val exhibs: MutableList<CExhibitor> = mutableListOf()

                for (eventSnapshot in dataSnapshot.children) {
                    val exhibitor: CExhibitor = eventSnapshot.getValue(CExhibitor::class.java)!!
                    exhibs.add(exhibitor)
                }
                m_exhibitors.value = exhibs
            }
        })
    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        m_pickedDateTime.set(Calendar.YEAR, year)
        m_pickedDateTime.set(Calendar.MONTH, monthOfYear + 1)
        m_pickedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        m_pickedDateString.value = "%02d/%02d/%04d".format(dayOfMonth, monthOfYear, year)
    }

    fun onAddEventClick(
        name: String,
        about: String,
        date: String,
        time: String,
        hall: String,
        type: String,
        duration: String,
        presenterIds: MutableList<String>
    ): Boolean {
        // TODO: Later maybe add some more checking instead of only empty line, but it is okay like this for now.
        if (name == "" || date == "" || time == "" || hall == "" || type == "" || duration == "") {
            return false
        }

        when (presenterIds.size) {
            0 -> {
                presenterIds.add("-1")
                presenterIds.add("-2")
            }
            1 -> {
                presenterIds.add(0, "-1")
            }
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val eventDate = Calendar.getInstance()
        eventDate.time = dateFormat.parse(date)
        eventDate.set(Calendar.MONTH, eventDate.get(Calendar.MONTH) + 1)

        val timeFormat = SimpleDateFormat("HH:mm")
        val eventTime = Calendar.getInstance()
        eventTime.time = timeFormat.parse(time)

        eventDate.set(Calendar.HOUR_OF_DAY, eventTime.get(Calendar.HOUR_OF_DAY))
        eventDate.set(Calendar.MINUTE, eventTime.get(Calendar.MINUTE))

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val stringDateTime = sdf.format(eventDate.time)

        // val comment = CComment(-1, "dummy", "dummy", "dummy")
        val eventUid = UUID.randomUUID().toString()
        val event = CEvent(
            about, stringDateTime, duration.toInt(), hall, eventUid, name, presenterIds,
            listOf("-1", "-1"), type.toLowerCase()
        )

        saveEventToDatabase(event)

        return true
    }

    private fun saveEventToDatabase(event: CEvent) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
        val key = ref.child("Data/event").push().key
        event.id = key!!

        val presenters: MutableList<String> = mutableListOf()
        for (i in 0 until selectedPresentersArray.size) {
            if (selectedPresentersArray[i] == true) {
                presenters.add(m_exhibitors.value!![i].id)
            }
        }

        val evtToSave = CEvent(
            event.about,
            event.date,
            event.durationInMinutes,
            event.hall,
            event.id,
            event.name,
            presenters,
            event.comments,
            event.type
        )

        ref.child("Data/event/$key").setValue(evtToSave)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        m_pickedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        m_pickedDateTime.set(Calendar.MINUTE, minute)

        //TODO: here
        m_pickedTimeString.value = "%02d:%02d".format(hourOfDay, minute)
    }
}
