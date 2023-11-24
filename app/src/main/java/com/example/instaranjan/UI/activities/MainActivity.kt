package com.example.instaranjan.UI.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.instaranjan.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor=Color.TRANSPARENT





        Handler(Looper.getMainLooper()).postDelayed({

            if(FirebaseAuth.getInstance().currentUser==null){
                startActivity(Intent(this,Signup_Activity::class.java))
                finish()
            }
           else{
                startActivity(Intent(this,Home_Activity::class.java))
                finish()

            }

        },2000)
    }
}