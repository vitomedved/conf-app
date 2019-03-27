package com.example.confapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class RegistrationActivity : AppCompatActivity() {
    lateinit var username: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var registerBtn: Button
    lateinit var selectImgBtn: Button
    lateinit var alreadyHaveAcc: TextView
    lateinit var circleImg: CircleImageView
    lateinit var progress: ProgressDialog
    val DEFAULT_AVATAR_URL: String = "https://firebasestorage.googleapis.com/v0/b/conf-app-14914.appspot.com/o/images%2F16480.png?alt=media&token=e43cdb99-36cb-4305-b0d2-5b63d34fc7cd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        username = findViewById(R.id.editText_username_registration)
        email = findViewById(R.id.editText_email_registration)
        password = findViewById(R.id.editText_password_registration)
        registerBtn = findViewById(R.id.button_register_registration)
        selectImgBtn = findViewById(R.id.button_select_image_register)
        alreadyHaveAcc = findViewById(R.id.textView_alreadyHaveAcc_registration)

        FirebaseApp.initializeApp(this);

        registerBtn.setOnClickListener {
            //startLoadingCircle()
            performRegistration()
        }


        selectImgBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        alreadyHaveAcc.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            circleImg = findViewById(R.id.circleimageview_profile_image_registration)
            circleImg.setImageBitmap(bitmap)
            selectImgBtn.alpha = 0F
/*
            val bitmapDrawable = BitmapDrawable(bitmap)
            selectImgBtn.setBackgroundDrawable(bitmapDrawable)
*/
        }
    }

    private fun startLoadingCircle(){
        val progress = ProgressDialog(this)
        progress.setMessage("Please wait..")
        progress.setCancelable(false)
        progress.show()
    }

    private fun performRegistration() {

        if(email.text.toString().isEmpty() || password.text.toString().isEmpty()){
            Toast.makeText(this, "Invalid email/password", Toast.LENGTH_LONG).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener

                uploadImgToFirebaseStorage()
                Log.d("Regg", "USPJEEEH: ${it.result.user.uid}")
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImgToFirebaseStorage() {
        if(selectedPhotoUri == null){
            saveUserToFirebaseDatabase(DEFAULT_AVATAR_URL)

        }
        else{
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        saveUserToFirebaseDatabase(it.toString())
                        // Toast.makeText(this, "URL: $it", Toast.LENGTH_LONG).show()
                    }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed; exception: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed; exception: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }


    }

    private fun saveUserToFirebaseDatabase(avatar_url: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Data/user/$uid")
        val user = CUsers(uid,
                        avatar_url,
                        email.text.toString(),
                        username.text.toString(),
                        password.text.toString().hashCode().toString()  //nac bolji hash?
                        , listOf(-1, -2)
                        )
        ref.setValue(user)
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
