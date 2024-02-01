package com.example.instaranjan.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.instaranjan.R
import com.example.instaranjan.UI.fragments.CommentDialogFragment

import com.example.instaranjan.databinding.PostItemviewBinding
import com.example.instaranjan.models.NewPostModel
import com.example.instaranjan.models.User
import com.example.instaranjan.utils.POST_IMAGE_DETAILS

import com.example.instaranjan.utils.USER_NODE
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore

import com.google.firebase.ktx.Firebase


class homePostAdapter (private val fragmentManager: FragmentManager, val context: Context, var postList:ArrayList<NewPostModel>): RecyclerView.Adapter<homePostAdapter.viewHolder>() {


    class viewHolder(val binding: PostItemviewBinding): RecyclerView.ViewHolder(binding.root)

    fun setData(newItemList: ArrayList<NewPostModel>) {
        postList = newItemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = PostItemviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {


        var isSave=false
        val currentItem= postList.get(position)
      var FirebasePostPath = Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(currentItem.postUID!!)
      var FirebaseAllPostPath = Firebase.firestore.collection(POST_IMAGE_DETAILS).document(currentItem.postUID!!)
       var FirebaseCurrentUser= Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)


        var islike:Boolean?=null
         isCurrentUserLikes(FirebaseAllPostPath,FirebaseCurrentUser){
            result->   islike= result


             //Like Handling


             if (islike==true){
                 holder.binding.homePostLike.setImageResource(R.drawable.heart_red)

             }else{
                 holder.binding.homePostLike.setImageResource(R.drawable.heart)
             }



             FirebaseAllPostPath.get().addOnSuccessListener {

                 val newPostModel = it.toObject<NewPostModel>(NewPostModel::class.java)
                 val currentLikes = newPostModel?.like?.size
                 if (currentLikes==1   || currentLikes==0){
                     holder.binding.homePostLikeNumbers.text= currentLikes.toString() + " "+"like"
                 }else{
                     holder.binding.homePostLikeNumbers.text= currentLikes.toString() + " "+"likes"
                 }




             }



             holder.binding.homePostLike.setOnClickListener{

                 if(islike==false) {
                     holder.binding.homePostLike.setImageResource(R.drawable.heart_red)

                     FirebaseAllPostPath.get().addOnSuccessListener {
                         var newPostModel = it.toObject<NewPostModel>(NewPostModel::class.java)
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
                                 holder.binding.homePostLikeNumbers.text = (temp).toString() + " "+ "like"
                             }
                             else{

                                 holder.binding.homePostLikeNumbers.text = (temp).toString() + " "+ "likes"
                             }






                         }


                     }


                 }
                 else{


                     holder.binding.homePostLike.setImageResource(R.drawable.heart)

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
                                 holder.binding.homePostLikeNumbers.text = (temp).toString() + " "+ "like"
                             }
                             else{

                                 holder.binding.homePostLikeNumbers.text = (temp).toString() + " "+ "likes"
                             }






                         }


                     }


                 }


             }


         }




        holder.binding.homePostPB.visibility= View.VISIBLE
        if(currentItem.image==null){
            Toast.makeText(context,"image is null",Toast.LENGTH_LONG).show()
        }
        Glide.with(context).load(currentItem.image) .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {

               return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                holder.binding.homePostPB.visibility= View.GONE
                return false
            }

        }).into(holder.binding.homePostImage)


holder.binding.homePostName.text=currentItem.user?.name
holder.binding.homePostCaption.text= currentItem.caption


           if(currentItem.user?.image!=null){

               Glide.with(context).load(currentItem.user?.image).into(holder.binding.homePostProfileImage)

           }
        else{
               holder.binding.homePostProfileImage.setImageResource(R.drawable.person)


           }

         try {
             holder.binding.homePostDate.text= TimeAgo.using(currentItem.date as Long).toString()
             Glide.with(context).load(currentItem.user?.image).into(holder.binding.homePostProfileImage)
         }
         catch (e:Exception){}







        holder.binding.homePostComment.setOnClickListener{

            val dialogFragment = CommentDialogFragment(currentItem)

            // Use show() to display the DialogFragment
            dialogFragment.show(fragmentManager, "MyDialogFragment")

        }
        holder.binding.homePostSend.setOnClickListener{
         var i= Intent(Intent.ACTION_SEND)
            i.type="text/plain"
            i.putExtra(Intent.EXTRA_TEXT,currentItem.image)

           context.startActivity(i)
        }
        holder.binding.homePostSave.setOnClickListener{



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