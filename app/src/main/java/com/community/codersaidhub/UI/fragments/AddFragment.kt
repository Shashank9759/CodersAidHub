package com.community.codersaidhub.UI.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.community.codersaidhub.R
import com.community.codersaidhub.UI.activities.AddProjectActivity
import com.community.codersaidhub.UI.activities.addPostAvtivity
import com.community.codersaidhub.UI.activities.uploadReelsActivity
import com.community.codersaidhub.databinding.FragmentAddBinding
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
            val intent = Intent(activity,uploadReelsActivity::class.java)
            startActivity(intent)
            fragmentManager?.beginTransaction()?.remove(this)?.commit()

        }

        binding.addProject.setOnClickListener {
            val intent = Intent(activity,AddProjectActivity::class.java)
            startActivity(intent)
            fragmentManager?.beginTransaction()?.remove(this)?.commit()

        }
        return binding.root
    }



}