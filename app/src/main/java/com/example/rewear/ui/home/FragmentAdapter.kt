package com.example.rewear.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Adapter for managing fragments (pages) within a ViewPager2.
 */
class FragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    // Defines the total number of pages (tabs) this adapter will manage.
    override fun getItemCount(): Int = 2

    /**
     * Provides the [Fragment] instance to display for a given [position].
     */
    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> SwapFragment() // The fragment for the first page/tab (position 0)
            1 -> UpcycledonateFragment() // The fragment for the second page/tab (position 1)
            else -> SwapFragment()
        }
    }
}
