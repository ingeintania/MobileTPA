package com.example.mobiletpa.activities.ui.order

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.mobiletpa.R
import com.example.mobiletpa.activities.OrderActivity
import com.example.mobiletpa.activities.ui.viewholder.PrintShopViewHolder
import com.example.mobiletpa.adapters.PrintShopAdapter
import com.example.mobiletpa.models.Order
import com.example.mobiletpa.models.PrintShop
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class FragmentOne() : Fragment() {

    lateinit var orderBtn : Button

    lateinit var myShopList: RecyclerView
    lateinit var shopReference: DatabaseReference
    lateinit var options : FirebaseRecyclerOptions<PrintShop>
    lateinit var adapter : FirebaseRecyclerAdapter<PrintShop, PrintShopViewHolder>

    lateinit var shopView : View

    lateinit var userId : String

    lateinit var mDb: FirebaseDatabase
    lateinit var mAuth: FirebaseAuth
    lateinit var recyclerView: RecyclerView
    lateinit var sortByPriceBtn: Button
    lateinit var orderList: ArrayList<PrintShop>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        shopView = inflater.inflate(R.layout.fragment_one, container, false)

        orderBtn = shopView.findViewById(R.id.orderBtn)

        orderBtn.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent (activity, OrderActivity::class.java)
                startActivity(intent)
            }

        })

//        myShopList = shopView.findViewById(R.id.shop_list)
//        myShopList.layoutManager = (LinearLayoutManager(context))
//
//        shopReference = FirebaseDatabase.getInstance().getReference("PrintShop")

        mDb = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        recyclerView = shopView.findViewById(R.id.shop_list)


        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.activity!!)

        orderList = ArrayList<PrintShop>()

        val adapter = PrintShopAdapter(orderList, this.activity!!)

        recyclerView.adapter = adapter

        val ol = mDb.getReference("PrintShop")


        ol.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                orderList.clear()
                for (dt in p0.children) {

                            var data = PrintShop()
                            data.id = dt.getValue(PrintShop::class.java)?.id.toString()
                            data.name = dt.getValue(PrintShop::class.java)?.name.toString()
                            data.address = dt.getValue(PrintShop::class.java)?.address.toString()
                            data.imageUrl = dt.getValue(PrintShop::class.java)?.imageUrl.toString()
                            data.latitude = dt.getValue(PrintShop::class.java)?.latitude.toString()
                            data.longitude =  dt.getValue(PrintShop::class.java)?.longitude.toString()
                            data.openHour = dt.getValue(PrintShop::class.java)?.openHour.toString()
                            data.closeHour = dt.getValue(PrintShop::class.java)?.closeHour.toString()
                            data.rating = dt.getValue(PrintShop::class.java)?.rating.toString()

                            orderList.add(data)

                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })


        return shopView
    }



//    override fun onStart() {
//        super.onStart()
//
//        val options = FirebaseRecyclerOptions.Builder<PrintShop>()
//            .setQuery(shopReference, PrintShop::class.java).build()
//
//        val adapter : FirebaseRecyclerAdapter<PrintShop, ShopViewHolder> =
//            object : FirebaseRecyclerAdapter<PrintShop, ShopViewHolder>(options){
//                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
//                    val view : View
//
//                    view = LayoutInflater.from(parent.context).inflate(R.layout.printshop_display_layout, parent,false)
//
//                    val viewHolder : ShopViewHolder
//
//                    viewHolder = ShopViewHolder(view)
//
//                    return viewHolder
//                }
//
//                override fun onBindViewHolder(
//                    holder: ShopViewHolder,
//                    position: Int,
//                    model: PrintShop
//                ) {
//
//                    shopReference.addValueEventListener(
//                        object : ValueEventListener{
//                            override fun onCancelled(p0: DatabaseError) {
//                                TODO("Not yet implemented")
//                            }
//
//                            override fun onDataChange(p0: DataSnapshot) {
//                                    for(dt in p0.children){
//                                        val imageUrl = dt.getValue(PrintShop::class.java)?.imageUrl.toString()
//                                        val name = dt.getValue(PrintShop::class.java)?.name.toString()
//                                        val address = dt.getValue(PrintShop::class.java)?.address.toString()
//
//                                        holder.shop_name.setText(name)
//                                        holder.shop_address.setText(address)
//                                        Picasso.get().load(imageUrl).
//                                        placeholder(R.drawable.marker).into(holder.shop_image)
//                                    }
//
//                            }
//                        }
//                    )
//                }
//            }
//        myShopList.setAdapter(adapter)
//        adapter.startListening()
//    }
//
//    class ShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var shop_name : TextView
//        var shop_address : TextView
//
//        var shop_image : ImageView
//
//        init {
//            shop_name = itemView.findViewById(R.id.shop_name)
//            shop_address = itemView.findViewById(R.id.shop_address)
//            shop_image = itemView.findViewById(R.id.shop_image)
//        }
//
//    }


}
