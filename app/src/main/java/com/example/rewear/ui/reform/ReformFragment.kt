package com.example.rewear.ui.reform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.rewear.databinding.FragmentReformBinding
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.R
import com.example.rewear.network.ApiReformItem
import com.example.rewear.network.RetrofitClientApp
import kotlinx.coroutines.launch
import java.io.IOException


class ReformFragment : Fragment() {
    enum class ReformStatus {
        RECEIVED, IN_PROGRESS, COMPLETED;


        companion object {
            fun fromApiString(apiStatus: String): ReformStatus {
                return when (apiStatus.uppercase()) {
                    "PENDING" -> RECEIVED
                    "IN_PROGRESS" -> IN_PROGRESS
                    "COMPLETED" -> COMPLETED
                    else -> RECEIVED
                }
            }
        }
    }


    private var _binding: FragmentReformBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReformAdapter
    private var currentDisplayedItems: List<ReformItem> = listOf()


    private var userItemFromArgs: ReformItem? = null
    private val hardcodedDummyItems: List<ReformItem> by lazy {
        listOf(
            ReformItem(-1, "old jeans", ReformStatus.RECEIVED, null, "Stella", R.drawable.jeans),
            ReformItem(-2, "grey knit", ReformStatus.IN_PROGRESS, null, "Lilly", R.drawable.knit),
            ReformItem(-3, "blue skirt", ReformStatus.IN_PROGRESS, null, "Kate", R.drawable.skirt),
            ReformItem(-4, "Check mini skirt", ReformStatus.COMPLETED, null, "Ena", R.drawable.cloth_example5)
        )
    }


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

        val imageResIdFromArgs = arguments?.getInt("imageResId") ?: 0
        val partnerNameFromArgs = arguments?.getString("partnerName") ?: ""
        if (partnerNameFromArgs.isNotEmpty()) {
            userItemFromArgs = ReformItem(
                0, "new", ReformStatus.RECEIVED, null, partnerNameFromArgs,
                if (imageResIdFromArgs != 0) imageResIdFromArgs else null
            )
        }


        setupRecyclerView()
        setupFilterButtons()
        fetchReformItems(apiStatusQuery = null, localFilterStatus = ReformStatus.RECEIVED, isForAllButton = true)
    }
    private fun setupRecyclerView() {

        adapter = ReformAdapter()
        binding.reformSwapRecyclerView.adapter = adapter
        binding.reformSwapRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.reform_border)?.let {
            divider.setDrawable(it)
        }
        binding.reformSwapRecyclerView.addItemDecoration(divider)
    }

    private fun fetchReformItems(apiStatusQuery: String?, localFilterStatus: ReformStatus?, isForAllButton: Boolean = false) {
        viewLifecycleOwner.lifecycleScope.launch {
            val mappedItemsFromApi = mutableListOf<ReformItem>()

            try {
                val response = RetrofitClientApp.reformApiService.getReformItems(status = apiStatusQuery)
                if (response.isSuccessful) {
                    response.body()?.let { mappedItemsFromApi.addAll(mapApiToUiModel(it)) }
                } else {
                    val errorBodyString = try { response.errorBody()?.string() } catch (e: Exception) { null }
                    Toast.makeText(requireContext(), "Server Error (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
            }

            val finalCombinedList = mutableListOf<ReformItem>()

            userItemFromArgs?.let {
                if (isForAllButton || it.status == localFilterStatus) {
                    finalCombinedList.add(it)
                }
            }

            val filteredHardcodedDummies = if (isForAllButton) {
                hardcodedDummyItems //
            } else {
                hardcodedDummyItems.filter { it.status == localFilterStatus }
            }
            finalCombinedList.addAll(filteredHardcodedDummies)


            finalCombinedList.addAll(mappedItemsFromApi)


            currentDisplayedItems = finalCombinedList.distinctBy { it.id }


            if (currentDisplayedItems.isEmpty()) {
                binding.reformSwapRecyclerView.visibility = View.GONE
                binding.reformedEmpty.visibility = View.VISIBLE
            } else {
                binding.reformSwapRecyclerView.visibility = View.VISIBLE
                binding.reformedEmpty.visibility = View.GONE
            }

            adapter.submitList(currentDisplayedItems)
        }
    }
    private fun mapApiToUiModel(apiItems: List<ApiReformItem>): List<ReformItem> {
        return apiItems.map { apiItem ->
            ReformItem(
                id = apiItem.id,
                title = apiItem.title,
                status = ReformStatus.fromApiString(apiItem.reformStatus),
                imageUrl = apiItem.imageUrls.firstOrNull(),
                corpName = apiItem.partnerName,
                originalImageResId = null
            )
        }
    }

    private fun setupFilterButtons() {
        updateFilterButtons(binding.btnAll)

        binding.btnAll.setOnClickListener {
            updateFilterButtons(binding.btnAll)
            fetchReformItems(apiStatusQuery = null, localFilterStatus = ReformStatus.RECEIVED, isForAllButton = true)
        }
        binding.btnInProgress.setOnClickListener {
            updateFilterButtons(binding.btnInProgress)
            fetchReformItems(apiStatusQuery = "IN_PROGRESS", localFilterStatus = ReformStatus.IN_PROGRESS)
        }
        binding.btnCompleted.setOnClickListener {
            updateFilterButtons(binding.btnCompleted)
            fetchReformItems(apiStatusQuery = "COMPLETED", localFilterStatus = ReformStatus.COMPLETED)
        }
    }

    private fun updateFilterButtons(selectedButton: Button) {
        listOf(binding.btnAll, binding.btnInProgress, binding.btnCompleted).forEach { button ->
            button.isSelected = (button == selectedButton)
            val fontId =
                if (button.isSelected) R.font.pretendard_semibold else R.font.pretendard_regular
            button.typeface = ResourcesCompat.getFont(requireContext(), fontId)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}