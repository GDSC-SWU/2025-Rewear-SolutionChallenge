package com.example.rewear.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter(fragment:Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> SwapFragment()
            1 -> UpcycledonateFragment()
            else -> SwapFragment()
        }
    }
}
