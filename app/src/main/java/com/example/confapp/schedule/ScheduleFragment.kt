package com.example.confapp.schedule


import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.firebase.client.Firebase
import com.example.confapp.model.CEvent
import com.example.confapp.R
import java.text.SimpleDateFormat
import java.util.*


class ScheduleFragment : Fragment() {
    private val adapter = ScheduleRecyclerAdapter()

    private lateinit var retView: View
    private lateinit var recyclerView: RecyclerView

    private lateinit var buttonPrevDay: ImageButton
    private lateinit var buttonNextDay: ImageButton

    private lateinit var textCurrentDate: TextView
    private lateinit var textCurrentMonth: TextView
    private lateinit var textCurrentWeekday: TextView

    private lateinit var eventDate: Calendar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val isConnected = checkConnectivity()

        val model = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)

        if(!isConnected)
        {
            retView = inflater.inflate(R.layout.fragment_no_connection, container, false)
            return retView
        }

        retView = inflater.inflate(R.layout.fragment_schedule, container, false)

        recyclerView = retView.findViewById(R.id.recyclerView_schedule)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        model.events.observe(this, android.arch.lifecycle.Observer {newEvents ->
            adapter.eventList = newEvents!!//model.getTodaysEvents()
            recyclerView.adapter = adapter
        })

        model.currentDateString.observe(this, android.arch.lifecycle.Observer { newDate ->
            textCurrentDate.text = newDate
        })

        model.currentMonthString.observe(this, android.arch.lifecycle.Observer { newMonth ->
            textCurrentMonth.text = newMonth
        })

        model.currentWeekdayString.observe(this, android.arch.lifecycle.Observer { newWeekday ->
            textCurrentWeekday.text = newWeekday
        })

        model.currentLeftArrowIcon.observe(this, android.arch.lifecycle.Observer { currentLeftArrowIcon ->
            if (currentLeftArrowIcon != null) {
                buttonPrevDay.setBackgroundResource(currentLeftArrowIcon)
            }
        })

        model.currentRightArrowIcon.observe(this, android.arch.lifecycle.Observer { currentRightArrowIcon ->
            if (currentRightArrowIcon != null) {
                buttonNextDay.setBackgroundResource(currentRightArrowIcon)
            }
        })

        val swipeRefresh: SwipeRefreshLayout = retView.findViewById(R.id.swipeRefresh_schedule)
        swipeRefresh.setOnRefreshListener {
            if(checkConnectivity())
            {
                /*Handler().postDelayed({
                    // Only use this if you want at least 2 secs of spinning shit
                    swipeRefresh.isRefreshing = false
                }, 2000)*/
                model.updateEvents()
            }
            else
            {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
            }
            swipeRefresh.isRefreshing = false
        }

        eventDate = Calendar.getInstance()

        textCurrentDate = retView.findViewById(R.id.textView_currentDate)
        textCurrentMonth = retView.findViewById(R.id.textView_currentMonth)
        textCurrentWeekday = retView.findViewById(R.id.textView_currentWeekday)

        buttonPrevDay = retView.findViewById(R.id.imageButton_previousDay)
        buttonNextDay = retView.findViewById(R.id.imageButton_nextDay)

        buttonPrevDay.setOnClickListener {
            model.onPrevDayClicked()
        }
        buttonNextDay.setOnClickListener {
            model.onNextDayClicked()
        }

        return retView
    }

    // Returns false if device is not connected to internet, true if device is connected to internet
    private fun checkConnectivity(): Boolean {
        var ret = false

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if(isConnected)
        {
            ret = true
        }
        return ret
    }



}
