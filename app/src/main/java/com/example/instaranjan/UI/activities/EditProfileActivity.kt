package com.example.instaranjan.UI.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Transition
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.instaranjan.R
import com.example.instaranjan.databinding.ActivityEditProfileBinding
import com.example.instaranjan.databinding.ActivityExploreBinding
import com.example.instaranjan.models.User
import com.example.instaranjan.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class EditProfileActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityEditProfileBinding.inflate(layoutInflater)
    }
    var edit_User=User()
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//setting progress bar
        setSupportActionBar(binding.editProfileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.editProfileToolbar.setNavigationOnClickListener {
             if(isUserAnythingChanged()){
                 saveEdit()
             }


            finish()
        }
   //setting primarydarkcolor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }






                db.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                       edit_User= it.toObject<User>()!!
                        if(edit_User==null){
                            Toast.makeText(this,"null User",Toast.LENGTH_LONG).show()
                        }
                        if(!edit_User?.image.isNullOrEmpty()){


                            Glide.with(this@EditProfileActivity)
                                .asBitmap()
                                .load(edit_User.image)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                                    ) {
                                        binding.editPB.visibility = View.GONE
                                        binding.editProfileImage.setImageBitmap(resource)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {

                                        binding.editPB.visibility = View.GONE
                                    }

                                    override fun onLoadFailed(errorDrawable: Drawable?) {
                                        super.onLoadFailed(errorDrawable)
                                        Toast.makeText(this@EditProfileActivity, "Image Uploading Failed", Toast.LENGTH_SHORT).show()
                                        binding.editPB.visibility = View.GONE
                                    }

                                })

                        }
                        binding.editName.setText(edit_User?.name)
                        binding.editEmail.setText(edit_User?.email)
                        binding.editBio.setText(edit_User?.bio)





                    }







    }


    private fun saveEdit(){

                val currentUser = FirebaseAuth.getInstance().currentUser

// Assuming the user is signed in,
                currentUser?.let {
                    val user=User()


                    val bio= binding.editBio.text.toString()
                    val userMap = hashMapOf( "bio" to bio)
                    val userUpdates: MutableMap<String, Any?> = userMap.toMutableMap()
                    db.collection(USER_NODE).document(it.uid).update(userUpdates)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Information Updated",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,Home_Activity::class.java))
                            finish()
                        }



        }
    }
    private fun isUserAnythingChanged() :Boolean{

        if(edit_User.bio!=binding.editBio.text.toString()  ){

            return true
        }
        else{
            return false
        }
    }

    override fun onBackPressed() {
        if(isUserAnythingChanged()){
            saveEdit()
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        if(isUserAnythingChanged()){
            saveEdit()
        }
        super.onDestroy()
    }
}