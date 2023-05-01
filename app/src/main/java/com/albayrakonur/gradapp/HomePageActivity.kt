package com.albayrakonur.gradapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomePageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profileOption -> {
                val redirectToProfileIntent = Intent(this@HomePageActivity, ProfileActivity::class.java)
                startActivity(redirectToProfileIntent)
                true
            }
            R.id.logoutOption -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        auth.signOut()

        if (auth.currentUser == null) {
            val redirectToLoginIntent = Intent(this@HomePageActivity, LoginActivity::class.java)
            startActivity(redirectToLoginIntent)
        }
    }
}