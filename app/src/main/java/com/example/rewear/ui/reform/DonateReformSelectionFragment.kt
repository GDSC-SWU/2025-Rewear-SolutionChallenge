package com.example.rewear.ui.reform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rewear.R
import com.example.rewear.databinding.FragmentDonateReformSelectionBinding


class DonateReformSelectionFragment : Fragment() {
    private var _binding: FragmentDonateReformSelectionBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDonateReformSelectionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("title") ?: ""

        binding.BtnClose.setOnClickListener {

            findNavController().popBackStack()
        }

        binding.btnUpcycle.setOnClickListener {
            val bundle = Bundle().apply {
                putString("title", title)
            }
            findNavController().navigate(
                R.id.action_donateReformSelectionFragment_to_reformItemsFragment,
                bundle
            )
        }
    }
}

