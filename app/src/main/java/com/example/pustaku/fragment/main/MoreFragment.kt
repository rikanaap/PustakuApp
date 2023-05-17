package com.example.pustaku.fragment.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.pustaku.R
import com.example.pustaku.activity.PageLogin
import com.example.pustaku.activity.PageProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoreFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var buttonProfile : LinearLayout
    private lateinit var buttonLogout : LinearLayout
    private lateinit var textUsername : TextView
    private lateinit var imageProfilePhoto : CircleImageView
    private lateinit var dbRef : DatabaseReference
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var username : String
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
        return inflater.inflate(R.layout.fragment_page_more, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MoreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MoreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //SETTING VIEW
        buttonProfile = view.findViewById(R.id.linearProfileWrapper)
        buttonLogout = view.findViewById(R.id.linearLogoutWrapper)
        textUsername = view.findViewById(R.id.textSetUsername)
        imageProfilePhoto = view.findViewById(R.id.imageSetProfile)

        //CONNECTING TO DATABASE
        firebaseAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("User")

        //GETTING USER ID FROM AUTH
        val userId = firebaseAuth.currentUser!!.uid
        val getUsername = dbRef.child(userId).child("userUsername").get().addOnSuccessListener {
            val usernameasli = it.value as String
            username = "@$usernameasli"
            textUsername.setText(username)
        }

        //GETTING USER PROFILE PHOTO FROM USER DATABASE
        val getProfilePhoto = dbRef.child(userId).child("userProfilePhoto").get().addOnSuccessListener {
            val imgLink = "https://firebasestorage.googleapis.com/v0/b/pustaku.appspot.com/o/images%2F${it.value}?alt=media&token=be406d30-7a24-4107-bc02-db83067a66e7"
            Picasso.get().load(imgLink).placeholder(R.color.black).into(imageProfilePhoto)
        }

        //WHEN BUTTON PROFILE CLICKED
        buttonProfile.setOnClickListener{
            val intent = context?.let { Intent(it, PageProfile::class.java) }
            startActivity(intent)
        }

        //WHEN BUTTON LOGOUT CLICKED
        buttonLogout.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure, want to Logout?")
            builder.setCancelable(true)
            builder.setPositiveButton("Yes"){
                _, _, -> firebaseAuth.signOut()
                val intent = Intent(context, PageLogin::class.java)
                startActivity(intent)
            }
            builder.setNegativeButton("No"){
                dialog,_, -> dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}