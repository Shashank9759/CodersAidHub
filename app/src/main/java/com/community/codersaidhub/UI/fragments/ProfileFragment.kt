package com.community.codersaidhub.UI.fragments

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide

import com.community.codersaidhub.databinding.FragmentProfileBinding
import com.community.codersaidhub.Adapters.ViewPagerAdapter
import com.community.codersaidhub.R
import com.community.codersaidhub.UI.activities.EditProfileActivity
import com.community.codersaidhub.UI.activities.MessageActivity2
import com.community.codersaidhub.ViewModel.SharedViewModel
import com.community.codersaidhub.models.User
import com.community.codersaidhub.utils.FOLLOWER
import com.community.codersaidhub.utils.FOLLOWING
import com.community.codersaidhub.utils.POST_IMAGE_DETAILS
import com.community.codersaidhub.utils.POST_Project_DETAILS
import com.community.codersaidhub.utils.POST_Reel_DETAILS
import com.community.codersaidhub.utils.USER_NODE

import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
   lateinit var user:User
    lateinit var binding :FragmentProfileBinding
    private lateinit var sharedViewModel: SharedViewModel
   val firebaseCurrentUser=Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
    val firebaseFollowerCollection= Firebase.firestore.collection(FOLLOWER)
    val firebaseFollowingCollection= Firebase.firestore.collection(FOLLOWING)
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    var isFromSearchFragment=false
   lateinit var currentUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProfileBinding.inflate(layoutInflater,container,false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

         isFromSearchFragment= arguments?.containsKey("User")?: false
        val argumunt= arguments?.getSerializable("User") as? User
        setHasOptionsMenu(true)


        //setting toolbar
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.myProfileToolbarName)


        CoroutineScope(Dispatchers.IO).launch {


            firebaseCurrentUser.get().addOnSuccessListener {

                currentUser = it.toObject<User>()!!



                if(isFromSearchFragment){

                    //setting message
                    binding.profileEdit.setOnClickListener {
                        //Toast.makeText(requireContext(),"Not Implemented",Toast.LENGTH_SHORT).show()
                        val intent=Intent(requireContext(),MessageActivity2::class.java)
                        intent.putExtra("anotherUser",argumunt)
                        intent.putExtra("currentUser",currentUser)
                        requireContext().startActivity(intent)
                    }




                    // setting toolbar

                    activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    binding.myProfileToolbarName.setNavigationOnClickListener {
                        requireActivity().onBackPressed()
                    }






                    sendRealTimePostData(argumunt?.email)
                    sendRealTimeReelData(argumunt?.email)

                    binding.profileFollow.visibility=View.VISIBLE
                    binding.profileEdit.text="Message"
                    getNumberOfPost(argumunt?.email as String){
                            postSize->
                        binding.numberOfPost.text=postSize.toString()
                    }
                    settingfollowAndFollowers(argumunt?.email as String)

                    CoroutineScope(Dispatchers.IO).launch {

                        isFollow(argumunt?.email,currentUser.email){
                                isfollow->



                            if(isfollow){
                                // setting icons
                                binding.profileFollow.setBackgroundResource(R.drawable.custom_editprofile_button_shape)
                                binding.profileFollow.setTextColor(resources.getColor(R.color.black))
                                binding.profileFollow.text="Following"
                            }
                            else{

                                // setting icons
                                binding.profileFollow.setBackgroundResource(R.drawable.custom_follow_button_shape)
                                binding.profileFollow.setTextColor(resources.getColor(R.color.white))
                                binding.profileFollow.text="Follow"
                            }

                        }



                    }



                }
                else{
                    binding.profileEdit.setOnClickListener {
                        val intent = Intent(activity, EditProfileActivity::class.java)
                        intent.putExtra("insta", 1)

                        activity?.startActivity(intent)
                    }
                    sendRealTimePostData(null)
                    sendRealTimeReelData(null)
                    getNumberOfPost(currentUser?.email as String){
                            postSize->
                        binding.numberOfPost.text=postSize.toString()
                    }
                    settingfollowAndFollowers(currentUser.email as String)

                    binding.profileFollow.visibility=View.GONE
                }
            }
        }







        viewPagerAdapter= ViewPagerAdapter(childFragmentManager)
        viewPagerAdapter.addFragments(MyProjectFragment(),R.drawable.icon_project)
        viewPagerAdapter.addFragments(MyPostFragment(), R.drawable.upload_image)
        viewPagerAdapter.addFragments(MyReelsFragment(),R.drawable.icon_reels)
        binding.profileViewpager.adapter=viewPagerAdapter
        binding.profileViewpager.offscreenPageLimit = 0
        binding.profileTablayout.setupWithViewPager(binding.profileViewpager)
        for (i in 0 until  binding.profileTablayout.tabCount) {
            binding.profileTablayout.getTabAt(i)?.setIcon(viewPagerAdapter.getIcon(i))
        }




        binding.profileTablayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(resources.getColor(R.color.grey), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(resources.getColor(android.R.color.black), PorterDuff.Mode.SRC_IN)
            }
        })
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        user=User()

         if (isFromSearchFragment){

             val argument= arguments?.getSerializable("User") as? User
             sendRealTimePostData(argument?.email)
             sendRealTimeReelData(argument?.email)
             Firebase.firestore.collection(USER_NODE).whereEqualTo("email",argument?.email).get().addOnSuccessListener { snapshot->



                 if (!snapshot.isEmpty) {

                     val userDocument = snapshot.documents[0]
                     user = userDocument.toObject(User::class.java) as User
                     if (user != null) {


                         //setting follow button
                         binding.profileFollow.setOnClickListener {



                     if(currentUser!=null){
                         CoroutineScope(Dispatchers.IO).launch {

                             isFollow(user.email,currentUser.email){
                                     isfollow->
                                 if(!isfollow){


                                     // setting icons
                                     binding.profileFollow.setBackgroundResource(R.drawable.custom_editprofile_button_shape)
                                     binding.profileFollow.setTextColor(resources.getColor(R.color.black))
                                     binding.profileFollow.text="Following"




                                     firebaseFollowerCollection.document(user.email as String).get()
                                         .addOnSuccessListener { AnotherUsersnapshot->
                                             firebaseFollowingCollection.document(currentUser.email as String).get()
                                                 .addOnSuccessListener { currentUserSnapshot->
                                                     if(currentUserSnapshot.exists()){
                                                         //  val existingList= currentUserSnapshot.toObject<MutableList<String>>()
                                                         var existingList = currentUserSnapshot.get("followingList") as? MutableList<String>?
                                                         existingList?.add(user.email as String)

                                                         firebaseFollowingCollection.document(currentUser.email as String)
                                                             .update("followingList", existingList)
                                                             .addOnSuccessListener {

                                                                 settingfollowAndFollowers(user?.email as String)
                                                             }
                                                             .addOnFailureListener { e ->

                                                             }
                                                     }else{
                                                         val newFollowerList= mutableListOf<String>(user.email as String)
                                                         firebaseFollowingCollection.document(currentUser.email as String)

                                                             .set(mapOf("followingList" to newFollowerList))
                                                             .addOnSuccessListener {

                                                             }

                                                     }




                                                 }
                                             if(AnotherUsersnapshot.exists()){
                                                 //     val existingList= it.toObject<MutableList<String>>()
                                                 var existingList = AnotherUsersnapshot.get("followerList") as? MutableList<String>?
                                                 existingList?.add(currentUser.email as String)

                                                 firebaseFollowerCollection.document(user.email as String)
                                                     .update("followerList", existingList)
                                                     .addOnSuccessListener {

                                                     }

                                             }else{
                                                 val newFollowerList= mutableListOf<String>(currentUser.email as String)
                                                 firebaseFollowerCollection.document(user.email as String)

                                                     .set(mapOf("followerList" to newFollowerList))
                                                     .addOnSuccessListener {

                                                     }

                                             }


                                         }
                                 }else{
                                     // setting icons
                                     binding.profileFollow.setBackgroundResource(R.drawable.custom_follow_button_shape)
                                     binding.profileFollow.setTextColor(resources.getColor(R.color.white))
                                     binding.profileFollow.text="Follow"


                                     try{
                                         firebaseFollowerCollection.document(user.email as String).get().addOnSuccessListener{


                                             firebaseFollowingCollection.document(currentUser.email as String).get().addOnSuccessListener{
                                                 var list = it.get("followingList") as? MutableList<String>?
                                                 var tempList= mutableListOf<String>()
                                                 if(list!=null){
                                                     for(i in list){
                                                         if(i!=user.email){
                                                             tempList?.add(i)
                                                         }
                                                     }
                                                 }

                                                 firebaseFollowingCollection.document(currentUser.email as String).update("followingList", tempList).addOnSuccessListener {

                                                     settingfollowAndFollowers(user?.email as String)

                                                 }
                                             }
                                             var list = it.get("followerList") as? MutableList<String>?
                                             var tempList= mutableListOf<String>()
                                             if(list!=null){
                                                 for(i in list){
                                                     if(i!=currentUser.email){
                                                         tempList?.add(i)
                                                     }
                                                 }
                                             }

                                             firebaseFollowerCollection.document(user.email as String).update("followerList", tempList).addOnSuccessListener {




                                             }
                                         }

                                     }
                                     catch (e:Exception){
                                         Toast.makeText(requireContext(),"Error : $e",Toast.LENGTH_LONG).show()
                                     }
                                 }

                             }




                         }
                     }






                         }
                         if (!user?.image.isNullOrEmpty()) {
                             Glide.with(this)
                                 .load(user?.image)
                                 .into(binding.myProfileImage)
                         }
                         binding.profileName.setText(user?.name)
                         binding.profileBio.setText(user?.bio)


                         binding.myProfileToolbarName.title = user?.name
                     }
                 } else {


                 }

         }}

             else{
             sendRealTimePostData(null)
             sendRealTimeReelData(null)
                 firebaseCurrentUser.get()
                     .addOnSuccessListener {
                         user = it.toObject<User>()!!

                         if (!user?.image.isNullOrEmpty()) {
                             Glide.with(this)
                                 .load(user?.image)
                                 .into(binding.myProfileImage)
                         }
                         binding.profileName.setText(user?.name)
                         binding.profileBio.setText(user?.bio)


                         binding.myProfileToolbarName.title = user?.name


             }
    }




    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
      inflater.inflate(R.menu.myprofiletoolbar_menu,menu)








        super.onCreateOptionsMenu(menu, inflater)


    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {



        when(item.itemId){
            R.id.myprofileToolbarAdd ->{
                showAddFragment()

                return true
            }

            android.R.id.home -> {

                requireActivity().onBackPressed()
                return true
            }
           else -> return super.onOptionsItemSelected(item)

        }



    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)


        val myProfileToolbarAddItem: MenuItem? = menu.findItem(R.id.myprofileToolbarAdd)



        val shouldShowAddItem = !(arguments?.containsKey("User")?: false)

            // Set the visibility of the menu item based on the condition
            myProfileToolbarAddItem?.isVisible = shouldShowAddItem

    }
    private fun showAddFragment() {
        val bottomSheetFragment = AddFragment()
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)

    }

    private fun sendRealTimePostData(data: String?) {

        sharedViewModel.Postdata.value = data
    }
    private fun sendRealTimeReelData(data: String?) {

        sharedViewModel.Reeldata.value = data
    }




    private suspend fun isFollow(profile:String?,follower:String?,callback:(Boolean)->Unit) {
         var temp=false
          firebaseFollowerCollection.document(profile as String).get().addOnSuccessListener {
              if(it.exists()){

                  var list = it.get("followerList") as? MutableList<String>?
                  if (list != null) {
                      for(i in list ) {
                          if (i == follower) {

                              temp = true

                              break
                          }
                      }

                      callback(temp)

                  }

              }

              callback(temp)

          }




    }


    private  fun getUserFollower(email:String,callback:(List<String>)->Unit){
        var temp= mutableListOf<String>()
        firebaseFollowerCollection.document(email).get().addOnSuccessListener {

            var list = it.get("followerList") as? MutableList<String>?
             if (list!=null){
                callback(list)
             }
            else{
                callback(temp)
             }

        }


    }

    private fun getUserFollowing(email:String,callback:(List<String>)->Unit){
        var temp= mutableListOf<String>()
        firebaseFollowingCollection.document(email).get().addOnSuccessListener {

            var list = it.get("followingList") as? MutableList<String>?
            if (list!=null){
                callback(list)
            }
            else{
                callback(temp)
            }

        }
    }



    private fun settingfollowAndFollowers(email: String){
        getUserFollowing(email as String){
                followingList->
            getUserFollower(email as String){
                    followerList->
                binding.numberOfFollower.text= followerList.size.toString()
                binding.numberOfFollowing.text=followingList.size.toString()
            }


        }

    }


    private fun getNumberOfPost(email:String,callback:(Int)->Unit) {
        var temp=0
        Firebase.firestore.collection(POST_Reel_DETAILS).whereEqualTo("user.email",email).get().addOnSuccessListener {
             temp= it.size()
            Firebase.firestore.collection(POST_IMAGE_DETAILS).whereEqualTo("user.email",email).get().addOnSuccessListener {
                temp=temp+it.size()
                Firebase.firestore.collection(POST_Project_DETAILS).whereEqualTo("user.email",email).get().addOnSuccessListener {
                    Log.d("dhkhdc",it.size().toString())
                    temp=temp+ it.size()
                    callback(temp)
                }

            }








        }
    }

//    override fun onStop() {
//        sendRealTimePostData(null)
//        sendRealTimeReelData(null)
//        super.onStop()
//    }
    }

