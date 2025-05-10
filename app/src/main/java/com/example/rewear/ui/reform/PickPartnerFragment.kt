package com.example.rewear.ui.reform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.R
import com.example.rewear.databinding.FragmentPickPartnerBinding

class PickPartnerFragment : Fragment() {
    private var _binding: FragmentPickPartnerBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPickPartnerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val partners = listOf(
            Partner("Lilly Willson", R.drawable.reform_corp1, isSelected = true),
            Partner("Lilly Willson", R.drawable.reform_corp2),
            Partner("Lilly Willson", R.drawable.reform_corp3),
            Partner("Lilly Willson", R.drawable.reform_corp4),
            Partner("Lilly Willson", R.drawable.reform_corp5),
        )

        val adapter = PartnerAdapter(partners)
        binding.recyclerViewPartners.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewPartners.adapter = adapter

        binding.BtnBack.setOnClickListener {

            findNavController().popBackStack()
        }

        binding.btnDone.setOnClickListener {
            val selectedPartner=adapter.getSelectedPartner()
            if(selectedPartner!=null){
                val bundle=Bundle().apply{
                    putInt("imageResId",selectedPartner.imageResId)
                    putString("partnerName",selectedPartner.name)
                }
                findNavController().navigate(R.id.action_pickPartnerFragment_to_reformFragment)
            }

        }


    }
}