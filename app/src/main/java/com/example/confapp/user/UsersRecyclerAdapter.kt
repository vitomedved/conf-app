package com.example.confapp.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.confapp.R
import com.example.confapp.exhibitors.ExhibitorsRecyclerAdapter
import com.example.confapp.model.CExhibitor
import com.example.confapp.model.CUser
import com.squareup.picasso.Picasso
import java.security.AccessController.getContext

class UsersRecyclerAdapter(val usersList: MutableList<CUser>): RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userName = itemView.findViewById(R.id.textView_exhibitorCompany) as TextView
        val userAvatar = itemView.findViewById(R.id.imageView_exhibitorLogo) as ImageView
        val banBtn = itemView.findViewById(R.id.imageView_urlExhibitor) as ImageView
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_item_exhibitor, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val user: CUser = usersList[p1]

        p0.userName.text = user.name
        Picasso.get().load(user.avatar_url).into(p0.userAvatar)

        //Picasso.get().load(R.drawable.ic_ban_user).into(p0.banBtn)


        p0.userName.setOnClickListener {

            val intent = Intent(it.getContext(), UserProfileActivity::class.java).putExtra("uid", user.uid)
            p0.itemView.context.startActivity(intent)
            /*
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("uid", user.uid)
            startActivity(intent)
            */

        }

        p0.banBtn.setOnClickListener {
            //implement ban
            p0.userName.text = "Banned!"
            /*
            val uris = Uri.parse(exhibitor.web)
            val intents = Intent(Intent.ACTION_VIEW, uris)
            val b = Bundle()
            b.putBoolean("new_window", true)
            intents.putExtras(b)
            p0.itemView.context.startActivity(intents)
            */
        }

    }

}