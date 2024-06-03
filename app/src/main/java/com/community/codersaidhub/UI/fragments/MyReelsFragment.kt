package com.community.codersaidhub.UI.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.community.codersaidhub.Adapters.myPostAdapter
import com.community.codersaidhub.Adapters.myReelAdapter
import com.community.codersaidhub.R
import com.community.codersaidhub.ViewModel.SharedViewModel
import com.community.codersaidhub.databinding.FragmentMyPostBinding
import com.community.codersaidhub.databinding.FragmentMyReelsBinding
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.models.NewReelModel
import com.community.codersaidhub.utils.POST_IMAGE_DETAILS
import com.community.codersaidhub.utils.POST_Reel_DETAILS
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


class MyReelsFragment : Fragment() {
 lateinit var binding:FragmentMyReelsBinding
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var adapter:myReelAdapter
    var email:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMyReelsBinding.inflate(layoutInflater, container, false)


        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)




        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        if(email== null){
//            loadDataForCurrentUser(requireContext())
//        }

        sharedViewModel.Reeldata.observe(viewLifecycleOwner, { newData ->
            email= newData

            if(email==null){
                loadDataForCurrentUser(requireContext())
            }else{

                val data= email
                loadDataForAnotherUser(data?:"",requireContext())
            }

        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun loadDataForCurrentUser(context:Context){


        var ReelList= ArrayList<NewReelModel>()
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+"reel").get().addOnSuccessListener {it->
            var tempList= arrayListOf<NewReelModel>()
            for(i in it.documents){
                val myReel= i.toObject<NewReelModel>()!!
                tempList.add(myReel)
            }
            ReelList.addAll(tempList)
          adapter= myReelAdapter(context,ReelList)
            binding.myreelRV.layoutManager= StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            binding.myreelRV.adapter=adapter




        }

    }


    fun loadDataForAnotherUser(data:String,context:Context){
        val email= data
        Firebase.firestore.collection(POST_Reel_DETAILS).whereEqualTo("user.email",email).get().addOnSuccessListener { snapshot->
            val list= mutableListOf<NewReelModel>()
            if (!snapshot.isEmpty) {


                for(i in snapshot){
                    val newreelmodel= i.toObject(NewReelModel::class.java)
                    list.add(newreelmodel)
                }


                 adapter= myReelAdapter(context,list as ArrayList<NewReelModel>)
                binding.myreelRV.layoutManager= StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                binding.myreelRV.adapter=adapter


            }
            else{
                adapter= myReelAdapter(context,list as ArrayList<NewReelModel>)
                binding.myreelRV.layoutManager= StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                binding.myreelRV.adapter=adapter

            }


        }


    }

//    override fun onDestroy() {
//        super.onDestroy()
//        email=null
//        sharedViewModel.Reeldata.value = null
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        email=null
//        sharedViewModel.Reeldata.value = null
//    }
//
//    override fun onStop() {
//        super.onStop()
//        email=null
//        sharedViewModel.Reeldata.value = null
//    }
}