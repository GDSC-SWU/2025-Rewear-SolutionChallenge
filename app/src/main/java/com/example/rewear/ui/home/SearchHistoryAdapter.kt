package com.example.rewear.ui.home

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.rewear.databinding.ItemSearchHistoryBinding

class SearchHistoryAdapter(
    private val items: MutableList<SearchHistory>,
    private val onItemChecked: (SearchHistory) -> Unit
) : RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class ViewHolder(val binding: ItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemSearchHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchhistories = items[position]
        val s = holder.binding

        s.locationText.text = searchhistories.title
        s.locationDetail.text = searchhistories.detail

        s.icCheck.isSelected = position == selectedPosition

        s.icCheck.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            val previousPosition = selectedPosition
            selectedPosition = adapterPosition

            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(selectedPosition)

            onItemChecked(searchhistories)
        }
    }

    override fun getItemCount() = items.size

    fun clearItems() {
        items.clear()
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    fun addItem(item: SearchHistory) {
        items.add(0, item)
        notifyItemInserted(0)
    }

}
