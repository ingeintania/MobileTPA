package com.example.mobiletpa.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiletpa.R
import com.example.mobiletpa.adapters.ViewOrderHistoryAdapter
import com.example.mobiletpa.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_view_history.*

class ViewHistoryActivity : AppCompatActivity() {

    lateinit var mDb: FirebaseDatabase
    lateinit var mAuth: FirebaseAuth
    lateinit var recyclerView: RecyclerView
    lateinit var sortByPriceBtn: Button
    lateinit var orderList: ArrayList<Order>
    lateinit var noOrder: LinearLayout
    lateinit var yesOrder: LinearLayout
    lateinit var  progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_history)

        mDb = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        progressBar = findViewById(R.id.progressBarLl)

        recyclerView = findViewById(R.id.viewhistoryRv)

        sortByPriceBtn = findViewById(R.id.sortbypriceBtn)

        noOrder = findViewById(R.id.no_order)
        yesOrder = findViewById(R.id.order)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val orderList = ArrayList<Order>()

        val adapter = ViewOrderHistoryAdapter(orderList, this)

        recyclerView.adapter = adapter

        val ol = mDb.getReference("Orders")

        progressBar.visibility = View.VISIBLE
        noOrder.visibility = View.GONE
        yesOrder.visibility = View.GONE

        ol.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                orderList.clear()
                for (dt in p0.children) {
                    if (dt.key.toString().contains(mAuth.currentUser?.uid.toString())) {
                        if (dt.getValue(Order::class.java)?.status.toString().equals("Done")) {
                            var data = Order()
                            data.id = dt.getValue(Order::class.java)?.id.toString()
                            data.paper = dt.getValue(Order::class.java)?.paper.toString()
                            data.color = dt.getValue(Order::class.java)?.color.toString()
                            data.desc = dt.getValue(Order::class.java)?.desc.toString()
                            data.price = dt.getValue(Order::class.java)?.price.toString().toInt()
                            data.date =  dt.getValue(Order::class.java)?.date.toString()
                            data.payment = dt.getValue(Order::class.java)?.payment.toString()
                            data.gdriveLink = dt.getValue(Order::class.java)?.gdriveLink.toString()
                            data.status = dt.getValue(Order::class.java)?.status.toString()

                            orderList.add(data)
                        }
                    }
                }

                if (orderList.size == 0) {
                    progressBar.visibility = View.GONE
                    noOrder.visibility = View.VISIBLE
                    yesOrder.visibility = View.GONE
                } else {
                    progressBar.visibility = View.GONE
                    noOrder.visibility = View.GONE
                    yesOrder.visibility = View.VISIBLE
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        sortByPriceBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                orderList.sortBy { selector01(it) }
                adapter.notifyDataSetChanged()
            }
        })
    }

    fun selector01(o: Order): Int {
        return o.price
    }

}
