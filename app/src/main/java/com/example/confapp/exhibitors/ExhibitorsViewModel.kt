package com.example.confapp.exhibitors

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.confapp.model.CExhibitor
import com.firebase.client.DataSnapshot
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener

import com.google.firebase.database.*


class ExhibitorsViewModel : ViewModel() {

    private lateinit var database: DatabaseReference

    private val m_exhibitors = MutableLiveData<List<CExhibitor>>()
    val exhibitors: LiveData<List<CExhibitor>>
        get() = m_exhibitors


    init {
        database = FirebaseDatabase.getInstance().reference
        initExhibitorsFromDatabase()
    }

    private fun initExhibitorsFromDatabase() {
        database.child("Data/exhibitor").addValueEventListener(object : ValueEventListener,
            com.google.firebase.database.ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {
                return
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val exhibitorList: MutableList<CExhibitor> = mutableListOf()

                for(eventSnapshot in p0!!.children){
                    val exhibitor: CExhibitor = eventSnapshot.getValue(CExhibitor::class.java)!!
                    exhibitorList.add(exhibitor)
                }

                m_exhibitors.value = exhibitorList
            }


            // ovo dolje je useless, ali baca error bez
            override fun onCancelled(p0: DatabaseError) {
                    return
            }

            override fun onDataChange(p0: com.google.firebase.database.DataSnapshot) {
                val exhibitorList: MutableList<CExhibitor> = mutableListOf()

                for(eventSnapshot in p0.children){
                    val exhibitor: CExhibitor = eventSnapshot.getValue(CExhibitor::class.java)!!
                    exhibitorList.add(exhibitor)
                }

                m_exhibitors.value = exhibitorList
            }


        })

    }


}