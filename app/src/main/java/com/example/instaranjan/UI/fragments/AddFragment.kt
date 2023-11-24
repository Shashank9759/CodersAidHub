package com.example.instaranjan.UI.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instaranjan.R
import com.example.instaranjan.UI.activities.addPostAvtivity
import com.example.instaranjan.databinding.FragmentAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddFragment : BottomSheetDialogFragment() {

lateinit var binding:FragmentAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding= FragmentAddBinding.inflate(layoutInflater,container,false)

        binding.addPost.setOnClickListener {
            val intent = Intent(activity,addPostAvtivity::class.java)
            startActivity(intent)
            fragmentManager?.beginTransaction()?.remove(this)?.commit()


        }

        binding.uploadReels.setOnClickListener {


        }
        return binding.root
    }



}