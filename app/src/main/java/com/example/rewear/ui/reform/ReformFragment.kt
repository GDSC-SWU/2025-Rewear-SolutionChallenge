package com.example.rewear.ui.reform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.rewear.databinding.FragmentReformBinding
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.R

class ReformFragment : Fragment() {
    enum class ReformStatus {
        RECEIVED, IN_PROGRESS, COMPLETED
    }

    private lateinit var adapter: ReformAdapter
    private var _binding: FragmentReformBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReformBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReformAdapter()
        binding.reformSwapRecyclerView.adapter = adapter
        binding.reformSwapRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.reform_border)?.let {
            divider.setDrawable(it)
        }
        binding.reformSwapRecyclerView.addItemDecoration(divider)

        val allItems = listOf(
            ReformItem(1, "Layering T-shirt", ReformStatus.RECEIVED),
            ReformItem(2, "Letter Open Hoodie", ReformStatus.IN_PROGRESS),
            ReformItem(3, "Ballon pants", ReformStatus.IN_PROGRESS),
            ReformItem(4, "Check mini skirt", ReformStatus.COMPLETED),
        )


        adapter.submitList(allItems)

        updateFilterButtons(binding.btnAll)

        binding.btnAll.setOnClickListener {
            updateFilterButtons(binding.btnAll)
            adapter.submitList(allItems)

        }
        binding.btnInProgress.setOnClickListener {
            updateFilterButtons(binding.btnInProgress)
            adapter.filterByStatus(ReformStatus.IN_PROGRESS)

        }
        binding.btnCompleted.setOnClickListener {
            updateFilterButtons(binding.btnCompleted)
            adapter.filterByStatus(ReformStatus.COMPLETED)
        }


    }

    private fun updateFilterButtons(selectedButton: Button) {
        listOf(binding.btnAll, binding.btnInProgress, binding.btnCompleted).forEach { button ->
            button.isSelected = (button == selectedButton)
            val fontId =
                if (button == selectedButton) R.font.pretendard_semibold else R.font.pretendard_regular
            button.typeface = ResourcesCompat.getFont(requireContext(), fontId)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}