package com.example.pustaku.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.pustaku.R
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class PageDetailBook : AppCompatActivity() {
    private lateinit var bookCover : ImageView
    private lateinit var bookTitle : TextView
    private lateinit var bookDesc : TextView
    private lateinit var linearBack : LinearLayout
    private lateinit var buttonRead : Button
    private lateinit var imgId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_detailbook)
        linearBack = findViewById(R.id.topBackSection)
        linearBack.setOnClickListener{
            finish()
        }
        buttonRead = findViewById(R.id.buttonRead)
        bookCover = findViewById(R.id.imageSetBookCover)
        bookTitle = findViewById(R.id.textSetBookTitle)
        bookDesc = findViewById(R.id.textSetBookDescription)
        setValuesToViews()
        imgId = intent.getStringExtra("bookCover").toString()
        val imgLink = "https://firebasestorage.googleapis.com/v0/b/pustaku.appspot.com/o/images%2F${imgId}?alt=media&token=be406d30-7a24-4107-bc02-db83067a66e7"
        Picasso.get().load(imgLink).placeholder(R.color.white).into(bookCover)
        buttonRead.setOnClickListener{
            readClick()
        }
    }

    private fun readClick() {
        if(intent.extras != null){
            val intents = Intent(this, PageReadBook::class.java)
            intents.putExtra("cover", intent.getStringExtra("bookCover"))
            intents.putExtra("title", intent.getStringExtra("bookTitle"))
            intents.putExtra("chapter", intent.getStringExtra("bookChapter"))
            intents.putExtra("chapterTitle", intent.getStringExtra("bookChapterTitle"))
            intents.putExtra("text", intent.getStringExtra("bookText"))
            startActivity(intents)
            finish()
        }else{
            Toast.makeText(this, "No Data Recorded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setValuesToViews() {
        if(intent.extras != null) {
            bookTitle.text = intent.getStringExtra("bookTitle")
            bookDesc.text = intent.getStringExtra("bookDesc")
        }
    }
}