package com.example.confapp.user

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.confapp.R
import com.example.confapp.exhibitors.ExhibitorsRecyclerAdapter
import com.example.confapp.model.CExhibitor
import com.example.confapp.model.CUser
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import kotlinx.android.synthetic.main.fragment_exhibitors.view.*

class UserListFragmentAdmin : Fragment(){
    private lateinit var firebaseRef: Firebase
    private val users: MutableList<CUser> = mutableListOf()

    private lateinit var retView: View
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
/*
        // TODO: Odvojiti checkConnectivity funkciju iu NewEventFragment-a - pitaj Vitu za zeleno svijetlo
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        // TODO: kad se poveze na internet automatski reloadati ili kad se prebaci iz jednog fragmenta u drugi (onResume?)
        if(!isConnected) {
            retView = inflater.inflate(R.layout.fragment_no_connection, container, false)
            return retView
        }
*/
        retView = inflater.inflate(R.layout.fragment_exhibitors, container, false)

        recyclerView = retView.findViewById(R.id.recyclerView_exhibitors)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)

        retView.fragment_exhibitors_loading_animation.visibility = View.VISIBLE

        Firebase.setAndroidContext(this.context)

        firebaseRef = Firebase("https://conf-app-14914.firebaseio.com")

        firebaseRef.child("model/user").addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: FirebaseError?) {
                //Log.d("FIREBASE", "Events from database are not loaded.")
                return
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()

                for(eventSnapshot in dataSnapshot.children){
                    val user: CUser = eventSnapshot.getValue(CUser::class.java)
                    users.add(user)
                }

                //Log.d("FIREBASE", "Events loaded from database, adding adapter. Total events: " + exhibitors.size)

                retView.fragment_exhibitors_loading_animation.visibility = View.INVISIBLE
                val adapter = UsersRecyclerAdapter(users)
                recyclerView.adapter = adapter
            }

        })

        return retView
    }

}