package com.community.codersaidhub.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast

import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.community.codersaidhub.R
import com.community.codersaidhub.UI.activities.ExploreActivity
import com.community.codersaidhub.UI.fragments.ReelsFragment
import com.community.codersaidhub.models.CombineReelPostModel
import com.community.codersaidhub.models.NewReelModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class combineAdapter(val context: Context,val list:ArrayList<CombineReelPostModel>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolderPost( itemview:View):RecyclerView.ViewHolder(itemview){
      val postImage= itemview.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.mypostImage)


    }
    class ViewHolderReel(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val videoView= itemview.findViewById<VideoView>(R.id.videoView)
        val videoViewConstraintLayout= itemview.findViewById<ConstraintLayout>(R.id.videoViewConstraintLayout)
        val imageViewConstraintLayout= itemview.findViewById<ConstraintLayout>(R.id.imageViewConstraintLayout)
        val imageViewReels= itemview.findViewById<ImageView>(R.id.imageViewReels)

    }

companion object{
    val postViewType=0
    val reelViewType=1
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {



       return when(viewType){

           postViewType->{
               val view= LayoutInflater.from(context).inflate( R.layout.mypost_itemview,parent,false)
               ViewHolderPost(view)
           }

           reelViewType->{

               val view= LayoutInflater.from(context).inflate(R.layout.myreels_itemview,parent,false)
                 ViewHolderReel(view)

           }
           else -> throw IllegalArgumentException("Invalid view type")

       }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem= list.get(position)

        when(holder){
            is ViewHolderPost->{
                Glide.with(context).load(currentItem.postModel.image).into(holder.postImage)



                holder.itemView.setOnClickListener {



                    // Handle item click
                    val postModel = currentItem.postModel

                    val intent=Intent(context,ExploreActivity::class.java)
                    intent.putExtra("postmodel",postModel)
                    context.startActivity(intent)
                }
            }
           is  ViewHolderReel->{

               Glide.with(context).load(currentItem.reelModel.video ).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageViewReels)
               holder.videoView.setVideoPath(currentItem.reelModel.video)

//               holder.videoView.setOnPreparedListener { mediaPlayer ->
//                   holder.imageViewConstraintLayout.visibility=View.GONE
//
//
//
//
//                   mediaPlayer.setVolume(0f, 0f)
//                   holder.videoView.start()
//
//               }
//
//               holder.videoView.setOnErrorListener { mediaPlayer, what, extra ->
//
//
//                   Toast.makeText(context,"  $what : $extra",Toast.LENGTH_LONG).show()
//                   return@setOnErrorListener false
//               }



       holder.itemView.setOnClickListener {



           // Handle item click
           val reelModel = currentItem.reelModel

           // Create a bundle to pass data to ReelsFragment
           val bundle = Bundle()
           bundle.putSerializable("reelModel", reelModel)

           // Get the NavController
           val navController = Navigation.findNavController(holder.itemView)

           // Navigate to ReelsFragment using NavController
           navController?.navigate(R.id.action_search_Post_Fragment_to_navigation_reels, bundle)
           navController.findDestination(R.id.navigation_reels)
       }


            }
        }
    }

    override fun getItemCount(): Int {
return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }



}