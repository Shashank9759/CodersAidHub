package com.community.codersaidhub.Adapters

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.community.codersaidhub.databinding.ProjectImageLayoutItemviewBinding
import com.community.codersaidhub.databinding.ProjectViewImageDialogBinding
import com.community.codersaidhub.databinding.StoryRvItemviewBinding
import com.community.codersaidhub.models.storyModel



class projectImageDialogAdapter(val context: Context, val uriList:List<Uri>): RecyclerView.Adapter<projectImageDialogAdapter.viewHolder>() {


    class viewHolder(val binding: ProjectImageLayoutItemviewBinding): RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = ProjectImageLayoutItemviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return uriList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem= uriList.get(position)
   Glide.with(context).load(currentItem).into(holder.binding.imagelayoutImageview)
}


    }
