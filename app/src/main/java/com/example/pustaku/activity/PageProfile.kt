package com.example.pustaku.activity

import android.app.Activity
import android.app.ProgressDialog
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
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
    private lateinit var editPassword : EditText
    private lateinit var profilePhoto : CircleImageView
    private lateinit var saveButton : Button
    private lateinit var cropActivityResultLauncher : ActivityResultLauncher<Any?>

    //FOR DATA
    private var imageUri : Uri? = null
    private var profileUri : Uri? = null
    private lateinit var username : String
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var level : String

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
        editPassword = findViewById(R.id.editInputPasswordEdit)
        checkbox = findViewById(R.id.checkboxInputEnable)
        saveButton = findViewById(R.id.buttonSaveProfile)
        profilePhoto = findViewById(R.id.imageSetProfile)

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

        //GETTING USER ID FROM FIREBASE AUTH
        firebaseAuth = FirebaseAuth.getInstance()
        val userId: String = firebaseAuth.currentUser!!.uid

        //INSERTING USER ID TO MATCHING DATA IN USER DATABASE
        UserdbRef = FirebaseDatabase.getInstance().getReference("User")
        PermissiondbRef = FirebaseDatabase.getInstance().getReference("Permission")

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

        //GETTING USER LEVEL FROM USER FIELD
        val getLevel = UserdbRef.child(userId).child("userLevel").get().addOnSuccessListener {
            level = it.value.toString()
            if(level.equals("2")){
                checkbox.visibility = View.GONE
            }else{
                checkbox.visibility = View.VISIBLE
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        //GETTING USER USERNAME FROM USER FIELD
        val getUsername = UserdbRef.child(userId).child("userUsername").get().addOnSuccessListener {
            username = it.value as String
            textUsername.setText("@$username")
            editUsername.setText(username)
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        //GETTING USER PASSWORD FROM USER FIELD
        val getPassword = UserdbRef.child(userId).child("userPassword").get().addOnSuccessListener {
            password = it.value as String
            editPassword.setText(password)
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        //GETTING USER EMAIL FROM USER FIELD
        val getEmail = UserdbRef.child(userId).child("userEmail").get().addOnSuccessListener {
            email = it.value as String
            //WHEN CHECKBOX CLICK
            checkbox.setOnClickListener {
                if (checkbox.isChecked) {
                    checkbox.toggle() //REMOVE CHECKED IN CHECKBOX
                    openDialog(userId, email)
                } else {
                    checkbox.toggle()
                }
            }
        }

        //GETTING USER PROFILE PHOTO FROM USER FIELD
        val getProfilePhoto = UserdbRef.child(userId).child("userProfilePhoto").get().addOnSuccessListener {
                if (it.value != null) {
                    val imgLink = "https://firebasestorage.googleapis.com/v0/b/pustaku.appspot.com/o/images%2F${it.value}?alt=media&token=be406d30-7a24-4107-bc02-db83067a66e7"
                    Picasso.get().load(imgLink).placeholder(R.color.black).into(profilePhoto)
                }else{
                    profilePhoto.setImageResource(R.drawable.empty_profile)
                }
            }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        saveButton.setOnClickListener {
            val updateUsername = editUsername.text.toString()
            val updatePassword = editPassword.text.toString()
            //GETTING PROFILE ID
            val profileId = "$userId-$date-user"
            val userUpdate = Users(userId, updateUsername, email, updatePassword, level.toInt(), profileId)

            //UPLOAD PROFILE PICT TO FIREBASE STORAGE
            fun uploadProfile(onComplete: () -> Unit) {
                val storageReference = FirebaseStorage.getInstance().getReference("images/$profileId")
                if (profileUri != null) {
                    storageReference.putFile(profileUri!!)
                        .addOnSuccessListener {
                            profileUri = null
                            profilePhoto.setImageResource(R.color.black)
                            onComplete()
                        }.addOnFailureListener {
                            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Mohon masukan foto", Toast.LENGTH_SHORT).show()
                }
            }

            //CHECKING IF ALL FIELD IS FILL
            if (profileUri != null && updateUsername.isNotEmpty() && updatePassword.isNotEmpty() && profileId.isNotEmpty()) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Updating data....")
                progressDialog.setCancelable(false)
                progressDialog.show()

                UserdbRef.child(userId).setValue(userUpdate).addOnCompleteListener {
                    uploadProfile {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        Toast.makeText(this, "Mohon tunggu....", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Error $(err.message)", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all field", Toast.LENGTH_SHORT).show()
            }
        }
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