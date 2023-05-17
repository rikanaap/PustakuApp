package com.example.pustaku.fragment.favorite

import android.content.res.Resources
import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.*
import com.example.pustaku.R
import com.example.pustaku.`object`.books
import com.example.pustaku.adapters.RecycleBestSellerAdapter
import com.example.pustaku.adapters.RecycleContinueFavoriteAdapter
import com.example.pustaku.adapters.RecycleReadlistFavoriteAdapter
import com.example.pustaku.data.Books
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReadListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReadListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var dataList: ArrayList<Books>
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
        return inflater.inflate(R.layout.fragment_favorites_readlist, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReadListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReadListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataList = arrayListOf(Books())
        //RecycleView for Continue
        recycleView = view.findViewById(R.id.recycleViewReadListFavorite)
        recycleView.layoutManager = GridLayoutManager(context, 3)
        recycleView.setHasFixedSize(true)

        //RECYCLE VIEW DATA FOR READING LIST
        dbRef = FirebaseDatabase.getInstance().getReference("Book")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                if (snapshot.exists()) {
                    for (bookSnap in snapshot.children) {
                        val bookData = bookSnap.getValue(Books::class.java)
                        dataList.add(bookData!!)
                    }
                    val recycleReadingListadapter = context?.let { RecycleReadlistFavoriteAdapter(dataList, it) }
                    recycleView.adapter = recycleReadingListadapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}