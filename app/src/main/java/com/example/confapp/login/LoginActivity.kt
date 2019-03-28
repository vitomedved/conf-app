package com.example.confapp.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.confapp.model.CUsers
import com.example.confapp.MainActivity
import com.example.confapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var loginBtn: Button
    lateinit var backToReg: TextView
    lateinit var gSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    private lateinit var auth: FirebaseAuth
    val GOOGLE_SIGN_IN_RC: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        email = findViewById(R.id.editText_email_login)
        password = findViewById(R.id.editText_password_login)
        loginBtn = findViewById(R.id.button_login_login)
        val loginBtnGoogle = findViewById<View>(R.id.button_google_login) as SignInButton
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        gSignInClient = GoogleSignIn.getClient(this, gso)
        loginBtn.setOnClickListener {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener {
                    Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        loginBtnGoogle.setOnClickListener {
            view: View -> signInGoogle()
        }


        backToReg = findViewById(R.id.textView_backToReg_login)
        backToReg.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

    }

    private fun signInGoogle() {
        val signinIntent: Intent = gSignInClient.signInIntent
        startActivityForResult(signinIntent, GOOGLE_SIGN_IN_RC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN_IN_RC){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            firebaseAuthWithGoogle(account!!)
        }catch (e: ApiException){
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Google sign in SUCCESS", Toast.LENGTH_LONG).show()
                    saveGoogleUserToFirebaseDatabase(account)
                }
                else{
                    Toast.makeText(this, "Error!!", Toast.LENGTH_LONG).show()
                }
                
            }

    }

    private fun saveGoogleUserToFirebaseDatabase(account: GoogleSignInAccount) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val databaseRef = FirebaseDatabase.getInstance().getReference("/model/user/$uid")

        val user: CUsers = CUsers(
            account.id!!,
            account.photoUrl.toString(),
            account.email!!,
            account.displayName!!,
            "-1"
            , listOf(-1, -2)
        )

        databaseRef.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Zapisano u bazu", Toast.LENGTH_LONG).show()

                //Handler().postDelayed({progress.dismiss()}, 5000)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed; exception: ${it.message}", Toast.LENGTH_LONG).show()
            }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
