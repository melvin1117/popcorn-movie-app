package com.smfelix.movieapp.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.smfelix.movieapp.data.User

class AuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storage: StorageReference = FirebaseStorage.getInstance().reference

    // Check if a user is logged in
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Get user details
    fun getUserDetails(): DatabaseReference? {
        return auth.currentUser?.uid?.let { database.child("users").child(it) }
    }

    // Login user
    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.localizedMessage)
                }
            }
    }

    // Signup user and return userId
    fun signupUser(email: String, password: String, onComplete: (Boolean, String?, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    onComplete(true, userId, null)
                } else {
                    onComplete(false, null, task.exception?.localizedMessage)
                }
            }
    }

    // Logout user
    fun logoutUser(onComplete: (Boolean) -> Unit) {
        auth.signOut()
        onComplete(true)
    }

    // Update user profile in Firebase Realtime Database
    fun updateUserProfile(
        userId: String,
        user: User,
        onComplete: (Boolean, String?) -> Unit
    ) {
        database.child("users").child(userId).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.localizedMessage)
                }
            }
    }

    // Upload profile photo to Firebase Storage
    fun uploadProfilePhoto(userId: String, photoUri: String, onComplete: (Boolean, String?) -> Unit) {
        val photoRef = storage.child("users").child(userId).child("profile_photo.jpg")
        photoRef.putFile(android.net.Uri.parse(photoUri))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        onComplete(true, uri.toString())
                    }
                } else {
                    onComplete(false, task.exception?.localizedMessage)
                }
            }
    }
}
