package com.example.instaranjan.UI.fragments

import android.app.Dialog
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.instaranjan.Adapters.CommentAdapter

import com.example.instaranjan.R
import com.example.instaranjan.databinding.FragmentCommentDialogBinding
import com.example.instaranjan.models.NewPostModel
import com.example.instaranjan.models.User
import com.example.instaranjan.models.UserComment
import com.example.instaranjan.utils.POST_IMAGE_DETAILS
import com.example.instaranjan.utils.USER_NODE

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CommentDialogFragment(private val currentpostmodel: NewPostModel) : BottomSheetDialogFragment() {
    var FirebasePostPath = Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(currentpostmodel.postUID!!)
    var FirebaseAllPostPath = Firebase.firestore.collection(POST_IMAGE_DETAILS).document(currentpostmodel.postUID!!)
    var FirebaseCurrentUser= Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
lateinit var binding:FragmentCommentDialogBinding
   private var commentListener: ListenerRegistration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCommentDialogBinding.inflate(layoutInflater, container, false)


       var comment: ArrayList<UserComment>?= arrayListOf<UserComment>()
       var adapter:CommentAdapter= CommentAdapter(requireContext(),comment!!)
        FirebaseAllPostPath.get().addOnSuccessListener {

            val newPostModel = it.toObject<NewPostModel>(NewPostModel::class.java)

             comment= newPostModel?.comment?.toList()?.reversed()?.let {
                ArrayList(it)
            }
              adapter= CommentAdapter(requireContext(),comment!!)
            binding.commentRV.adapter= adapter






        }
        //seting comment profile image
        FirebaseCurrentUser.get().addOnSuccessListener {

            val currentUser = it.toObject<User>(User::class.java)



            Glide.with(requireContext()).load(currentUser?.image).into(binding.commentCurrentUserProfile)
        }


            binding.commentCurrentUserPostButton.setOnClickListener {
                val currentUserComment = binding.commentCurrentUser.text.toString()
                binding.commentCurrentUser.setText("")
                FirebaseAllPostPath.get().addOnSuccessListener {

                    var newPostModel = it.toObject<NewPostModel>(NewPostModel::class.java)


                    FirebaseCurrentUser.get().addOnSuccessListener {

                        val currentUser = it.toObject<User>(User::class.java)
                        val userComment = UserComment(currentUser!!, currentUserComment)
                        newPostModel?.comment?.add(userComment)

                        FirebaseAllPostPath.update("comment", newPostModel?.comment)
                        FirebasePostPath.update("comment", newPostModel?.comment)


                        commentListener = FirebaseAllPostPath.addSnapshotListener { documentSnapshot, error ->
                            if (error != null) {
                                // Handle the error
                                return@addSnapshotListener
                            }

                            val newPostModel = documentSnapshot?.toObject<NewPostModel>(NewPostModel::class.java)

                            // Update the adapter with the new comments
                            comment?.clear()
                            comment?.addAll(newPostModel?.comment?.toList()?.reversed() ?: emptyList())
                            adapter.notifyDataSetChanged()
                        }





                    }

                }


            }



        return binding.root
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // Set your custom animations
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation


        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the Firestore listener when the view is destroyed
        commentListener?.remove()
    }
}