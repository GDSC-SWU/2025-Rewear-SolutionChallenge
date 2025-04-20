package com.example.rewear.ui.home

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.rewear.databinding.ItemRecentSearchesBinding

data class RecentSearchHistory(
    val recentSearch:String,
)

class RecentSearchAdapter(
    private val items: MutableList<RecentSearchHistory>,
    private val onItemChecked: (RecentSearchHistory) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecentSearchesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecentSearchHistory) {
            binding.recentSearchesLabel.text = item.recentSearch
            binding.clearIcon.setOnClickListener {
                removeItem(adapterPosition)
                onItemChecked(item)
            }
        }
    }
    private fun removeItem(position: Int){
        if(position!=RecyclerView.NO_POSITION){
            val removedItem=items[position]
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemRecentSearchesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recentSearch = items[position]
        holder.bind(recentSearch)

    }

    override fun getItemCount(): Int = items.size
}

