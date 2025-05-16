package com.example.rewear.ui.reform

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rewear.R
import com.example.rewear.databinding.ItemReformListBinding
import com.example.rewear.ui.reform.ReformFragment.ReformStatus

data class ReformItem(
    val id: Int,
    val title: String,
    val status: ReformFragment.ReformStatus,
    val imageUrl: String?,
    val corpName: String,
    val originalImageResId: Int? = null
)

class ReformAdapter : RecyclerView.Adapter<ReformAdapter.ReformViewHolder>() {

    private var allItems: List<ReformItem> = listOf()
    private var filteredItems: List<ReformItem> = listOf()

    fun submitList(items: List<ReformItem>) {
        allItems = items
        filteredItems = items
        notifyDataSetChanged()
    }

    fun filterByStatus(statusToFilter: ReformFragment.ReformStatus?) {
        filteredItems = if (statusToFilter == null) {
            allItems
        } else {
            allItems.filter { it.status == statusToFilter }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReformAdapter.ReformViewHolder {
        val binding =
            ItemReformListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReformViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReformAdapter.ReformViewHolder, position: Int) {
        if (position < filteredItems.size) {
            holder.bind(filteredItems[position])
        }
    }

    override fun getItemCount(): Int = filteredItems.size

    inner class ReformViewHolder(private val binding: ItemReformListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReformItem) {
            binding.clothingName.text = item.title

            val statusImageRes = when (item.status) {
                ReformStatus.RECEIVED -> R.drawable.reform_received
                ReformStatus.IN_PROGRESS -> R.drawable.reform_inprogress
                ReformStatus.COMPLETED -> R.drawable.reform_completed
            }
            binding.reformProgress.setImageResource(statusImageRes)

            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.clothing.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.cloth_example)
                    .into(binding.clothing)
            } else if (item.originalImageResId != null && item.originalImageResId != 0) {

                binding.clothing.setImageResource(item.originalImageResId)
            } else {

                binding.clothing.setImageResource(R.drawable.cloth_example)
            }

            binding.corpName.text = item.corpName

        }
    }
}