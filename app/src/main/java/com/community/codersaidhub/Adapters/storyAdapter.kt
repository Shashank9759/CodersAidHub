package com.community.codersaidhub.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.community.codersaidhub.R
import com.community.codersaidhub.UI.activities.ExploreActivity
import com.community.codersaidhub.UI.activities.StoryActivity
import com.community.codersaidhub.databinding.InstagramStoryItemviewBinding
import com.community.codersaidhub.databinding.MypostItemviewBinding
import com.community.codersaidhub.databinding.StoryRvItemviewBinding
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.models.storyModel


class storyAdapter(val context: Context, var storyList:ArrayList<List<storyModel>>,var messageText:TextView): RecyclerView.Adapter<storyAdapter.viewHolder>() {


    class viewHolder(val binding: InstagramStoryItemviewBinding): RecyclerView.ViewHolder(binding.root)

    fun setData(newItemList: ArrayList<List<storyModel>>) {
        storyList = newItemList
        notifyDataSetChanged()
    }
    fun addData(newItem: List<storyModel>) {
        storyList.add(newItem)
        notifyDataSetChanged()
    }
    fun setvisiblityofText(){
        if(storyList.isEmpty()){
            messageText.visibility= View.VISIBLE
        }else{
            messageText.visibility= View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = InstagramStoryItemviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem= storyList.get(position)
     Glide.with(context).load(currentItem.get(0).profileImage).placeholder(R.drawable.person).into(holder.binding.storyImage)
    //  holder.binding.storyProfileName.text=currentItem.get(0).name.toString()
        holder.itemView.setOnClickListener {
            val intent=Intent(context,StoryActivity::class.java)
            intent.putExtra("storyModelList",ArrayList(currentItem))

            context.startActivity(intent)
        }

    }
}