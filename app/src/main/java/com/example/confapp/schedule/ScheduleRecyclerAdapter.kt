package com.example.confapp.schedule

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.confapp.model.CEvent
import com.example.confapp.R
import com.example.confapp.event.EventScrollingActivity
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import kotlinx.android.synthetic.main.list_item_event.view.*
import java.text.SimpleDateFormat
import java.util.*



class ScheduleRecyclerAdapter(/*var eventList: List<CEvent>*/): RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder>() {

    var eventList: List<CEvent> = listOf()
    //val events: MutableList<CEvent> = mutableListOf()

    class ViewHolder(itemView: View, event: List<CEvent>): RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnCreateContextMenuListener{

        val evt = event
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(this.adapterPosition, v!!.id, 0, "Remove")
        }

        override fun onClick(v: View) {
            val intent = Intent(v.context, EventScrollingActivity::class.java).apply {
                putExtra(ScheduleViewModel.KEY_CURRENT_EVENT, evt[adapterPosition])
            }
            v.context.startActivity(intent)

        }

        val eventName = itemView.textView_eventName as TextView//itemView.findViewById(R.id.textView_eventName) as TextView
        val eventTime = itemView.textView_eventTime as TextView//itemView.findViewById(R.id.textView_eventTime) as TextView
        val eventDuration = itemView.textView_eventDuration as TextView//itemView.findViewById(R.id.textView_eventDuration) as TextView
        val eventLocation = itemView.textView_eventLocation as TextView//itemView.findViewById(R.id.textView_eventLocation) as TextView
        val eventTypeImage = itemView.imageView_eventType as ImageView//itemView.findViewById(R.id.imageView_eventType) as ImageView

        init{
            itemView.setOnClickListener(this)
            itemView.setOnCreateContextMenuListener(this)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_item_event, p0, false)
        return ViewHolder(view, eventList)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val event: CEvent = eventList[p1]

        p0.eventName.text = event.name

        var time: String = event.date.substring(11)
        // TODO: add timezones
        time = time + "h"
        p0.eventTime.text = time

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
}