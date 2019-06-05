package com.example.confapp.user

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.confapp.model.CUser
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

import android.widget.ImageView
import com.example.confapp.login.LoginActivity
import com.example.confapp.login.RegistrationActivity


class UserProfileActivity : AppCompatActivity(){
    lateinit var avatar: ImageView
    lateinit var name: TextView
    lateinit var type: TextView
    lateinit var firebaseRef: Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(com.example.confapp.R.layout.popupwindowuser)

        avatar = findViewById(com.example.confapp.R.id.imageView_avatar_userpopup)

        name = findViewById(com.example.confapp.R.id.user_name)
        type = findViewById(com.example.confapp.R.id.user_type)

        firebaseRef = Firebase("https://conf-app-14914.firebaseio.com")
        val uid = intent.getStringExtra("uid").toString()
        //Toast.makeText(this, uid, Toast.LENGTH_LONG).show()

        firebaseRef.child("model/user/${uid}").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {
                Log.d("FIREBASE", "model from database is not loaded.")

                return
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usr: CUser = dataSnapshot.getValue(CUser::class.java)

                val url = usr.avatar_url
                Picasso.get().load(url).into(avatar)

                name.text = usr.name
                if(usr.level == 9){
                    type.text = "Guest"
                }else{
                    type.text = "sanko je sjebo.."
                }

            }
        })


        var dm : DisplayMetrics = DisplayMetrics()
        getWindowManager().defaultDisplay.getMetrics(dm)

        var width : Int = dm.widthPixels
        var height : Int = dm.heightPixels

        width = (width * 0.8).toInt()
        height = (height * 0.6).toInt()

        getWindow().setLayout(width, height)



    }

}