package com.albayrakonur.gradapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.albayrakonur.gradapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userDetails: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = Firebase.firestore
        auth = Firebase.auth

        val user = auth.currentUser
        val userDB = db.collection("Users")

        val fullNameTextView = findViewById<TextView>(R.id.ProfileActivityFullName)
        val educationTextView = findViewById<TextView>(R.id.ProfileActivityEducation)
        val yearTextView = findViewById<TextView>(R.id.ProfileActivityYear)
        val workPlaceTextView = findViewById<TextView>(R.id.ProfileActivityWorkPlace)
        val emailTextView = findViewById<TextView>(R.id.ProfileActivityEmail)
        val numberTextView = findViewById<TextView>(R.id.ProfileActivityNumber)

        userDB.whereEqualTo("uid", user!!.uid).get().addOnSuccessListener {
            if (!it.isEmpty) {
                userDetails = convertToUserModel(it)

                fullNameTextView.text = userDetails.fullName
                educationTextView.text = userDetails.education
                yearTextView.text = userDetails.entryYear + "-" + userDetails.gradYear
                workPlaceTextView.text = userDetails.workPlace
                emailTextView.text = userDetails.email
                numberTextView.text = userDetails.number
            }

        }.addOnFailureListener {

        }


    }

    private fun convertToUserModel(snapshot: QuerySnapshot): UserModel {
        val userDetails = snapshot.documents[0]

        return UserModel(
            userDetails["uid"].toString(),
            userDetails["fullName"].toString(),
            userDetails["email"].toString(),
            userDetails["entryYear"].toString(),
            userDetails["gradYear"].toString(),
            userDetails["number"].toString(),
            userDetails["photo"].toString(),
            userDetails["education"].toString(),
            userDetails["workPlace"].toString()
        )
    }

    companion object {

        private val TAG = "DocSnippets"

        private val EXECUTOR = ThreadPoolExecutor(
            2,
            4,
            60,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
        )
    }
}