package com.example.rewear.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rewear.databinding.ItemClothesBinding

class ClothesAdapter(
    private var clothesList: MutableList<Clothes>,
    private val onItemClick:(Clothes)->Unit
): RecyclerView.Adapter<ClothesAdapter.ClothesViewHolder>(){


    fun addItem(newClothes: Clothes){
        clothesList.add(0,newClothes)
        notifyItemInserted(0)
    }

    inner class ClothesViewHolder(val binding:ItemClothesBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(item:Clothes){
            binding.clothesName.text=item.name

            binding.root.setOnClickListener{
                onItemClick(item)
            }
        }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesViewHolder {
        val binding=ItemClothesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ClothesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothesViewHolder, position: Int) {
        val clothes=clothesList[position]
        val b=holder.binding
        holder.bind(clothes)

        b.clothesImages.setImageResource(clothes.imageList[0])
        b.clothesLabel.text=clothes.label
        b.clothesName.text=clothes.name
        b.clothesLocation.text=clothes.location
        b.clothesAgo.text=" . ${clothes.timeAgo}"
        b.likeCount.text="${clothes.likeCount}"
    }

    override fun getItemCount()=clothesList.size

    }