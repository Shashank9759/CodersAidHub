package com.community.codersaidhub.UI.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.community.codersaidhub.Adapters.homePostAdapter
import com.community.codersaidhub.R
import com.community.codersaidhub.databinding.FragmentPhotoPostBinding
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.utils.POST_IMAGE_DETAILS
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


class PhotoPostFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    lateinit var binding:FragmentPhotoPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPhotoPostBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var postList= mutableListOf<NewPostModel>()
        var adapter= homePostAdapter(childFragmentManager,requireContext(),postList as ArrayList<NewPostModel>)
        binding.homePostRecyclerView.adapter=adapter

        Firebase.firestore.collection(POST_IMAGE_DETAILS).get().addOnSuccessListener {
            var temp= mutableListOf<NewPostModel>()


            for(i in it.documents){
                var post= i.toObject<NewPostModel>()!!
                temp.add(post)

            }

            postList.addAll(temp)
            adapter.notifyDataSetChanged()
        }
    }
}