package com.example.mobile_project_3.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    fun loadFavoritesFromFirebase(onLoaded: (Set<String>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onLoaded(emptySet())
        val favRef = database.child("users").child(uid).child("favorite")

        favRef.get().addOnSuccessListener { snapshot ->
            val favIds = snapshot.children.mapNotNull { it.key }.toSet()
            onLoaded(favIds)
        }.addOnFailureListener {
            onLoaded(emptySet())
        }
    }

    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener onResult(false)
                    val user = mapOf(
                        "email" to email,
                        "favorite" to emptyMap<String, Boolean>()
                    )
                    database.child("users").child(uid).setValue(user)
                        .addOnSuccessListener { onResult(true) }
                        .addOnFailureListener { onResult(false) }
                } else {
                    Log.e("create", "회원가입 실패: ${task.exception?.message}")
                    onResult(false)
                }
            }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            onResult(task.isSuccessful)
        }


        fun deleteUser(onResult: (Boolean) -> Unit) {
            val user = auth.currentUser
            val uid = user?.uid ?: return onResult(false)

            database.child("users").child(uid).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.delete()
                            .addOnCompleteListener { auth ->
                                onResult(auth.isSuccessful)
                            }
                            .addOnFailureListener { onResult(false) }
                    } else {
                        onResult(false)
                    }
                }
        }
    }

    fun addFavorite(facilityId: String, onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false)
        val userRef = database.child("users").child(uid).child("favorite")

        userRef.child(facilityId).setValue(true)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun removeFavorite(facilityId: String, onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false)
        val userRef = database.child("users").child(uid).child("favorite")

        userRef.child(facilityId).removeValue()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

}
