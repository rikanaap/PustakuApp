package com.example.pustaku.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.pustaku.R
import com.example.pustaku.`object`.books
import com.example.pustaku.activity.PageDetailBook
import com.example.pustaku.data.Books
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class RecycleContinueFavoriteAdapter(private val books : ArrayList<Books>, private val context : Context):RecyclerView.Adapter<RecycleContinueFavoriteAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_continue_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = books[position]
        val imgLink = "https://firebasestorage.googleapis.com/v0/b/pustaku.appspot.com/o/images%2F${itemView.imageid}?alt=media&token=be406d30-7a24-4107-bc02-db83067a66e7"
        Picasso.get().load(imgLink).placeholder(R.color.black).into(holder.imageBookCover)
        holder.textBookTitle.setText(itemView.title)
        holder.textBookChapter.setText(itemView.chapter)
        holder.continueWrapper.setOnClickListener{
            val intent = Intent(context, PageDetailBook::class.java)
            intent.putExtra("bookCover", itemView.imageid)
            intent.putExtra("bookTitle", itemView.title)
            intent.putExtra("bookDesc", itemView.shortdesc)
            intent.putExtra("bookChapter", itemView.chapter)
            intent.putExtra("bookChapterTitle", itemView.chaptertitle)
            intent.putExtra("bookText", itemView.booktext)
            intent.putExtra("bookDate", itemView.releaseDate)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }
    class ViewHolder(val v : View) : RecyclerView.ViewHolder(v){
        val imageBookCover : ImageView = v.findViewById(R.id.imageSetBookCoverContinue)
        val textBookTitle : TextView = v.findViewById(R.id.textSetBookTitleContinue)
        val textBookChapter : TextView = v.findViewById(R.id.textSetBookChapter)
        val continueWrapper :ConstraintLayout=v.findViewById(R.id.constraintContinueWrapper)
    }

}