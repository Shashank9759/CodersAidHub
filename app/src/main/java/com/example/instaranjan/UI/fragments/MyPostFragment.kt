package com.example.instaranjan.UI.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.instaranjan.Adapters.myPostAdapter
import com.example.instaranjan.R
import com.example.instaranjan.ViewModel.SharedViewModel
import com.example.instaranjan.databinding.FragmentMyPostBinding
import com.example.instaranjan.models.NewPostModel
import com.example.instaranjan.models.User
import com.example.instaranjan.utils.POST_IMAGE_DETAILS
import com.example.instaranjan.utils.USER_NODE
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


class MyPostFragment : Fragment() {
   lateinit var adapter:myPostAdapter
    private lateinit var sharedViewModel: SharedViewModel
lateinit var binding :FragmentMyPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
   var email:String?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostBinding.inflate(layoutInflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)


        return binding.root
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//       if(email== null){
//           loadDataForCurrentUser(requireContext())
//       }

        sharedViewModel.Postdata.observe(viewLifecycleOwner, { newData ->
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



        var PostList = ArrayList<NewPostModel>()

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { it ->
                var tempList = arrayListOf<NewPostModel>()
                for (i in it.documents) {
                    val mypost = i.toObject<NewPostModel>()!!
                    tempList.add(mypost)
                }
                PostList.addAll(tempList)
                var adapter = myPostAdapter(context, PostList)
                binding.mypostRV.layoutManager =
                    StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                binding.mypostRV.adapter = adapter



            }
    }


    fun loadDataForAnotherUser(data:String,context:Context){
        val email= data
        Firebase.firestore.collection(POST_IMAGE_DETAILS).whereEqualTo("user.email",email).get().addOnSuccessListener { snapshot->
            var list= mutableListOf<NewPostModel>()
            if (!snapshot.isEmpty) {


                for(i in snapshot){
                    val newpostModel= i.toObject(NewPostModel::class.java)
                    list.add(newpostModel)
                }


                var adapter = myPostAdapter(context, list as ArrayList<NewPostModel>)
                binding.mypostRV.layoutManager =
                    StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                binding.mypostRV.adapter = adapter

            }else{

                var adapter = myPostAdapter(context, list as ArrayList<NewPostModel>)
                binding.mypostRV.layoutManager =
                    StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                binding.mypostRV.adapter = adapter
            }


        }


    }

//    override fun onDestroy() {
//        super.onDestroy()
//        email=null
//        sharedViewModel.Postdata.value = null
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        email=null
//        sharedViewModel.Postdata.value = null
//    }
//
//    override fun onStop() {
//        super.onStop()
//        email=null
//        sharedViewModel.Postdata.value = null
//    }
}