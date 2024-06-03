package com.community.codersaidhub.UI.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.community.codersaidhub.R

import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //SETTING COLORPRIMARYDARK

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }



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