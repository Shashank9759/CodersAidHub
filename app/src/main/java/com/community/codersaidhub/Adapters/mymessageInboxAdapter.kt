package com.community.codersaidhub.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.community.codersaidhub.R
import com.community.codersaidhub.UI.activities.MessageActivity2
import com.community.codersaidhub.models.User

class mymessageInboxAdapter(val context: Context,var list:List<User>,val currentUser:User):RecyclerView.Adapter<mymessageInboxAdapter.myviewholder>() {

    class myviewholder(val itemview: View):RecyclerView.ViewHolder(itemview){
        val image= itemview.findViewById<ImageView>(R.id.messagnerProfileImage)
        val name= itemview.findViewById<TextView>(R.id.messagnerProfileName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {
        val view=LayoutInflater.from(context).inflate(R.layout.messageanger_itemiew,parent,false)
        return myviewholder(view)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: myviewholder, position: Int) {
       val currentItem= list.get(position)
        Glide.with(context).load(currentItem.image).placeholder(R.drawable.icon_profile).into(holder.image)
        holder.name.text=currentItem.name
        holder.itemview.setOnClickListener {
            val intent= Intent(context, MessageActivity2::class.java)
            intent.putExtra("anotherUser",currentItem)
            intent.putExtra("currentUser",currentUser)


            Log.d("dkjnvyjy",currentUser.email+"-"+currentItem.email)
            context.startActivity(intent)
        }
    }


    fun ondatachanged(newList:List<User>){
        this.list=newList
        notifyDataSetChanged()
    }
}