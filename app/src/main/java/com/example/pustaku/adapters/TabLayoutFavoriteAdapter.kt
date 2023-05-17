package com.example.pustaku.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.pustaku.fragment.favorite.ContinueFragment
import com.example.pustaku.fragment.favorite.ReadListFragment

internal class FragmentProfileAdapter(var context: Context, fragmentManager: FragmentManager, var totalTabs: Int): FragmentPagerAdapter(fragmentManager){
    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> (ContinueFragment())
            1 -> (ReadListFragment())
            else -> getItem(position)
        }
    }
}