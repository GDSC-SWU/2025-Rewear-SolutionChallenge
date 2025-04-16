package com.example.rewear.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.rewear.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val swapCount = 1
        val donationCount = 5

        val messageView: TextView = binding.userMessage
        val digitLayout: LinearLayout = binding.digitLayout

        if (swapCount == 0 && donationCount == 0) {
            messageView.visibility = View.VISIBLE
            digitLayout.visibility = View.GONE
        } else {
            messageView.visibility = View.GONE
            digitLayout.visibility = View.VISIBLE

            val swapDigits = String.format("%02d", swapCount)
            val donationDigits = String.format("%02d", donationCount)

            binding.swapDigit1.text = swapDigits[0].toString()
            binding.swapDigit2.text = swapDigits[1].toString()
            binding.donationDigit1.text = donationDigits[0].toString()
            binding.donationDigit2.text = donationDigits[1].toString()

        }

        val fab: FloatingActionButton = binding.fabAdd
        fab.setOnClickListener {

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}