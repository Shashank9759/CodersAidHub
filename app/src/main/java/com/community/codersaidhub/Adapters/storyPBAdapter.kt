package com.community.codersaidhub.Adapters

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.community.codersaidhub.R
import com.community.codersaidhub.databinding.InstagramStoryItemviewBinding
import com.community.codersaidhub.databinding.StoryRvItemviewBinding
import com.community.codersaidhub.models.storyModel




class storyPBAdapter(val context: Context, val storyList:List<storyModel>, var callback:(Int)->Unit): RecyclerView.Adapter<storyPBAdapter.viewHolder>() {


    class viewHolder(val binding: StoryRvItemviewBinding): RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = StoryRvItemviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem= storyList.get(position)
        val handler=Handler()
        holder.binding.StoryprogressBar.max=5
        var count =0
//        while(true){
//            handler.postDelayed({
//                count=count+1
//
//                holder.binding.StoryprogressBar.progress=count
//
//            },1000)
//            if(count>5){
//                handler.removeCallbacksAndMessages(null)
//                callback(position)
//               break
//            }
//        }


    }
}