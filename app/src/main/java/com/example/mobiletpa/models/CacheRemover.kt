package com.example.mobiletpa.models

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mobiletpa.activities.ui.home.HomeViewModel
import com.example.mobiletpa.activities.ui.order.OrderViewModel
import com.example.mobiletpa.activities.ui.profile.ProfileViewModel

class CacheRemover {

    companion object {
        val profileViewModel = ProfileViewModel()
        val homeViewModel = HomeViewModel()
        val orderViewModel = OrderViewModel()

        fun clearCache() {
            profileViewModel.clearCache()
            homeViewModel.clearCache()
            orderViewModel.clearCache()
        }

    }

}