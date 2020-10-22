package com.example.mobiletpa.activities

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mobiletpa.R
import com.example.mobiletpa.activities.ui.order.FragmentOne
import com.example.mobiletpa.activities.ui.order.OrderActivityViewModel
import com.example.mobiletpa.activities.ui.profile.ProfileViewModel
import com.example.mobiletpa.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.set


class OrderActivity : AppCompatActivity() {

    private lateinit var profileViewModel: ProfileViewModel
    lateinit var orderActivityViewModel: OrderActivityViewModel

    lateinit var location: Spinner
    lateinit var locationString: String

    lateinit var id: String
    lateinit var gdriveLink: EditText

    lateinit var paper: Spinner
    lateinit var paperString: String

    lateinit var color: Spinner
    lateinit var colorString: String

    lateinit var qty: EditText

    lateinit var desc: EditText

    lateinit var payment: Spinner
    lateinit var paymentString: String

    lateinit var date: String
    var price = 0
    var status = ""

    lateinit var mAuth: FirebaseAuth
    lateinit var orderNowBtn : Button

    lateinit var userId : String
    lateinit var printShopId : String

    lateinit var notificationRef : DatabaseReference

    lateinit var fragmentObject : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        fragmentObject = FragmentOne()

        mAuth = FirebaseAuth.getInstance()
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications")

        orderActivityViewModel =
            ViewModelProviders.of(this).get(OrderActivityViewModel::class.java)

        profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        profileViewModel.getProfile().observe(this, object: Observer<User> {
            override fun onChanged(t: User?) {
                userId = (t!!.id)
            }
        })

        orderNowBtn = findViewById(R.id.orderNowBtn)

        id = ""
        location = findViewById(R.id.locationEt) as Spinner
        val locations = arrayOf("Pandawa", "Cano", "Aladdin")
        location.adapter = ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, locations)
        location.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                locationString = "Pandawa"
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                locationString = locations.get(position)
            }

        }


        qty = findViewById(R.id.qtyEt)
        gdriveLink = findViewById(R.id.gdriveLinkEt)
        //paper = findViewById(R.id.paperEt)
        paper = findViewById(R.id.paperEt) as Spinner
        val papers = arrayOf("A4", "A5")
        paper.adapter = ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, papers)
        paper.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                paperString = "A4"
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                paperString = papers.get(position)
            }

        }

        //color = findViewById(R.id.colorEt)
        color = findViewById(R.id.colorEt) as Spinner
        val colors = arrayOf("Color", "BW")
        color.adapter = ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, colors)
        color.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                colorString = "Color"
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                colorString = colors.get(position)
            }

        }

        desc = findViewById(R.id.descEt)
        //payment = findViewById(R.id.paymentEt)
        payment = findViewById(R.id.paymentEt) as Spinner
        val payments = arrayOf("Cash", "Transfer")
        payment.adapter = ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, payments)
        payment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                paymentString = "Cash"
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                paymentString = payments.get(position)
            }

        }

        orderNowBtn.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val c: Calendar = Calendar.getInstance()
                val datex: String = c.get(Calendar.YEAR).toString() + c.get(Calendar.MONTH)  + c.get(Calendar.DATE)
                val timex : String = c.get(Calendar.HOUR).toString() + c.get(Calendar.MINUTE) + c.get(Calendar.SECOND) + c.get(Calendar.MILLISECOND)
                val date: String = c.get(Calendar.YEAR).toString() + "-" + c.get(Calendar.MONTH) +  "-" + c.get(Calendar.DATE)

                var editgdriveLink = gdriveLink.text.toString()
                var editpaper = paperString
                var editcolor = colorString
                var editqty = qty.text.toString()
                var editdesc = desc.text.toString()
                var editpayment = paymentString

                var map = mutableMapOf<String, Any>()

                if(gdriveLink.text.toString() == ""){
                    Toast.makeText(this@OrderActivity, "Link must be filled!", Toast.LENGTH_LONG).show()
                }else if(qty.text.toString() == ""){
                    Toast.makeText(this@OrderActivity, "Quantity be filled!", Toast.LENGTH_LONG).show()
                }else{
                    if(locationString == "Pandawa"){
                        printShopId = "CqRjnZAdqEhaQ2gIbtR45YIX7EE2"
                    }else if(locationString == "Aladdin"){
                        printShopId ="NkJ5bWcSkKdWyooDqua88g8j3002"
                    }else if(locationString == "Cano"){
                        printShopId ="YB8eZ98EsGMFioXZjXXCWxaJQ6m1"
                    }

                    id = userId + "X"+ printShopId + datex+timex

                    if(paperString == "A4" && colorString== "Color" ){
                        price = 1000
                    }else if(paperString == "A4" && colorString == "BW" ){
                        price = 500
                    }else if(paperString == "A5" && colorString == "Color" ){
                        price = 700
                    }else if(paperString == "A5" && colorString == "BW" ){
                        price = 300
                    }

                    map["id"] = id
                    map["gdriveLink"] = editgdriveLink
                    map["paper"] = editpaper
                    map["color"] = editcolor
                    map["qty"] = editqty
                    map["desc"] = editdesc
                    map["payment"] = editpayment
                    map["date"] = date
                    map["price"] = price
                    map["status"] = status

                    FirebaseDatabase.getInstance().reference
                        .child("Orders").child(id).setValue(map).addOnCompleteListener { task ->
                            if(task.isSuccessful){

                                var newOrderNotificationMap : HashMap<String, String>
                                newOrderNotificationMap = HashMap()

                                newOrderNotificationMap.put("from", userId)
                                newOrderNotificationMap.put("type", "new order")

                                notificationRef.child(printShopId).push().setValue(newOrderNotificationMap)
                                    .addOnCompleteListener { object : OnCompleteListener<Void> {
                                        override fun onComplete(p0: Task<Void>) {
                                            if(task.isSuccessful){

                                            }
                                        }

                                    } }

//                                val ft: FragmentTransaction =
//                                    supportFragmentManager.beginTransaction()
//                                ft.replace(R.id.fragment_one, fragmentObject)
//                                ft.commit()


                                Toast.makeText(this@OrderActivity, "Success add order!", Toast.LENGTH_LONG).show()



                            }else{
                                Toast.makeText(this@OrderActivity, "Failed add order!", Toast.LENGTH_LONG).show()
                            }
                        }


                }


            }

        })
    }
}
