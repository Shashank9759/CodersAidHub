package com.example.instaranjan.UI.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts


import com.example.instaranjan.databinding.ActivitySignupBinding
import com.example.instaranjan.models.User
import com.example.instaranjan.utils.PROFILE_IMAGE
import com.example.instaranjan.utils.USER_NODE
import com.example.instaranjan.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class Signup_Activity : AppCompatActivity() {

    val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }
    val db = Firebase.firestore
    lateinit var user: User

private val launcher= registerForActivityResult(ActivityResultContracts.GetContent()){
    uri->
    uri?.let {
        binding.progressBar.visibility = View.VISIBLE
        binding.registerButton.setText("Please Wait...")
        binding.registerButton.isEnabled = false

        uploadImage(uri, PROFILE_IMAGE) {
                if (it == null) {

                } else {
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                    binding.progressBar.visibility = View.GONE
                    binding.registerButton.isEnabled = true
                    if(intent.hasExtra("insta")) {
                        if (intent.getIntExtra("insta", -1) == 1) {
                            binding.registerButton.setText("Update")
                        }
                    }else {
                        binding.registerButton.setText("Register")
                    }
                }
            }
        }





}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
         user=User()
  binding.progressBar.visibility=View.GONE
        //login button coloring
        binding.login.text= Html.fromHtml("<font color=${Color.BLACK}>Already have an account </font>" +
                "<font color=#405DE6>Login ?</font>")




        binding.login.setOnClickListener {

            startActivity(Intent(this,Login_Activity::class.java))
            finish()
        }



        if(intent.hasExtra("insta")){
            if(intent.getIntExtra("insta",-1)==1){
                binding.registerButton.setText("Update")
                binding.login.visibility= View.GONE

                db.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        val edit_User= it.toObject<User>()!!
                        if(!edit_User?.image.isNullOrEmpty()){


                            Picasso.get().load(edit_User.image).into(object :Target{
                                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                    binding.progressBar.visibility= View.GONE
                                    binding.profileImage.setImageBitmap(bitmap);
                                }

                                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                                    Toast.makeText(this@Signup_Activity,"Image Uploading Failed",Toast.LENGTH_SHORT).show()
                                    binding.progressBar.visibility= View.GONE

                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                    binding.progressBar.visibility= View.VISIBLE
                                }

                            })
                        }
                        binding.registerName.editText?.setText(edit_User?.name)
                        binding.registerEmail.editText?.setText(edit_User?.email)
                        user.email=edit_User.email
                        binding.registerEmail.editText?.isClickable=false




                    }




            }
        }
        binding.registerButton.setOnClickListener {

            if(intent.hasExtra("insta")){
                if(intent.getIntExtra("insta",-1)==1){
                    val currentUser = FirebaseAuth.getInstance().currentUser

// Assuming the user is signed in
                    currentUser?.let {

                             // now update password
                                    val newPassword =binding.registerPassword.editText?.text.toString()
                                    it.updatePassword(newPassword)
                                        .addOnCompleteListener { taskPasswordUpdate ->
                                            if (taskPasswordUpdate.isSuccessful) {
                                                // Password update successful
                                                // Update user data in Firestore if needed
                                                user.password = newPassword
                                                user.name= binding.registerName.editText?.text.toString()
                                                db.collection(USER_NODE).document(it.uid).set(user)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(this,"Information Updated",Toast.LENGTH_SHORT).show()
                                                        startActivity(Intent(this,Home_Activity::class.java))
                                                        finish()
                                                    }
                                            } else {
                                                // Handle password update failure
                                                Toast.makeText(
                                                    this,
                                                    "${taskPasswordUpdate.exception?.localizedMessage}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            }

            }
            else {

                if (binding.registerName.editText?.text.toString() == "" || binding.registerEmail.editText?.text.toString() == ""
                    || binding.registerPassword.editText?.text.toString() == ""
                ) {

                    Toast.makeText(this, "Please Fill Required Information", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        binding.registerEmail.editText?.text.toString(),
                        binding.registerPassword.editText?.text.toString()
                    )
                        .addOnCompleteListener { result ->
                            if (result.isSuccessful) {
                                user.email = binding.registerEmail.editText?.text.toString()
                                user.name = binding.registerName.editText?.text.toString()
                                user.password = binding.registerPassword.editText?.text.toString()
                                db.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
                                    .set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Registeration Successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this, Home_Activity::class.java))
                                        finish()
                                    }
                            } else {
                                Toast.makeText(
                                    this,
                                    result.exception?.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        }
                }
            }
        }





        binding.add.setOnClickListener {
            launcher.launch("image/*")






        }
    }
}