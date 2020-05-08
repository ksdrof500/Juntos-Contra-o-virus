package com.juntoscontracorona.ui.needed

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.juntoscontracorona.MainActivity
import com.juntoscontracorona.model.User
import com.juntoscontracorona.util.PreferencesDataSource
import java.util.*

class UserViewModel : ViewModel() {

    private val TAG = "PerfilViewModel"

    val getUser =  MutableLiveData<User>()
    val getFeedback =  MutableLiveData<Boolean>()
    val getFeedbackLoading =  MutableLiveData<Boolean>()

    init {
        get()
    }

    fun save(user: User, context: Context) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid->

            val latitude = MainActivity.location?.latitude ?: 0
            val longitude = MainActivity.location?.longitude ?: 0
            val updates = hashMapOf<String, Any>(
                "help" to user.help.toString(),
                "phone" to user.phone,
                "latLong" to GeoPoint(latitude as Double, longitude as Double),
                "name" to user.name,
                "pharmacy" to user.pharmacy,
                "store" to user.store,
                "talk" to user.talk,
                "date" to Timestamp(Date())
            )
            FirebaseFirestore.getInstance().collection("user")
                .document(uid)
                .update(updates)
                .addOnSuccessListener {
                    getFeedback.postValue(true)
                    PreferencesDataSource(context).set("myUser", MainActivity.myUser?.apply {
                        help = user.help
                        phone = user.phone
                        name = user.name
                        pharmacy = user.pharmacy
                        store = user.store
                        talk = user.talk
                    })
                    Log.d(TAG, "DocumentSnapshot successfully written!")

                }
                .addOnFailureListener {
                    getFeedback.postValue(false)
                    Log.w(TAG, "Error writing document", it)
                }
        }
    }

    private fun get() {
        getFeedbackLoading.postValue(true)
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("user")
                .document(uid)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot != null && firebaseFirestoreException == null) {
                        if(documentSnapshot.exists()) {
                            getUser.postValue(documentSnapshot.toObject(User::class.java))
                            getFeedbackLoading.postValue(false)
                        }
                    } else {
                        getFeedbackLoading.postValue(false)
                    }
                }
        } ?: getFeedbackLoading.postValue(false)
    }
}