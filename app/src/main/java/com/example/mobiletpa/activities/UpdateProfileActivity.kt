package com.example.mobiletpa.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mobiletpa.R
import com.example.mobiletpa.activities.ui.profile.ProfileViewModel
import com.example.mobiletpa.models.CacheRemover
import com.example.mobiletpa.models.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var profileViewModel: ProfileViewModel
    lateinit var email: TextView
    lateinit var name: TextView
    lateinit var phone: TextView
    lateinit var passwordNew: TextInputLayout

    lateinit var profile: LinearLayout
    lateinit var mAuth: FirebaseAuth
    lateinit var updateBtn : Button

    lateinit var userId : String
    lateinit var userRole : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        mAuth = FirebaseAuth.getInstance()
        profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        updateBtn = findViewById(R.id.updateBtn)

        email = findViewById(R.id.emailEt)
        name = findViewById(R.id.nameEt)
        phone = findViewById(R.id.phoneEt)
        profile = findViewById(R.id.profileLl)
//        passwordNew = findViewById(R.id.passwordNewEt)
//        val textView: TextView = root.findViewById(R.id.text_profile)
//        profileViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        profileViewModel.getProfile().observe(this, object: Observer<User> {
            override fun onChanged(t: User?) {
                userId = (t!!.id)
                userRole = (t!!.role)
                email.text = (t?.email)
                name.text = (t?.name)
                phone.text = (t?.phone)
                profile.visibility = View.VISIBLE
            }
        })

        updateBtn.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {

                var editEmail = email.text.toString()
                var editName = name.text.toString()
                var editPhone = phone.text.toString()

                var map = mutableMapOf<String, Any>()
                map["id"] = userId
                map["role"] = userRole
                map["email"] = editEmail
                map["name"] = editName
                map["phone"] = editPhone

                FirebaseDatabase.getInstance().reference
                    .child("Users").child(userId).setValue(map)

                Toast.makeText(this@UpdateProfileActivity, "Success Update!", Toast.LENGTH_LONG).show()
            }
        })
    }
}
