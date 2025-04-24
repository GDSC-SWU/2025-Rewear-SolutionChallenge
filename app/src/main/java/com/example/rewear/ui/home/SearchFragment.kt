package com.example.rewear.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.databinding.FragmentSearchBinding
import com.example.rewear.ui.home.RecentSearchAdapter
import com.example.rewear.ui.home.RecentSearchHistory

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecentSearchAdapter
    private val recentSearchList = mutableListOf(
        RecentSearchHistory("Layered"),
        RecentSearchHistory("dress")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.BtnBack.setOnClickListener {

            findNavController().popBackStack()
        }
        adapter = RecentSearchAdapter(recentSearchList) { item ->
            recentSearchList.remove(item)
            adapter.notifyDataSetChanged()
            if (recentSearchList.isEmpty()) {
                binding.recentSearches.visibility = View.GONE
            }
        }
        binding.recentSearchRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recentSearchRecyclerView.adapter = adapter

        binding.recentSearches.visibility =
            if (recentSearchList.isEmpty()) View.GONE else View.VISIBLE


        binding.recentSearchesClear.setOnClickListener {
            recentSearchList.clear()
            adapter.notifyDataSetChanged()
            binding.recentSearches.visibility = View.GONE
        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
