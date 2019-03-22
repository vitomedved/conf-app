package com.example.confapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var loginBtn: Button
    lateinit var backToReg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.editText_email_login)
        password = findViewById(R.id.editText_password_login)
        loginBtn = findViewById(R.id.button_login_login)

        loginBtn.setOnClickListener {
            // TODO: implement login
        }


        backToReg = findViewById(R.id.textView_backToReg_login)
        backToReg.setOnClickListener {
            intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

    }
}
