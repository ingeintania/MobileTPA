package com.example.mobiletpa.activities.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.mobiletpa.R
import com.example.mobiletpa.activities.ui.adapter.PageAdapater
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_order.*

class OrderFragment : Fragment() {

    private lateinit var orderViewModel: OrderViewModel


    lateinit var toolbar : Toolbar
    lateinit var toolbartab : Toolbar
    lateinit var viewPager: ViewPager
    lateinit var tabLayout : TabLayout
    lateinit var pageAdapter : PageAdapater


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        orderViewModel =
            ViewModelProviders.of(this).get(OrderViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_order, container, false)

//        ContextCompat.checkSelfPermission(this.activity!!, android.Manifest.permission.ACCESS_FINE_LOCATION)

        toolbar = root.findViewById(R.id.toolbar)
        toolbartab = root.findViewById(R.id.toolbartab)
        viewPager = root.findViewById(R.id.viewpager)
        tabLayout = root.findViewById(R.id.tablayout)

        pageAdapter = PageAdapater(childFragmentManager)
        pageAdapter.addFragment(FragmentOne(), "Order Now")
        pageAdapter.addFragment(FragmentTwo(), "Map Search")

        viewPager.adapter = pageAdapter

        tabLayout.setupWithViewPager(viewPager)

        return root
    }

}
