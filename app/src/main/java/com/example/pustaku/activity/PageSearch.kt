package com.example.pustaku.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pustaku.R
import com.example.pustaku.`object`.books
import com.example.pustaku.adapters.RecycleBestSellerAdapter
import com.example.pustaku.adapters.RecycleSearchAdapter
import com.example.pustaku.data.Books
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.intellij.lang.annotations.Language
import java.util.*
import kotlin.collections.ArrayList

class PageSearch : AppCompatActivity() {
    private lateinit var recycleView : RecyclerView
    private lateinit var searchView: SearchView
    private var mList = ArrayList<Books>()
    private lateinit var adapter : RecycleSearchAdapter
    private lateinit var dbRef : DatabaseReference
    private lateinit var linearBlocker : LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_search)
        //SETTING CONNECTION
        dbRef = FirebaseDatabase.getInstance().getReference("Book")
        //SETTING VIEW
        searchView = findViewById(R.id.searchBarBook)
        linearBlocker = findViewById(R.id.linearNoData)
        linearBlocker.visibility = View.GONE
        recycleView = findViewById(R.id.recyclerViewSearch)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.setHasFixedSize(true)
        recycleView.visibility = View.VISIBLE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        //RECYCLE VIEW DATA FOR NEWEST BOOK
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                if (snapshot.exists()) {
                    for (bookSnap in snapshot.children) {
                        val bookData = bookSnap.getValue(Books::class.java)
                        mList.add(bookData!!)
                    }
                    adapter = RecycleSearchAdapter(mList, this@PageSearch)
                    recycleView.adapter = adapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun filterList(query : String?) {
        if (query != null) {
            val filteredList = ArrayList<Books>()
            for (i in mList) {
                if (i.title!!.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                recycleView.visibility = View.GONE
                linearBlocker.visibility = View.VISIBLE
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }
    }
}