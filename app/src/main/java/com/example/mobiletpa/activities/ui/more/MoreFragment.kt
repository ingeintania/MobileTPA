package com.example.mobiletpa.activities.ui.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mobiletpa.R
import com.example.mobiletpa.activities.ChatActivity
import com.example.mobiletpa.activities.SearchMapsActivity

class MoreFragment : Fragment() {

    private lateinit var moreViewModel: MoreViewModel
    private lateinit var customerService: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        moreViewModel =
            ViewModelProviders.of(this).get(MoreViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_more, container, false)
        customerService = root.findViewById(R.id.customerserviceLl)

        customerService.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val toChat = Intent(activity, ChatActivity::class.java)
                startActivity(toChat)
            }
        })

        return root
    }
}
