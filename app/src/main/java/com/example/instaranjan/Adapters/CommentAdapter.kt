package com.example.instaranjan.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instaranjan.databinding.CommentItemviewBinding
import com.example.instaranjan.databinding.MypostItemviewBinding
import com.example.instaranjan.models.NewPostModel
import com.example.instaranjan.models.UserComment

class CommentAdapter(val context: Context, var commentList:ArrayList<UserComment>): RecyclerView.Adapter<CommentAdapter.viewHolder>() {


    class viewHolder(val binding: CommentItemviewBinding): RecyclerView.ViewHolder(binding.root)

    fun setData(newItemList: ArrayList<UserComment>) {
        commentList = newItemList
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = CommentItemviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem= commentList.get(position)
        Glide.with(context).load(currentItem.user.image).into(  holder.binding.commentUserProfile)
        holder.binding.commentUserName.text= currentItem.user.name
        holder.binding.commentUser.text= currentItem.comment

    }
}