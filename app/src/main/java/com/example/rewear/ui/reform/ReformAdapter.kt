package com.example.rewear.ui.reform

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rewear.R
import com.example.rewear.databinding.ItemReformListBinding
import com.example.rewear.ui.reform.ReformFragment.ReformStatus

data class ReformItem(
    val id: Int,
    val title: String,
    val status: ReformStatus,
    val imageResId:Int?=null,
    val corpName:String?=null
)

class ReformAdapter : RecyclerView.Adapter<ReformAdapter.ReformViewHolder>() {

    private var allItems: List<ReformItem> = listOf()
    private var filteredItems: List<ReformItem> = listOf()

    fun submitList(items: List<Any>) {
        allItems = items as List<ReformItem>
        filteredItems = items
        notifyDataSetChanged()
    }

    fun filterByStatus(status: ReformStatus?) {
        filteredItems = if (status == null) {
            allItems
        } else {
            allItems.filter { it.status == status }
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
        holder.bind(filteredItems[position])
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
            binding.clothing.setImageResource(item.imageResId?:R.drawable.cloth_example)
            binding.corpName.text=item.corpName?:"알 수 없음"
        }
    }

}