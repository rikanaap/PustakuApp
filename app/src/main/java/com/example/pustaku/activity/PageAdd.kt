package com.example.pustaku.activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.pustaku.R
import com.example.pustaku.data.Books
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PageAdd : AppCompatActivity() {
    private lateinit var bookCover : ImageView
    private lateinit var editTitle : EditText
    private lateinit var editDesc : EditText
    private lateinit var editBook : EditText
    private lateinit var editChapter : EditText
    private lateinit var editChapterTitle : EditText
    private  lateinit var publishBook : TextView
    private var imageUri : Uri? = null
    private lateinit var dbRef : DatabaseReference
    private lateinit var firebaseAuth : FirebaseAuth

    //Pick date, uses for Update
    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", Locale.getDefault())
    private val now = Date()
    private val date = formatter.format(now)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_addbook)
        bookCover = findViewById(R.id.add_cover)
        editTitle = findViewById(R.id.editInputTitle)
        editDesc = findViewById(R.id.editInputDesc)
        editBook = findViewById(R.id.editInputStory)
        editChapter = findViewById(R.id.editInputChapter)
        editChapterTitle = findViewById(R.id.editInputChapterTitle)
        publishBook = findViewById(R.id.textPublishBook)
        dbRef = FirebaseDatabase.getInstance().getReference("Book")
        firebaseAuth = FirebaseAuth.getInstance()
        bookCover.setOnClickListener {
            selectImage()
        }
        publishBook.setOnClickListener{
            saveBookData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val sourceUri: Uri = data.data!!
            val destinationUri: Uri = Uri.fromFile(File(cacheDir, "cropped"))
            val options = UCrop.Options()
            options.setToolbarColor(ContextCompat.getColor(this, R.color.white))
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.white))
            options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.white))
            options.setToolbarTitle("Crop Image")
            UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1f, 1.5f)
                .withOptions(options)
                .start(this)
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imageUri = UCrop.getOutput(data!!)
            bookCover.setImageURI(imageUri)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Error ${UCrop.getError(data!!)}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveBookData() {
        //Getting User Input
        val bookTitle = editTitle.text.toString()
        val bookDesc = editDesc.text.toString()
        val bookChapter = editChapter.text.toString()
        val bookChapterTitle = editChapterTitle.text.toString()
        val bookText = editBook.text.toString()

        if(bookTitle.isEmpty()){
            editTitle.setError("Required")
        }
        if(bookDesc.isEmpty()){
            editDesc.setError("Required")
        }
        if(bookText.isEmpty()){
            editBook.setError("Required")
        }
        if(bookChapter.isEmpty()){
            editBook.setError("Required")
        }
        if(bookChapterTitle.isEmpty()){
            editBook.setError("Required")
        }
        val bookId = dbRef.push().key!!
        val imgId = "$bookId-$date"
        val userId = firebaseAuth.currentUser!!.uid
        val book = Books(bookId, imgId, bookTitle, bookDesc, bookChapter, bookChapterTitle, bookText, false, userId, date)

        fun uploadImage(onComplete: () -> Unit) {
            val storageReference = FirebaseStorage.getInstance().getReference("images/$imgId")

            if (imageUri != null) {
                storageReference.putFile(imageUri!!)
                    .addOnSuccessListener {
                        imageUri = null
                        bookCover.setImageResource(R.color.black)
                        onComplete()

                    }.addOnFailureListener{
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Mohon masukkan foto", Toast.LENGTH_SHORT).show()
            }
        }

        if(imageUri != null && bookText.isNotEmpty() && bookTitle.isNotEmpty() && bookDesc.isNotEmpty() && bookChapter.isNotEmpty() && bookChapterTitle.isNotEmpty()){
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Uploading data...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            dbRef.child(bookId).setValue(book).addOnCompleteListener{
                uploadImage {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    Toast.makeText(this, "Buku anda berhasil diupload", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.addOnFailureListener{
                Toast.makeText(this, "Error $(err.message)", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
    }

}