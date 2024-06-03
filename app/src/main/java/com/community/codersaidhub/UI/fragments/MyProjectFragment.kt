package com.community.codersaidhub.UI.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.community.codersaidhub.Adapters.myPostAdapter
import com.community.codersaidhub.Adapters.myProjectAdapter
import com.community.codersaidhub.R
import com.community.codersaidhub.ViewModel.SharedViewModel
import com.community.codersaidhub.databinding.FragmentMyPostBinding
import com.community.codersaidhub.databinding.FragmentMyProjectBinding
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.models.projectModel
import com.community.codersaidhub.utils.POST_IMAGE_DETAILS
import com.community.codersaidhub.utils.POST_Project_DETAILS
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


class MyProjectFragment : Fragment() {
  lateinit var binding:FragmentMyProjectBinding
    lateinit var adapter: myProjectAdapter
    private lateinit var sharedViewModel: SharedViewModel
    var email:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding=FragmentMyProjectBinding.inflate(layoutInflater,container,false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.Postdata.observe(viewLifecycleOwner, { newData ->
            email= newData
            if(email==null){
                loadDataForCurrentUser(requireContext())
                //   Log.d("cjndkj","email null")
            }else{
                val data= email
                loadDataForAnotherUser(data?:"",requireContext())
                //        Log.d("cjndkj","email ")
            }

        })
    }


    fun loadDataForCurrentUser(context: Context){



        var ProjectList = ArrayList<projectModel>()

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+"PROJECT").get()
            .addOnSuccessListener { it ->
                var tempList = arrayListOf<projectModel>()
                for (i in it.documents) {
                    val mypost = i.toObject<projectModel>()!!
                    tempList.add(mypost)
                }
                ProjectList.addAll(tempList)
//                Log.d("cjndkj",ProjectList.get(0).remoteLink.toString())
                var adapter = myProjectAdapter(childFragmentManager,context, ProjectList)

                binding.myprojectRV.adapter = adapter



            }
    }


    fun loadDataForAnotherUser(data:String,context: Context){
        val email= data
        Firebase.firestore.collection(POST_Project_DETAILS).whereEqualTo("user.email",email).get().addOnSuccessListener { snapshot->
            var list= mutableListOf<projectModel>()
            if (!snapshot.isEmpty) {


                for(i in snapshot){
                    val newprojecttModel= i.toObject(projectModel::class.java)
                    list.add(newprojecttModel)
                }


                var adapter = myProjectAdapter(childFragmentManager,context, list as ArrayList<projectModel>)

                binding.myprojectRV.adapter = adapter

            }else{

                var adapter = myPostAdapter(context, list as ArrayList<NewPostModel>)

                binding.myprojectRV.adapter = adapter
            }


        }


    }
}