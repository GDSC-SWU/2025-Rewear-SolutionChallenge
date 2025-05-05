package com.example.rewear.ui.swap

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rewear.databinding.DialogEnlargedImageBinding
import com.example.rewear.databinding.FragmentDetailBinding
import com.example.rewear.ui.home.Clothes


class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.BtnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val clothes = arguments?.getParcelable<Clothes>("selectedClothes")
        clothes?.let {selectedClothes->
            binding.titleText.text = selectedClothes.name
            binding.categoryText.text = selectedClothes.label
            binding.locationText.text=selectedClothes.location

            val imageAdapter = DetailImageAdapter(selectedClothes.imageList) {imageUrl ->
                showEnlargedImage(imageUrl)
            }
            binding.detailImg.adapter = imageAdapter
            binding.dotsIndicator.setViewPager2(binding.detailImg)

            binding.icZoom.setOnClickListener{
                val currentPosition=binding.detailImg.currentItem
                if(selectedClothes.imageList.isNotEmpty()){
                    val currentImageUrl=selectedClothes.imageList[currentPosition]
                    showEnlargedImage(currentImageUrl)
                }
            }
        }
    }

    private fun showEnlargedImage(imageUrl:Int){
        val dialog= Dialog(requireContext())
        val binding=DialogEnlargedImageBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.enlargedImageView.setImageResource(imageUrl)

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}