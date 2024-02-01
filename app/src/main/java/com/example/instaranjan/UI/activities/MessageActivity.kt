package com.example.instaranjan.UI.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.instaranjan.R
import com.example.instaranjan.databinding.ActivityMessageBinding
import com.example.instaranjan.models.User
import com.example.instaranjan.utils.USER_NODE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityMessageBinding.inflate(layoutInflater)
    }
    val firebaseCurrentUser= Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
    lateinit var currentUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //setting toolbar
        setSupportActionBar(binding.messangerToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.messangerToolbar.setNavigationOnClickListener {
            finish()

        }

        //setting primarydarkcolor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }




        CoroutineScope(Dispatchers.IO).launch {


            firebaseCurrentUser.get().addOnSuccessListener {

                currentUser = it.toObject<User>()!!
                binding.messangerToolbar.title= currentUser.name
            }
        }
    }
}