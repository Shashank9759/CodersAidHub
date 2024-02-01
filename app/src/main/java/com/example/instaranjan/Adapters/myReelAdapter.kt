package com.example.instaranjan.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.instaranjan.R
import com.example.instaranjan.databinding.MypostItemviewBinding
import com.example.instaranjan.databinding.MyreelsItemviewBinding
import com.example.instaranjan.models.NewReelModel

class myReelAdapter(val context: Context, var reelList:ArrayList<NewReelModel>): RecyclerView.Adapter<myReelAdapter.viewHolder>() {


    class viewHolder(val binding: MyreelsItemviewBinding): RecyclerView.ViewHolder(binding.root)

    fun setData(newItemList: ArrayList<NewReelModel>) {
        reelList = newItemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = MyreelsItemviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem= reelList.get(position)
        holder.binding.videoViewConstraintLayout.visibility= View.GONE
        Glide.with(context).load(currentItem.video ).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.imageViewReels)


        holder.itemView.setOnClickListener {


            // Handle item click
            val reelModel = currentItem

            // Create a bundle to pass data to ReelsFragment
            val bundle = Bundle()
            bundle.putSerializable("reelModel", reelModel)

            // Get the NavController
            val navController = Navigation.findNavController(holder.itemView)

            // Navigate to ReelsFragment using NavController
            navController?.navigate(R.id.action_myReelsFragment_to_navigation_reels, bundle)
            navController.findDestination(R.id.navigation_reels)
        }
    }
}