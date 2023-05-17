package com.example.pustaku.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pustaku.R
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class PageReadBook : AppCompatActivity() {
    private lateinit var linearBack : LinearLayout
    private lateinit var imageCover : ImageView
    private lateinit var textChapter : TextView
    private lateinit var textChapterTitle : TextView
    private lateinit var textBook : TextView
    private lateinit var textBookTitle : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_bookread)
        imageCover = findViewById(R.id.imageSetBookCover)
        textChapter = findViewById(R.id.textSetBookChapter)
        textChapterTitle = findViewById(R.id.textSetBookChapterTitle)
        textBook = findViewById(R.id.textSetBookContent)
        textBookTitle = findViewById(R.id.textSetBookTitle)
        linearBack = findViewById(R.id.topBackSection)
        linearBack.setOnClickListener {
            finish()
        }
        if(intent.extras != null) {
            val imgId = intent.getStringExtra("cover").toString()
            val imgLink = "https://firebasestorage.googleapis.com/v0/b/pustaku.appspot.com/o/images%2F${imgId}?alt=media&token=be406d30-7a24-4107-bc02-db83067a66e7"
            Picasso.get().load(imgLink).placeholder(R.color.black).into(imageCover)
            textChapter.setText(intent.getStringExtra("chapter"))
            textBook.setText(intent.getStringExtra("text"))
            textChapterTitle.text = intent.getStringExtra("chapterTitle")
            textBookTitle.text = intent.getStringExtra("title")
        }
    }
}