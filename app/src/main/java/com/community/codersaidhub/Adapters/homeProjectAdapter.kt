package com.community.codersaidhub.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.codebyashish.autoimageslider.Enums.ImageActionTypes
import com.codebyashish.autoimageslider.Enums.ImageScaleType
import com.codebyashish.autoimageslider.Interfaces.ItemsListener
import com.codebyashish.autoimageslider.Models.ImageSlidesModel
import com.community.codersaidhub.R
import com.community.codersaidhub.UI.activities.WebViewActivity
import com.community.codersaidhub.UI.fragments.CommentDialogFragment
import com.community.codersaidhub.databinding.PostItemviewBinding
import com.community.codersaidhub.databinding.ProjectItemviewBinding
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.models.User
import com.community.codersaidhub.models.projectModel
import com.community.codersaidhub.utils.POST_IMAGE_DETAILS
import com.community.codersaidhub.utils.POST_Project_DETAILS


import com.community.codersaidhub.utils.USER_NODE
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


class homeProjectAdapter (private val fragmentManager: FragmentManager, val context: Context, var postList:ArrayList<projectModel>): RecyclerView.Adapter<homeProjectAdapter.viewHolder>() {


    class viewHolder(val binding: ProjectItemviewBinding): RecyclerView.ViewHolder(binding.root)

