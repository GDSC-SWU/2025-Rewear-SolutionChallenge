package com.example.rewear.ui.swap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rewear.databinding.ItemDetailImageBinding

class DetailImageAdapter(
    private val imageUrls: List<String>,
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<DetailImageAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemDetailImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            Glide.with(binding.root.context)
                .load(imageUrl)
                .into(binding.detailImage)
            binding.root.setOnClickListener {
                onImageClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDetailImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size
}