package com.example.rewear.ui.swap

import android.app.Dialog
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rewear.network.ItemDetailResponse
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.rewear.R
import com.example.rewear.databinding.DialogEnlargedImageBinding
import com.example.rewear.databinding.FragmentDetailBinding
import com.example.rewear.ui.home.Clothes
import com.example.rewear.ui.home.ClothesAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import com.example.rewear.network.RetrofitClientApp
import com.example.rewear.network.SwapRequest
import com.example.rewear.network.SwapResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Fragment to display the details of a selected [Clothes] item.
 */
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private var currentDisplayItem: Clothes? = null

    private var relatedItemsList: MutableList<Clothes> = mutableListOf()

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

        val initialClothesData = arguments?.getParcelable<Clothes>("selectedClothes")

        if (initialClothesData == null) {
            Toast.makeText(requireContext(), "아이템 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        currentDisplayItem = initialClothesData
        setupTemporaryRelatedItems()
        populateUiWithData(currentDisplayItem!!)

        fetchItemDetailsAndUpdate(initialClothesData.id)

        binding.BtnSwapNow.isEnabled = currentDisplayItem != null
        binding.BtnSwapNow.setOnClickListener {
            currentDisplayItem?.let { item ->
                initiateSwap(item.id)
            }
        }
    }

    private fun populateUiWithData(clothes: Clothes) {
        if (_binding == null) return

        binding.titleText.text = clothes.name
        binding.locationText.text = clothes.location
        binding.clothesLabel.text = clothes.label
        binding.descriptionText.text = clothes.description
        binding.swapMethod.text = clothes.swapMethod
        binding.location.text = clothes.location
        binding.likeCount.text = clothes.likeCount.toString()


        val imageAdapter = DetailImageAdapter(clothes.imageList) { imageUrl ->
            showEnlargedImage(imageUrl.toString())
        }
        binding.detailImg.adapter = imageAdapter
        binding.relatedRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.relatedRecyclerView.adapter = ClothesAdapter(relatedItemsList) { selectedClothes ->

        }

        binding.icZoom.setOnClickListener {
            val currentPosition = binding.detailImg.currentItem
            if (clothes.imageList.isNotEmpty() && currentPosition < clothes.imageList.size) {
                showEnlargedImage(clothes.imageList[currentPosition])
            }
        }

        if (clothes.location.isNotBlank()) {
            updateMapLocation(clothes.location, clothes.name)
        }
    }

    private fun fetchItemDetailsAndUpdate(itemId: String) {
        if (_binding == null || context == null) return

        RetrofitClientApp.apiService.getItemDetail(itemId)
            .enqueue(object : Callback<ItemDetailResponse> {
                override fun onResponse(
                    call: Call<ItemDetailResponse>,
                    response: Response<ItemDetailResponse>
                ) {
                    if (_binding == null) return

                    if (response.isSuccessful) {
                        val detailsFromServer = response.body()
                        if (detailsFromServer != null) {
                            val imageListToUse =
                                if (currentDisplayItem?.imageList?.isNotEmpty() == true) {
                                    currentDisplayItem!!.imageList
                                } else {
                                    detailsFromServer.imageUrls
                                }
                            currentDisplayItem = currentDisplayItem?.copy(
                                name = detailsFromServer.title,
                                label = detailsFromServer.category,
                                imageList = imageListToUse,
                                description = detailsFromServer.description
                                    ?: currentDisplayItem?.description ?: "",
                                swapMethod = detailsFromServer.swapMethod
                                    ?: currentDisplayItem?.swapMethod ?: ""
                            )
                            currentDisplayItem?.let { populateUiWithData(it) }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to get item details (no response data).",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        if (response.code() == 404) {
                            Toast.makeText(
                                requireContext(),
                                "Item not found (it may have been deleted).",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to load details. Error: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ItemDetailResponse>, t: Throwable) {
                    if (_binding == null) return
                }
            })
    }

    private fun initiateSwap(itemId: String) {
        if (_binding == null || context == null) return

        binding.BtnSwapNow.isEnabled = false
        val swapRequest = SwapRequest(swapStatus = "PENDING")

        RetrofitClientApp.apiService.requestSwap(itemId, swapRequest)
            .enqueue(object : Callback<SwapResponse> {
                override fun onResponse(
                    call: Call<SwapResponse>,
                    response: Response<SwapResponse>
                ) {
                    if (_binding == null) return

                    if (response.isSuccessful && response.code() == 201) {
                        val swapResponseData = response.body()

                        binding.BtnSwapNow.text = "Swap Requested"
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to send swap request. Error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.BtnSwapNow.isEnabled = true
                    }
                }

                override fun onFailure(call: Call<SwapResponse>, t: Throwable) {
                    if (_binding == null) return
                    Toast.makeText(
                        requireContext(),
                        "Swap request failed due to a network error.",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.BtnSwapNow.isEnabled = true
                }
            })
    }

    /**
     * Updates the Google Map to display a marker at the given [locationName].
     */
    private fun updateMapLocation(locationName: String?, markerTitle: String) {
        if (locationName.isNullOrBlank() || _binding == null || context == null) return

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            googleMap.clear()
            try {
                val geocoder = Geocoder(requireContext())
                val addressList = geocoder.getFromLocationName(locationName, 1)
                if (addressList != null && addressList.isNotEmpty()) {
                    val latLng = LatLng(addressList[0].latitude, addressList[0].longitude)
                    val customMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(markerTitle)
                            .icon(customMarker)
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Could not find '$locationName' on the map.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    context,
                    "Could not display location: Network or I/O error.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error displaying location on map.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun setupTemporaryRelatedItems() {
        relatedItemsList.clear() // Clear previous items if any
        val packageName = requireContext().packageName
        val localImageUriForRelated1 = Uri.parse("android.resource://$packageName/${R.drawable.cloth_example3}").toString()
        val localImageUriForRelated2 = Uri.parse("android.resource://$packageName/${R.drawable.cloth_example}").toString()
        relatedItemsList.add(
            Clothes(
                id = "r1",
                imageList = listOf(localImageUriForRelated2),
                label = "Zip-up Hoodie",
                name = "Letter Open Hoodie",
                location = "Mapo-dong",
                timeAgo = "1m ago",
                likeCount = 5,
                description = "A cool jacket similar to what you are viewing.",
                swapMethod = "Shipping"
            )
        )
        relatedItemsList.add(
            Clothes(
                id = "r2",
                imageList = listOf(localImageUriForRelated1),
                label = "Dress",
                name = "Pink dress",
                location = "Seokgyo-dong",
                timeAgo = "1m ago",
                likeCount = 5,
                description = "A nice scarf that might go well.",
                swapMethod = "In-person"
            )
        )
        // Add more dummy items as needed
    }
    /**
     * Displays an enlarged view of the selected image in a [Dialog].
     */
    private fun showEnlargedImage(imageUrl: String) {
        if (context == null || _binding == null) return
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogEnlargedImageBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        Glide.with(requireContext())
            .load(imageUrl)
            .into(dialogBinding.enlargedImageView)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}