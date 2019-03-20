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
import android.support.v4.os.HandlerCompat.postDelayed





@Suppress("DEPRECATION")
class ScheduleFragment : Fragment() {

    private lateinit var firebaseRef: Firebase
    private val events: MutableList<CEvent> = mutableListOf()

    private lateinit var retView: View
    private lateinit var recyclerView: RecyclerView

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
            //swipeRefresh.isRefreshing = false


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
                    events.add(event)
                }

                Log.d("FIREBASE", "Events loaded from database, adding adapter. Total events: " + events.size)

                val adapter = CScheduleRecyclerAdapter(events)
                recyclerView.adapter = adapter
            }
        })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        val ft = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()

        //events.clear()
    }


}
