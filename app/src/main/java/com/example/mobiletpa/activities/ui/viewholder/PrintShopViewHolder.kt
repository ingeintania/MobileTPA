package com.example.mobiletpa.activities.ui.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiletpa.R
import kotlinx.android.synthetic.main.fragment_one.view.*

class PrintShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var t1 : TextView = itemView.findViewById(R.id.myname)
    var i1 : ImageView = itemView.findViewById(R.id.image)

    init {


    }
}