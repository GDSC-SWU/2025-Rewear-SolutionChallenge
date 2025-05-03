package com.example.rewear.ui.swap

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.rewear.R
import com.example.rewear.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoryBottomSheetFragment(
    private val onCategorySelected: (String, Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var selectedCheckImage: ImageView? = null
    private val sharedPreferences by lazy{
        requireContext().getSharedPreferences("CategoryPrefs",Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        val categories = listOf(
            binding.knit to binding.knitCheck,
            binding.sweatshirt to binding.sweatshirtCheck,
            binding.shirts to binding.shirtsCheck,
            binding.zipUpHoodie to binding.zipUpHoodieCheck,
            binding.pufferJacket to binding.pufferJacketCheck,
            binding.jacket to binding.jacketCheck,
            binding.shorts to binding.shortsCheck,
            binding.jean to binding.jeanCheck,
            binding.sweatpants to binding.sweatpantsCheck,
            binding.dress to binding.dressCheck,
            binding.skirt to binding.skirtCheck
        )

        val selectedCategory=sharedPreferences.getString("selectedCategory",null)
        categories.forEach { (textView,checkImage)->
            if(textView.text.toString()==selectedCategory){
                checkImage.visibility=View.VISIBLE
                selectedCheckImage=checkImage
            }else{
                checkImage.visibility=View.GONE
            }
        }


        categories.forEach { (textView,checkImage) ->
            textView.setOnClickListener {
                selectedCheckImage?.visibility=View.GONE

                checkImage.visibility=View.VISIBLE
                selectedCheckImage=checkImage

                with(sharedPreferences.edit()){
                    putString("selectedCategory",textView.text.toString())
                    apply()
                }

                val selectedName = textView.text.toString()
                val selectedId = textView.id

                onCategorySelected(selectedName, selectedId)
                dismiss()
            }
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

