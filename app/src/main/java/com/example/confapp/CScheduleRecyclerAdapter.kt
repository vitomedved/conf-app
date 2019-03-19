package com.example.confapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class CScheduleRecyclerAdapter(val eventList: MutableList<CEvent>): RecyclerView.Adapter<CScheduleRecyclerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val eventName = itemView.findViewById(R.id.textView_eventName) as TextView
        val eventDate= itemView.findViewById(R.id.textView_eventDate) as TextView
        val eventTime = itemView.findViewById(R.id.textView_eventTime) as TextView
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_item_event, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val event: CEvent = eventList[p1]

        p0.eventName.text = event.name
        p0.eventDate.text = event.date
        p0.eventTime.text = event.time
    }
}