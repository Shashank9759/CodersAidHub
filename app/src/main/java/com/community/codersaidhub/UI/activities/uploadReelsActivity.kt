package com.community.codersaidhub.UI.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.community.codersaidhub.Adapters.combineAdapter.Companion.reelViewType
import com.community.codersaidhub.R

import com.community.codersaidhub.databinding.ActivityUploadReelsBinding
import com.community.codersaidhub.models.CombineReelPostModel
import com.community.codersaidhub.models.NewReelModel
import com.community.codersaidhub.models.User
import com.community.codersaidhub.utils.CAMERA_PERMISSION_CODE
import com.community.codersaidhub.utils.COMBINE_IMAGE_REEL
import com.community.codersaidhub.utils.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_CODE

import com.community.codersaidhub.utils.POST_Reel_DETAILS
import com.community.codersaidhub.utils.UPLOAD_REEL
import com.community.codersaidhub.utils.UPLOAD_VIDEO_REQ_CODE
import com.community.codersaidhub.utils.USER_NODE

import com.community.codersaidhub.utils.uploadVideo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.File
import java.util.UUID

class uploadReelsActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog
    lateinit var dialog :AlertDialog
    lateinit var handler: Handler

    lateinit var currentCaption:String

    lateinit var currentCaption2:String
    lateinit var  captionHashset :HashSet<String>

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



            val selectedVideoPath = getFilePathFromUri(this, uri)

            // Example destination path in external storage
            val destinationDir = this.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
            val destinationPath = File(destinationDir, "compressed_video.mp4").path
            binding.videoframe.visibility=View.GONE


            // binding.linearlayoutAI2.visibility=View.VISIBLE

     //    edittextTypewriterAnimationHandler("Use AI to write Tag.")
         //  val id= VideoCompressAsyncTask(this.execute(selectedVideoPath, f.getPath())

            uploadVideo(this@uploadReelsActivity, uri, UPLOAD_REEL, progressDialog) {
                if (!it.isNullOrEmpty()) {
                    newReelModel.video = it
                    videoUploadOrNot = "UPLOADED"
                    binding.uploadReel.isEnabled = true
                    binding.uploadReel.setText("Post")
                    binding.videoframe.visibility=View.VISIBLE
                    val mediaController = MediaController(this)
                    mediaController.setAnchorView(binding.reelVideoView)
                    binding.reelVideoView.setMediaController(mediaController)
                    binding.reelVideoView.setVideoURI(uri)
                    binding.reelVideoView.start()

                    Log.d("Uri@@@@",uri.toString())
                }
            }



        }



    }


    private fun getFilePathFromUri(context: Context, uri: Uri): String {
        // Code to get the file path from URI will depend on your specific implementation
        // Here's a simple example using content resolver
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex("_data")
            if (columnIndex != -1) {
                val filePath = it.getString(columnIndex)
                it.close()
                return filePath
            }
        }
        return uri.path ?: ""
    }

    private fun edittextTypewriterAnimationHandler(captionText:String) {
        handler.removeCallbacksAndMessages(null)

        currentCaption=""

        var delay = 0L
        for (i in captionText.indices) {
            handler.postDelayed({
                currentCaption += captionText[i]
                binding.reelcaption.hint = currentCaption
                if (currentCaption == captionText) {
                    // Animation completed
                }
            }, delay)
            delay += 100
        }

    }






    private fun imagemllistner(listofBitmap:List<Bitmap>,index:Int) {

        if(index==listofBitmap.size){
            return
        }
        val image: InputImage
        image = InputImage.fromBitmap(listofBitmap.get(index), 0)

        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->

                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    val index = label.index
                    captionHashset.add("#"+text)


                }
              //  imagemllistner(listofBitmap,index+1)

            }
            .addOnFailureListener { e ->
                Log.d("@@@@@",e.toString())
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
        //setting Caption hint animation

        handler=Handler()





       edittextTypewriterAnimationHandler("Write Your Caption.")
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
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is not granted, request the permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_CODE
                    )
                } else {
                    launcher.launch("video/*")
                }






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



    fun isStoragePermissionGranted(context: Context):Boolean {

        // Check if the permission is granted
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        // Return true if permission is granted, false otherwise
        return permissionCheck == PackageManager.PERMISSION_GRANTED

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







    fun extractFramesFromVideo(context: Context, videoUri: Uri, frameIntervalMs: Long): List<Bitmap> {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)

        val frames = mutableListOf<Bitmap>()
        var timeMs = 0L

        while (true) {
            val frame = retriever.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?: break // No more frames available

            frames.add(frame)

            // Move to the next frame
            timeMs += frameIntervalMs
        }

        retriever.release()
        return frames
    }


}





