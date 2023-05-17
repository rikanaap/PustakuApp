package com.example.pustaku.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.pustaku.R
import com.example.pustaku.data.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

class PageRegister : AppCompatActivity() {
    private lateinit var textLogin: TextView
    private lateinit var buttonRegister: Button
    private lateinit var dbRef: DatabaseReference
    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputEmail: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_register)
        inputEmail = findViewById(R.id.editInputEmail)
        inputPassword = findViewById(R.id.editInputPassword)
        inputUsername = findViewById(R.id.editInputUsername)
        dbRef = FirebaseDatabase.getInstance().getReference("User")//Masuk ke rDatabase User
        firebaseAuth = FirebaseAuth.getInstance()

        //Button to Login page
        textLogin = findViewById(R.id.textIntentLogin)
        textLogin.setOnClickListener {
            val intent = Intent(this, PageLogin::class.java)
            startActivity(intent)
        }

        //Button to Main Page
        buttonRegister = findViewById(R.id.buttonIntentRegister)
        buttonRegister.setOnClickListener {
            if (inputEmail.text.isEmpty()) {
                inputEmail.setError("Required")
            }
            if (inputPassword.text.isEmpty()) {
                inputEmail.setError("Required")
            }
            if (inputUsername.text.isEmpty()) {
                inputEmail.setError("Required")
            }
            if (inputEmail.text.isNotEmpty() && inputPassword.text.isNotEmpty() && inputUsername.text.isNotEmpty()) {
                userRegister(
                    inputUsername.text.toString(),
                    inputEmail.text.toString(),
                    inputPassword.text.toString()
                )
            }

        }
    }

    private fun userRegister(
        username: String, email: String, password: String
    ) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Signing Up...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val uId = firebaseAuth.currentUser!!.uid
                val user = Users(uId, username, email, password, 1, "") //MEMASUKAN DATA KE DATABASE

                dbRef.child(uId).setValue(user).addOnCompleteListener {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        val intent = Intent(this, PageMain::class.java)
                        startActivity(intent)
                        finish()

                    }.addOnFailureListener { err ->
                        Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                if (progressDialog.isShowing) progressDialog.dismiss()
            }
        }
    }
}