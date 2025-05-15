package com.example.rewear.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rewear.databinding.ItemClothesBinding

/**
 * Adapter for displaying a list of [Clothes] items in a RecyclerView.
 */
class ClothesAdapter(
    private var clothesList: MutableList<Clothes>,
    private val onItemClick:(Clothes)->Unit
): RecyclerView.Adapter<ClothesAdapter.ClothesViewHolder>() {

    /**
     * ViewHolder for a single [Clothes] item.
     * Handles binding data to the item's view.
     */
    inner class ClothesViewHolder(val binding: ItemClothesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Clothes) {
            binding.clothesName.text = item.name
            binding.clothesLabel.text = item.label
            binding.clothesLocation.text = item.location
            binding.clothesAgo.text = " . ${item.timeAgo}"
            binding.likeCount.text = item.likeCount.toString()

            if (item.imageList.isNotEmpty()) {
                Glide.with(binding.clothesImages.context)
                    .load(item.imageList[0]) // Loads the first image from the list
                    .into(binding.clothesImages)
            } else {
                binding.clothesImages.setImageDrawable(null)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesViewHolder {
        // Inflates the item layout and creates a ViewHolder.
        val binding = ItemClothesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClothesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothesViewHolder, position: Int) {
        // Binds the data at the given position to the ViewHolder.
        holder.bind(clothesList[position])
    }

    override fun getItemCount(): Int {
        // Returns the total number of items in the list.
        return clothesList.size
    }
}