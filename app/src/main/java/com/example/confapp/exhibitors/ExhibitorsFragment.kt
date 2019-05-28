package com.example.confapp.exhibitors

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.confapp.R
import com.example.confapp.model.CExhibitor
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener

@Suppress("DEPRECATION")
class ExhibitorsFragment : Fragment() {

    private lateinit var firebaseRef: Firebase
    private val exhibitors: MutableList<CExhibitor> = mutableListOf()

    private lateinit var retView: View
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // TODO: Odvojiti checkConnectivity funkciju iu NewEventFragment-a - pitaj Vitu za zeleno svijetlo
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        // TODO: kad se poveze na internet automatski reloadati ili kad se prebaci iz jednog fragmenta u drugi (onResume?)
        if(!isConnected) {
            retView = inflater.inflate(R.layout.fragment_no_connection, container, false)
            return retView
        }

        retView = inflater.inflate(R.layout.fragment_exhibitors, container, false)

        recyclerView = retView.findViewById<RecyclerView>(R.id.recyclerView_exhibitors)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)

        Firebase.setAndroidContext(this.context)

        firebaseRef = Firebase("https://conf-app-14914.firebaseio.com")

        firebaseRef.child("Data/exhibitor").addValueEventListener(object :ValueEventListener {

            override fun onCancelled(p0: FirebaseError?) {
                //Log.d("FIREBASE", "Events from database are not loaded.")
                return
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                exhibitors.clear()

                for(eventSnapshot in dataSnapshot.children){
                    val exhibitor: CExhibitor = eventSnapshot.getValue(CExhibitor::class.java)
                    exhibitors.add(exhibitor)
                }

                //Log.d("FIREBASE", "Events loaded from database, adding adapter. Total events: " + exhibitors.size)

                val adapter = ExhibitorsRecyclerAdapter(exhibitors)
                recyclerView.adapter = adapter
            }

        })

        return retView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        val ft = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()
    }



}