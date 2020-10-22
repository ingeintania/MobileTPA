package com.example.mobiletpa.activities.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobiletpa.models.PrintShop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList

class OrderViewModel : ViewModel() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference
    var psdata = ArrayList<PrintShop>()
    
    companion object {
        var order: MutableLiveData<List<PrintShop>>? = null
    }

    fun getData(): LiveData<List<PrintShop>> {
        if (order == null) {
            order = MutableLiveData()
            loadData()
        }

        return order as LiveData<List<PrintShop>>
    }

    fun loadData() {
        mDbRef = FirebaseDatabase.getInstance().getReference("PrintShop")

        mDbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (dt in p0.children) {
                    var data = PrintShop()

                    data.id = dt.getValue(PrintShop::class.java)?.id!!.toString()
                    data.name = dt.getValue(PrintShop::class.java)?.name.toString()
                    data.address = dt.getValue(PrintShop::class.java)?.address.toString()
                    data.imageUrl = dt.getValue(PrintShop::class.java)?.imageUrl.toString()
                    data.latitude = dt.getValue(PrintShop::class.java)?.latitude.toString()
                    data.longitude = dt.getValue(PrintShop::class.java)?.longitude.toString()
                    data.rating = dt.getValue(PrintShop::class.java)?.rating.toString()
                    data.openHour = dt.getValue(PrintShop::class.java)?.openHour.toString()
                    data.closeHour = dt.getValue(PrintShop::class.java)?.closeHour.toString()

                    psdata.add(data)
                }
                order?.value = psdata
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun clearCache() {
        order = null
    }
}