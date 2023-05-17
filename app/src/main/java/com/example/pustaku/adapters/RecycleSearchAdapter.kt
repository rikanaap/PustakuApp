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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class RecycleSearchAdapter(var mList : List<Books>, private val context : Context): RecyclerView.Adapter<RecycleSearchAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemView = mList[position]
        val imgLink = "https://firebasestorage.googleapis.com/v0/b/pustaku.appspot.com/o/images%2F${itemView.imageid}?alt=media&token=be406d30-7a24-4107-bc02-db83067a66e7"
        Picasso.get().load(imgLink).placeholder(R.color.black).into(holder.imageCoverBook)
        holder.textTitleBook.setText(itemView.title)

        //CONNECTING PUBLISHER TO USER DATABASE
        val dbRef = FirebaseDatabase.getInstance().getReference("User").child(itemView.publisher.toString()).child("userUsername").get().addOnSuccessListener {
            holder.textPublisherBook.setText(it.value.toString())
        }
        holder.constraintWrapper.setOnClickListener{
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
        return mList.size
    }

    fun setFilteredList(mList: List<Books>){
        this.mList = mList
        notifyDataSetChanged()
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageCoverBook : ImageView = itemView.findViewById(R.id.imageSetBookCoverSearch)
        val textTitleBook : TextView = itemView.findViewById(R.id.textSetBookTitleSearch)
        val textPublisherBook : TextView = itemView.findViewById(R.id.textSetBookPublisherSearch)
        val constraintWrapper : ConstraintLayout = itemView.findViewById(R.id.searchConstraintWrapper)
    }

}