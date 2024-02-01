package com.example.instaranjan.Adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm:FragmentManager): FragmentStatePagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val fragmentList= mutableListOf<Fragment>()

  val iconList = mutableListOf<Int>()
    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList.get(position)
    }


    fun addFragments(fragment: Fragment, iconResId: Int){

        fragmentList.add(fragment)

        iconList.add(iconResId)
    }
    fun getIcon(position: Int): Int {
        return iconList[position]
    }
}