package com.example.confapp.event.comment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.confapp.R
import com.example.confapp.model.CComment


class CommentsRecyclerAdapter():  RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder>() {

    private var commentsList: MutableList<CComment> = mutableListOf()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val commentDate = itemView.findViewById(R.id.textView_commentDate) as TextView
        val commentAuthor = itemView.findViewById(R.id.textView_commentAuthor) as TextView
        val commentContent = itemView.findViewById(R.id.textView_commentContent) as TextView

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_item_comment, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val comment: CComment = commentsList[p1]
        p0.commentDate.text = comment.date
        p0.commentAuthor.text = comment.author
        p0.commentContent.text = comment.content
    }

    fun setData(comments: MutableList<CComment>){
        commentsList.clear()
        commentsList.addAll(comments)

        notifyDataSetChanged()//sec mozda je problem u xml-u id i to
    }//radi? ne crasha, ali ne prikazuje niš ajde ponovno

}
