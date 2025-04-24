package com.example.rewear.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.databinding.FragmentNotificationBinding
import com.google.android.material.tabs.TabLayoutMediator

class NotificationFragment :Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.BtnBack.setOnClickListener {

            findNavController().popBackStack()
        }

        val adapter=FragmentAdapter(this)
        binding.viewPager.adapter=adapter

        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab,position->
            tab.text=when(position){
                0->"Swap"
                1->"Upcycle & Donate"
                else->""
            }
        }.attach()



    }

}