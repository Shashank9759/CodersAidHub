package com.example.instaranjan.UI.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.example.instaranjan.Adapters.homePostAdapter
import com.example.instaranjan.R
import com.example.instaranjan.databinding.ActivityExploreBinding
import com.example.instaranjan.models.NewPostModel
import com.example.instaranjan.utils.POST_IMAGE_DETAILS
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExploreActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityExploreBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



        //SETTING COLORPRIMARYDARK

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setSupportActionBar(binding.explorePost)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.explorePost.setNavigationOnClickListener {
            finish()

        }




         //Rv

        var postModelFromCombineAdapter: NewPostModel?= null

        postModelFromCombineAdapter = intent.getSerializableExtra("postmodel") as NewPostModel



        var postList= mutableListOf<NewPostModel>()
        var adapter= homePostAdapter(supportFragmentManager,this,postList as ArrayList<NewPostModel>)
        binding.exploreRecyclerView.adapter=adapter

              CoroutineScope(Dispatchers.IO).launch {

                  Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                      var temp= mutableListOf<NewPostModel>()


                      for(i in it.documents){
                          var post= i.toObject<NewPostModel>()!!

                          if(post.postUID!= postModelFromCombineAdapter.postUID){
                              temp.add(post)
                          }


                      }
                      temp.add(postModelFromCombineAdapter)
                      postList.addAll(temp.reversed())
                      adapter.notifyDataSetChanged()
                  }



              }

    }
}