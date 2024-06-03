package com.community.codersaidhub.UI.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.community.codersaidhub.Adapters.homePostAdapter
import com.community.codersaidhub.Adapters.homeProjectAdapter
import com.community.codersaidhub.R
import com.community.codersaidhub.databinding.FragmentProjectPostBinding
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.models.projectModel
import com.community.codersaidhub.utils.POST_IMAGE_DETAILS
import com.community.codersaidhub.utils.POST_Project_DETAILS
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


class ProjectPostFragment : Fragment() {
    lateinit var binding:FragmentProjectPostBinding
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentProjectPostBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var projectList= mutableListOf<projectModel>()
        var adapter= homeProjectAdapter(childFragmentManager,requireContext(),projectList as ArrayList<projectModel>)
        binding.homeProjectRecyclerView.adapter=adapter
        Firebase.firestore.collection(POST_Project_DETAILS).get().addOnSuccessListener {
            var temp= mutableListOf<projectModel>()


            for(i in it.documents){
                var post= i.toObject<projectModel>()!!
                temp.add(post)

            }

            projectList.addAll(temp)
            adapter.notifyDataSetChanged()
        }
    }


}