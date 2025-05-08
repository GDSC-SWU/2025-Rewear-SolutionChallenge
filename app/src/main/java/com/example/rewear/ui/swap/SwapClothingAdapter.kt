package com.example.rewear.ui.swap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rewear.databinding.ItemSwapClothingBinding

class SwapClothingAdapter(
    private val imageList: List<Int>
) : RecyclerView.Adapter<SwapClothingAdapter.ClothingViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class ClothingViewHolder(private val binding: ItemSwapClothingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageResId: Int, isSelected: Boolean) {
            binding.clothesImages.setImageResource(imageResId)
            binding.swapCheck.visibility = if (isSelected) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothingViewHolder {
        val binding = ItemSwapClothingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ClothingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothingViewHolder, position: Int) {
        val isSelected = position == selectedPosition
        holder.bind(imageList[position], isSelected)
    }

    override fun getItemCount(): Int = imageList.size
}