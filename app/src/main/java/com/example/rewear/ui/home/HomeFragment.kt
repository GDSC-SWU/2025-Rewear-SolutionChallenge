package com.example.rewear.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rewear.R
import com.example.rewear.databinding.FragmentHomeBinding
import com.example.rewear.network.HomeApiResponseItem
import com.example.rewear.network.RetrofitClientApp
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.IOException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var clothesAdapter: ClothesAdapter

    private val originalClothesDataSource = mutableListOf(
        Clothes(
            "1",
            imageList = listOf(
                "https://picsum.photos/id/101/159/186",
                "https://picsum.photos/id/102/159/186",
                "https://picsum.photos/id/103/159/186"
            ),
            "Tops",
            "Letter Open Hoodie",
            "Mapo-dong",
            "1m ago",
            5,
            "description",
            "In-person Trade"
        ),
        Clothes(
            "2",
            imageList = listOf(
                "https://picsum.photos/id/201/159/186",
                "https://picsum.photos/id/202/159/186"
            ),
            "Tops",
            "Layering T-shirt",
            "Mapo-dong",
            "1m ago",
            5,
            "description",
            "In-person Trade"
        ),
        Clothes(
            "3",
            imageList = listOf(
                "https://picsum.photos/seed/hoodie_pics/159/186",
                "https://picsum.photos/seed/hoodie_pics_alt/159/186"
            ),
            "Tops",
            "Letter Open Hoodie",
            "Mapo-dong",
            "1m ago",
            5,
            "description",
            "In-person Trade"
        ),
        Clothes(
            "4",
            imageList = listOf(
                "https://picsum.photos/id/305/159/186",
                "https://picsum.photos/id/306/159/186"
            ),
            "Tops",
            "Layering T-shirt",
            "Seokgyo-dong",
            "1m ago",
            5,
            "description",
            "In-person Trade"
        ),
        Clothes(
            "5",
            imageList = listOf(
                "https://picsum.photos/seed/another_hoodie/159/186",
                "https://picsum.photos/seed/detail_shot/158/186"
            ),
            "Tops",
            "Letter Open Hoodie",
            "Seokgyo-dong",
            "1m ago",
            5,
            "description",
            "In-person Trade"
        ),
        Clothes(
            "6",
            imageList = listOf(
                "https://picsum.photos/seed/another_hoodie/159/186",
                "https://picsum.photos/seed/detail_shot/158/186"
            ),
            "Tops",
            "Layering T-shirt",
            "Seokgyo-dong",
            "1m ago",
            5,
            "description",
            "In-person Trade"
        )
    )

    private val displayedClothesList = mutableListOf<Clothes>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup for swap/donation count display - specific UI logic.
        val swapCount = 1;
        val donationCount = 5
        val messageView: TextView = binding.userMessage
        val digitLayout: LinearLayout = binding.digitLayout
        if (swapCount == 0 && donationCount == 0) {
            messageView.visibility = View.VISIBLE
            digitLayout.visibility = View.GONE
        } else {
            messageView.visibility = View.GONE
            digitLayout.visibility = View.VISIBLE
            val swapDigits = String.format("%02d", swapCount)
            val donationDigits = String.format("%02d", donationCount)
            binding.swapDigit1.text = swapDigits[0].toString();
            binding.swapDigit2.text = swapDigits[1].toString()
            binding.donationDigit1.text = donationDigits[0].toString();
            binding.donationDigit2.text = donationDigits[1].toString()
        }

        // Setup for FloatingActionButton to navigate to registration.
        val fab: FloatingActionButton = binding.fabAdd
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_registrationFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        // Initialize and set up the RecyclerView adapter.
        clothesAdapter = ClothesAdapter(displayedClothesList) { clickedItem ->
            val bundle = Bundle().apply {
                putParcelable("selectedClothes", clickedItem)
            }
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }

        binding.clothesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.clothesRecyclerView.adapter = clothesAdapter

        // Observe address changes from other fragments (e.g., LocationFragment).
        navController.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("selectedAddress")
            ?.observe(viewLifecycleOwner) { address ->
                if (!address.isNullOrBlank()) {
                    binding.userLocation.text = address
                    applyFilterAndDisplay(address) // Apply filter with the new address
                }
            }

        // Observe new clothes added from RegistrationFragment.
        navController.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Clothes>("newClothes")
            ?.observe(viewLifecycleOwner) { newClothes ->
                if (newClothes != null) {
                    originalClothesDataSource.add(0, newClothes)
                    val newLocationToShow = if (newClothes.location.isNotBlank()) {
                        binding.userLocation.text = newClothes.location // Update displayed location
                        newClothes.location
                    } else {
                        binding.userLocation.text.toString() // Keep current location if new item has no location
                    }
                    applyFilterAndDisplay(newLocationToShow) // Refresh list based on the new item's location or current

                    // Remove the "newClothes" LiveData to prevent re-processing on configuration change.
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Clothes>("newClothes")
                }
            }

        // Click listeners for navigation icons and location text.
        val clickListener =
            View.OnClickListener { findNavController().navigate(R.id.action_homeFragment_to_locationFragment) }
        val searchClickListener =
            View.OnClickListener { findNavController().navigate(R.id.action_homeFragment_to_searchFragment) }
        val notificationClickListener =
            View.OnClickListener { findNavController().navigate(R.id.action_homeFragment_to_notificationFragment) }

        binding.icSearch.setOnClickListener(searchClickListener)
        binding.icNotification.setOnClickListener(notificationClickListener)
        binding.userLocation.setOnClickListener(clickListener)
        binding.userLocationIc.setOnClickListener(clickListener)

        // Initial data load if no new clothes are being passed back.
        if (navController.currentBackStackEntry?.savedStateHandle?.get<Clothes>("newClothes") == null) {
            val initialAddressToShow = "Mapo-dong" // Default initial address
            binding.userLocation.text = initialAddressToShow
            applyFilterAndDisplay(initialAddressToShow)
        }
    }

    /**
     * Filters the [originalClothesDataSource] based on the [addressQuery] (typically a 'dong')
     * and updates the [displayedClothesList] which is bound to the adapter.
     */
    private fun applyFilterAndDisplay(addressQuery: String) {
        displayedClothesList.clear()

        if (addressQuery.equals("All Areas", ignoreCase = true) || addressQuery.isBlank()) {
            displayedClothesList.addAll(originalClothesDataSource)
        } else {
            val targetDong = extractDong(addressQuery) ?: addressQuery

            val filteredList = originalClothesDataSource.filter { clothes ->
                val itemDong = extractDong(clothes.location) ?: clothes.location
                // Compares extracted 'dong' or full location if 'dong' extraction fails.
                itemDong.equals(
                    targetDong,
                    ignoreCase = true
                ) || clothes.location.equals(targetDong, ignoreCase = true)
            }
            displayedClothesList.addAll(filteredList)
        }

        clothesAdapter.notifyDataSetChanged() // Consider using DiffUtil for better performance.
        updateEmptyViewVisibility(
            displayedClothesList.isEmpty(),
            if (addressQuery.equals(
                    "All Areas",
                    ignoreCase = true
                ) && displayedClothesList.isNotEmpty()
            ) "Showing all clothes."
            else if (displayedClothesList.isEmpty() && !addressQuery.equals(
                    "All Areas",
                    ignoreCase = true
                )
            ) "No clothes found in this area."
            else "No clothes to display."
        )
    }

    private fun updateEmptyViewVisibility(showEmptyView: Boolean, message: String) {
        if (isAdded && _binding != null) {
            binding.root.post {
                if (_binding == null) return@post

                if (showEmptyView) {
                    binding.clothesRecyclerView.visibility = View.GONE
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                } else {
                    binding.clothesRecyclerView.visibility = View.VISIBLE
                }
            }
        }
    }
    /*
        private fun fetchHomeItems(addressQuery: String) {
            // binding.progressBar.visibility = View.VISIBLE

            viewLifecycleOwner.lifecycleScope.launch {
                val newUiList = mutableListOf<Clothes>()
                var success = false

                try {
                    val response = RetrofitClientApp.apiService.getHomeItems(address = addressQuery)

                    if (response.isSuccessful) {
                        val apiItems = response.body()
                        if (apiItems != null) {
                            newUiList.addAll(apiItems.map { apiItem -> apiItem.toClothesUiModel() })
                            success = true
                        } else {
                            if (isAdded) Toast.makeText(context, "아이템 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (isAdded) Toast.makeText(context, "API 요청 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    if (isAdded) Toast.makeText(context, "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    if (isAdded) Toast.makeText(context, "데이터 로딩 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                } finally {

                    originalClothesDataSource.clear()
                    if (success) {
                        originalClothesDataSource.addAll(newUiList)
                    }

                    clothesAdapter.notifyDataSetChanged()
                    updateEmptyViewVisibility(originalClothesDataSource.isEmpty(),
                        if (success && originalClothesDataSource.isEmpty()) "이 지역에는 등록된 옷이 없어요." else "데이터를 불러올 수 없습니다.")
                }
            }
        }


        private fun HomeApiResponseItem.toClothesUiModel(): Clothes {
            return Clothes(
                id = this.id.toString(),
                name = this.title,
                label = this.category,
                location = extractDong(this.address) ?: this.address,
                imageList = this.imageUrls,
                likeCount = this.likeCount,
                timeAgo = "정보 없음",
                description = "",
                swapMethod = ""
            )
        }

    */
    /**
     * Extracts the 'dong' (neighborhood) part from a full address string.
     * Example: "Mapo-gu, Seokgyo-dong" -> "Seokgyo-dong".
     */
    private fun extractDong(fullAddress: String): String? {
        val dongRex = Regex("""\b([A-Za-z]+-dong)\b""", RegexOption.IGNORE_CASE)
        val match = dongRex.find(fullAddress)
        return match
            ?.groupValues?.get(1) //Returns the matched 'dong' part
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}