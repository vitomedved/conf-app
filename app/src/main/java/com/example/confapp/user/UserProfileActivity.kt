package com.example.confapp.user

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageButton
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
import com.airbnb.lottie.LottieAnimationView
import com.example.confapp.login.LoginActivity
import com.example.confapp.login.RegistrationActivity
import de.hdodenhof.circleimageview.CircleImageView


class UserProfileActivity : AppCompatActivity(){
    lateinit var avatar: ImageView
    lateinit var avatar2: CircleImageView
    lateinit var name: TextView
    lateinit var type: TextView
    lateinit var mail: TextView
    lateinit var mail_icon: LottieAnimationView
    lateinit var date_joined: TextView
    lateinit var firebaseRef: Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(com.example.confapp.R.layout.popupwindowuser)

        avatar = findViewById(com.example.confapp.R.id.imageView_avatar_userpopup)
        avatar2 = findViewById(com.example.confapp.R.id.imageViewC_avatar_userpopup)
        name = findViewById(com.example.confapp.R.id.user_name)
        type = findViewById(com.example.confapp.R.id.user_type)
        mail = findViewById(com.example.confapp.R.id.user_email)
        mail_icon = findViewById(com.example.confapp.R.id.user_email_animation)
        date_joined = findViewById(com.example.confapp.R.id.date_joined_user)

        firebaseRef = Firebase("https://conf-app-14914.firebaseio.com")
        val uid = intent.getStringExtra("uid").toString()
        //Toast.makeText(this, uid, Toast.LENGTH_LONG).show()

        val logged_uid = FirebaseAuth.getInstance().uid

        firebaseRef.child("model/user/${uid}").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {
                Log.d("FIREBASE", "model from database is not loaded.")

                return
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usr: CUser = dataSnapshot.getValue(CUser::class.java)

                val url = usr.avatar_url
                //Picasso.get().load(url).into(avatar)

                Picasso.get().load(url).into(avatar2)
                name.text = usr.name
                mail.text = usr.mail
                date_joined.text = usr.date_joined

                if(usr.level == 9){
                    type.text = "Guest"
                }else if(usr.level == 0){
                    type.text = "Admin"
                }

                mail_icon.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/html"
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(usr.mail))
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Conf App")
                    intent.putExtra(Intent.EXTRA_TEXT, "Type message here")
                    startActivity(intent)
                }

                if(logged_uid == uid){
                    mail_icon.visibility = View.INVISIBLE
                }else{
                    mail_icon.visibility = View.VISIBLE
                }

            }
        })


        var dm : DisplayMetrics = DisplayMetrics()
        getWindowManager().defaultDisplay.getMetrics(dm)

        var width : Int = dm.widthPixels
        var height : Int = dm.heightPixels

        width = (width * 0.8).toInt()
        height = (height * 0.6).toInt() //malo vidjet kako se renderira // TODO promjeniti

        getWindow().setLayout(width, height)



    }

}