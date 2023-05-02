package com.albayrakonur.gradapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userPhotoUri: Uri
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        setupDB()

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            createAccount()
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                userPhotoUri = uri
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        val pickPhotoButton = findViewById<Button>(R.id.buttonPickPhoto)
        pickPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setupDB() {
        // [START get_firestore_instance]
        db = Firebase.firestore
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
        // [END set_firestore_settings]
    }

    private fun createAccount() {
        // [START create_user_with_email]

        var email = findViewById<EditText>(R.id.editTextEmail).text.toString()
        var password = findViewById<EditText>(R.id.editTextPassword).text.toString()
        var entryYear = findViewById<EditText>(R.id.editTextEntryYear).text.toString()
        var gradYear = findViewById<EditText>(R.id.editTextGradYear).text.toString()
        var fullName = findViewById<EditText>(R.id.editTextFullName).text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext,
                        "Successful!",
                        Toast.LENGTH_SHORT,
                    ).show()

                    val profileUpdates = userProfileChangeRequest {
                        displayName = fullName
                        photoUri = userPhotoUri
                    }

                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User profile updated.")

                                val signUpIntent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                startActivity(signUpIntent)

                            } else {
                                Log.d(TAG, "Error occurred while updating user profile!")
                            }
                        }

                    val dbUser = hashMapOf(
                        "uid" to  user.uid.toString(),
                        "fullName" to fullName,
                        "email" to user.email,
                        "entryYear" to entryYear,
                        "gradYear" to gradYear,
                        "photo" to userPhotoUri.toString()
                    )

                    db.collection("GradAppDB")
                        .add(dbUser)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }

                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    private fun reload() {
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}