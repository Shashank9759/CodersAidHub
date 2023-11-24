package com.example.instaranjan.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class uploadImage(uri : Uri,folderName:String,callback:(String?)->Unit){
    var imageUrl:String?=null
    val storage = FirebaseStorage.getInstance().getReference(folderName)
        .child(UUID.randomUUID().toString())
        .putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {

                imageUrl= it.toString()
                callback(imageUrl)
            }

        }



}