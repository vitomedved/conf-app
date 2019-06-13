package com.example.confapp.exhibitors

import android.arch.lifecycle.ViewModelProviders
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
import com.airbnb.lottie.LottieAnimationView
import com.example.confapp.R
import com.example.confapp.model.CExhibitor
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import kotlinx.android.synthetic.main.fragment_exhibitors.view.*

@Suppress("DEPRECATION")
class ExhibitorsFragment : Fragment() {

    private val adapter = ExhibitorsRecyclerAdapter()

    private lateinit var retView: View
    private lateinit var model: ExhibitorsViewModel

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

        model = ViewModelProviders.of(this).get(ExhibitorsViewModel::class.java)


        retView = inflater.inflate(R.layout.fragment_exhibitors, container, false)


        retView.recyclerView_exhibitors.layoutManager = LinearLayoutManager(activity)
        retView.fragment_exhibitors_loading_animation.visibility = View.VISIBLE


        model.exhibitors.observe(this, android.arch.lifecycle.Observer {newExhibitors ->
            adapter.exhibitorsList = (newExhibitors as MutableList<CExhibitor>)
            retView.recyclerView_exhibitors.adapter = adapter
            retView.fragment_exhibitors_loading_animation.visibility = View.INVISIBLE
        })

        return retView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        val ft = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()
    }



}