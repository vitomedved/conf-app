package com.example.confapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class RegistrationActivity : AppCompatActivity() {
    lateinit var username: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var registerBtn: Button
    lateinit var alreadyHaveAcc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        username = findViewById(R.id.editText_username_registration)
        email = findViewById(R.id.editText_email_registration)
        password = findViewById(R.id.editText_password_registration)
        registerBtn = findViewById(R.id.button_register_registration)
        alreadyHaveAcc = findViewById(R.id.textView_alreadyHaveAcc_registration)


        registerBtn.setOnClickListener {

        }

        alreadyHaveAcc.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
