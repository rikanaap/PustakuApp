package com.example.pustaku.fragment.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.pustaku.R
import com.example.pustaku.`object`.books
import com.example.pustaku.activity.PageSearch
import com.example.pustaku.adapters.RecycleBestSellerAdapter
import com.example.pustaku.data.Books
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomePage.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomePage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var bestSellerRV : RecyclerView
    private lateinit var continueReadingRV : RecyclerView
    private lateinit var top10RV : RecyclerView
    private lateinit var bookList : ArrayList<Books>
    private lateinit var recommend : ArrayList<Books>
    private lateinit var newest : ArrayList<Books>
    private lateinit var progBar : ProgressBar
    private lateinit var refresh : SwipeRefreshLayout
    private lateinit var searchBar : LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomePage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progBar = view.findViewById(R.id.progressBar)
        searchBar = view.findViewById(R.id.searchBar)
        searchBar.setOnClickListener{
            val intent = Intent(context, PageSearch::class.java)
            startActivity(intent)
        }
        bookList = arrayListOf(Books())
        newest = arrayListOf(Books())
        recommend = arrayListOf(Books())
        bestSellerRV = view.findViewById(R.id.recycleviewBestSeller)
        continueReadingRV = view.findViewById(R.id.recycleviewContinue)
        top10RV = view.findViewById(R.id.recycleviewTopIndonesian)

        //RecycleView for Best Seller
        bestSellerRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bestSellerRV.setHasFixedSize(true)

        //RecycleView for Continue Reading
        continueReadingRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        continueReadingRV.setHasFixedSize(true)

        //RecycleView for Top 10
        top10RV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        top10RV.setHasFixedSize(true)

        //REFRESH
        refresh = view.findViewById(R.id.refreshLayoutHome)
        refresh.setColorSchemeResources(R.color.dark_purple)
        refresh.setOnRefreshListener {
            getBookData()
        }
        getBookData()
    }
    private fun getBookData() {
        //HIDING LAYOUT BECAUSE NO DATA
        progBar.visibility = View.VISIBLE
        bestSellerRV.visibility = View.INVISIBLE
        continueReadingRV.visibility = View.INVISIBLE
        top10RV.visibility = View.INVISIBLE
        top10RV.visibility = View.INVISIBLE
        //GETTING DATA FROM BOOK FIELD
        val dbRef = FirebaseDatabase.getInstance().getReference("Book").orderByChild("title")
        //GETTING DATA FROM FIREBASE FIELD BOOK USING FILTER FROM recommend
        val newestData = FirebaseDatabase.getInstance().getReference("Book").orderByChild("releaseDate")
        //GETTING DATA FROM FIREBASE FIELD BOOK USING FILTER FROM booktext
        val recommendData = FirebaseDatabase.getInstance().getReference("Book").orderByChild("recommend").equalTo(true)

        //RECYCLE VIEW DATA FOR ALL
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                bookList.clear()
                if (snapshot.exists()) {
                    for (bookSnap in snapshot.children) {
                        val bookData = bookSnap.getValue(Books::class.java)
                        bookList.add(bookData!!)
                    }
                    val recycleBestSelleradapter = context?.let { RecycleBestSellerAdapter(bookList, it) }
                    bestSellerRV.adapter = recycleBestSelleradapter

                    //RECYCLE VIEW DATA FOR NEWEST BOOK
                    newestData.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            newest.clear()
                            if (snapshot.exists()) {
                                for (bookSnap in snapshot.children) {
                                    val bookData = bookSnap.getValue(Books::class.java)
                                    newest.add(bookData!!)
                                }
                                val recycleContinueadapter = context?.let { RecycleBestSellerAdapter(newest, it) }
                                continueReadingRV.adapter = recycleContinueadapter
                                progBar.visibility = View.GONE
                                continueReadingRV.visibility = View.VISIBLE
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                    //RECYCLE VIEW DATA FOR RECOMMENDATION BOOK
                    recommendData.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            recommend.clear()
                            if (snapshot.exists()) {
                                for (bookSnap in snapshot.children) {
                                    val bookData = bookSnap.getValue(Books::class.java)
                                    recommend.add(bookData!!)
                                }
                                val recycleTop10adapter = context?.let { RecycleBestSellerAdapter(recommend, it) }
                                top10RV.adapter = recycleTop10adapter
                                progBar.visibility = View.GONE
                                top10RV.visibility = View.VISIBLE
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                    //SHOWING DATA
                    progBar.visibility = View.GONE
                    bestSellerRV.visibility = View.VISIBLE
                }
                refresh.isRefreshing = false
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })




    }
}