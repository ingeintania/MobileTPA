package com.example.mobiletpa.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.mobiletpa.activities.ui.home.HomeFragment
import com.example.mobiletpa.fragment.SliderFragment
import com.example.mobiletpa.models.New

class SliderAdapter(context: HomeFragment, fragmentManager: FragmentManager, sliderImages: ArrayList<New>):
    FragmentPagerAdapter(fragmentManager) {

    lateinit var context: HomeFragment
    lateinit var fragmentManager: FragmentManager
    lateinit var sliderImages: ArrayList<New>

    init {
        this.context = context
        this.fragmentManager = fragmentManager
        this.sliderImages = sliderImages
        SliderFragment.prepareSliderImages(this.sliderImages)
    }

    override fun getItem(position: Int): Fragment {
        var newPosition  = position % this.context.PAGES
        return SliderFragment.createInstance(this.context, newPosition)
    }

    override fun getCount(): Int {
        return this.context.PAGES * this.context.LOOPS
    }
}