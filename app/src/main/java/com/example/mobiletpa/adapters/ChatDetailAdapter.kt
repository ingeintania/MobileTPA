package com.example.mobiletpa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiletpa.R
import com.example.mobiletpa.models.ChatContent
import com.example.mobiletpa.models.ChatThumb
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChatDetailAdapter(chatDetailList: ArrayList<ChatContent>, context: Context): RecyclerView.Adapter<ChatDetailAdapter.ViewHolder>() {

    lateinit var mAuth: FirebaseAuth
    lateinit var chatDetailList: ArrayList<ChatContent>
    lateinit var context: Context

    init {
        this.mAuth = FirebaseAuth.getInstance()
        this.chatDetailList = chatDetailList
        this.context = context
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        lateinit var message: TextView

        init {
            message = itemView.findViewById(R.id.messageTv)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (mAuth.currentUser?.uid.equals(chatDetailList.get(position).sender)) return 0
        else return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatDetailAdapter.ViewHolder {
        var itemView: View

        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_chat, parent, false)
        }else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chat, parent, false)
        }

        return ChatDetailAdapter.ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chatDetailList.size
    }

    override fun onBindViewHolder(holder: ChatDetailAdapter.ViewHolder, position: Int) {
        val chatContent = chatDetailList.get(position)
        holder.message.setText(chatContent.content)
    }

}