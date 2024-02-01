package com.example.instaranjan.UI.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.instaranjan.Adapters.combineAdapter.Companion.postViewType


import com.example.instaranjan.R
import com.example.instaranjan.databinding.ActivityAddPostActivityBinding
import com.example.instaranjan.models.CombineReelPostModel
import com.example.instaranjan.models.NewPostModel
import com.example.instaranjan.models.User
import com.example.instaranjan.utils.CAMERA_PERMISSION_CODE
import com.example.instaranjan.utils.COMBINE_IMAGE_REEL
import com.example.instaranjan.utils.POST_CAMERA_REQ_CODE
import com.example.instaranjan.utils.POST_IMAGE
import com.example.instaranjan.utils.POST_IMAGE_DETAILS
import com.example.instaranjan.utils.USER_NODE
import com.example.instaranjan.utils.uploadImage

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.util.UUID

class addPostAvtivity : AppCompatActivity() {

    lateinit var dialog :AlertDialog



    private val binding: ActivityAddPostActivityBinding by lazy {
        ActivityAddPostActivityBinding.inflate(layoutInflater)
    }




    lateinit var newPostModel: NewPostModel
    private val launcher= registerForActivityResult(ActivityResultContracts.GetContent()){
            uri->


            if(uri==null){

            dialog.dismiss()
                return@registerForActivityResult

        }
            else {
                binding.horizontalProgressBar.visibility = View.VISIBLE
                binding.uploadPost.setText("Uploading..")
                binding.uploadPost.isEnabled = false
                binding.newPostImage.setImageURI(uri)
                dialog.dismiss()
                uploadImage(this, uri, POST_IMAGE, binding.horizontalProgressBar) {
                    if (!it.isNullOrEmpty()) {
                        newPostModel.image = it
                        binding.horizontalProgressBar.visibility = View.GONE
                        binding.uploadPost.isEnabled = true
                        binding.uploadPost.setText("Post")

                    }

                }
            }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //SETTING COLORPRIMARYDARK

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }



         newPostModel=NewPostModel()

        var user: User= User()



        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.topAppBar.setNavigationOnClickListener {
            finish()

        }

        binding.newPostImage.setOnClickListener {



            showCustomDialog()
        }

        binding.uploadPost.setOnClickListener {
            newPostModel.caption=binding.postcaption.text.toString()
            if (  newPostModel.image!=null
            ) {

                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                        it->
                    user=it.toObject<User>()!!

                  newPostModel.user=user

                   newPostModel.date= System.currentTimeMillis()
                }.addOnSuccessListener {
                    val randomDocumentId = UUID.randomUUID().toString()

                    newPostModel.postUID=randomDocumentId
                    Firebase.firestore.collection(POST_IMAGE_DETAILS).document(randomDocumentId).set(newPostModel)
                        .addOnSuccessListener {
                            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(randomDocumentId)
                                .set(newPostModel).addOnSuccessListener {
                                    val combineReelPostModel= CombineReelPostModel()
                                    combineReelPostModel.postModel=newPostModel
                                    combineReelPostModel.viewType= postViewType

                                    Firebase.firestore.collection(COMBINE_IMAGE_REEL).document(randomDocumentId)
                                        .set(combineReelPostModel).addOnSuccessListener {
                                            Toast.makeText(
                                                this,
                                                "Post Uploaded",
                                                Toast.LENGTH_SHORT
                                            ).show()


                                            startActivity(Intent(this@addPostAvtivity,Home_Activity::class.java))
                                            finish()

                                        }


                                }

                        }


                }




            } else {
                Toast.makeText(this, "Fill Required Information", Toast.LENGTH_SHORT).show()
            }

        }


        binding.cancelPost.setOnClickListener {
            val intent= Intent(this,Home_Activity::class.java)
            startActivity(intent)
            finish()

        }

    }



    private fun showCustomDialog() {
        // Create an AlertDialog.Builder instance
        val builder = AlertDialog.Builder(this)

        // Inflate the custom layout for the dialog
        val dialogView = layoutInflater.inflate(R.layout.custom_add_post_dialog_button, null)

        // Find the buttons in the custom layout
        val PostGallery = dialogView.findViewById<Button>(R.id.PostGallery)
        val PostCamera = dialogView.findViewById<Button>(R.id.PostCamera)

        // Set click listeners for the buttons
        PostGallery.setOnClickListener {
             launcher.launch("image/*")

        }

        PostCamera.setOnClickListener {


            if(isCameraPresent()){

                checkPermission()
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                 captureImage()
                }
                else{

                    checkPermission()
                }



            }
            else{


                Toast.makeText(this,"Camera is not Present",Toast.LENGTH_SHORT).show()
            }


        }

        // Set the custom view for the dialog
        builder.setView(dialogView)

        // Create and show the AlertDialog
         dialog = builder.create()
        dialog.show()



    }



    private fun checkPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.CAMERA),CAMERA_PERMISSION_CODE)
        } else {
            // Permission is already granted, you can proceed with using the camera
            // Add your camera-related logic here
        }

    }


    private fun isCameraPresent():Boolean{

          if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){

              return true
          }
        else{
            return false
          }
    }



    private fun captureImage(){


        val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,POST_CAMERA_REQ_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==POST_CAMERA_REQ_CODE){
            dialog.dismiss()
            if(resultCode==RESULT_OK){
           try{
               val imageBitmap = data?.extras?.get("data") as? Bitmap

               val convertedUri = bitmapToUri(imageBitmap!!)


               binding.horizontalProgressBar.visibility = View.VISIBLE
               binding.uploadPost.setText("Uploading..")
               binding.uploadPost.isEnabled = false
               binding.newPostImage.setImageURI(convertedUri)
               uploadImage(this,convertedUri, POST_IMAGE,binding.horizontalProgressBar) {
                   if(!it.isNullOrEmpty()){
                       newPostModel.image = it
                       binding.horizontalProgressBar.visibility = View.GONE
                       binding.uploadPost.isEnabled = true
                       binding.uploadPost.setText("Post")
                   }
               }

           }catch (e:Exception){

               Toast.makeText(this,"$e",Toast.LENGTH_LONG).show()
           }

            }


        }




    }

    private fun bitmapToUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

}