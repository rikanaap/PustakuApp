package com.example.pustaku.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.pustaku.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PageLogin : AppCompatActivity() {
    private lateinit var textRegister : TextView
    private lateinit var buttonLogin : Button
    private lateinit var dbRef : DatabaseReference
    private lateinit var inputEmail : EditText
    private lateinit var inputPassword : EditText
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_login)
        inputEmail = findViewById(R.id.editInputEmail)
        inputPassword = findViewById(R.id.editInputPassword)

        dbRef = FirebaseDatabase.getInstance().getReference("User")//MENGHUBUNGKAN KE DATABASE FIELD USER
        //Button to Register Page
        textRegister = findViewById(R.id.textIntentRegister)
        textRegister.setOnClickListener{
            val intent = Intent(this, PageRegister::class.java)
            startActivity(intent)
        }

        //Button to Main Page
        buttonLogin = findViewById(R.id.buttonIntentLogin)
        buttonLogin.setOnClickListener{
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()
            if (email.isEmpty()) {
                inputEmail.setError("Please insert email")
            }
            if (password.isEmpty()) {
                inputPassword.setError("Please insert password")
            }

            if (email.isNotEmpty() &&  password.isNotEmpty()) {
                userLogin(email, password)
            }
        }
    }

    private fun userLogin(
        email : String, password : String
    ) {
        firebaseAuth = FirebaseAuth.getInstance()//MENGHUBUNGKAN KE FIREBASE AUTH
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Signing In...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ //MEMASUKAN INPUTAN USER KE FIREBASE AUTH
            if (it.isSuccessful) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                val intent = Intent(this, PageMain::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                if (progressDialog.isShowing) progressDialog.dismiss()
            }
        }
    }
}