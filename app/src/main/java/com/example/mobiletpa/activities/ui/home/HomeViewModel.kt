package com.example.mobiletpa.activities.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobiletpa.models.New
import com.example.mobiletpa.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeViewModel : ViewModel() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference
    var sliderImages = ArrayList<New>()

    companion object {
        var news: MutableLiveData<List<New>>? = null
    }

    fun getImages(): LiveData<List<New>> {
        if (news == null) {
            news = MutableLiveData()
            loadImages()
        }

        return news as LiveData<List<New>>
    }

    fun loadImages() {
        mDbRef = FirebaseDatabase.getInstance().getReference("News")

        mDbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (dt in p0.children) {
                    var data = New()

                    data.id = dt.getValue(New::class.java)?.id.toString()
                    data.imageUrl = dt.getValue(New::class.java)?.imageUrl.toString()

                    sliderImages.add(data)
                }
                news?.value = sliderImages
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun clearCache() {
        news = null
    }

}