    fun setData(newItemList: ArrayList<projectModel>) {
        postList = newItemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = ProjectItemviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {


        var isSave=false
        val currentItem= postList.get(position)
        var FirebasePostPath = Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(currentItem.postUID!!)
        var FirebaseAllPostPath = Firebase.firestore.collection(POST_Project_DETAILS).document(currentItem.postUID!!)
        var FirebaseCurrentUser= Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)


        var islike:Boolean?=null
        isCurrentUserLikes(FirebaseAllPostPath,FirebaseCurrentUser){
                result->   islike= result


            //Like Handling


            if (islike==true){
                holder.binding.projectLike.setImageResource(R.drawable.heart_red)

            }else{
                holder.binding.projectLike.setImageResource(R.drawable.heart)
            }



            FirebaseAllPostPath.get().addOnSuccessListener {

                val projectmodel = it.toObject<projectModel>(projectModel::class.java)
                val currentLikes = projectmodel?.like?.size
                if (currentLikes==1   || currentLikes==0){
                    holder.binding.projectLikeNumbers.text= currentLikes.toString() + " "+"like"
                }else{
                    holder.binding.projectLikeNumbers.text= currentLikes.toString() + " "+"likes"
                }




            }



            holder.binding.projectLike.setOnClickListener{

                if(islike==false) {
                    holder.binding.projectLike.setImageResource(R.drawable.heart_red)

                    FirebaseAllPostPath.get().addOnSuccessListener {
                        var newPostModel = it.toObject<projectModel>(projectModel::class.java)
                        val currentLikes = newPostModel?.like?.size
                        FirebaseCurrentUser.get().addOnSuccessListener {

                            val currentUser = it.toObject<User>(User::class.java)


                            newPostModel?.like?.add(currentUser?.email!!)

                            FirebaseAllPostPath.update("like", newPostModel?.like)
                            FirebasePostPath.update("like", newPostModel?.like)
                            isCurrentUserLikes(FirebaseAllPostPath,FirebaseCurrentUser){
                                    result-> islike=result

                            }
                            val temp= currentLikes!!+1
                            if(temp==1|| temp==0){
                                holder.binding.projectLikeNumbers.text = (temp).toString() + " "+ "like"
                            }
                            else{

                                holder.binding.projectLikeNumbers.text = (temp).toString() + " "+ "likes"
                            }






                        }


                    }


                }
                else{


                    holder.binding.projectLike.setImageResource(R.drawable.heart)

                    FirebaseAllPostPath.get().addOnSuccessListener {
                        var newPostModel = it.toObject<NewPostModel>(NewPostModel::class.java)
                        val currentLikes = newPostModel?.like?.size
                        FirebaseCurrentUser.get().addOnSuccessListener {

                            val currentUser = it.toObject<User>(User::class.java)

                            newPostModel?.like?.remove(currentUser?.email)


                            FirebaseAllPostPath.update("like", newPostModel?.like)
                            FirebasePostPath.update("like", newPostModel?.like)
                            isCurrentUserLikes(FirebaseAllPostPath,FirebaseCurrentUser){
                                    result-> islike=result

                            }
                            val temp= currentLikes!!-1
                            if(temp==1 || temp==0){
                                holder.binding.projectLikeNumbers.text = (temp).toString() + " "+ "like"
                            }
                            else{

                                holder.binding.projectLikeNumbers.text = (temp).toString() + " "+ "likes"
                            }






                        }


                    }


                }


            }


        }




      //  holder.binding.homePostPB.visibility= View.VISIBLE
        if(currentItem.listOfPhoto==null){
            Toast.makeText(context,"image is null", Toast.LENGTH_LONG).show()
        }

        // find and initialize ImageSlider
        val autoImageSlider = holder.binding.autoImageSlider
val imagesliderlistner=object : ItemsListener{
    private var startX: Float = 0f
    private var startY: Float = 0f
    override fun onItemChanged(position: Int) {

    }

    override fun onItemClicked(position: Int) {

    }

    override fun onTouched(actionTypes: ImageActionTypes?, position: Int) {

        // Use a ViewGroup parent type
        val parent = autoImageSlider.parent as? ViewGroup
        if (parent != null) {
            when (actionTypes) {
                ImageActionTypes.DOWN -> {
                    startX = autoImageSlider.x
                    startY = autoImageSlider.y
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                ImageActionTypes.MOVE -> {
                    val diffX = autoImageSlider.x - startX
                    val diffY = autoImageSlider.y - startY

                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        // Horizontal scroll - handle internally
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        // Vertical scroll - let parent handle
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                ImageActionTypes.UP -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                else -> {
                    // Default case, no action needed
                }
            }

        }}

}

         var listener : ItemsListener? = null
        // initialization of the listener
        listener = imagesliderlistner

        // create an imageArrayList which extend ImageSlideModel class
         val autoImageList : ArrayList<ImageSlidesModel> = ArrayList()



        // add some imagees or titles (text) inside the imagesArrayList
//        autoImageList.add(ImageSlidesModel("https://picsum.photos/id/237/200/300", "First image"))
//        autoImageList.add(ImageSlidesModel("https://picsum.photos/id/238/200/300", ""))
//        autoImageList.add(ImageSlidesModel("https://picsum.photos/id/239/200/300", "Third image")

             currentItem.listOfPhoto?.forEachIndexed  { index, photoUrl ->
                 autoImageList.add(ImageSlidesModel(photoUrl, "Project Image ${index+1}"))
             }

        // set the added images inside the AutoImageSlider
        autoImageSlider.setImageList(autoImageList, ImageScaleType.CENTER_INSIDE)

        // set any default animation or custom animation (setSlideAnimation(ImageAnimationTypes.ZOOM_IN))
        autoImageSlider.setDefaultAnimation()




        // handle click event on item click
        autoImageSlider.onItemClickListener(listener)




        holder.binding.projectProfileName.text=currentItem.user?.name
        holder.binding.projectExpandableTextView.setTextWithEllipsis(currentItem.descriptionName.toString(),100)


        if(currentItem.user?.image!=null){

            Glide.with(context).load(currentItem.user?.image).into(holder.binding.projectProfileImage)

        }
        else{
            holder.binding.projectProfileImage.setImageResource(R.drawable.person)


        }

        try {
            holder.binding.projectPostDate.text= TimeAgo.using(currentItem.time as Long).toString()
            Glide.with(context).load(currentItem.user?.image).into(holder.binding.projectProfileImage)
        }
        catch (e:Exception){}







        holder.binding.projectComment.setOnClickListener{

            val dialogFragment = CommentDialogFragment(currentItem.postUID.toString(),
                POST_Project_DETAILS)

            // Use show() to display the DialogFragment
            dialogFragment.show(fragmentManager, "MyDialogFragment")

        }
        holder.binding.projectSend.setOnClickListener{
            var shareContent = "Project Name : ${currentItem.projectName}".trim() +"\n"
                               "Technology Used : ${currentItem.technologyName}".trim() +"\n"+
                                "Description : ${currentItem.descriptionName}".trim() +"\n"






            currentItem.listOfPhoto?.forEachIndexed{ index,link->
                shareContent=shareContent+" Project Image ${index+1} : ${link} \n"

            }


            var i= Intent(Intent.ACTION_SEND)
            i.type="text/plain"
            i.putExtra(Intent.EXTRA_TEXT,shareContent)



            context.startActivity(i)
        }
        holder.binding.homePostSave.setOnClickListener{



        }



        //setting project name and technology name
        holder.binding.projectNameText.text="Project Name : ${currentItem.projectName}"
        holder.binding.projectTechnologyText.text="Technology Used : ${currentItem.technologyName}"

holder.binding.remotelinkbutton.visibility=View.GONE
        CoroutineScope(Dispatchers.Main).launch {
            if (isValidURLCoroutine(currentItem.remoteLink.toString())) {
                holder.binding.remotelinkbutton.visibility=View.VISIBLE
              holder.binding.remotelinkbutton.setOnClickListener {
                  val intent=Intent(context,WebViewActivity::class.java)
                  intent.putExtra("Url",currentItem.remoteLink.toString())
                  Log.d("url1",currentItem.remoteLink.toString())
                  context.startActivity(intent)
              }
            } else {
                Log.d("url1","url is not valid")
            }
        }


    }





    companion object{
        suspend fun isValidURLCoroutine(urlString: String): Boolean {
            return withContext(Dispatchers.IO) {
                try {
                    val url = URL(urlString)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "HEAD"
                    connection.connectTimeout = 3000 // 3 seconds timeout
                    connection.readTimeout = 3000
                    connection.connect()

                    val responseCode = connection.responseCode
                    connection.disconnect()
                    responseCode == HttpURLConnection.HTTP_OK
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

        fun isCurrentUserLikes(
            firebaseAllPost: DocumentReference,
            firebaseCurrentUser: DocumentReference,
            callback: (Boolean) -> Unit
        ) {
            var isCurrentUserLiked = false

            firebaseAllPost.get().addOnSuccessListener { allPostSnapshot ->
                val newPostModel = allPostSnapshot.toObject<NewPostModel>(NewPostModel::class.java)

                firebaseCurrentUser.get().addOnSuccessListener { currentUserSnapshot ->
                    val currentUser = currentUserSnapshot.toObject<User>(User::class.java)

                    newPostModel?.like?.let { likes ->
                        for (i in likes) {
                            if (i == currentUser?.email) {
                                isCurrentUserLiked = true
                                break
                            }
                        }
                    }

                    // Invoke the callback after both Firestore calls are completed
                    callback(isCurrentUserLiked)
                }
            }
        }
    }
}

