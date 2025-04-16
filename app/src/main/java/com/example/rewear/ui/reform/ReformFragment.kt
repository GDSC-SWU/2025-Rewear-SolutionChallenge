package com.example.rewear.ui.reform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rewear.databinding.FragmentReformBinding

class ReformFragment : Fragment() {

    private var _binding: FragmentReformBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val reformViewModel =
            ViewModelProvider(this).get(ReformViewModel::class.java)

        _binding = FragmentReformBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textReform
        reformViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}