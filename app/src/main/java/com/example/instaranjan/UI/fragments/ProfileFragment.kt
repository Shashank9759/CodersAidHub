package com.example.instaranjan.UI.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.instaranjan.UI.activities.Signup_Activity
import com.example.instaranjan.databinding.FragmentProfileBinding
import com.example.instaranjan.models.Adapters.ViewPagerAdapter
import com.example.instaranjan.models.User
import com.example.instaranjan.utils.USER_NODE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {
   lateinit var user:User
lateinit var binding :FragmentProfileBinding

private lateinit var viewPagerAdater:ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProfileBinding.inflate(layoutInflater,container,false)
        binding.profileEdit.setOnClickListener {

            val intent= Intent(activity,Signup_Activity::class.java)
            intent.putExtra("insta",1)

            activity?.startActivity(intent)
        }


        viewPagerAdater=ViewPagerAdapter(requireActivity().supportFragmentManager)
        viewPagerAdater.addFragments(MyPostFragment(),"My Posts")
        viewPagerAdater.addFragments(MyReelsFragment(),"My Reels")
        binding.profileViewpager.adapter=viewPagerAdater
        binding.profileTablayout.setupWithViewPager(binding.profileViewpager)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                user= it.toObject<User>()!!
                if(!user?.image.isNullOrEmpty()){
                    Picasso.get().load(user?.image).into(binding.myProfileImage);
                }
                binding.profileName.setText(user?.name)
                binding.profileBio.setText(user?.email)




            }




    }


    }
