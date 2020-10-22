package com.example.mobiletpa.adapters

import com.example.mobiletpa.models.PrintShop

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiletpa.R
import com.example.mobiletpa.activities.ChatActivity
import com.example.mobiletpa.activities.ChatDetailActivity
import com.squareup.picasso.Picasso

class PrintShopAdapter(chatThumbList: ArrayList<PrintShop>, context: Context): RecyclerView.Adapter<PrintShopAdapter.ViewHolder>() {

    var chatThumbList: ArrayList<PrintShop>
    var context: Context

    init {
        this.chatThumbList = chatThumbList
        this.context = context
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        lateinit var username: TextView
        lateinit var lastSentMessage: TextView
        lateinit var imageUrl: ImageView

        init {
            username = itemView.findViewById(R.id.shop_name)
            lastSentMessage = itemView.findViewById(R.id.shop_address)
            imageUrl = itemView.findViewById(R.id.shop_image)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.printshop_display_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return this.chatThumbList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatThumb = chatThumbList.get(position)
        holder.username.setText(chatThumb.name)
        holder.lastSentMessage.setText(chatThumb.address)
        Picasso.get().load(chatThumb.imageUrl).placeholder(R.drawable.marker).into(holder.imageUrl)

    }


}