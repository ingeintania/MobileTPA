package com.example.mobiletpa.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mobiletpa.R
import com.example.mobiletpa.activities.ui.profile.ProfileViewModel
import com.example.mobiletpa.models.Order
import com.example.mobiletpa.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class TakeOrderActivity : AppCompatActivity() {

    lateinit var mDb: FirebaseDatabase

    private lateinit var profileViewModel: ProfileViewModel

    lateinit var orderNo: TextView
    lateinit var orderPaper: TextView
    lateinit var orderColor: TextView
    lateinit var orderDesc: TextView
    lateinit var orderPrice: TextView
    lateinit var orderId: String
    lateinit var orderDate: TextView
    lateinit var orderGDriveLink: TextView
    lateinit var orderPayment: TextView
    lateinit var takeOrderBtn: Button
    lateinit var userId : String
    lateinit var myId : String

    lateinit var notificationRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_order)

        val parentIntent = intent

        mDb = FirebaseDatabase.getInstance()

        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications")
        profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        profileViewModel.getProfile().observe(this, object: Observer<User> {
            override fun onChanged(t: User?) {
                myId = (t!!.id)
            }
        })

        orderNo = findViewById(R.id.ordernumTv)
        orderPaper = findViewById(R.id.paperTv)
        orderColor = findViewById(R.id.colorTv)
        orderDesc = findViewById(R.id.descTv)
        orderPrice = findViewById(R.id.priceTv)
        orderDate = findViewById(R.id.dateTv)
        orderGDriveLink = findViewById(R.id.gdriveLinkTv)
        orderPayment = findViewById(R.id.paymentTv)
        takeOrderBtn = findViewById(R.id.takeorderBtn)

        orderNo.setText(parentIntent.getStringExtra("orderNo"))
        orderPaper.setText(parentIntent.getStringExtra("orderPaper"))
        orderColor.setText(parentIntent.getStringExtra("orderColor"))
        orderDesc.setText(parentIntent.getStringExtra("orderDesc"))
        orderPrice.setText("Rp. " + parentIntent.getStringExtra("orderPrice"))
        orderId = parentIntent.getStringExtra("orderId")
        orderDate.setText(parentIntent.getStringExtra("orderDate"))
        orderGDriveLink.setText(parentIntent.getStringExtra("orderGDriveLink"))
        orderPayment.setText(parentIntent.getStringExtra("orderPayment"))

        takeOrderBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                userId = orderId.dropLast(45)

                val data = Order()
                data.id = orderId
                data.paper = orderPaper.text.toString()
                data.color = orderColor.text.toString()
                data.desc = orderDesc.text.toString()
                data.price = orderPrice.text.toString().split(" ")[1].toInt()
                data.date = orderDate.text.toString()
                data.gdriveLink = orderGDriveLink.text.toString()
                data.payment = orderPayment.text.toString()
                data.status = "Done"

                mDb.getReference("Orders").child(orderId).setValue(data).addOnCompleteListener { task ->
                    if(task.isSuccessful){

                        var newOrderNotificationMap : HashMap<String, String>
                        newOrderNotificationMap = HashMap()

                        newOrderNotificationMap.put("from", myId)
                        newOrderNotificationMap.put("type", "order success")

                        notificationRef.child(userId).push().setValue(newOrderNotificationMap)
                            .addOnCompleteListener { object : OnCompleteListener<Void> {
                                override fun onComplete(p0: Task<Void>) {
                                    if(task.isSuccessful){

                                    }
                                }

                            } }
                        Toast.makeText(this@TakeOrderActivity, "Success take order!", Toast.LENGTH_LONG).show()

                    }else{
                        Toast.makeText(this@TakeOrderActivity, "Failed take order!", Toast.LENGTH_LONG).show()
                    }
                }

                finish()
            }

        })

    }
}
