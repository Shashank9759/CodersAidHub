package com.example.instaranjan.Adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.instaranjan.R
import com.example.instaranjan.UI.activities.ExploreActivity
import com.example.instaranjan.models.CombineReelPostModel
import com.example.instaranjan.models.messageModel

class messageAdapter (val context: Context, val list:ArrayList<messageModel>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolderCurrentUser( itemview: View): RecyclerView.ViewHolder(itemview){
        val message= itemview.findViewById<TextView>(R.id.currentuserMessage)


    }
    class ViewHolderAnotherUser(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val message= itemview.findViewById<TextView>(R.id.anotheruserMessage)

    }

    companion object{
        val currentUserViewType=0
        val AnotherUserType=1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {



        return when(viewType){

            currentUserViewType->{
                val view= LayoutInflater.from(context).inflate( R.layout.currentuser_message_itemview,parent,false)
                ViewHolderCurrentUser(view)
            }

            AnotherUserType->{

                val view= LayoutInflater.from(context).inflate(R.layout.anotheruser_message_itemview,parent,false)
                ViewHolderAnotherUser(view)

            }
            else -> throw IllegalArgumentException("Invalid view type")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = list.get(position)

        when (holder) {
            is ViewHolderCurrentUser -> {
             holder.message.text=currentItem.message
            }

            is ViewHolderAnotherUser -> {
                holder.message.text=currentItem.message


            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }




}



