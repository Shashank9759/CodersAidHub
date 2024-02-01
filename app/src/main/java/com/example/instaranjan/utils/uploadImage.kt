package com.example.instaranjan.utils

import android.content.Context
import android.net.Uri
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class uploadImage{
    var imageUrl: String? = null
    constructor(uri : Uri,folderName:String,callback:(String?)->Unit) {

        val storage = FirebaseStorage.getInstance().getReference(folderName)
            .child(UUID.randomUUID().toString())
            .putFile(uri).addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {

                    this.imageUrl = it.toString()
                    callback(imageUrl)
                }

            }

    }

    constructor(context: Context, uri : Uri, folderName:String, progressBar:ProgressBar, callback:(String?)->Unit){

        val storage = FirebaseStorage
            .getInstance().getReference(folderName)
            .child(UUID.randomUUID().toString())
            .putFile(uri).addOnProgressListener {taskSnapshot ->

            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            progressBar.progress = progress
            }
            .addOnCompleteListener {

                if (it.isSuccessful){
                    it.result.storage.downloadUrl.addOnSuccessListener {

                        this.imageUrl = it.toString()
                        callback(imageUrl)
                    }

                }
                else{

                    Toast.makeText(context,"upload failed",Toast.LENGTH_LONG).show()
                }
            }
            }

}