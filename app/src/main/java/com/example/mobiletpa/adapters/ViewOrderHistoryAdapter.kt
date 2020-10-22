package com.example.mobiletpa.adapters

import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiletpa.R
import com.example.mobiletpa.models.ChatContent
import com.example.mobiletpa.models.ChatThumb
import com.example.mobiletpa.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ViewOrderHistoryAdapter(orderList: ArrayList<Order>, context: Context): RecyclerView.Adapter<ViewOrderHistoryAdapter.ViewHolder>() {

    lateinit var orderList: ArrayList<Order>
    lateinit var context: Context

    init {
        this.orderList = orderList
        this.context = context
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        lateinit var ordernum: TextView
        lateinit var paper: TextView
        lateinit var color: TextView
        lateinit var price: TextView
        lateinit var date: TextView
        lateinit var gDriveLink: TextView
        lateinit var order: LinearLayout
        lateinit var  copyBtn: Button


        init {
            ordernum = itemView.findViewById(R.id.ordernumTv)
            paper = itemView.findViewById(R.id.paperTv)
            color = itemView.findViewById(R.id.colorTv)
            price = itemView.findViewById(R.id.priceTv)
            date = itemView.findViewById(R.id.dateTv)
            gDriveLink = itemView.findViewById(R.id.gdriveLinkTv)
            order = itemView.findViewById(R.id.vieworderLl)
            copyBtn = itemView.findViewById(R.id.copyBtn)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_view, parent, false)

        return ViewOrderHistoryAdapter.ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: ViewOrderHistoryAdapter.ViewHolder, position: Int) {
        val order = orderList.get(position)

        holder.ordernum.setText("No. " + (position+ 1))
        holder.paper.setText(order.paper)
        holder.color.setText(order.color)
        holder.price.setText(order.price.toString())
        holder.date.setText(order.date)
        holder.gDriveLink.setText(order.gdriveLink)

        holder.copyBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as (ClipboardManager)
                cm.text = holder.gDriveLink.text
                Toast.makeText(context, "Copied To Clipboard", Toast.LENGTH_SHORT).show()
            }
        })

    }

}