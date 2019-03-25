package com.example.confapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.View
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.nav_header_main.*



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var firebaseTV: TextView
    lateinit var firebaseRef: Firebase
    var presenters: MutableList<CPresenter> = mutableListOf()
    lateinit var username_header: TextView
    lateinit var useremail_header: TextView
    lateinit var imageview_avatar_header: ImageView
    val DEFAULT_AVATAR_URL: String = "https://firebasestorage.googleapis.com/v0/b/conf-app-14914.appspot.com/o/images%2F16480.png?alt=media&token=e43cdb99-36cb-4305-b0d2-5b63d34fc7cd"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)


        Firebase.setAndroidContext(this)

        firebaseRef = Firebase("https://conf-app-14914.firebaseio.com")

        /*firebaseRef.child("Data/presenter").addValueEventListener(object: ValueEventListener {
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

        val fragmentAdapter = ViewPagerAdapter(supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = fragmentAdapter

        val tabs = findViewById<TabLayout>(R.id.tabLayout)
        tabs.setupWithViewPager(viewPager)

        var testtt = findViewById<NavigationView>(R.id.nav_view)
        var headd = testtt.getHeaderView(0)
        username_header = headd.findViewById(R.id.textView_username_nav_header)
        useremail_header = headd.findViewById(R.id.textView_userEmail_nav_header)
        imageview_avatar_header = headd.findViewById(R.id.imageView_avatar_nav_header)
        verifyUserIsLoggedIn()  //baci u onStart override?
    }


    private fun verifyUserIsLoggedIn() {

        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            username_header.text = "Login"
            useremail_header.visibility = View.INVISIBLE
            Picasso.get().load(DEFAULT_AVATAR_URL).into(imageview_avatar_header)

            username_header.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        else{
            firebaseRef.child("Data/user/${uid}").addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: FirebaseError?) {
                    Log.d("FIREBASE", "Data from database is not loaded.")
                    username_header.text = "logcat je super"
                    return
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val usr: CUsers = dataSnapshot.getValue(CUsers::class.java)

                    val url = usr.avatar_url
                    Picasso.get().load(url).into(imageview_avatar_header)
                    username_header.visibility = View.VISIBLE
                    useremail_header.visibility = View.VISIBLE
                    username_header.text = usr.name
                    useremail_header.text = usr.mail


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

        if (id == R.id.nav_activityFeed) {
            // Handle the camera action
        } else if (id == R.id.nav_schedule) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
