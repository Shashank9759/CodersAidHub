package com.community.codersaidhub.UI.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.community.codersaidhub.Adapters.SearchResultAdapter
import com.community.codersaidhub.ViewModel.SharedViewModel
import com.community.codersaidhub.databinding.FragmentSearchResultProfileBinding
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.models.User
import com.community.codersaidhub.utils.POST_IMAGE_DETAILS
import com.community.codersaidhub.utils.USER_NODE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class search_ResultProfile_Fragment : Fragment() {
    lateinit var binding:FragmentSearchResultProfileBinding
 lateinit var list:ArrayList<User>
    var FirebaseUser= Firebase.firestore.collection(USER_NODE)
   lateinit var madapter:SearchResultAdapter
    var FirebaseCurrentUser= Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding= FragmentSearchResultProfileBinding.inflate(layoutInflater,container,false)




        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Observe changes in data and update UI accordingly
        sharedViewModel.data.observe(viewLifecycleOwner, { newData ->
            setRV(newData)

        })

    }


  private fun setRV(data:String) {


      try {
          FirebaseCurrentUser.addSnapshotListener { documentSnapshot, error ->
              if (error != null) {
                  // Handle the error
                  return@addSnapshotListener
              }

              if (isAdded) {

                  val currentUser = documentSnapshot?.toObject<User>(User::class.java)




                  list = ArrayList()
                  madapter = SearchResultAdapter(requireContext(), list)
                  binding.searchResultRV.adapter = madapter

                  val defaultUser = User()

                  lifecycleScope.launch(Dispatchers.Main) {
                      try {
                          val result = FirebaseUser.get().await()
                          for (user in result.documents) {
                              val User = user.toObject<User>(User::class.java) ?: defaultUser
                              val name = User.name.toString()
                              val email = User.email.toString()


                              if (name.toLowerCase().contains(data) || email.toLowerCase()
                                      .contains(data)
                              ) {
                                  if (!isAlreadyAdded(
                                          User,
                                          list
                                      ) && User.email != currentUser?.email
                                  ) {
                                      list.add(User)
                                  }

                              }

                          }

                          madapter.setData(list)
                          madapter.notifyDataSetChanged()


                      } catch (e: Exception) {
                          Toast.makeText(requireContext(), "Error : $e", Toast.LENGTH_SHORT).show()
                      }
                  }

              }

//      FirebaseCurrentUser.get().addOnSuccessListener {
//
//
//
//      }


          }

      } catch (e: Exception) {
          Toast.makeText(requireContext(), "Error : $e", Toast.LENGTH_SHORT).show()
      }

  }

    fun isAlreadyAdded(user:User,list:ArrayList<User>):Boolean{

        for(i in list){
            if(i.email==user.email){
                return true
            }
        }
        return false
    }
}