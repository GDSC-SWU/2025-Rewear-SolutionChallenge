package com.example.rewear.ui.swap

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.rewear.databinding.ItemGalleryBinding
import com.bumptech.glide.Glide

class GalleryAdapter(
    private val imageUris:MutableList<Uri>,
    private val onItemRemoved:()->Unit
):RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>(){

    inner class GalleryViewHolder(private val binding:ItemGalleryBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(uri:Uri){
            Glide.with(binding.root.context).load(uri).into(binding.galleryView)

            binding.btnDelete.setOnClickListener{
                val position=adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    imageUris.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position,imageUris.size)
                    onItemRemoved()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding=ItemGalleryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(imageUris[position])
    }

    override fun getItemCount(): Int=imageUris.size
}