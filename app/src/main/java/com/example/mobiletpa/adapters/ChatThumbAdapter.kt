package com.example.mobiletpa.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiletpa.R
import com.example.mobiletpa.activities.ChatActivity
import com.example.mobiletpa.activities.ChatDetailActivity
import com.example.mobiletpa.models.ChatThumb

class ChatThumbAdapter(chatThumbList: ArrayList<ChatThumb>, context: Context): RecyclerView.Adapter<ChatThumbAdapter.ViewHolder>() {

    lateinit var chatThumbList: ArrayList<ChatThumb>
    lateinit var context: Context

    init {
        this.chatThumbList = chatThumbList
        this.context = context
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        lateinit var username: TextView
        lateinit var lastSentMessage: TextView
        lateinit var date: TextView
        lateinit var chatThumb: LinearLayout

        init {
            username = itemView.findViewById(R.id.usernameTv)
            lastSentMessage = itemView.findViewById(R.id.lastsentmessageTv)
            date = itemView.findViewById(R.id.dateTv)
            chatThumb = itemView.findViewById(R.id.chatThumbLl)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_thumb, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return this.chatThumbList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatThumb = chatThumbList.get(position)
        holder.username.setText(chatThumb.username)
        holder.lastSentMessage.setText(chatThumb.lastSentMessage)
        if (chatThumb.lastSentTime.trim().equals("") == false) {
            val dateTime = chatThumb.lastSentTime.split(" ")
            val date = dateTime[0].split("-")
            val lastTime = date[2] + "/" + date[1]
            holder.date.setText(lastTime)
        } else {
            holder.date.setText("")
        }

        holder.chatThumb.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val toChatDetail = Intent(context, ChatDetailActivity::class.java).putExtra("id", chatThumb.id).
                    putExtra("username", chatThumb.username)
                context.startActivity(toChatDetail)
            }
        })
    }


}