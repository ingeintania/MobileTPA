package com.example.mobiletpa.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.mobiletpa.R

class OwnerHomeActivity : AppCompatActivity() {

    lateinit var viewOrderBtn: Button
    lateinit var viewHistoryBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_home)

        viewOrderBtn = findViewById(R.id.vieworderBtn)
        viewHistoryBtn = findViewById(R.id.viewhistoryBtn)

        viewOrderBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val toViewOrder = Intent(this@OwnerHomeActivity, ViewOrderActivity::class.java)
                startActivity(toViewOrder)
            }
        })

        viewHistoryBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val toViewOrder = Intent(this@OwnerHomeActivity, ViewHistoryActivity::class.java)
                startActivity(toViewOrder)
            }
        })

    }
}
