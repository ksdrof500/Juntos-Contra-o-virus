package com.juntoscontracorona

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.juntoscontracorona.model.User
import com.juntoscontracorona.util.PreferencesDataSource
import com.juntoscontracorona.util.toastError


class LoginActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_acticity)

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.mipmap.ic_launcher)
                .setAvailableProviders(
                    listOf(
                        GoogleBuilder().build(),
                        FacebookBuilder()
                            .setPermissions(listOf("public_profile", "email"))
                            .build(),
                        EmailBuilder().build(),
                        PhoneBuilder().build()
                    )
                )
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                saveUser()
            } else {
                if (response == null) {
                    this@LoginActivity.resources?.getString(R.string.save_error)?.let {
                        toastError(it)
                    }
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    this@LoginActivity.resources?.getString(R.string.save_error)?.let {
                        toastError(it)
                    }
                    return
                }
            }
        }
    }

    private fun saveUser() {
        FirebaseAuth.getInstance().currentUser?.let {
            val user = User(
                id = it.uid,
                image = it.photoUrl.toString(),
                name = it.displayName ?: ""
            )

            FirebaseFirestore.getInstance().collection("user")
                .document(it.uid)
                .set(user)
                .addOnSuccessListener {
                    PreferencesDataSource(this@LoginActivity).set("myUser", user)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    this@LoginActivity.resources?.getString(R.string.save_error)?.let { message ->
                        toastError(message)
                    }
                }
        }
    }
}
