package com.example.pustaku.activity

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.pustaku.R
import com.example.pustaku.data.Permission
import com.example.pustaku.data.Users
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class PageProfile : AppCompatActivity() {
    private lateinit var checkbox : CheckBox
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var UserdbRef : DatabaseReference
    private lateinit var PermissiondbRef : DatabaseReference
    private lateinit var textUsername : TextView
    private lateinit var gambarSet : ImageView
    private lateinit var textFoto : TextView
    private lateinit var editUsername : EditText
    private lateinit var profilePhoto : CircleImageView
    private lateinit var saveButton : Button
    private lateinit var progress : ProgressBar
    private lateinit var cropActivityResultLauncher : ActivityResultLauncher<Any?>

    //FOR DATA
    private var imageUri : Uri? = null
    private var profileUri : Uri? = null
    private lateinit var username : String
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var level : String
    private lateinit var userId : String
    private lateinit var profileId : String

    //Pick date, uses for Update
    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", Locale.getDefault())
    private val now = Date()
    private val date = formatter.format(now)

    //USES FOR PICKING USER PROFILE PHOTO
    private val cropActivityResultContext = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Select Image")
                .setAspectRatio(1,1)
                .setActivityMenuIconColor(R.color.dark_purple)
                .setAllowFlipping(true)
                .setAllowRotation(true)
                .setCropMenuCropButtonTitle("Select")
                .getIntent(this@PageProfile)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_profile)

        //SETTING VIEW
        textUsername = findViewById(R.id.textSetUsername)
        editUsername = findViewById(R.id.editInputUsernameEdit)
        checkbox = findViewById(R.id.checkboxInputEnable)
        saveButton = findViewById(R.id.buttonSaveProfile)
        profilePhoto = findViewById(R.id.imageSetProfile)
        progress = findViewById(R.id.progresBarProfile)

        //ON ACTIVITY START
        progress.visibility = View.VISIBLE
        editUsername.visibility = View.GONE
        checkbox.visibility = View.GONE
        saveButton.visibility = View.GONE
        profilePhoto.visibility = View.GONE

        //CHANGE PROFILE PICTURE
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContext) {
            it?.let { uri ->
                profileUri = uri
                profilePhoto.setImageURI(uri)
            }
        }
        profilePhoto.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        //WHEN CHECKBOX CLICK
        checkbox.setOnClickListener {
            if (checkbox.isChecked) {
                checkbox.toggle() //REMOVE CHECKED IN CHECKBOX
                openDialog(userId, email)
            } else {
                checkbox.toggle()
            }
        }

        //GETTING USER ID FROM FIREBASE AUTH
        firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser!!.uid

        //INSERTING USER ID TO MATCHING DATA IN USER DATABASE
        UserdbRef = FirebaseDatabase.getInstance().getReference("User")
        PermissiondbRef = FirebaseDatabase.getInstance().getReference("Permission")

        getUserData()
        //GETTING PERMISSION ALREADYASKING FROM PERMISSION FIELD (USES TO HIDE SENDING PERMISSION REPEATLY)
        val getAlreadyAsking = PermissiondbRef.child(userId).child("alreadyAsking").get().addOnSuccessListener {
            if(it.value == "noSend"){
                checkbox.visibility = View.GONE
            }else{
                checkbox.visibility = View.VISIBLE
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        saveButton.setOnClickListener {
            //PICKING DATA FROM USER INPUT
            val updateUsername = editUsername.text.toString()
            val uriProfile = profileUri
            //GETTING PROFILE ID
            val updateProfileId = "$userId-$date-user"
            val userUpdate = Users(userId, updateUsername, email, password , level.toInt(), updateProfileId)


            //UPLOAD PROFILE PICT TO FIREBASE STORAGE
            fun uploadProfile(onComplete: () -> Unit) {
                val storageReference = FirebaseStorage.getInstance().getReference("images/$updateProfileId")
                if (uriProfile != null) {
                    storageReference.putFile(uriProfile)
                        .addOnSuccessListener {
                            profilePhoto.setImageResource(R.color.black)
                            onComplete()
                        }.addOnFailureListener {
                            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Mohon Masukan Gambar", Toast.LENGTH_SHORT).show()
                }
            }

            //IF NEW PROFILE  IS UPDATE
            fun removeFromStorage(){
                val imgLink = "https://firebasestorage.googleapis.com/v0/b/pustaku.appspot.com/o/images%2F${profileId}?alt=media&token=be406d30-7a24-4107-bc02-db83067a66e7"
                val deleteReference = FirebaseStorage.getInstance().getReferenceFromUrl(imgLink)
                deleteReference.delete().addOnSuccessListener {
                    Toast.makeText(this, "Profile Photo Updated", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this, "New Profile Photo", Toast.LENGTH_SHORT).show()
                }
            }

            //CHECKING IF ALL FIELD IS FILL
            if (updateUsername.isNotEmpty() && updateProfileId.isNotEmpty()) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Updating data....")
                progressDialog.setCancelable(false)
                progressDialog.show()
                if(uriProfile != null){
                    uploadProfile {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        Toast.makeText(this, "Updating Porfile", Toast.LENGTH_SHORT).show()
                        removeFromStorage()
                        UserdbRef.child(userId).setValue(userUpdate).addOnCompleteListener {
                            val intent = Intent(this, PageMain::class.java)
                            startActivity(intent)
                        }.addOnFailureListener {
                            Toast.makeText(this, "Error $(err.message)", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    UserdbRef.child(userId).setValue(userUpdate).addOnCompleteListener {
                        val intent = Intent(this, PageMain::class.java)
                        startActivity(intent)
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error $(err.message)", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "No Data Change", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserData() {
        val queryUser = FirebaseDatabase.getInstance().getReference("User").child(userId)
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(Users::class.java)
                Log.d("getUserData", "user: $user")

                //GETTING USER DATA FROM FIREBASE
                //DECLARATION
                username = user?.userUsername.toString()
                email = user?.userEmail.toString()
                password = user?.userPassword.toString()
                level = user?.userLevel.toString()
                profileId = user?.userProfilePhoto.toString()

                //SET TO VIEW
                textUsername.setText("@$username")
                editUsername.setText(username)

                //IF USER LEVEL EQUAL TO 2, HIDE CHECKBOX
                if(level.equals("2")){
                    checkbox.visibility = View.GONE
                }else{
                    checkbox.visibility = View.VISIBLE
                }

                //IF USER HAS A PROFILE
                if(profileId != null){
                    val imgLink = "https://firebasestorage.googleapis.com/v0/b/pustaku.appspot.com/o/images%2F${profileId}?alt=media&token=be406d30-7a24-4107-bc02-db83067a66e7"
                    Picasso.get().load(imgLink).placeholder(R.color.black).into(profilePhoto)
                }else{}
                //ON ALL INPUT ALREADY SET
                progress.visibility = View.GONE
                editUsername.visibility = View.VISIBLE
                checkbox.visibility = View.VISIBLE
                saveButton.visibility = View.VISIBLE
                profilePhoto.visibility = View.VISIBLE
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching user", databaseError.toException())
            }
        }
        queryUser.addListenerForSingleValueEvent(userListener)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //FOR USER PHOTO (PERMISSION)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val sourceUri: Uri = data.data!!
            val destinationUri: Uri = Uri.fromFile(File(cacheDir, "cropped"))
            val options = UCrop.Options()
            options.setToolbarColor(ContextCompat.getColor(this, R.color.white))
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.white))
            options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.white))
            options.setToolbarTitle("Crop Image")
            UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withOptions(options)
                .start(this)
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imageUri = UCrop.getOutput(data!!)
            gambarSet.setImageURI(imageUri)
            textFoto.setText("")
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Error ${UCrop.getError(data!!)}", Toast.LENGTH_SHORT).show()
        }else{
            return
        }
    }

    private fun openDialog(
        userId : String, userEmail : String
    ){
        //SETTING DIALOG VIEW
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.dialog_addpermis, null)
        mDialog.setView(mDialogView)

        //SETTING VIEW
        val usernameText : EditText = mDialogView.findViewById(R.id.editInputNama)
        val alasanText : EditText = mDialogView.findViewById(R.id.editInputAlasan)
        val buttonKirim : Button = mDialogView.findViewById(R.id.buttonKirimPermintaan)
        gambarSet = mDialogView.findViewById(R.id.imageSetFotoAsli)
        textFoto = mDialogView.findViewById(R.id.textSetFoto)
        val gambarView : LinearLayout = mDialogView.findViewById(R.id.linearFotoAsliWrapper)
        gambarView.setOnClickListener {
            selectImage()
        }

        //SET VIEW
        usernameText.setText(userEmail)
        val alasan = alasanText.text.toString()
        if(alasan.isEmpty()){
            alasanText.setError("Harap isi semua")
        }


        val imgId = "$userId-$date-perm"
        val permission = Permission(userId, userEmail, alasan, imgId, "noSend")
        val alertDialog = mDialog.create()
        alertDialog.show()

        //UPLOAD IMAGE
        fun uploadImage(onComplete: () -> Unit) {
            val storageReference = FirebaseStorage.getInstance().getReference("images/$imgId")

            if (imageUri != null) {
                storageReference.putFile(imageUri!!)
                    .addOnSuccessListener {
                        imageUri = null
                        gambarSet.setImageResource(R.color.black)
                        onComplete()

                    }.addOnFailureListener {
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Mohon masukkan foto", Toast.LENGTH_SHORT).show()
            }
        }


        buttonKirim.setOnClickListener{
            //UPLOADING DATA
            if(imageUri != null && usernameText.text.isNotEmpty() && alasanText.text.isNotEmpty() && imgId.isNotEmpty()){
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Uploading data...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                PermissiondbRef.child(userId).setValue(permission).addOnCompleteListener{
                    uploadImage {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        Toast.makeText(this, "Permintaan anda terkirim. Mohon tunggu....", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }.addOnFailureListener{
                    Toast.makeText(this, "Error $(err.message)", Toast.LENGTH_SHORT).show()
                }
            }
            checkbox.setChecked(true)
            checkbox.isVisible = false
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
    }


}