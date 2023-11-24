package com.example.instaranjan.UI.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.instaranjan.R
import com.example.instaranjan.databinding.ActivityLoginBinding
import com.example.instaranjan.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login_Activity : AppCompatActivity() {
    val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)





        binding.LoginButton.setOnClickListener {
            val email= binding.loginEmail.editText?.text.toString()
            val password= binding.loginPassword.editText?.text.toString()

            if(email=="" || password==""){
                Toast.makeText(this,"Please Fill Required Information", Toast.LENGTH_SHORT).show()
            }
            else{
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener {
                        result->
                    if(result.isSuccessful){
                        Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,Home_Activity::class.java))
                        finish()

                    }
                    else{
                        Toast.makeText(this,result.exception?.localizedMessage,Toast.LENGTH_SHORT).show()

                    }

                }
            }
        }

        binding.SignUpButton.setOnClickListener {


            startActivity(Intent(this,Signup_Activity::class.java))
            finish()
        }


    }
}