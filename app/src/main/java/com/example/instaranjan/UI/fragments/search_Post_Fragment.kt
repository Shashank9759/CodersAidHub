package com.example.instaranjan.UI.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.instaranjan.Adapters.combineAdapter
import com.example.instaranjan.Adapters.homePostAdapter
import com.example.instaranjan.R
import com.example.instaranjan.databinding.FragmentSearchPostBinding
import com.example.instaranjan.models.CombineReelPostModel
import com.example.instaranjan.models.NewPostModel
import com.example.instaranjan.models.NewReelModel
import com.example.instaranjan.utils.COMBINE_IMAGE_REEL
import com.example.instaranjan.utils.POST_IMAGE_DETAILS
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class search_Post_Fragment : Fragment() {
    lateinit var binding:FragmentSearchPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding= FragmentSearchPostBinding.inflate(layoutInflater,container,false)


        var postAndReelList= mutableListOf<CombineReelPostModel>()
        var adapter= combineAdapter(requireContext(),postAndReelList as ArrayList<CombineReelPostModel>)
        binding.searchPostRV.adapter=adapter

        CoroutineScope(Dispatchers.IO).launch {
            Firebase.firestore.collection(COMBINE_IMAGE_REEL).get().addOnSuccessListener {
                var temp= mutableListOf<CombineReelPostModel>()


                for(i in it.documents){
                    var post= i.toObject<CombineReelPostModel>()!!
                    temp.add(post)

                }

                postAndReelList.addAll(temp)
                adapter.notifyDataSetChanged()
            }.addOnFailureListener {

                // Toast.makeText(requireContext(),"something wrong $it",Toast.LENGTH_LONG).show()
            }
        }



        return binding.root
    }


}