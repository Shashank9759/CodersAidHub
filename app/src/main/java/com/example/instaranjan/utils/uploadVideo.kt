package com.example.instaranjan.utils

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.example.instaranjan.R
import com.example.instaranjan.UI.activities.Home_Activity
import com.example.instaranjan.UI.activities.uploadReelsActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import java.util.UUID

class uploadVideo {
    var videoUrl: String? = null


    constructor(context: Context, uri : Uri, folderName:String, progressBar:ProgressDialog, callback:(String?)->Unit){
progressBar.setTitle("Uploading.....")
       progressBar.setCancelable(true)

        progressBar.setCanceledOnTouchOutside(false)
        val storage = FirebaseStorage
            .getInstance().getReference(folderName)
            .child(UUID.randomUUID().toString())
            .putFile(uri)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {

                    this.videoUrl = it.toString()
                   progressBar.dismiss()
                    callback(videoUrl)
                }

            }.addOnFailureListener{

                Toast.makeText(context,"uploading falied :  ${(it as StorageException).errorCode} ",Toast.LENGTH_LONG).show()
                progressBar.dismiss()
             var postButton= (context as uploadReelsActivity).findViewById<Button>(R.id.upload_reel)
                postButton.text="Post"

            }.addOnProgressListener {taskSnapshot ->

                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                progressBar.setMessage("Uploaded  $progress%")

            }
    }

}