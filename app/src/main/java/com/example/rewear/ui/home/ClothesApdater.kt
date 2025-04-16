package com.example.rewear.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rewear.databinding.ItemClothesBinding

class ClothesApdater(private val clothesList: List<Clothes>):
        RecyclerView.Adapter<ClothesApdater.ClothesViewHolder>(){

    inner class ClothesViewHolder(val binding:ItemClothesBinding):
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesViewHolder {
        val binding=ItemClothesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ClothesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothesViewHolder, position: Int) {
        val clothes=clothesList[position]
        val b=holder.binding

        b.clothesImages.setImageResource(clothes.imageResId)
        b.clothesLabel.text=clothes.label
        b.clothesName.text=clothes.name
        b.clothesLocation.text=clothes.location
        b.clothesAgo.text=" . ${clothes.timeAgo}"
        b.likeCount.text="${clothes.likeCount}"
    }

    override fun getItemCount()=clothesList.size

    }