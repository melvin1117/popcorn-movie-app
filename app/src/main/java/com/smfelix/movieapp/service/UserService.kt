package com.smfelix.movieapp.service

import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.smfelix.movieapp.data.User

class UserService(private val authService: AuthService) {
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val storage = FirebaseStorage.getInstance().reference

    // Fetch user data from the database
    fun fetchUser(onComplete: (User?) -> Unit) {
        val userId = authService.getCurrentUser()?.uid
        if (userId == null) {
            onComplete(null)
            return
        }

        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                onComplete(user)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(null)
            }
        })
    }

    fun updateUserProfile(user: User, photoUri: Uri?, onComplete: (Boolean, String?) -> Unit) {
        val userId = user.userId
        if (photoUri != null) {
            val photoRef = storage.child("users/$userId/profile_photo.jpg")
            photoRef.putFile(photoUri).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        val updatedUser = user.copy(profilePhotoUrl = uri.toString())
                        saveUserProfile(updatedUser, onComplete)
                    }
                } else {
                    onComplete(false, task.exception?.localizedMessage)
                }
            }
        } else {
            saveUserProfile(user, onComplete)
        }
    }

    private fun saveUserProfile(user: User, onComplete: (Boolean, String?) -> Unit) {
        database.child(user.userId).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(true, null)
            } else {
                onComplete(false, task.exception?.localizedMessage)
            }
        }
    }

    fun updateUserFavorites(userId: String, favorites: List<Long>, onComplete: (Boolean) -> Unit) {
        val userFavoritesRef = database.child(userId).child("favorites")
        userFavoritesRef.setValue(favorites)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
