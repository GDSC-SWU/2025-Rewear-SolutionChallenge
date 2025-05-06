package com.example.rewear.ui.swap

import android.app.Dialog
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.R
import com.example.rewear.databinding.DialogEnlargedImageBinding
import com.example.rewear.databinding.FragmentDetailBinding
import com.example.rewear.ui.home.Clothes
import com.example.rewear.ui.home.ClothesAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var clothesList: MutableList<Clothes>


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

        clothesList = mutableListOf(
            Clothes(
                imageList = listOf(
                    R.drawable.example3,
                    R.drawable.cloth_example,
                    R.drawable.cloth_example2
                ),
                "Tops",
                "Letter Open Hoodie",
                "Mapo-dong",
                "1m ago",
                5,
                description = "description",
                "In-person Trade",
            ),
            Clothes(
                imageList = listOf(
                    R.drawable.cloth_example,
                    R.drawable.cloth_example2
                ),
                "Tops",
                "Layering T-shirt",
                "Mapo-dong",
                "1m ago",
                5,
                description = "description",
                "In-person Trade",
            ),
            Clothes(
                imageList = listOf(
                    R.drawable.cloth_example,
                    R.drawable.cloth_example2
                ),
                "Tops",
                "Letter Open Hoodie",
                "Mapo-dong",
                "1m ago",
                5, description = "description",
                "In-person Trade",
            ),
            Clothes(
                imageList = listOf(
                    R.drawable.cloth_example,
                    R.drawable.cloth_example2
                ),
                "Tops",
                "Layering T-shirt",
                "Mapo-dong",
                "1m ago",
                5,
                description = "description",
                "In-person Trade",
            ),
            Clothes(
                imageList = listOf(
                    R.drawable.cloth_example,
                    R.drawable.cloth_example2
                ),
                "Tops",
                "Letter Open Hoodie",
                "Mapo-dong",
                "1m ago",
                5, description = "description",
                "In-person Trade",
            ),
            Clothes(
                imageList = listOf(
                    R.drawable.cloth_example,
                    R.drawable.cloth_example2
                ),
                "Tops",
                "Layering T-shirt",
                "Mapo-dong",
                "1m ago",
                5,
                description = "description",
                "In-person Trade",
            ),
        )
        binding.BtnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val clothes = arguments?.getParcelable<Clothes>("selectedClothes")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            googleMap.clear()

            clothes?.let { selectedClothes ->
                val location = selectedClothes.location
                val geocoder = Geocoder(requireContext())
                val addressList = geocoder.getFromLocationName(location, 1)

                if (addressList != null && addressList.isNotEmpty()) {
                    val latLng = LatLng(addressList[0].latitude, addressList[0].longitude)

                    val customMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(selectedClothes.name)
                            .icon(customMarker)
                    )
                }
            }
        }
        clothes?.let { selectedClothes ->
            binding.titleText.text = selectedClothes.name
            binding.locationText.text = selectedClothes.location
            binding.clothesLabel.text = selectedClothes.label
            binding.descriptionText.text = selectedClothes.description
            binding.swapMethod.text = selectedClothes.swapMethod
            binding.location.text = selectedClothes.location
            binding.likeCount.text = selectedClothes.likeCount.toString()

            val imageAdapter = DetailImageAdapter(selectedClothes.imageList) { imageUrl ->
                showEnlargedImage(imageUrl)
            }
            binding.detailImg.adapter = imageAdapter
            binding.dotsIndicator.setViewPager2(binding.detailImg)
            binding.relatedRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.relatedRecyclerView.adapter = ClothesAdapter(clothesList) { selectedClothes ->

            }

            binding.icZoom.setOnClickListener {
                val currentPosition = binding.detailImg.currentItem
                if (selectedClothes.imageList.isNotEmpty()) {
                    val currentImageUrl = selectedClothes.imageList[currentPosition]
                    showEnlargedImage(currentImageUrl)
                }
            }
        }
    }

    private fun showEnlargedImage(imageUrl: Int) {
        val dialog = Dialog(requireContext())
        val binding = DialogEnlargedImageBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.enlargedImageView.setImageResource(imageUrl)

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}