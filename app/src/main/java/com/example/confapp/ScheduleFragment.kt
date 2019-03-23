package com.example.confapp


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class ScheduleFragment : Fragment() {

    private lateinit var firebaseRef: Firebase
    private val events: MutableList<CEvent> = mutableListOf()
    private val adapter = ScheduleRecyclerAdapter(events)

    private lateinit var retView: View
    private lateinit var recyclerView: RecyclerView

    private lateinit var buttonPrevDay: ImageButton
    private lateinit var buttonNextDay: ImageButton

    private lateinit var textCurrentDate: TextView
    private lateinit var textCurrentMonth: TextView
    private lateinit var textCurrentWeekday: TextView


    private lateinit var startDate: Calendar
    private lateinit var endDate: Calendar
    private lateinit var currentDate: Calendar

    private lateinit var eventDate: Calendar

    private val shortWeekNames = DateFormatSymbols.getInstance().shortWeekdays
    private val shortMonthNames = DateFormatSymbols.getInstance().shortMonths

    private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    private lateinit var eventListItem: LinearLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val isConnected = checkConnectivity()

        if(!isConnected)
        {
            return inflater.inflate(R.layout.fragment_no_connection, container, false)
        }

        retView = inflater.inflate(R.layout.fragment_schedule, container, false)

        recyclerView = retView.findViewById(R.id.recyclerView_schedule)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        Firebase.setAndroidContext(this.context)
        firebaseRef = Firebase("https://conf-app-14914.firebaseio.com")

        getConferenceDays()

        updateData()

        val swipeRefresh: SwipeRefreshLayout = retView.findViewById(R.id.swipeRefresh_schedule)
        swipeRefresh.setOnRefreshListener {
            if(checkConnectivity())
            {
                Handler().postDelayed({
                    updateData()
                    swipeRefresh.isRefreshing = false
                }, 2000)

            }
            else
            {
                //inflater.inflate(R.layout.fragment_no_connection, container, false)
                Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
                swipeRefresh.isRefreshing = false
            }
        }

        startDate = Calendar.getInstance()
        endDate = Calendar.getInstance()
        currentDate = Calendar.getInstance()
        eventDate = Calendar.getInstance()

        textCurrentDate = retView.findViewById(R.id.textView_currentDate)
        textCurrentMonth = retView.findViewById(R.id.textView_currentMonth)
        textCurrentWeekday = retView.findViewById(R.id.textView_currentWeekday)

        buttonPrevDay = retView.findViewById(R.id.imageButton_previousDay)
        buttonNextDay = retView.findViewById(R.id.imageButton_nextDay)

        buttonPrevDay.setOnClickListener {
            if(currentDate.compareTo(startDate) > 0){
                /*if(currentDate.compareTo(endDate) == 0){
                    buttonNextDay.setBackgroundResource(R.drawable.ic_arrow_right)
                }*/
                // Add -1 day
                currentDate.add(Calendar.DAY_OF_YEAR, -1)

                /*if(currentDate.compareTo(startDate) == 0){
                    buttonPrevDay.setBackgroundResource(R.drawable.ic_arrow_left_grayed)
                }*/

                updateDate()
                updateData()
            }
        }
        buttonNextDay.setOnClickListener {
            if(currentDate.compareTo(endDate) < 0){

                /*if(currentDate.compareTo(startDate) == 0){
                    buttonPrevDay.setBackgroundResource(R.drawable.ic_arrow_left)
                }*/

                currentDate.add(Calendar.DATE, 1)

                /*if(currentDate.compareTo(startDate) == 0){
                    buttonNextDay.setBackgroundResource(R.drawable.ic_arrow_right_grayed)
                }*/

                updateDate()
                updateData()
            }
        }

        return retView
    }

    private fun getConferenceDays() {
        firebaseRef.child("Data/conference").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {
                Log.d("FIREBASE", "Data from database is not loaded.")
                return
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                events.clear()
                for(conferenceSnapshot in dataSnapshot.children){
                    val conference: CConference = conferenceSnapshot.getValue(CConference::class.java)
                    //val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                    startDate.time = dateFormat.parse(conference.startDate)
                    endDate.time = dateFormat.parse(conference.endDate)

                    currentDate.time = startDate.time

                    updateDate()
                }
            }
        })
    }

    private fun updateDate() {

        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)

        startDate.set(Calendar.HOUR_OF_DAY, 0)
        startDate.set(Calendar.MINUTE, 0)

        endDate.set(Calendar.HOUR_OF_DAY, 0)
        endDate.set(Calendar.MINUTE, 0)

        if(currentDate.compareTo(startDate) > 0){
            buttonPrevDay.setBackgroundResource(R.drawable.ic_arrow_left)
        }else{
            buttonPrevDay.setBackgroundResource(R.drawable.ic_arrow_left_grayed)
        }

        if(currentDate.compareTo(endDate) < 0){
            buttonNextDay.setBackgroundResource(R.drawable.ic_arrow_right)
        }else{
            buttonNextDay.setBackgroundResource(R.drawable.ic_arrow_right_grayed)
        }

        textCurrentDate.text = currentDate.get(Calendar.DAY_OF_MONTH).toString()
        textCurrentMonth.text = shortMonthNames[currentDate.get(Calendar.MONTH)]
        textCurrentWeekday.text = shortWeekNames[currentDate.get(Calendar.DAY_OF_WEEK)]
    }

    // Returns false if device is not connected to internet, true if device is connected to internet
    private fun checkConnectivity(): Boolean {
        var ret = false

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if(isConnected)
        {
            //Toast.makeText(context, "Please connect to the internet", Toast.LENGTH_LONG).show()
            //return inflater.inflate(R.layout.fragment_no_connection, container, false)
            ret = true
        }
        return ret
    }

    private fun updateData() {
        firebaseRef.child("Data/event").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {
                Log.d("FIREBASE", "Events from database are not loaded.")
                return
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                events.clear()
                for(eventSnapshot in dataSnapshot.children){
                    val event: CEvent = eventSnapshot.getValue(CEvent::class.java)

                    eventDate.time = dateFormat.parse(event.date)
                    if(eventDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR))
                    {
                        events.add(event)
                        Log.d("ADDED_EVENTS: ", events.size.toString())
                    }
                }

                //Log.d("FIREBASE", "Events loaded from database, adding adapter. Total events: " + events.size)

                events.sortBy { it.date }
                adapter.eventList = events
                recyclerView.adapter = adapter
            }
        })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        //val ft = fragmentManager!!.beginTransaction()
        //ft.detach(this).attach(this).commit()
    }


}
