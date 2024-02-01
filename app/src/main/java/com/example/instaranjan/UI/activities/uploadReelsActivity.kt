package com.example.instaranjan.UI.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.instaranjan.Adapters.combineAdapter.Companion.reelViewType
import com.example.instaranjan.R

import com.example.instaranjan.databinding.ActivityUploadReelsBinding
import com.example.instaranjan.models.CombineReelPostModel
import com.example.instaranjan.models.NewPostModel
import com.example.instaranjan.models.NewReelModel
import com.example.instaranjan.models.User
import com.example.instaranjan.utils.CAMERA_PERMISSION_CODE
import com.example.instaranjan.utils.COMBINE_IMAGE_REEL
import com.example.instaranjan.utils.POST_CAMERA_REQ_CODE
import com.example.instaranjan.utils.POST_IMAGE

import com.example.instaranjan.utils.POST_IMAGE_DETAILS
import com.example.instaranjan.utils.POST_Reel_DETAILS
import com.example.instaranjan.utils.UPLOAD_REEL
import com.example.instaranjan.utils.UPLOAD_VIDEO_REQ_CODE
import com.example.instaranjan.utils.USER_NODE
import com.example.instaranjan.utils.uploadImage
import com.example.instaranjan.utils.uploadVideo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.util.UUID

class uploadReelsActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog
    lateinit var dialog :AlertDialog

    private val binding by lazy{

        ActivityUploadReelsBinding.inflate(layoutInflater)
    }


    var  videoUploadOrNot:String?= null

    lateinit var newReelModel: NewReelModel
    private val launcher= registerForActivityResult(ActivityResultContracts.GetContent()){
            uri->
        if(uri==null){
            dialog.dismiss()
        }
        else {
            dialog.dismiss()
            progressDialog.show()
            progressDialog.max = 100
            binding.uploadReel.setText("Uploading..")
            binding.uploadReel.isEnabled = false
            uploadVideo(this@uploadReelsActivity, uri, UPLOAD_REEL, progressDialog) {
                if (!it.isNullOrEmpty()) {
                    newReelModel.video = it
                    videoUploadOrNot = "UPLOADED"
                    binding.uploadReel.isEnabled = true
                    binding.uploadReel.setText("Post")

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



        progressDialog = ProgressDialog(this)

        progressDialog.setOnCancelListener {
            val intent= Intent(this,Home_Activity::class.java)
            startActivity(intent)
            finish()

        }
        newReelModel=NewReelModel()
        var user: User= User()



        setSupportActionBar(binding.topAppBarReels)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.topAppBarReels.setNavigationOnClickListener {
            finish()

        }

        binding.newReel.setOnClickListener {
            showCustomDialog()



        }







        binding.uploadReel.setOnClickListener {

            newReelModel.caption=binding.reelcaption.text.toString()
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                    it->
                user=it.toObject<User>()!!

                newReelModel.user=user

            }.addOnSuccessListener {
                val randomDocumentId = UUID.randomUUID().toString()

                newReelModel.postUID=randomDocumentId


                if (!videoUploadOrNot.isNullOrEmpty()) {
                    Firebase.firestore.collection(POST_Reel_DETAILS).document().set(newReelModel)
                        .addOnSuccessListener {
                            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+"reel").document()
                                .set(newReelModel).addOnSuccessListener {

                                    val combineReelPostModel= CombineReelPostModel()
                                    combineReelPostModel.reelModel=newReelModel
                                    combineReelPostModel.viewType= reelViewType
                                    Firebase.firestore.collection(COMBINE_IMAGE_REEL).document(randomDocumentId)
                                        .set(combineReelPostModel).addOnSuccessListener {
                                            Toast.makeText(
                                                this,
                                                "Reel Uploaded",
                                                Toast.LENGTH_SHORT
                                            ).show()


                                            startActivity(Intent(this@uploadReelsActivity,Home_Activity::class.java))
                                            finish()


                                        }


                                }


                        }
                } else {
                    Toast.makeText(this, "Fill Required Information", Toast.LENGTH_SHORT).show()
                }


            }



        }

        binding.cancelReel.setOnClickListener {
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
            launcher.launch("video/*")

        }

        PostCamera.setOnClickListener {


            if(isCameraPresent()){

                checkPermission()
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    captureVideo()

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
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
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



    private fun captureVideo(){


        val intent=Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10)
        startActivityForResult(intent, UPLOAD_VIDEO_REQ_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== UPLOAD_VIDEO_REQ_CODE){
            dialog.dismiss()
            if(resultCode==RESULT_OK){
                try{
                   val uri= data?.data



                    if(uri!=null) {
                        dialog.dismiss()
                        progressDialog.show()
                        progressDialog.max = 100
                        binding.uploadReel.setText("Uploading..")
                        binding.uploadReel.isEnabled = false
                        uploadVideo(this@uploadReelsActivity, uri, UPLOAD_REEL, progressDialog) {
                            if (!it.isNullOrEmpty()) {
                                newReelModel.video = it
                                videoUploadOrNot = "UPLOADED"
                                binding.uploadReel.isEnabled = true
                                binding.uploadReel.setText("Post")

                            }
                        }

                    }
                }catch (e:Exception){

                    Toast.makeText(this,"$e",Toast.LENGTH_LONG).show()
                }

            }


        }




    }








}





