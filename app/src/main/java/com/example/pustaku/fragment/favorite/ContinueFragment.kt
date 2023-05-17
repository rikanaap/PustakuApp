package com.example.pustaku.fragment.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pustaku.R
import com.example.pustaku.`object`.books
import com.example.pustaku.adapters.RecycleBestSellerAdapter
import com.example.pustaku.adapters.RecycleContinueFavoriteAdapter
import com.example.pustaku.data.Books
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContinueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContinueFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var dataList : ArrayList<Books>
    private lateinit var recycleView : RecyclerView
    private lateinit var dbRef : DatabaseReference
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
        return inflater.inflate(R.layout.fragment_favorite_continue, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContinueFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContinueFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataList = arrayListOf(Books())
        recycleView = view.findViewById(R.id.recycleViewContinueFavorite)
        //RecycleView for Continue
        recycleView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycleView.setHasFixedSize(true)
        dbRef = FirebaseDatabase.getInstance().getReference("Book")
        //RECYCLE VIEW DATA FOR CONTINUE
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                if (snapshot.exists()) {
                    for (bookSnap in snapshot.children) {
                        val bookData = bookSnap.getValue(Books::class.java)
                        dataList.add(bookData!!)
                    }
                    val recycleContinueadapter = context?.let { RecycleContinueFavoriteAdapter(dataList, it) }
                    recycleView.adapter = recycleContinueadapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



    }
}