package com.example.blogginapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.blogginapp.Adapter.TabPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ProfieActivity : AppCompatActivity() {
    private var viewPager: ViewPager2? = null
    private var tabLayout: TabLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profie)
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        viewPager?.adapter = TabPagerAdapter(this)

        TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->

            val titles = resources.getStringArray(R.array.tab_titles)
            tab.text = titles[position]

        }.attach()



    }
}