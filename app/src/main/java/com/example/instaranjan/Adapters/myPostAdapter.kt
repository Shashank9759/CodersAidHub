package com.example.instaranjan.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instaranjan.UI.activities.ExploreActivity
import com.example.instaranjan.databinding.MypostItemviewBinding


import com.example.instaranjan.models.NewPostModel


class myPostAdapter(val context: Context, var postList:ArrayList<NewPostModel>): RecyclerView.Adapter<myPostAdapter.viewHolder>() {


    class viewHolder(val binding: MypostItemviewBinding): RecyclerView.ViewHolder(binding.root)

    fun setData(newItemList: ArrayList<NewPostModel>) {
        postList = newItemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = MypostItemviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem= postList.get(position)
        Glide.with(context).load(currentItem.image).into(holder.binding.mypostImage)

        holder.itemView.setOnClickListener {
            // Handle item click
            val postModel = currentItem

            val intent= Intent(context, ExploreActivity::class.java)
            intent.putExtra("postmodel",postModel)
            context.startActivity(intent)

        }
    }
}