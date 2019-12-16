package com.example.confapp.event.favourite.recycler

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.confapp.R
import com.example.confapp.event.EventScrollingActivity
import com.example.confapp.model.CEvent
import com.example.confapp.schedule.ScheduleViewModel
import kotlinx.android.synthetic.main.list_item_event.view.*
import java.text.SimpleDateFormat
import java.util.*


class FavouriteEventAdapter : RecyclerView.Adapter<FavouriteEventAdapter.ViewHolder>() {

    var m_events: List<CEvent> = listOf()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(p0.context).inflate(
            R.layout.list_item_event, p0, false
        )
    )

    override fun getItemCount(): Int = m_events.size

    fun isDataEmpty() = m_events.isEmpty()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val event: CEvent = m_events[p1]

        p0.eventName.text = event.name

        val time: String = event.date

        val readingFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val outputFormat = SimpleDateFormat("dd.MM.yyyy., HH:mm")

        val date: Date = readingFormat.parse(time)

        p0.eventTime.text = outputFormat.format(date)

        p0.eventDuration.text = event.getTimeString()//duration

        p0.eventLocation.text = event.hall

        when(event.type){
            CEvent.EventType.WORKSHOP.value -> p0.eventTypeImage.setImageResource(
                R.drawable.ic_tag_workshop
            )
            CEvent.EventType.PRESENTATION.value -> p0.eventTypeImage.setImageResource(
                R.drawable.ic_tag_presentation
            )
            CEvent.EventType.QA.value -> p0.eventTypeImage.setImageResource(
                R.drawable.ic_tag_qa
            )
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val eventName = itemView.textView_eventName as TextView//itemView.findViewById(R.id.textView_eventName) as TextView
        val eventTime = itemView.textView_eventTime as TextView//itemView.findViewById(R.id.textView_eventTime) as TextView
        val eventDuration = itemView.textView_eventDuration as TextView//itemView.findViewById(R.id.textView_eventDuration) as TextView
        val eventLocation = itemView.textView_eventLocation as TextView//itemView.findViewById(R.id.textView_eventLocation) as TextView
        val eventTypeImage = itemView.imageView_eventType as ImageView//itemView.findViewById(R.id.imageView_eventType) as ImageView

        override fun onClick(v: View) {
            val intent = Intent(v.context, EventScrollingActivity::class.java).apply {
                putExtra(ScheduleViewModel.KEY_CURRENT_EVENT, m_events[adapterPosition])
            }
            v.context.startActivity(intent)
        }

        init{
            itemView.setOnClickListener(this)
        }
    }
}