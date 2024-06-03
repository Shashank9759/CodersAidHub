package com.community.codersaidhub.Adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.community.codersaidhub.UI.fragments.PhotoPostFragment
import com.community.codersaidhub.UI.fragments.ProjectPostFragment


class homeViewpagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProjectPostFragment()
            else -> PhotoPostFragment()
        }
    }
}