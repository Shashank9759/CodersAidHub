package com.community.codersaidhub.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.community.codersaidhub.R
import com.community.codersaidhub.models.messageModel2Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class messageAdapter2 (val context: Context, var list:ArrayList<messageModel2Type>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolderCurrentUser( itemview: View): RecyclerView.ViewHolder(itemview){
        val message= itemview.findViewById<TextView>(R.id.currentuserMessage)
        val date= itemview.findViewById<TextView>(R.id.currentuserMessagedate)


    }
    class ViewHolderAnotherUser(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val message= itemview.findViewById<TextView>(R.id.anotheruserMessage)
        val date= itemview.findViewById<TextView>(R.id.anotheruserMessagedate)

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
                holder.message.text=currentItem.messages

                val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())

                val date = Date(currentItem.date)


                val formattedDateTime: String = dateFormat.format(date)
                holder.date.text=formattedDateTime
            }

            is ViewHolderAnotherUser -> {
                holder.message.text=currentItem.messages


                val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())

                val date = Date(currentItem.date)


                val formattedDateTime: String = dateFormat.format(date)
                holder.date.text=formattedDateTime

            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list.get(position).type
    }

public fun  ondatechanged(newlist:ArrayList<messageModel2Type>){
    this.list=newlist
    notifyDataSetChanged()
}


}