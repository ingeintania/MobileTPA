package com.example.mobiletpa.activities.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobiletpa.models.Order
import com.example.mobiletpa.models.PrintShop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrderActivityViewModel : ViewModel() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference

    companion object {
        var orders: MutableLiveData<Order>? = null
    }

    fun getData(): LiveData<Order> {
        if (orders == null) {
            orders = MutableLiveData()
            loadData()
        }

        return orders as LiveData<Order>
    }

    fun loadData() {
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.currentUser?.uid.toString())

        mDbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                orders?.value = p0.getValue(Order::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun clearCache() {
        orders = null
    }
}