package com.community.codersaidhub.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.community.codersaidhub.R
import com.community.codersaidhub.UI.fragments.search_ResultProfile_Fragment
import com.community.codersaidhub.models.User

class SearchResultAdapter(val context: Context, var list:ArrayList<User> ):RecyclerView.Adapter<SearchResultAdapter.viewholder>() {

    class viewholder(val itemview: View):RecyclerView.ViewHolder(itemview){
        val name= itemview.findViewById<TextView>(R.id.searchProfileName)
        val email= itemview.findViewById<TextView>(R.id.searchProfileEmail)
        val image= itemview.findViewById<ImageView>(R.id.searchProfileImage)
    }
    fun setData(newList : ArrayList<User>){
        list=newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val view= LayoutInflater.from(context).inflate(R.layout.search_profile_itemview,parent,false)
        return viewholder(view)
    }

    override fun getItemCount(): Int {
      return list.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        val currentItem= list.get(position)
       holder.name.text=currentItem.name
       holder.email.text=currentItem.email
        Glide.with(context).load(currentItem.image).into(holder.image)
        holder.itemView.setOnClickListener {


            // Handle item click
            val user =currentItem

            // Create a bundle to pass data to ReelsFragment
            val bundle = Bundle()
            bundle.putSerializable("User", user)

            // Get the NavController
            val navController = Navigation.findNavController(holder.itemView)

            // Navigate to ReelsFragment using NavController
            navController?.navigate(R.id.action_search_ResultProfile_Fragment_to_navigation_profile,bundle)
            navController.findDestination(R.id.navigation_profile)


        }
    }
}