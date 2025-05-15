package com.example.rewear.ui.swap

import android.app.Dialog
import android.location.Geocoder
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


class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private var currentDisplayItem: Clothes? = null
    private var clothesList: MutableList<Clothes> = mutableListOf()


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
        binding.relatedRecyclerView.adapter = ClothesAdapter(clothesList) { selectedClothes ->

        }

        binding.icZoom.setOnClickListener {
            val currentPosition = binding.detailImg.currentItem
            if (clothes.imageList.isNotEmpty() && currentPosition < clothes.imageList.size) {
                showEnlargedImage(clothes.imageList[currentPosition])
            }
        }

        if (!clothes.location.isNullOrBlank()) {
            updateMapLocation(clothes.location, clothes.name)
        }
    }

    private fun fetchItemDetailsAndUpdate(itemId: String) {
        if (_binding == null || context == null) return

        RetrofitClientApp.apiService.getItemDetail(itemId)
            .enqueue(object : Callback<ItemDetailResponse> {
                override fun onResponse(call: Call<ItemDetailResponse>, response: Response<ItemDetailResponse>) {
                    if (_binding == null) return

                    if (response.isSuccessful) {
                        val detailsFromServer = response.body()
                        if (detailsFromServer != null) {
                            currentDisplayItem = currentDisplayItem?.copy(
                                name = detailsFromServer.title,
                                label = detailsFromServer.category,
                                imageList = detailsFromServer.imageUrls,
                                description = detailsFromServer.description ?: currentDisplayItem?.description ?: "",
                                swapMethod = detailsFromServer.swapMethod ?: currentDisplayItem?.swapMethod ?: ""
                            )
                            currentDisplayItem?.let { populateUiWithData(it) }
                        } else {
                            Toast.makeText(requireContext(), "아이템 상세 정보를 가져오지 못했습니다 (응답 데이터 없음).", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (response.code() == 404) {
                            Toast.makeText(requireContext(), "아이템을 찾을 수 없습니다 (삭제되었을 수 있습니다).", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "상세 정보 로드 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ItemDetailResponse>, t: Throwable) {
                    if (_binding == null) return
                    Toast.makeText(requireContext(), "상세 정보 로드 중 네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun initiateSwap(itemId: String) {
        if (_binding == null || context == null) return

        binding.BtnSwapNow.isEnabled = false

        val swapRequest = SwapRequest(swapStatus = "PENDING")

        RetrofitClientApp.apiService.requestSwap(itemId, swapRequest)
            .enqueue(object : Callback<SwapResponse> {
                override fun onResponse(call: Call<SwapResponse>, response: Response<SwapResponse>) {
                    if (_binding == null) return

                    if (response.isSuccessful && response.code() == 201) {
                        val swapResponseData = response.body()
                        Log.d("DetailFragment", "스왑 요청 성공: $swapResponseData")
                        Toast.makeText(requireContext(), "교환 요청이 전송되었습니다!", Toast.LENGTH_SHORT).show()
                        binding.BtnSwapNow.text = "교환 요청됨"
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "알 수 없는 에러"
                        Log.e("DetailFragment", "스왑 요청 실패: ${response.code()} - $errorBody")
                        Toast.makeText(requireContext(), "교환 요청에 실패했습니다: ${response.code()}", Toast.LENGTH_SHORT).show()
                        binding.BtnSwapNow.isEnabled = true
                    }
                }

                override fun onFailure(call: Call<SwapResponse>, t: Throwable) {
                    if (_binding == null) return
                    Log.e("DetailFragment", "스왑 요청 네트워크 오류: ${t.message}", t)
                    Toast.makeText(requireContext(), "네트워크 오류로 교환 요청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    binding.BtnSwapNow.isEnabled = true
                }
            })
    }

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
                    Log.w("DetailFragment", "주소 변환 실패 (updateMapLocation): $locationName")
                }
            } catch (e: IOException) {
                Log.e("DetailFragment", "Geocoding IOException for $locationName", e)
                Toast.makeText(context, "위치 정보를 변환하는 데 문제가 발생했습니다.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("DetailFragment", "Geocoding 기타 에러 for $locationName", e)
            }
        }
    }

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