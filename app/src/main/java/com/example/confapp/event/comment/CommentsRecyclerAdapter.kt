package com.example.confapp.event.comment

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.confapp.R
import com.example.confapp.model.CComment
import com.example.confapp.model.CUser
import com.example.confapp.user.UserProfileActivity
import kotlin.collections.forEach as forEach1




class CommentsRecyclerAdapter():  RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder>() {

    private var commentsList: MutableList<CComment> = mutableListOf()
    private var usersList: MutableList<CUser> = mutableListOf()

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

        var myUser: CUser = CUser("","", "", comment.author)
        for (user in usersList) {
            if (user.uid == comment.author){
                myUser = user
                // ovo prebaciti dolje kad pobrišemo milicu i branimira
                p0.commentAuthor.setOnClickListener {
                    val intent = Intent(p0.itemView.context, UserProfileActivity::class.java)
                    intent.putExtra("uid", myUser.uid)
                    p0.itemView.context.startActivity(intent)
                }
            }
        }

        p0.commentDate.text = comment.date
        p0.commentAuthor.text = myUser.name
        p0.commentContent.text = comment.content



    }

    fun updateUsers(newUsers: MutableList<CUser>){
        usersList.clear()
        usersList.addAll(newUsers)

        notifyDataSetChanged()
    }

    fun setData(comments: MutableList<CComment>){
        commentsList.clear()
        commentsList.addAll(comments)

        notifyDataSetChanged()
    }

}
