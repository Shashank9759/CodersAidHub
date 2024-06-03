package com.community.codersaidhub.UI.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.community.codersaidhub.Adapters.combineAdapter.Companion.postViewType
import java.io.FileOutputStream
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide


import com.community.codersaidhub.R
import com.community.codersaidhub.databinding.ActivityAddPostActivityBinding
import com.community.codersaidhub.models.CombineReelPostModel
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.models.User
import com.community.codersaidhub.utils.CAMERA_PERMISSION_CODE
import com.community.codersaidhub.utils.COMBINE_IMAGE_REEL
import com.community.codersaidhub.utils.POST_CAMERA_REQ_CODE
import com.community.codersaidhub.utils.POST_IMAGE
import com.community.codersaidhub.utils.POST_IMAGE_DETAILS
import com.community.codersaidhub.utils.USER_NODE
import com.community.codersaidhub.utils.uploadImage

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.UUID

class addPostAvtivity : AppCompatActivity() {

    lateinit var dialog :AlertDialog
    lateinit var handler:Handler
    lateinit var captionText:String
    lateinit var currentCaption:String
    lateinit var captionText2:String
    lateinit var currentCaption2:String




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
                //binding.newPostImage.setImageURI(uri)
                Glide.with(this).load(uri).into(binding.newPostImage);
                binding.linearlayoutAI.visibility=View.VISIBLE
                captionText="Use AI to write Tag."
                currentCaption=""
                edittextTypewriterAnimationHandler()
                setAIclick(uri,this@addPostAvtivity)
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
//    fun loadAndOptimizeImage(context: Context, imageUri: Uri, targetWidth: Int, targetHeight: Int): Bitmap? {
//        var inputStream: InputStream? = null
//        try {
//            inputStream = context.contentResolver.openInputStream(imageUri)
//            val options = BitmapFactory.Options().apply {
//                // Decode bounds only to get the dimensions of the image without loading it into memory
//                inJustDecodeBounds = true
//            }
//            BitmapFactory.decodeStream(inputStream, null, options)
//            // Calculate inSampleSize to resize the image while decoding
//            options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)
//            // Decode the image with the calculated inSampleSize
//            options.inJustDecodeBounds = false
//            inputStream?.close()
//            inputStream = context.contentResolver.openInputStream(imageUri)
//            return BitmapFactory.decodeStream(inputStream, null, options)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            inputStream?.close()
//        }
//        return null
//    }
//
//    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
//        val height = options.outHeight
//        val width = options.outWidth
//        var inSampleSize = 1
//
//        if (height > reqHeight || width > reqWidth) {
//            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
//            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
//            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
//        }
//        return inSampleSize
//    }
    private fun setAIclick(uri:Uri,context:Context) {
       binding.useAi.setOnClickListener {
           val image: InputImage
           try {
               image = InputImage.fromFilePath(context, uri)

               val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

               labeler.process(image)
                   .addOnSuccessListener { labels ->
                       captionText2=""
                       for (label in labels) {
                           val text = label.text
                           val confidence = label.confidence
                           val index = label.index
                           captionText2=captionText2+" #"+text
                       }


                       currentCaption2=""
                       edittextTypewriterAnimationHandler2()
                   }
                   .addOnFailureListener { e ->
                      Log.d("@@@@@",e.toString())
                   }
           } catch (e: IOException) {
               e.printStackTrace()
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




        //setting Caption hint animation

        handler=Handler()

        captionText="Write Your Caption."
         currentCaption=""
        edittextTypewriterAnimationHandler()



    }

    private fun edittextTypewriterAnimationHandler() {
        handler.removeCallbacksAndMessages(null)



        var delay = 0L
        for (i in captionText.indices) {
            handler.postDelayed({
                currentCaption += captionText[i]
                binding.postcaption.hint = currentCaption
                if (currentCaption == captionText) {
                    // Animation completed
                }
            }, delay)
            delay += 100
        }

       }

    private fun edittextTypewriterAnimationHandler2() {


        handler.removeCallbacksAndMessages(null)

        var delay = 0L
        for (i in captionText2.indices) {
            handler.postDelayed({
                currentCaption2 += captionText2[i]
                binding.postcaption.setText(currentCaption2)
                if (currentCaption == captionText) {
                    // Animation completed
                }
            }, delay)
            delay += 100
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

               val convertedUri = bitmapToUri(this,imageBitmap!!)


               binding.horizontalProgressBar.visibility = View.VISIBLE
               binding.uploadPost.setText("Uploading..")
               binding.uploadPost.isEnabled = false
               binding.newPostImage.setImageURI(convertedUri)
               binding.linearlayoutAI.visibility=View.VISIBLE
               captionText="Use AI to write Tag."
               currentCaption=""
               edittextTypewriterAnimationHandler()
               setAIclick(convertedUri,this@addPostAvtivity)
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

    private fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
        val imagesFolder = File(context.cacheDir, "images")
        imagesFolder.mkdirs()

        val file = File(imagesFolder, "image.jpg")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()

        return Uri.fromFile(file)
    }


}