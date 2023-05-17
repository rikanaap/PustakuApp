package com.example.pustaku.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.icu.text.CaseMap.Title
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.pustaku.R
import com.example.pustaku.data.Books
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class PageEdit : AppCompatActivity() {
    private lateinit var buttonSave : TextView
    private lateinit var buttonBack : LinearLayout
    private lateinit var buttonDelete : TextView
    private lateinit var bookCover : ImageView
    private lateinit var editChapterText : TextView
    private lateinit var editChapterTitle : EditText
    private lateinit var editBookText : EditText
    private lateinit var firebaseAuth: FirebaseAuth

    //DATA TO UPDATE
    private lateinit var bookId : String
    private lateinit var imgId : String
    private lateinit var bookDesc : String
    private lateinit var title : String
    private lateinit var publisher : String
    private lateinit var chapter : String
    private lateinit var chapterTitle : String
    private lateinit var bookText : String
    private lateinit var releaseDate : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_bookread_edit)
        //SETTING VIEW
        buttonSave = findViewById(R.id.textSaveEdit)
        buttonBack = findViewById(R.id.topBackSection)
        buttonDelete = findViewById(R.id.textDeleteBook)
        bookCover = findViewById(R.id.imageSetBookCover)
        editChapterText = findViewById(R.id.textSetBookChapter)
        editChapterTitle = findViewById(R.id.textSetBookChapterTitle)
        editBookText = findViewById(R.id.textSetBookContent)
        firebaseAuth = FirebaseAuth.getInstance()
        setValuesToView()

        //IF SAVE CLICKED
        buttonSave.setOnClickListener{
            //PICKING USER INPUT
            bookText = editBookText.text.toString()
            chapterTitle = editChapterTitle.text.toString()
            updateBook(bookId, imgId, title, bookDesc, chapter, chapterTitle, bookText, publisher, releaseDate)
            Toast.makeText(this, "Book Updating.....", Toast.LENGTH_SHORT).show()
        }

        //IF BACK BUTTON CLICK
        buttonBack.setOnClickListener{
            finish()
        }

        //IF DELETE BUTTON CLICK
        buttonDelete.setOnClickListener{
            DeleteBook(bookId, title)
        }
    }

    private fun DeleteBook( id : String, title : String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure, want to remove $title ?")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes"){
                _, _, -> val dbRef = FirebaseDatabase.getInstance().getReference("Book").child(id)
            val mTask = dbRef.removeValue()
            mTask.addOnSuccessListener {
                Toast.makeText(this, "Book deleted", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener{
                error -> Toast.makeText(this, "Deleting Error ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("No"){
                dialog,_, -> dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setValuesToView() {
        //SETTING DATA FROM INTENT TO VIEW
        if(intent.extras != null) {
            //GETTING DATA FROM INTENT
            bookId = intent.getStringExtra("bookId").toString()
            imgId = intent.getStringExtra("bookCover").toString()
            bookDesc = intent.getStringExtra("bookDesc").toString()
            title = intent.getStringExtra("bookTitle").toString()
            publisher = intent.getStringExtra("bookPublisher").toString()
            chapter = intent.getStringExtra("bookChapter").toString()
            releaseDate = intent.getStringExtra("bookDate").toString()
            val imgLink =
                "https://firebasestorage.googleapis.com/v0/b/pustaku.appspot.com/o/images%2F${(imgId)}?alt=media&token=be406d30-7a24-4107-bc02-db83067a66e7"
            Picasso.get().load(imgLink).placeholder(R.color.black).into(bookCover)
            editBookText.setText(intent.getStringExtra("bookText"))
            editChapterText.setText(chapter)
            editChapterTitle.setText(intent.getStringExtra("bookChapterTitle"))
        }
    }

    private fun updateBook(
        idbook : String, idimage : String, bookTitle : String, descBook : String, bookChapter : String, bookChapterTitle : String, textBook : String, bookPublisher : String, release : String
    ) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Updateing data....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        //INSERTING INPUT INTO DATABASE
        val dbRef = FirebaseDatabase.getInstance().getReference("Book").child(idbook)
        val empInfo = Books(idbook, idimage, bookTitle, descBook, bookChapter, bookChapterTitle, textBook, false, bookPublisher, release)
        dbRef.setValue(empInfo).addOnSuccessListener {
            if(progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this, "Book has been updated", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener{
            Toast.makeText(this, "Error $(err.message)", Toast.LENGTH_SHORT).show()
        }
    }
}