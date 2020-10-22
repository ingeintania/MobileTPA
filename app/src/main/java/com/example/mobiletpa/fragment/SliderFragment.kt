package com.example.mobiletpa.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.mobiletpa.R
import com.example.mobiletpa.activities.ui.home.HomeFragment
import com.example.mobiletpa.models.New
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class SliderFragment(): Fragment() {

    companion object {
        lateinit var sliderImages: ArrayList<New>

        fun prepareSliderImages(sliderImages: ArrayList<New>) {
            this.sliderImages = sliderImages
        }

        fun createInstance(context: HomeFragment, position: Int):Fragment {
            val b = Bundle()
            b.putInt("position", position)
            return Fragment.instantiate(context.context as Context, SliderFragment::class.java.name, b)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container == null) {
            return null
        }

        val linearLayout = inflater.inflate(R.layout.slider_container, container, false) as LinearLayout

        val position = this.arguments?.getInt("position") as Int
        val sliderImageView = linearLayout.findViewById(R.id.sliderIv) as ImageView
//        sliderImageView.setImageResource(sliderImages.get(position).image.toInt())
        Picasso.get().load(sliderImages.get(position).imageUrl).into(sliderImageView)


        return linearLayout
    }
}