package com.community.codersaidhub.UI.activities

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.community.codersaidhub.Adapters.storyPBAdapter
import com.community.codersaidhub.R
import com.community.codersaidhub.databinding.ActivityStoryBinding
import com.community.codersaidhub.models.User
import com.community.codersaidhub.models.storyModel
import com.github.marlonlom.utilities.timeago.TimeAgo
import jp.shts.android.storiesprogressview.StoriesProgressView

class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {



    // Variables for press time and limit
    private var pressTime = 0L
    private val limit = 500L

    // Variables for StoriesProgressView and ImageView
    private lateinit var storiesProgressView: StoriesProgressView
    private lateinit var image: ImageView
        var storymodelList: ArrayList<storyModel>? =null
    // Counter for keeping track of stories
    private var counter = 0

    // Touch listener
    private val onTouchListener = View.OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                storiesProgressView.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                storiesProgressView.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_story)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

         storymodelList = intent.getSerializableExtra("storyModelList") as? ArrayList<storyModel>

//        Glide.with(this).load(storymodelList?.get(0)?.profileImage).placeholder(R.drawable.person).into(binding.storyProfileImage)
//        binding.storyProfileName.text = storymodelList?.get(0)?.name
//        binding.storyTime.text = TimeAgo.using(storymodelList?.get(0)?.currentTime as Long).toString()
//        Glide.with(this).load(storymodelList?.get(0)?.imageLink).into(binding.imageView2)
//        val madapter= storyPBAdapter(this,storymodelList as List<storyModel>){position->
////            if (storymodelList.size>=position+1){
////                binding.storyTime.text = TimeAgo.using(storymodelList?.get(position+1)?.currentTime as Long).toString()
////                Glide.with(this).load(storymodelList?.get(position+1)?.imageLink).into(binding.imageView2)
////            }else{
////                onBackPressed()
////            }
//
//
//        }
//
//        binding.storyseekbarRV.adapter=madapter

// Make the activity fullscreen
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
// Image resources
//         val resources = intArrayOf(
//            R.drawable.logo1,
//            R.drawable.logo2,
//            R.drawable.logo1,
//            R.drawable.logo2,
//            R.drawable.logo1,
//            R.drawable.logo2
//        )

        // Initialize variables
        storiesProgressView = findViewById(R.id.stories)
        storiesProgressView.setStoriesCount(storymodelList!!.size)
        storiesProgressView.setStoryDuration(3000L)
        storiesProgressView.setStoriesListener(this)
        storiesProgressView.startStories(counter)

        image = findViewById(R.id.imageOfStory)
        Glide.with(this).load(storymodelList?.get(counter)?.imageLink).into(image)


        val profileImageView= findViewById<ImageView>(R.id.StoryProfileImage)
        val profileNameView= findViewById<TextView>(R.id.story_profilename)
        val profileDateView= findViewById<TextView>(R.id.story_date)
        //setting profile image,name,date
        Glide.with(this).load(storymodelList?.get(0)?.profileImage).placeholder(R.drawable.person).into(profileImageView)
        profileNameView.text=storymodelList?.get(0)?.name
        profileDateView.text=TimeAgo.using(storymodelList?.get(0)?.currentTime as Long).toString()

        // Set up reverse view
        val reverse = findViewById<View>(R.id.story_reverse)
        reverse.setOnClickListener { storiesProgressView.reverse() }
        reverse.setOnTouchListener(onTouchListener)

        // Set up skip view
        val skip = findViewById<View>(R.id.story_skip)
        skip.setOnClickListener { storiesProgressView.skip() }
        skip.setOnTouchListener(onTouchListener)
    }

    override fun onNext() {

        Glide.with(this).load(storymodelList?.get(++counter)?.imageLink).into(image)
    }

    override fun onPrev() {
        // Move to the previous story
        if ((counter - 1) < 0) return
        Glide.with(this).load(storymodelList?.get(--counter)?.imageLink).into(image)
    }

    override fun onComplete() {
//        // When stories are complete, go back to MainActivity
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish()
        onBackPressed()
    }

}