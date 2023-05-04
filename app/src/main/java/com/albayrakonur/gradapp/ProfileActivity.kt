package com.albayrakonur.gradapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.albayrakonur.gradapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userDetails: UserModel
    private var buttonClickCount = 0
    private lateinit var userDocID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = Firebase.firestore
        auth = Firebase.auth

        val user = auth.currentUser
        val userDB = db.collection("Users")

        val fullNameTextView = findViewById<EditText>(R.id.ProfileActivityFullName)
        val educationTextView = findViewById<EditText>(R.id.ProfileActivityEducation)
        val yearTextView = findViewById<EditText>(R.id.ProfileActivityYear)
        val workPlaceTextView = findViewById<EditText>(R.id.ProfileActivityWorkPlace)
        val emailTextView = findViewById<EditText>(R.id.ProfileActivityEmail)
        val numberTextView = findViewById<EditText>(R.id.ProfileActivityNumber)
        val photoImageView = findViewById<ImageView>(R.id.ProfileActivityPhoto)

        userDB.whereEqualTo("uid", user!!.uid).get().addOnSuccessListener {
            if (!it.isEmpty) {
                userDetails = convertToUserModel(it)
                userDocID = it.documents[0].id

                val editableFactory = Editable.Factory.getInstance()

                fullNameTextView.text = editableFactory.newEditable(userDetails.fullName)
                educationTextView.text = editableFactory.newEditable(userDetails.education)
                yearTextView.text =
                    editableFactory.newEditable(userDetails.entryYear + "-" + userDetails.gradYear)
                workPlaceTextView.text = editableFactory.newEditable(userDetails.workPlace)
                emailTextView.text = editableFactory.newEditable(userDetails.email)
                numberTextView.text = editableFactory.newEditable(userDetails.number)
                photoImageView.setImageBitmap(convertBase64ToImage(userDetails.photo))

            }

        }.addOnFailureListener {

        }


        val editButton = findViewById<Button>(R.id.editProfileButton)
        editButton.setOnClickListener {
            if (++buttonClickCount % 2 == 0) {
                editButton.text = "edit"

                fullNameTextView.isEnabled = false
                educationTextView.isEnabled = false
                yearTextView.isEnabled = false
                workPlaceTextView.isEnabled = false
                emailTextView.isEnabled = false
                numberTextView.isEnabled = false

                userDB.document(userDocID).update(
                    "fullName", fullNameTextView.text.toString(),
                    "education", educationTextView.text.toString(),
                    "entryYear", yearTextView.text.trim().substring(0, yearTextView.text.indexOf("-")).toString(),
                    "gradYear", yearTextView.text.trim().substring(yearTextView.text.indexOf("-") + 1).toString(),
                    "workPlace", workPlaceTextView.text.toString(),
                    "email", emailTextView.text.toString(),
                    "number", numberTextView.text.toString()
                )
            } else {
                editButton.text = "save"
                fullNameTextView.isEnabled = true
                educationTextView.isEnabled = true
                yearTextView.isEnabled = true
                workPlaceTextView.isEnabled = true
                emailTextView.isEnabled = true
                numberTextView.isEnabled = true

            }
        }
    }

    private fun convertBase64ToImage(base64String: String): Bitmap {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
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