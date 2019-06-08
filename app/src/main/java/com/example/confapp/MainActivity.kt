package com.example.confapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.example.confapp.event.favourite.FavouriteEventFragment
import com.example.confapp.exhibitors.ExhibitorsFragment
import com.example.confapp.aboutconf.AboutFragment
import com.example.confapp.model.CPresenter
import com.example.confapp.model.CUser
import com.example.confapp.login.LoginActivity
import com.example.confapp.map.MapFragment
import com.example.confapp.schedule.ScheduleFragment
import com.example.confapp.user.UserProfileActivity
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var firebaseTV: TextView
    lateinit var firebaseRef: Firebase
    var presenters: MutableList<CPresenter> = mutableListOf()
    lateinit var username_header: TextView
    lateinit var useremail_header: TextView
    lateinit var imageview_avatar_header: ImageView
    lateinit var login_signout_textView: TextView
    val DEFAULT_AVATAR_URL: String =
        "https://firebasestorage.googleapis.com/v0/b/conf-app-14914.appspot.com/o/images%2F16480.png?alt=media&token=e43cdb99-36cb-4305-b0d2-5b63d34fc7cd"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        /*val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

        }*/

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)


        // odaberi koji fragment zelis da se prikazuje prvi
        var fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, AboutFragment()).commit()


        Firebase.setAndroidContext(this)

        firebaseRef = Firebase("https://conf-app-14914.firebaseio.com")

        /*firebaseRef.child("model/presenter").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {
                return
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(presenterSnapshot in dataSnapshot.children){
                    val presenter: CPresenter = presenterSnapshot.getValue(CPresenter::class.java)
                    presenters.add(presenter)

                    firebaseTV.text = presenter.name
                }
            }
        })*/


        var testtt = findViewById<NavigationView>(R.id.nav_view)
        var headd = testtt.getHeaderView(0)
        login_signout_textView = headd.findViewById(R.id.textView_log_in_sign_out_nav_header)
        username_header = headd.findViewById(R.id.textView_username_nav_header)
        useremail_header = headd.findViewById(R.id.textView_userEmail_nav_header)
        imageview_avatar_header = headd.findViewById(R.id.imageView_avatar_nav_header)
        verifyUserIsLoggedIn()  //baci u onStart override?

        val uid = FirebaseAuth.getInstance().uid

        imageview_avatar_header.setOnClickListener {
            if (uid != null){
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra("uid", uid)
                startActivity(intent)
            }else{
                makeAlert()
            }

        }

    }

    private fun makeAlert(){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("User Profile")
            .setMessage("To see user profile you need to be logged in, would you like to log in now?")
            .setPositiveButton("YES"){dialog, which ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            .setNeutralButton("Cancel"){dialog, which ->
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()

    }


    private fun verifyUserIsLoggedIn() {

        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            login_signout_textView.text = "Log in"
            username_header.visibility = View.INVISIBLE
            useremail_header.visibility = View.INVISIBLE
            Picasso.get().load(DEFAULT_AVATAR_URL).into(imageview_avatar_header)

            login_signout_textView.setOnClickListener {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        } else {
            login_signout_textView.text = "Sign out"
            firebaseRef.child("model/user/${uid}").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: FirebaseError?) {
                    Log.d("FIREBASE", "model from database is not loaded.")
                    username_header.text = "logcat je super - nes nevalja" //smislit pametnije
                    return
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val usr: CUser = dataSnapshot.getValue(CUser::class.java)

                    val url = usr.avatar_url
                    Picasso.get().load(url).into(imageview_avatar_header)
                    username_header.visibility = View.VISIBLE
                    useremail_header.visibility = View.VISIBLE
                    username_header.text = usr.name
                    useremail_header.text = usr.mail

                    login_signout_textView.setOnClickListener {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }


                }
            })
        }
    }


    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        // TODO: use when(id) {} instead of if-else cases, it's kotlin
        if (id == R.id.nav_activityFeed) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AboutFragment())
                .commit()
        } else if (id == R.id.nav_schedule) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ScheduleFragment()).commit()
        } else if (id == R.id.nav_about) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AboutFragment()).commit()
        } else if (id == R.id.nav_exhibitors) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ExhibitorsFragment()).commit()

        } else if (id == R.id.nav_share) {

            val sendIntent: Intent = Intent().apply {

                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out this developer! \nhttps://www.linkedin.com/in/vito-medved-48aa78135/"
                )
                type = "text/plain"
            }
            startActivity(sendIntent)

        } else if (id == R.id.nav_send) {
            sendMail()
        } else if (R.id.nav_favourite_event == id) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FavouriteEventFragment())
                .commit()
        } else if (R.id.nav_map == id) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MapFragment())
                .commit()
        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun sendMail(){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("rasanko@gmail.com", "matea.ignatoski@gmail.com"/*, sandi.ljubic@riteh.hr*/))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Conf App")
        intent.putExtra(Intent.EXTRA_TEXT, "Type message here")
        startActivity(intent)
    }
}
