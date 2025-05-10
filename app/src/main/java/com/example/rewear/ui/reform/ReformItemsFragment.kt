package com.example.rewear.ui.reform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rewear.R
import com.example.rewear.databinding.FragmentReformItemsBinding
import com.google.android.material.button.MaterialButton

class ReformItemsFragment : Fragment() {
    private var _binding: FragmentReformItemsBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReformItemsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.BtnBack.setOnClickListener {

            findNavController().popBackStack()
        }

        binding.btnNext.setOnClickListener{
            findNavController().navigate(R.id.action_reformItemsFragment_to_reformAiFragment)
        }

        binding.toggleGroup.addOnButtonCheckedListener{group,checkedId,isChecked->
            val checkedButton=group.findViewById<MaterialButton>(checkedId)

            for(i in 0 until group.childCount){
                val button=group.getChildAt(i)as MaterialButton
                button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lightgray_bar))
                button.iconTint=ContextCompat.getColorStateList(requireContext(),R.color.uncheck)
            }

            if(isChecked){
                checkedButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.main2_opacity))
                checkedButton.iconTint=ContextCompat.getColorStateList(requireContext(),R.color.blue)
            }
        }
    }
}

