package com.community.codersaidhub.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebyashish.autoimageslider.Enums.ImageActionTypes
import com.codebyashish.autoimageslider.Enums.ImageScaleType
import com.codebyashish.autoimageslider.Interfaces.ItemsListener
import com.codebyashish.autoimageslider.Models.ImageSlidesModel
import com.community.codersaidhub.Adapters.homeProjectAdapter.Companion.isCurrentUserLikes
import com.community.codersaidhub.Adapters.homeProjectAdapter.Companion.isValidURLCoroutine
import com.community.codersaidhub.R
import com.community.codersaidhub.UI.activities.ExploreActivity
import com.community.codersaidhub.UI.activities.WebViewActivity
import com.community.codersaidhub.UI.fragments.CommentDialogFragment
import com.community.codersaidhub.databinding.MypostItemviewBinding
import com.community.codersaidhub.databinding.MyprojectItemviewBinding
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.models.User
import com.community.codersaidhub.models.projectModel
import com.community.codersaidhub.utils.POST_Project_DETAILS
import com.community.codersaidhub.utils.USER_NODE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class myProjectAdapter(private val fragmentManager: FragmentManager, val context: Context, var postList:ArrayList<projectModel>): RecyclerView.Adapter<myProjectAdapter.viewHolder>() {


    class viewHolder(val binding: MyprojectItemviewBinding): RecyclerView.ViewHolder(binding.root)

    fun setData(newItemList: ArrayList<projectModel>) {
        postList = newItemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = MyprojectItemviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem= postList.get(position)


        var FirebasePostPath = Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(currentItem.postUID!!)
        var FirebaseAllPostPath = Firebase.firestore.collection(POST_Project_DETAILS).document(currentItem.postUID!!)
        var FirebaseCurrentUser= Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)


        var islike:Boolean?=null
        isCurrentUserLikes(FirebaseAllPostPath,FirebaseCurrentUser){
                result->   islike= result


            //Like Handling


            if (islike==true){
                holder.binding.myprojectLike.setImageResource(R.drawable.heart_red)

            }else{
                holder.binding.myprojectLike.setImageResource(R.drawable.heart)
            }



            FirebaseAllPostPath.get().addOnSuccessListener {

                val projectmodel = it.toObject<projectModel>(projectModel::class.java)
                val currentLikes = projectmodel?.like?.size
                if (currentLikes==1   || currentLikes==0){
                    holder.binding.myprojectLikeNumbers.text= currentLikes.toString() + " "+"like"
                }else{
                    holder.binding.myprojectLikeNumbers.text= currentLikes.toString() + " "+"likes"
                }




            }


    // like click listner
            holder.binding.myprojectLike.setOnClickListener{

                if(islike==false) {
                    holder.binding.myprojectLike.setImageResource(R.drawable.heart_red)

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
                                holder.binding.myprojectLikeNumbers.text = (temp).toString() + " "+ "like"
                            }
                            else{

                                holder.binding.myprojectLikeNumbers.text = (temp).toString() + " "+ "likes"
                            }






                        }


                    }


                }
                else{


                    holder.binding.myprojectLike.setImageResource(R.drawable.heart)

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
                                holder.binding.myprojectLikeNumbers.text = (temp).toString() + " "+ "like"
                            }
                            else{

                                holder.binding.myprojectLikeNumbers.text = (temp).toString() + " "+ "likes"
                            }






                        }


                    }


                }


            }

            //comment click listner
            holder.binding.myprojectComment.setOnClickListener{

                val dialogFragment = CommentDialogFragment(currentItem.postUID.toString(),
                    POST_Project_DETAILS)

                // Use show() to display the DialogFragment
                dialogFragment.show(fragmentManager, "MyDialogFragment")

            }


        }
        // find and initialize ImageSlider
        val autoImageSlider = holder.binding.autoImageSlider2
        val imagesliderlistner=object : ItemsListener {

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

        }}}

        var listener : ItemsListener? = null
        // initialization of the listener
        listener = imagesliderlistner

        // create an imageArrayList which extend ImageSlideModel class
        val autoImageList : ArrayList<ImageSlidesModel> = ArrayList()




        currentItem.listOfPhoto?.forEachIndexed  { index, photoUrl ->
            autoImageList.add(ImageSlidesModel(photoUrl, ""))
        }

        // set the added images inside the AutoImageSlider
        autoImageSlider.setImageList(autoImageList, ImageScaleType.CENTER_INSIDE)

        // set any default animation or custom animation (setSlideAnimation(ImageAnimationTypes.ZOOM_IN))
        autoImageSlider.setDefaultAnimation()




        // handle click event on item click
        autoImageSlider.onItemClickListener(listener)


        holder.binding.projectExpandableTextView2.setTextWithEllipsis("Description : "+currentItem.descriptionName.toString(),100)
        holder.binding.myprojectName.text="${currentItem.projectName}"
        holder.binding.myprojectTechnology.setTextWithEllipsis("Technology Used : "+currentItem.technologyName.toString(),100)

        holder.binding.myprojectRemotelink.visibility= View.GONE
        CoroutineScope(Dispatchers.Main).launch {
            if (isValidURLCoroutine(currentItem.remoteLink.toString())) {
                holder.binding.myprojectRemotelink.visibility= View.VISIBLE
                holder.binding.myprojectRemotelink.setOnClickListener {
                    val intent=Intent(context, WebViewActivity::class.java)
                    intent.putExtra("Url",currentItem.remoteLink.toString())
                    Log.d("url1",currentItem.remoteLink.toString())
                    context.startActivity(intent)
                }
            } else {
                Log.d("url1","url is not valid")
            }
        }
        holder.binding.myprojectSend.setOnClickListener {
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
    }
}