package com.community.codersaidhub.UI.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
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
import com.bumptech.glide.Glide
import com.community.codersaidhub.Adapters.homePostAdapter
import com.community.codersaidhub.Adapters.homeViewpagerAdapter
import com.community.codersaidhub.Adapters.storyAdapter
import com.community.codersaidhub.R
import com.community.codersaidhub.UI.activities.MessageActivity

import com.community.codersaidhub.databinding.FragmentHomeBinding
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.models.NewReelModel
import com.community.codersaidhub.models.User
import com.community.codersaidhub.models.messageModel2
import com.community.codersaidhub.models.storyModel
import com.community.codersaidhub.utils.CAMERA_PERMISSION_CODE
import com.community.codersaidhub.utils.FOLLOWING
import com.community.codersaidhub.utils.POST_CAMERA_REQ_CODE
import com.community.codersaidhub.utils.POST_IMAGE
import com.community.codersaidhub.utils.POST_IMAGE_DETAILS
import com.community.codersaidhub.utils.STORY
import com.community.codersaidhub.utils.STORY_IMAGE
import com.community.codersaidhub.utils.USER_NODE
import com.community.codersaidhub.utils.uploadImage
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.lang.System.currentTimeMillis


class HomeFragment : Fragment() {
 lateinit var binding:FragmentHomeBinding
 lateinit var handler:Handler
val storyDb=Firebase.firestore.collection(STORY)
    val firebaseCurrentUser=Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)

    lateinit var dialog :AlertDialog
    lateinit var storyadapter :storyAdapter
    private val launcher= registerForActivityResult(ActivityResultContracts.GetContent()){
            uri->


        if(uri==null){

            dialog.dismiss()
            return@registerForActivityResult

        }
        else {

            dialog.dismiss()

            uploadImage(requireContext(), uri, STORY_IMAGE, binding.StoryUploadprogressBar) {imagelink->
                if (!imagelink.isNullOrEmpty()) {

                    uploadStories(imagelink)
                    }

            }
        }


    }
