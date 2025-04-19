package com.example.rewear.ui.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.databinding.FragmentLocationBinding
import com.example.rewear.ui.home.SearchHistory
import com.example.rewear.ui.home.SearchHistoryAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.rewear.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior

class LocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        bottomSheetBehavior = BottomSheetBehavior.from(binding.locationBottomSheet)
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        binding.searchIcon.setOnClickListener {
            val locationName = binding.searchEditText.text.toString()
            if (locationName.isNotEmpty()) {
                searchLocationOnMap(locationName)
            } else {
                Toast.makeText(requireContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
                false
            } else {
                false
            }
        }
        binding.BtnBack.setOnClickListener {
            if (::mMap.isInitialized) {
                val currentLatLng = mMap.cameraPosition.target
                val currentAddress = try {
                    val address =
                        geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1)
                    address?.getOrNull(0)?.getAddressLine(0) ?: "Unknown location"
                } catch (e: Exception) {
                    "Unknown location"
                }

                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    "selectedLatLng",
                    currentLatLng
                )
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    "selectedAddress",
                    currentAddress
                )
            }
            findNavController().popBackStack()
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }


        val mapFragment = childFragmentManager
            .findFragmentById(com.example.rewear.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geocoder = Geocoder(requireContext(), Locale.getDefault())

        searchHistoryAdapter = SearchHistoryAdapter(mutableListOf()) { checkedItem ->
            val latLng = LatLng(checkedItem.latitude, checkedItem.longitude)
            val customIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)

            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(checkedItem.title)
                    .icon(customIcon)
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }

        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchHistoryAdapter

            val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            ContextCompat.getDrawable(context, R.drawable.recycler_divider)?.let {
                divider.setDrawable(it)
            }
            addItemDecoration(divider)
        }

        binding.btnUseCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }

        binding.btnClear.setOnClickListener {
            searchHistoryAdapter.clearItems()
        }


    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location: Location? = task.result
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    val customIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)
                    mMap.isMyLocationEnabled = false
                    mMap.clear()
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    mMap.addMarker(
                        MarkerOptions()
                            .position(currentLatLng)
                            .icon(customIcon)
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Can't load the current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun searchLocationOnMap(locationName: String) {
        try {
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)
                val customIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)

                mMap.clear()
                mMap.addMarker(
                    MarkerOptions().position(latLng).title(locationName).icon(customIcon)
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                val fullAddress = address.getAddressLine(0) ?: locationName
                val searchHistory = SearchHistory(
                    title = locationName,
                    detail = fullAddress,
                    latitude = address.latitude,
                    longitude = address.longitude
                )
                searchHistoryAdapter.addItem(searchHistory)
            } else {
                Toast.makeText(requireContext(), "No result", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        }

        val defaultLocation = LatLng(37.537130, 126.943842)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 18f))


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
