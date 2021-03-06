package com.example.confapp.exhibitors

import android.media.Image
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.confapp.R
import com.example.confapp.model.CExhibitor
import com.squareup.picasso.Picasso
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.CardView


class ExhibitorsRecyclerAdapter: RecyclerView.Adapter<ExhibitorsRecyclerAdapter.ViewHolder>() {

    var exhibitorsList: MutableList<CExhibitor> = mutableListOf()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val exhibitorCompany = itemView.findViewById(R.id.textView_exhibitorCompany) as TextView
        val exhibitorLogo = itemView.findViewById(R.id.imageView_exhibitorLogo) as ImageView
        val exhibitorWeb = itemView.findViewById(R.id.imageView_urlExhibitor) as ImageView
        val thisCodeIsSoBad = itemView.findViewById(R.id.list_item_exhibitor_layout) as CardView
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_item_exhibitor, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return exhibitorsList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val exhibitor: CExhibitor = exhibitorsList[p1]

        p0.exhibitorCompany.text = exhibitor.company
        Picasso.get().load(exhibitor.logo_url).into(p0.exhibitorLogo)

        p0.thisCodeIsSoBad.setOnClickListener {
            val uris = Uri.parse(exhibitor.web)
            val intents = Intent(Intent.ACTION_VIEW, uris)
            val b = Bundle()
            b.putBoolean("new_window", true)
            intents.putExtras(b)
            p0.itemView.context.startActivity(intents)
        }
    }

    fun setData(exhibitors: MutableList<CExhibitor>){
        exhibitorsList.clear()
        exhibitorsList.addAll(exhibitors)

        notifyDataSetChanged()
    }

}