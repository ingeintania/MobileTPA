package com.example.mobiletpa.activities.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.mobiletpa.R
import com.example.mobiletpa.adapters.SliderAdapter
import com.example.mobiletpa.fragment.SliderFragment.Companion.sliderImages
import com.example.mobiletpa.models.New
import com.example.mobiletpa.models.User

class HomeFragment : Fragment() {

    var PAGES = 0
    var LOOPS = 0
    var FIRST_PAGE = 0
    private lateinit var homeViewModel: HomeViewModel
    lateinit var progressBar: LinearLayout
    lateinit var content: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        progressBar = root.findViewById(R.id.progressBarLl)
        content = root.findViewById(R.id.contentLl)

        homeViewModel.getImages().observe(this, object: Observer<List<New>> {
            override fun onChanged(t: List<New>) {

                PAGES = t.size
                LOOPS = 1000
                FIRST_PAGE = PAGES * LOOPS / 2

                val pager = root.findViewById(R.id.sliderVp) as ViewPager

                val adapter = SliderAdapter(this@HomeFragment, childFragmentManager, t as ArrayList)

                pager.adapter = adapter

                pager.setCurrentItem(FIRST_PAGE)
                pager.offscreenPageLimit = 3

                progressBar.visibility = View.GONE
                content.visibility = View.VISIBLE
            }
        })
        return root
    }

}
