package com.example.rewear.ui.swap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rewear.R
import com.example.rewear.databinding.FragmentSwapConfirmBinding

class SwapConfirmedFragment : Fragment() {

    private var _binding: FragmentSwapConfirmBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSwapConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCloseClickListener = View.OnClickListener {
            findNavController().navigate(R.id.action_swapConfirmedFragment_to_homeFragment)
        }
        binding.btnHome.setOnClickListener(btnCloseClickListener)
    }
}