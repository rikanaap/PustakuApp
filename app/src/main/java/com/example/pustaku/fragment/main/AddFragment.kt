package com.example.pustaku.fragment.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.pustaku.R
import com.example.pustaku.`object`.books
import com.example.pustaku.activity.PageAdd
import com.example.pustaku.activity.PageProfile
import com.example.pustaku.adapters.RecycleAddAdapter
import com.example.pustaku.data.Books
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var addButton : TextView
    private lateinit var recycleView : RecyclerView
    private lateinit var refresh : SwipeRefreshLayout
    private lateinit var userBook : ArrayList<Books>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var linearNoAccses : ConstraintLayout
    private lateinit var buttonPermission : TextView
    private lateinit var scrollView: ScrollView
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
        return inflater.inflate(R.layout.fragment_page_add, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //CHECK IF USER IS LEVEL 1 OR 2
        linearNoAccses = view.findViewById(R.id.constraintNoPermission)
        scrollView = view.findViewById(R.id.scrolViewPreventer)
        firebaseAuth = FirebaseAuth.getInstance()
        val userlvl = firebaseAuth.currentUser!!.uid
        progressBar = view.findViewById(R.id.progresBarAdd)
        scrollView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        val getUserLevel = FirebaseDatabase.getInstance().getReference("User").child(userlvl).child("userLevel").get().addOnSuccessListener {
            val userLevel = it.value.toString()
            if(userLevel.equals("1")){
                linearNoAccses.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }else{
                scrollView.visibility = View.VISIBLE
                linearNoAccses.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        //VIEWS SET
        refresh = view.findViewById(R.id.swipeRefreshAdd)

        buttonPermission = view.findViewById(R.id.textIntentPermission)
        buttonPermission.setOnClickListener{
            val intent = context?.let { Intent(it, PageProfile::class.java) }
            startActivity(intent)
        }
        addButton = view.findViewById(R.id.textViewAddBook)
        addButton.setOnClickListener{
            val intent = context?.let { Intent(it, PageAdd::class.java) }
            startActivity(intent)
        }
        userBook = arrayListOf(Books())
        val books = books.getItemData()
        //RECYCLE VIEW FOR USER BOOK
        recycleView = view.findViewById(R.id.recycleViewAdd)
        recycleView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycleView.setHasFixedSize(true)
        getUserBookData()
    }

    private fun getUserBookData() {
        //HIDING LAYOUT BECAUSE NO DATA
        val userId : String = firebaseAuth.currentUser!!.uid
        //GETTING DATA FROM BOOK FIELD
        val bookRef = FirebaseDatabase.getInstance().getReference("Book").orderByChild("publisher").equalTo(userId)
        bookRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userBook.clear()
                if (snapshot.exists()) {
                    for (bookSnap in snapshot.children) {
                        val bookData = bookSnap.getValue(Books::class.java)
                        userBook.add(bookData!!)
                    }
                    val recycleUserBookadapter = context?.let { RecycleAddAdapter(userBook, it) }
                    recycleView.adapter = recycleUserBookadapter
                    recycleView.visibility = View.VISIBLE
                }
                refresh.isRefreshing = false
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}