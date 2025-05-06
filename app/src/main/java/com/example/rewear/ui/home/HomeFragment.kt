package com.example.rewear.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rewear.R
import com.example.rewear.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var clothesAdapter: ClothesAdapter
    private lateinit var clothesList: MutableList<Clothes>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val swapCount = 1
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

            binding.swapDigit1.text = swapDigits[0].toString()
            binding.swapDigit2.text = swapDigits[1].toString()
            binding.donationDigit1.text = donationDigits[0].toString()
            binding.donationDigit2.text = donationDigits[1].toString()

        }

        val fab: FloatingActionButton = binding.fabAdd
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_registrationFragment)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()


        navController.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("selectedAddress")
            ?.observe(viewLifecycleOwner) { address ->
                if (!address.isNullOrBlank()) {
                    binding.userLocation.text = address
                }
            }
        navController.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Clothes>("newClothes")
            ?.observe(viewLifecycleOwner) { newClothes ->
                clothesAdapter.addItem(newClothes)
            }

        val clickListener = View.OnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_locationFragment)
        }

        val searchClickListener = View.OnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        val notificationClickListener = View.OnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }

        binding.icSearch.setOnClickListener(searchClickListener)
        binding.icNotification.setOnClickListener(notificationClickListener)
        binding.userLocation.setOnClickListener(clickListener)
        binding.userLocationIc.setOnClickListener(clickListener)

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
        clothesAdapter = ClothesAdapter(clothesList) { clickedItem ->
            val bundle = Bundle().apply {
                putParcelable("selectedClothes", clickedItem)
            }
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }
        binding.clothesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return 1
                }
            }
        }
        binding.clothesRecyclerView.adapter = clothesAdapter

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}