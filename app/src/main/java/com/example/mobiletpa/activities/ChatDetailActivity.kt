package com.example.mobiletpa.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiletpa.R
import com.example.mobiletpa.adapters.ChatDetailAdapter
import com.example.mobiletpa.adapters.ChatThumbAdapter
import com.example.mobiletpa.models.ChatContent
import com.example.mobiletpa.models.ChatThumb
import com.example.mobiletpa.models.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class ChatDetailActivity : AppCompatActivity() {

    lateinit var id: String
    lateinit var username: TextView
    lateinit var chatDetailList: ArrayList<ChatContent>
    lateinit var recyclerView: RecyclerView
    lateinit var message: TextInputLayout
    lateinit var sendBtn: Button
    lateinit var mAuth: FirebaseAuth
    lateinit var mDb: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)

        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseDatabase.getInstance()

        val parentIntent = intent
        id = parentIntent.getStringExtra("id")
        username = findViewById(R.id.usernameTv)
        username.setText(parentIntent.getStringExtra("username"))

        recyclerView = findViewById(R.id.chatdetailRv)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        chatDetailList = ArrayList<ChatContent>()

        val adapter = ChatDetailAdapter(chatDetailList, this)

        recyclerView.adapter = adapter
        recyclerView.scrollToPosition(chatDetailList.size - 1)

        val myId = mAuth.currentUser?.uid.toString()
        val cc = mDb.getReference("Messages").child(myId + "X" + id)

        cc.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                chatDetailList.clear()
                for (dt in p0.children) {
                    var data = ChatContent()

                    data.sender = dt.getValue(ChatContent::class.java)?.sender.toString()
                    data.content = dt.getValue(ChatContent::class.java)?.content.toString()
                    data.date = dt.getValue(ChatContent::class.java)?.date.toString()


                    chatDetailList.add(data)
                }

                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(chatDetailList.size - 1)
            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })

        recyclerView.addOnLayoutChangeListener(object: View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                recyclerView.scrollToPosition(chatDetailList.size - 1)
            }

        })

        message = findViewById(R.id.messageEt)
        sendBtn = findViewById(R.id.sendBtn)

        sendBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                if (message.editText?.text.toString().length != 0) {
                    chatDetailList.add(createChatContent(message.editText?.text.toString()))
                    adapter.notifyDataSetChanged()
                    message.editText?.setText("")
                    updateThumb()
                }
            }
        })

    }

    fun updateThumb() {

        if (chatDetailList.size != 0) {
            mDb.getReference("Users").child(mAuth.currentUser?.uid.toString()).addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val lastMessage = chatDetailList.get(chatDetailList.size - 1).content
                    val lastTime = chatDetailList.get(chatDetailList.size - 1).date

                    var updateCSData = ChatThumb()
                    updateCSData.id = id
                    updateCSData.username = username.text.toString()
                    updateCSData.lastSentMessage = lastMessage
                    updateCSData.lastSentTime = lastTime

                    var updateMyData = ChatThumb()
                    updateMyData.id = mAuth.currentUser?.uid.toString()
                    updateMyData.username = p0.getValue(User::class.java)?.name.toString()
                    updateMyData.lastSentMessage = lastMessage
                    updateMyData.lastSentTime = lastTime

                    val cs = mDb.getReference("ChatThumb").child(id + "X" + mAuth.currentUser?.uid.toString()).setValue(updateCSData)
                    val my = mDb.getReference("ChatThumb").child(mAuth.currentUser?.uid.toString() + "X" + id).setValue(updateMyData)
                }

            })
        }
    }

    fun createChatContent(content: String): ChatContent {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        sdf.timeZone = TimeZone.getTimeZone("GMT+7")
        val currentDate = sdf.format(Calendar.getInstance().time)
        val chatContent = ChatContent()

        val myId = mAuth.currentUser?.uid.toString()

        chatContent.content = content
        chatContent.date = currentDate.toString()
        chatContent.sender = myId

        val dateTime = currentDate.split(" ")
        val date = dateTime[0].split("-")
        val time = dateTime[1].split(":")
        val second = time[2].split(".")

        val code = date[0] + date[1] + date[2] +
                         time[0] + time[1] +
                         second[0] + second[1]

        mDb.getReference("Messages").child(myId + "X" + id).child(code).setValue(chatContent)
        mDb.getReference("Messages").child(id + "X" + myId).child(code).setValue(chatContent)

        return chatContent
    }

}
