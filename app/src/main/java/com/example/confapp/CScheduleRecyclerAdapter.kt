package com.example.confapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class CScheduleRecyclerAdapter(val eventList: MutableList<CEvent>): RecyclerView.Adapter<CScheduleRecyclerAdapter.ViewHolder>() {

    enum class EventType(val value: String) {
        WORKSHOP("workshop"),
        PRESENTATION("presentation"),
        QA("q&a")
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val eventName = itemView.findViewById(R.id.textView_eventName) as TextView
        val eventTime = itemView.findViewById(R.id.textView_eventTime) as TextView
        val eventDuration = itemView.findViewById(R.id.textView_eventDuration) as TextView
        val eventLocation = itemView.findViewById(R.id.textView_eventLocation) as TextView
        val eventTypeImage = itemView.findViewById(R.id.imageView_eventType) as ImageView
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

        var time: String = event.date.substring(11)
        // TODO: replace with locale when calculating time (e.g. 'en-us', 'hr')
        time = time + " CET +0100"
        p0.eventTime.text = time

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val durationTime: Calendar = Calendar.getInstance()
        durationTime.time = dateFormat.parse(event.date)
        durationTime.add(Calendar.MINUTE, event.durationInMinutes)

        val duration: String = event.date.substring(11) + " - " + durationTime.get(Calendar.HOUR_OF_DAY).toString() + ":" + durationTime.get(Calendar.MINUTE).toString()
        p0.eventDuration.text = duration

        p0.eventLocation.text = event.hall

        when(event.type){
            EventType.WORKSHOP.value -> p0.eventTypeImage.setImageResource(R.drawable.ic_tag_workshop)
            EventType.PRESENTATION.value -> p0.eventTypeImage.setImageResource(R.drawable.ic_tag_presentation)
            EventType.QA.value -> p0.eventTypeImage.setImageResource(R.drawable.ic_tag_qa)
        }
    }
}