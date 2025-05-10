package com.example.rewear.ui.reform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rewear.R
import com.example.rewear.databinding.ItemPartnerBinding

class PartnerAdapter(
    private val partners: List<Partner>
) : RecyclerView.Adapter<PartnerAdapter.PartnerViewHolder>() {

    private var selectedPosion = RecyclerView.NO_POSITION

    inner class PartnerViewHolder(val binding: ItemPartnerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val previousPosition = selectedPosion
                selectedPosion = adapterPosition

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerViewHolder {
        val binding = ItemPartnerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PartnerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PartnerViewHolder, position: Int) {
        val partner = partners[position]
        with(holder.binding) {
            reformImage.setImageResource(partner.imageResId)
            womanName.text = partner.name

            val isSelected = position == selectedPosion
            val tintColor = if (isSelected) R.color.blue else R.color.uncheck
            icCheck.setColorFilter(ContextCompat.getColor(root.context, tintColor))
        }
    }

    override fun getItemCount() = partners.size

    fun getSelectedPartner():Partner?{
        return if(selectedPosion!=RecyclerView.NO_POSITION)partners[selectedPosion] else null
    }
}