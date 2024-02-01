package com.example.instaranjan.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.instaranjan.databinding.ReelsItemviewBinding


import com.example.instaranjan.models.NewReelModel

class ReelViewPagerAdapter(val context: Context,val reelList:ArrayList<NewReelModel>):RecyclerView.Adapter<ReelViewPagerAdapter.viewHolder>() {


    class viewHolder(var binding:ReelsItemviewBinding):ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = ReelsItemviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.binding.progessReelItem.visibility= View.VISIBLE
        val currentItem= reelList.get(position)
        var currentPosition=0
        Glide.with(context).load(currentItem.user?.image).into(holder.binding.profileImage)
        holder.binding.viewpagerReelCaption.text=currentItem.caption
        holder.binding.viewpagerReelUsername.text=currentItem.user?.name
        holder.binding.reelsVideo.setVideoPath(currentItem.video)
        holder.binding.reelsVideo.setOnPreparedListener {
            holder.binding.progessReelItem.visibility= View.GONE
            holder.binding.reelsVideo.start()

            holder.binding.reelsVideo.setOnClickListener {
                // Check if the video is currently playing
                if (holder.binding.reelsVideo.isPlaying) {
                    // If playing, pause the video
                    currentPosition = holder.binding.reelsVideo.currentPosition
                holder.binding.reelsVideo.pause()
                } else {
                    holder.binding.reelsVideo.seekTo(currentPosition)
                    holder.binding.reelsVideo.start()
                }
            }






            holder.binding.reelsVideo.setOnCompletionListener {
                holder.binding.reelsVideo.start()
            }

        }







    }
}