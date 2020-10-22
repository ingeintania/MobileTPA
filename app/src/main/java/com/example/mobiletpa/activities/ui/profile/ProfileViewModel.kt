package com.example.mobiletpa.activities.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobiletpa.models.New
import com.example.mobiletpa.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileViewModel : ViewModel() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference
    companion object {
        var profile: MutableLiveData<User>? = null
    }

    fun getProfile(): LiveData<User> {

        if (profile == null) {
            profile = MutableLiveData()
            loadProfile()
        }

        return profile as LiveData<User>
    }

    fun loadProfile() {
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.currentUser?.uid.toString())

        mDbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                profile?.value = p0.getValue(User::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    fun clearCache() {
        profile = null
    }

}
