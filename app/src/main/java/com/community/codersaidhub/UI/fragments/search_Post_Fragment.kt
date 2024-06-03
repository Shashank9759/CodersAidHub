package com.community.codersaidhub.UI.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.community.codersaidhub.Adapters.combineAdapter
import com.community.codersaidhub.Adapters.homePostAdapter
import com.community.codersaidhub.R
import com.community.codersaidhub.databinding.FragmentSearchPostBinding
import com.community.codersaidhub.models.CombineReelPostModel
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.models.NewReelModel
import com.community.codersaidhub.utils.COMBINE_IMAGE_REEL
import com.community.codersaidhub.utils.POST_IMAGE_DETAILS
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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



        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot = Firebase.firestore.collection(COMBINE_IMAGE_REEL).get().await()
                val temp = mutableListOf<CombineReelPostModel>()

                for (document in querySnapshot.documents) {
                    val post = document.toObject<CombineReelPostModel>()
                    if (post != null) {
                        temp.add(post)
                    }
                }

                withContext(Dispatchers.Main) {
                  //  postAndReelList.addAll(temp)
                 //   adapter.notifyDataSetChanged()
                    var postAndReelList= mutableListOf<CombineReelPostModel>()
                    var adapter= combineAdapter(requireContext(),temp as ArrayList<CombineReelPostModel>)
                    binding.searchPostRV.adapter=adapter

                }
            } catch (e: Exception) {
                // Handle failure
                Log.e(TAG, "Error fetching documents: $e")
            }
        }



        return binding.root
    }


}