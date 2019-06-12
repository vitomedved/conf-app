package com.example.confapp.schedule


import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.confapp.R
import com.example.confapp.event.NewEventActivity
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import java.util.*
import android.support.design.widget.CoordinatorLayout




class ScheduleFragment : Fragment() {
    private val adapter = ScheduleRecyclerAdapter()

    private lateinit var retView: View
    private lateinit var model: ScheduleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val isConnected = checkConnectivity()

        model = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)

        if(!isConnected)
        {
            retView = inflater.inflate(R.layout.fragment_no_connection, container, false)
            return retView
        }

        retView = inflater.inflate(R.layout.fragment_schedule, container, false)

        retView.findViewById<FloatingActionButton>(R.id.fab_addEvent).hide()

        retView.recyclerView_schedule.layoutManager = LinearLayoutManager(activity)// to fali

        retView.fragment_schedule_progress_bar.visibility = View.VISIBLE

        model.events.observe(this, android.arch.lifecycle.Observer {newEvents ->
            adapter.eventList = newEvents!!
            retView.recyclerView_schedule.adapter = adapter
            retView.fragment_schedule_progress_bar.visibility = View.INVISIBLE
        })

        model.user.observe(this, android.arch.lifecycle.Observer { newUser ->
            if(newUser?.level == 0){
                retView.fab_addEvent.show()
            }
        })

        model.currentDateString.observe(this, android.arch.lifecycle.Observer { newDate ->
            retView.textView_currentDate.text = newDate
        })

        model.currentMonthString.observe(this, android.arch.lifecycle.Observer { newMonth ->
            retView.textView_currentMonth.text = newMonth
        })

        model.currentWeekdayString.observe(this, android.arch.lifecycle.Observer { newWeekday ->
            retView.textView_currentWeekday.text = newWeekday
        })

        model.currentLeftArrowIcon.observe(this, android.arch.lifecycle.Observer { currentLeftArrowIcon ->
            if (currentLeftArrowIcon != null) {
                retView.imageButton_previousDay.setBackgroundResource(currentLeftArrowIcon)
            }
        })

        model.currentRightArrowIcon.observe(this, android.arch.lifecycle.Observer { currentRightArrowIcon ->
            if (currentRightArrowIcon != null) {
                retView.imageButton_nextDay.setBackgroundResource(currentRightArrowIcon)
            }
        })

        val swipeRefresh: SwipeRefreshLayout = retView.findViewById(R.id.swipeRefresh_schedule)
        swipeRefresh.setOnRefreshListener {
            if(checkConnectivity())
            {
                model.updateEvents()
            }
            else
            {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
            }
            swipeRefresh.isRefreshing = false
        }

        retView.imageButton_previousDay.setOnClickListener {
            model.onPrevDayClicked()
        }
        retView.imageButton_nextDay.setOnClickListener {
            model.onNextDayClicked()
        }

        retView.fab_addEvent.setOnClickListener {
            val intent = Intent(context, NewEventActivity::class.java)
            intent.putExtra(ScheduleViewModel.KEY_START_DATE, model.startDate.timeInMillis)
            intent.putExtra(ScheduleViewModel.KEY_END_DATE, model.endDate.timeInMillis)
            startActivity(intent)
        }

        return retView
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        super.onContextItemSelected(item)

        //Toast.makeText(context, "Clicked ${item!!.title}", Toast.LENGTH_SHORT).show()
        if(!checkConnectivity()){
            Toast.makeText(context, "Connect to the internet to remove event", Toast.LENGTH_LONG).show()
        }else{
            if(model.removeEvent(item!!.groupId)){
                Toast.makeText(context, "Event successfully removed", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context, "Insufficient permission", Toast.LENGTH_LONG).show()
            }
        }

        return true
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
