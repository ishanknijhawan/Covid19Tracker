package com.ishanknijhawan.covid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ishanknijhawan.covid.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tab_layout.setupWithViewPager(view_pager)

        val viewpageAdapter =
            ViewpageAdapter(
                supportFragmentManager,
                0
            )
        viewpageAdapter.addFragment(IndiaFragment(), "India")
        viewpageAdapter.addFragment(WorldFragment(), "World")
        view_pager.adapter = viewpageAdapter

    class ViewpageAdapter(fm: FragmentManager,behavior: Int)
        : FragmentPagerAdapter(fm, behavior) {

        val fragments = arrayListOf<Fragment>()
        val fragmentTitle = arrayListOf<String>()

        fun addFragment(fragment: Fragment, title: String){
            fragments.add(fragment)
            fragmentTitle.add(title)
        }

        override fun getItem(position: Int) = fragments[position]

        override fun getCount() = fragments.size

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitle[position]
        }

    }
}
