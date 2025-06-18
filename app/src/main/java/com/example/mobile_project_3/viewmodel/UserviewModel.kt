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

    fun changePassword(newPassword: String, onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, task.exception?.message)
                    }
                }
        } else {
            onResult(false, "로그인 정보가 없습니다.")
        }
    }

    fun logout(onResult: () -> Unit = {}) {
        auth.signOut()
        onResult()
    }

    fun deleteAccount(onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        val uid = user?.uid
        if (user == null || uid == null) {
            onResult(false, "로그인 정보가 없습니다.")
            return
        }
        // 1. DB에서 사용자 정보 삭제
        database.child("users").child(uid).removeValue().addOnCompleteListener { dbTask ->
            if (dbTask.isSuccessful) {
                // 2. Auth에서 계정 삭제
                user.delete().addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, authTask.exception?.message)
                    }
                }
            } else {
                onResult(false, dbTask.exception?.message)
            }
        }
    }

}