fun uploadStories(imagelink:String){
    firebaseCurrentUser.get().addOnSuccessListener {
        var currentUser=User()
        currentUser = it.toObject<User>()!!
        storyDb.document(currentUser.email.toString()).get().addOnSuccessListener {documentSnapshot->
            if(documentSnapshot.exists()){
                val existingStories: List<Map<String, Any>> = documentSnapshot["stories"] as? List<Map<String, Any>> ?: emptyList()

                val newstory = storyModel(imagelink,currentTimeMillis(),currentUser.name.toString(),currentUser.image.toString())

                // Convert the new message to a map
                val newStoryMap: Map<String, Any> = mapOf(
                    "imageLink" to newstory.imageLink as Any,
                    "currentTime" to newstory.currentTime as Any,
                    "name" to newstory.name as Any,
                    "profileImage" to newstory.profileImage as Any
                )
                val existingStoriesList =  existingStories.mapNotNull { it as? Map<String, Any> }
                val updatedStoriesList = existingStoriesList.toMutableList().apply {
                    add(newStoryMap)
                }



                //  val storymodel= storyModel(imagelink,System.currentTimeMillis())
                storyDb.document(currentUser.email.toString()).set(mapOf("stories" to updatedStoriesList))
            }

            else{

                iffirebaseEmpty(currentTimeMillis(),currentUser.email.toString(),imagelink,storyDb,currentUser)

            }
        }

    }
}
    fun iffirebaseEmpty(date:Long,currentUserEmail:String,imagelink: String,messagesCollection: CollectionReference,currentUser:User){

        val listOfstories = mutableListOf<storyModel>()
        listOfstories.add(storyModel(imagelink,date,currentUser.name.toString(),currentUser.image.toString()))


        val messageMaps = listOfstories.map { storymodel ->
            mapOf(
                "imageLink" to storymodel.imageLink,
                "currentTime" to storymodel.currentTime,
                "name" to storymodel.name as Any,
                "profileImage" to storymodel.profileImage as Any
                )
        }

// Set the list of messages under the document with specific ID

        val documentId = currentUserEmail


        messagesCollection.document(documentId).set(mapOf("stories" to messageMaps))
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->
                // Handle failure
                println("Error adding messages: $e")
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


        //story
        val listofstorymodel= listOf<storyModel>()
        val listofstorymodel2= arrayListOf<List<storyModel>>()
        //   listofstorymodel2.add(listofstorymodel)
        storyadapter= storyAdapter(requireContext(), listofstorymodel2,binding.homeStoryTitleMessage)
        binding.homeStoryRecyclerView.adapter=storyadapter
        handler= Handler()
        getstories()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
   //setting Story Feature
        binding.addStory.setOnClickListener {
            showCustomDialog()
        }





        val adapter = homeViewpagerAdapter(requireActivity())
        binding.homeviewpager.adapter = adapter

        TabLayoutMediator(binding.tablayout, binding.homeviewpager) { tab, position ->
            tab.text = when (position) {
                0 -> "Projects"
                else -> "Photos"
            }
        }.attach()


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){


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
                    uploadImage(requireContext(), convertedUri, STORY_IMAGE, binding.StoryUploadprogressBar) {imagelink->
                        if (!imagelink.isNullOrEmpty()) {

                            uploadStories(imagelink)
                        }

                    }

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
fun getstories(){
    firebaseCurrentUser.get().addOnSuccessListener {
        var currentUser = User()
        currentUser = it.toObject<User>()!!
        Glide.with(requireContext()).load(currentUser.image).placeholder(R.drawable.person).into(binding.storyMyImage)

        Firebase.firestore.collection(FOLLOWING).document(currentUser.email.toString()).get().addOnSuccessListener {documentSnapshot->
            Log.d("bdjhbdbc","success")
            if(documentSnapshot.exists()){
                Log.d("bdjhbdbc","exist")
                val listofFollowing= documentSnapshot.get("followingList") as MutableList<String>
                val allstoriesList= mutableListOf<List<storyModel>>()
                for (i in listofFollowing){
                    val storiesList= mutableListOf<storyModel>()
                    storyDb.document(i).get().addOnSuccessListener { snapshot->
                       if(snapshot.exists()){
                           val existingStories: List<Map<String, Any>> = snapshot["stories"] as? List<Map<String, Any>> ?: emptyList()


                           val existingStoriesList =  existingStories.mapNotNull { it as? Map<String, Any> }
                           val updatedStoriesList = existingStoriesList.toMutableList().map {

                               storyModel( it.get("imageLink") as String, it.get("currentTime") as Long,it.get("name") as String
                               ,it.get("profileImage") as String)
                           }
                           val currentTimeMillis = System.currentTimeMillis()
                           val oneDayMillis = 86400000L


                           val filteredStoriesList = updatedStoriesList.filter {
                               val storyTimeMillis = it.currentTime as Long
                               val after24HoursMillis=storyTimeMillis+oneDayMillis
                               currentTimeMillis < after24HoursMillis
                           }
                           Log.d("bdjhbdbc","size ${filteredStoriesList.size.toString()}")
//                           Log.d("fhkfhkfh",filteredStoriesList.get(0).currentTime.toString())
                           if(!filteredStoriesList.isEmpty()){
                               Log.d("bdjhbdbc","if")
                               binding.homeStoryTitleMessage.visibility=View.GONE
                               storyadapter.addData(filteredStoriesList)
                               allstoriesList.add(filteredStoriesList)
                           }else{

                           }

                       }
                    }
                }

                if(allstoriesList.isEmpty()){
                    Log.d("bdjhbdbc","empty all stories list")
                    binding.homeStoryTitleMessage.visibility=View.VISIBLE
                    edittextTypewriterAnimationHandler(getString(R.string.story_message))
                }

            }else{
                Log.d("bdjhbdbc","not exist")
                binding.homeStoryTitleMessage.visibility=View.VISIBLE
                edittextTypewriterAnimationHandler(getString(R.string.story_message))
            }
            }.addOnFailureListener {
            Log.d("bdjhbdbc","failed")
            storyadapter.setvisiblityofText()
        }

}}
    private fun edittextTypewriterAnimationHandler(captionText:String) {
        var currentCaption=""
        handler.removeCallbacksAndMessages(null)



        var delay = 0L
        for (i in captionText.indices) {
            handler.postDelayed({
                currentCaption += captionText[i]
                binding.homeStoryTitleMessage.text = currentCaption
                if (currentCaption == captionText) {
                    // Animation completed
                }
            }, delay)
            delay += 100
        }

    }
}