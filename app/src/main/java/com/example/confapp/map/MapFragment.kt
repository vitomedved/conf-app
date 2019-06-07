package com.example.confapp.map

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.example.confapp.model.CEvent
import com.example.confapp.model.CUser
import com.firebase.client.*
import com.squareup.picasso.Picasso
import java.security.cert.CertPath

import com.example.confapp.exhibitors.ExhibitorsRecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.confapp.R
import kotlinx.android.synthetic.main.fragment_map.view.*


class MapFragment: Fragment() {


    private lateinit var retView: View
    private lateinit var hallBtn1: ImageView
    private lateinit var hallBtn2: ImageView
    private lateinit var hallBtn3: ImageView
    private lateinit var hallBtn4: ImageView
    private lateinit var hallBtn5: ImageView
    private lateinit var textDisp: TextView
    val events = mutableListOf<CEvent>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        retView = inflater.inflate(R.layout.fragment_map, container, false)

        hallBtn1 = retView.findViewById(R.id.imageButton_hall1_map)
        hallBtn2 = retView.findViewById(R.id.imageButton_hall2_map)
        hallBtn3 = retView.findViewById(R.id.imageButton_hall3_map)
        hallBtn4 = retView.findViewById(R.id.imageButton_hall4_map)
        hallBtn5 = retView.findViewById(R.id.imageButton_hall5_map)

        textDisp = retView.findViewById(R.id.textView_display_text_map)

        recyclerView = retView.findViewById(R.id.recyclerView_map)
        // OVO VJEROVATNO NIJE LINEAR !!!!!!! ili ipak je, zivot nema smisla
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)

        FirebaseDatabase.getInstance().reference.child("Data/event").ref.addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("EventViewModel", "User can not be loaded.")
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                events.clear()

                for(commentSnapshot in dataSnapshot.children){
                    val event: CEvent = commentSnapshot.getValue(CEvent::class.java)!!

                    events.add(event)
                }
                events.sortBy { it.date }

                hallBtn1.setOnClickListener{loadEvents(events,"Hall 1")}
                hallBtn2.setOnClickListener{loadEvents(events,"Hall 2")}
                hallBtn3.setOnClickListener{loadEvents(events,"Hall 3")}
                hallBtn4.setOnClickListener{loadEvents(events,"Hall 4")}
                hallBtn5.setOnClickListener{loadEvents(events,"Hall 5")}

            }
        })

        return retView
    }

    private fun loadEvents(events : MutableList<CEvent>, hall: String){
        val filteredEvents = mutableListOf<CEvent>()
        val iter = events.iterator()
        iter.forEach {
            if (it.hall == hall) { filteredEvents.add(it) }
        }
        textDisp.text = "Showing events in  ".plus(hall)
        val adapter = HallRecyclerAdapter(filteredEvents)
        recyclerView.adapter = adapter
    }

}