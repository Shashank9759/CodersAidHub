package com.community.codersaidhub.UI.activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.community.codersaidhub.Adapters.combineAdapter
import com.community.codersaidhub.Adapters.projectImageDialogAdapter
import com.community.codersaidhub.R
import com.community.codersaidhub.databinding.ActivityAddProjectBinding
import com.community.codersaidhub.models.CombineReelPostModel
import com.community.codersaidhub.models.User
import com.community.codersaidhub.models.projectModel
import com.community.codersaidhub.utils.COMBINE_IMAGE_REEL
import com.community.codersaidhub.utils.POST_IMAGE
import com.community.codersaidhub.utils.POST_IMAGE_DETAILS
import com.community.codersaidhub.utils.POST_Project_DETAILS
import com.community.codersaidhub.utils.UPLOAD_PROJECT
import com.community.codersaidhub.utils.USER_NODE
import com.community.codersaidhub.utils.uploadImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AddProjectActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAddProjectBinding.inflate(layoutInflater)
    }
    lateinit var adapter:projectImageDialogAdapter
    lateinit var listofselectedImageUri:MutableList<Uri>
    lateinit var listofselectedImageLink:MutableList<String>
    private val launcher= registerForActivityResult(ActivityResultContracts.GetMultipleContents()){
            uris->


        if(uris==null){


            return@registerForActivityResult

        }
        else {


            uris.forEach { uri ->
                uploadImage(this, uri, UPLOAD_PROJECT, binding.projectImageUploadprogressBar) {
                    if (!it.isNullOrEmpty()) {
                       listofselectedImageUri.add(uri)
                        listofselectedImageLink.add(it)
                        binding.projectImageUploadprogressBar.visibility=View.GONE
                        binding.projectimageviewButton.visibility=View.VISIBLE
                    }
                }
            }

        }


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val rootView = findViewById<RelativeLayout>(R.id.addproject)
        val initialPaddingLeft = rootView.paddingLeft
        val initialPaddingTop = rootView.paddingTop
        val initialPaddingRight = rootView.paddingRight
        val initialPaddingBottom = rootView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addproject)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                initialPaddingLeft + systemBars.left,
                initialPaddingTop + systemBars.top,
                initialPaddingRight + systemBars.right,
                initialPaddingBottom + systemBars.bottom
            )
            insets
        }

        listofselectedImageUri= mutableListOf<Uri>()
        listofselectedImageLink= mutableListOf<String>()

        binding.buttonSelectImage.setOnClickListener {
            launcher.launch("image/*")
        }

        var user: User= User()
    binding.buttonSubmit.setOnClickListener {
  if(validation()){
      val randomDocumentId = UUID.randomUUID().toString()
      val projectName=binding.editTextName.text.toString()
      val technologyName=binding.editTextTechnology.text.toString()
      val descriptionName=binding.editTextDescription.text.toString()
      val remoteLink=binding.editTextRemoteLink.text.toString()
      val listOfPhoto=listofselectedImageLink
      val time=System.currentTimeMillis()
      var projectmodel=projectModel()
      projectmodel.projectName=projectName
      projectmodel.technologyName=technologyName
      projectmodel.descriptionName=descriptionName
      projectmodel.remoteLink=remoteLink
      projectmodel.listOfPhoto=listOfPhoto
      projectmodel.time=time
      projectmodel.postUID=randomDocumentId


      Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
              it->
          user=it.toObject<User>()!!
          projectmodel.user=user
          Firebase.firestore.collection(POST_Project_DETAILS).document(randomDocumentId).set(projectmodel)
              .addOnSuccessListener {
                  Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+"PROJECT").document(randomDocumentId)
                      .set(projectmodel).addOnSuccessListener {
                          Toast.makeText(
                              this,
                              "Project Uploaded",
                              Toast.LENGTH_SHORT
                          ).show()


                          startActivity(Intent(this@AddProjectActivity,Home_Activity::class.java))
                          finish()


                      }

              }

      }



  }
    }

        binding.projectimageviewButton.setOnClickListener {
            val dialog=Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.project_view_image_dialog)

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            val cancelbutton=dialog.findViewById<ImageView>(R.id.projectimagelayoutCancel)
            val recylerview=dialog.findViewById<RecyclerView>(R.id.projectimagelayoutRV)
            adapter= projectImageDialogAdapter(this,listofselectedImageUri)
            recylerview.adapter=adapter
            cancelbutton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }


    }

    fun validation():Boolean{
        if (binding.editTextName.text.isEmpty()){
            Toast.makeText(this,"Write Project Name",Toast.LENGTH_SHORT).show()
            return false
        }
        else if(binding.editTextTechnology.text.isEmpty()){
            Toast.makeText(this,"Write Technology Name",Toast.LENGTH_SHORT).show()
            return false
        }
        else if(binding.editTextDescription.text.isEmpty()){
            Toast.makeText(this,"Write Description",Toast.LENGTH_SHORT).show()
            return false
        }else if (listofselectedImageLink.isEmpty()){
            Toast.makeText(this,"Select Images",Toast.LENGTH_SHORT).show()
            return false

        }
        else{
            return true
        }
    }
}