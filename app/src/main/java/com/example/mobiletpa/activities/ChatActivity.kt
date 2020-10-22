package com.example.mobiletpa.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiletpa.R
import com.example.mobiletpa.adapters.ChatThumbAdapter
import com.example.mobiletpa.models.ChatThumb
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var mDb: FirebaseDatabase
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mDb = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.chatRv)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val chatThumbList = ArrayList<ChatThumb>()

        val adapter = ChatThumbAdapter(chatThumbList, this)

        recyclerView.adapter = adapter

        val sharedPreferences = getSharedPreferences("userSession", Context.MODE_PRIVATE)

        val ct = mDb.getReference("ChatThumb")

        ct.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                chatThumbList.clear()
                for (dt in p0.children) {
                    if (dt.key.toString().endsWith(mAuth.currentUser?.uid.toString())) {
                        var data = ChatThumb()

                        data.id = dt.getValue(ChatThumb::class.java)?.id.toString()
                        data.username = dt.getValue(ChatThumb::class.java)?.username.toString()
                        data.lastSentMessage = dt.getValue(ChatThumb::class.java)?.lastSentMessage.toString()
                        data.lastSentTime = dt.getValue(ChatThumb::class.java)?.lastSentTime.toString()

                        chatThumbList.add(data)
                    }
                }


                if (chatThumbList.size != 0) {
                    chatThumbList.sortByDescending { selector(it)}
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }

    fun selector(ct: ChatThumb): String {
        val currentDate = ct.lastSentTime
        if (currentDate.trim().equals("") == false) {
            val dateTime = currentDate.split(" ")
            val date = dateTime[0].split("-")
            val time = dateTime[1].split(":")
            val second = time[2].split(".")

            val code = date[0] + date[1] + date[2] +
                    time[0] + time[1] +
                    second[0] + second[1]
            return code
        }
        return ""
    }

}
