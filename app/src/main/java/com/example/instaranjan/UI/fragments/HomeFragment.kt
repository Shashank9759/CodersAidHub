package com.example.instaranjan.UI.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.instaranjan.Adapters.homePostAdapter
import com.example.instaranjan.R
import com.example.instaranjan.UI.activities.MessageActivity

import com.example.instaranjan.databinding.FragmentHomeBinding
import com.example.instaranjan.models.NewPostModel
import com.example.instaranjan.models.NewReelModel
import com.example.instaranjan.utils.CAMERA_PERMISSION_CODE
import com.example.instaranjan.utils.POST_CAMERA_REQ_CODE
import com.example.instaranjan.utils.POST_IMAGE
import com.example.instaranjan.utils.POST_IMAGE_DETAILS
import com.example.instaranjan.utils.STORY
import com.example.instaranjan.utils.USER_NODE
import com.example.instaranjan.utils.uploadImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream


class HomeFragment : Fragment() {
 lateinit var binding:FragmentHomeBinding
val storyDb=Firebase.firestore.collection(STORY)
val currentUserDB=Firebase.firestore.collection(USER_NODE)

    lateinit var dialog :AlertDialog
    private val launcher= registerForActivityResult(ActivityResultContracts.GetContent()){
            uri->


        if(uri==null){

            dialog.dismiss()
            return@registerForActivityResult

        }
        else {

            dialog.dismiss()
//            uploadImage(requireContext(), uri, POST_IMAGE, binding.horizontalProgressBar) {
//                if (!it.isNullOrEmpty()) {
//
//
//                }
//
//            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding= FragmentHomeBinding.inflate(layoutInflater,container,false)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.homeToolbar)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
   //setting Story Feature
        binding.addStory.setOnClickListener {
            showCustomDialog()
        }





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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.notification ->{

            }

            R.id.message ->{
                   val intent= Intent(requireContext(),MessageActivity::class.java)
                startActivity(intent)

            }



        }


        return super.onOptionsItemSelected(item)
    }
    private fun showCustomDialog() {
        // Create an AlertDialog.Builder instance
        val builder = AlertDialog.Builder(requireContext())

        // Inflate the custom layout for the dialog
        val dialogView = layoutInflater.inflate(R.layout.custom_add_post_dialog_button, null)

        // Find the buttons in the custom layout
        val PostGallery = dialogView.findViewById<Button>(R.id.PostGallery)
        val PostCamera = dialogView.findViewById<Button>(R.id.PostCamera)

        // Set click listeners for the buttons
        PostGallery.setOnClickListener {
            launcher.launch("image/*")

        }

        PostCamera.setOnClickListener {


            if(isCameraPresent()){

                checkPermission()
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    captureImage()
                }
                else{

                    checkPermission()
                }



            }
            else{


                Toast.makeText(requireContext(),"Camera is not Present", Toast.LENGTH_SHORT).show()
            }


        }

        // Set the custom view for the dialog
        builder.setView(dialogView)

        // Create and show the AlertDialog
        dialog = builder.create()
        dialog.show()



    }

    private fun checkPermission(){

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf<String>(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            // Permission is already granted, you can proceed with using the camera
            // Add your camera-related logic here
        }

    }
    private fun isCameraPresent():Boolean{

        if(activity?.packageManager!!.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){

            return true
        }
        else{
            return false
        }
    }
    private fun captureImage(){


        val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, POST_CAMERA_REQ_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==POST_CAMERA_REQ_CODE){
            dialog.dismiss()
            if(resultCode== AppCompatActivity.RESULT_OK){
                try{
                    val imageBitmap = data?.extras?.get("data") as? Bitmap

                    val convertedUri = bitmapToUri(imageBitmap!!)


//                    binding.horizontalProgressBar.visibility = View.VISIBLE
//                    binding.uploadPost.setText("Uploading..")
//                    binding.uploadPost.isEnabled = false
//                    binding.newPostImage.setImageURI(convertedUri)
//                    uploadImage(requireContext(),convertedUri, POST_IMAGE,binding.horizontalProgressBar) {
//                        if(!it.isNullOrEmpty()){
//                            newPostModel.image = it
//                            binding.horizontalProgressBar.visibility = View.GONE
//                            binding.uploadPost.isEnabled = true
//                            binding.uploadPost.setText("Post")
//                        }
//                    }

                }catch (e:Exception){

                    Toast.makeText(requireContext(),"$e",Toast.LENGTH_LONG).show()
                }

            }


        }




    }
    private fun bitmapToUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(activity?.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

}