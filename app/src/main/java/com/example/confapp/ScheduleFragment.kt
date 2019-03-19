package com.example.confapp


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.Fragment
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



@Suppress("DEPRECATION")
class ScheduleFragment : Fragment() {

    private lateinit var firebaseRef: Firebase
    private val events: MutableList<CEvent> = mutableListOf()

    private lateinit var retView: View
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //events.clear()

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if(!isConnected)
        {
            Toast.makeText(context, "Please connect to the internet", Toast.LENGTH_LONG).show()
            return inflater.inflate(R.layout.fragment_no_connection, container, false)
        }

        retView = inflater.inflate(R.layout.fragment_schedule, container, false)

        recyclerView = retView.findViewById(R.id.recyclerView_schedule)
        recyclerView.layoutManager = LinearLayoutManager(activity)//LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)

        Firebase.setAndroidContext(this.context)

        firebaseRef = Firebase("https://conf-app-14914.firebaseio.com")

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

        //val timestamp: Long = System.currentTimeMillis()
        return retView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        val ft = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()

        //events.clear()
    }


}
