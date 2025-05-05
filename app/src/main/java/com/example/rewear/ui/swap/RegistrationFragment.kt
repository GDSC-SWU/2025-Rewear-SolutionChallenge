package com.example.rewear.ui.swap

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.R
import com.example.rewear.databinding.FragmentRegistrationBinding
import com.example.rewear.ui.home.Clothes

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val selectedImageUris = mutableListOf<Uri>()
    private var selectedSwapMethod:String?=null
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private var isTitleFilled = false
    private var isCategorySelected = false
    private var isImageSelected = false

    companion object {
        private const val REQUEST_CODE_PICK_IMAGES = 1001
        private const val MAX_SELECTION = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val imageUris = mutableListOf<Uri>()
                    selectedImageUris.clear()
                    if (data?.clipData != null) {
                        val count = data.clipData!!.itemCount.coerceAtMost(MAX_SELECTION)
                        for (i in 0 until count) {
                            selectedImageUris.add(data.clipData!!.getItemAt(i).uri)
                        }

                    } else if (data?.data != null) {
                        selectedImageUris.add(data.data!!)
                    }

                    val bundle = Bundle().apply {
                        putParcelableArrayList("image_uris", ArrayList(selectedImageUris))
                    }

                    findNavController().navigate(
                        R.id.action_registrationFragment_to_aiCategoryProcessingFragment,
                        bundle
                    )


                    galleryAdapter.notifyDataSetChanged()
                    updateImageCounter()
                    binding.aiBubble.visibility = View.GONE

                    isImageSelected = selectedImageUris.isNotEmpty()
                    updateSubmitButtonState()
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        navController.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("selectedAddress")
            ?.observe(viewLifecycleOwner) { address ->
                binding.swapLocationText.text = address
            }

        binding.titleEditText.addTextChangedListener {
            isTitleFilled = it.toString().isNotEmpty()
            updateSubmitButtonState()
        }

        binding.swapLocationText.apply {
            movementMethod = ScrollingMovementMethod.getInstance()

        }

        if (selectedImageUris.isNotEmpty()) {
            binding.aiBubble.visibility = View.GONE
        }
        binding.BtnClose.setOnClickListener {

            findNavController().popBackStack()
        }
        binding.galleryView.setOnClickListener {
            openGallery()
        }
        galleryAdapter = GalleryAdapter(selectedImageUris) {
            updateImageCounter()
        }
        binding.galleryRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = galleryAdapter
        }
        updateImageCounter()

        binding.category.setOnClickListener {
            if (selectedImageUris.isEmpty()) {
                val toastMessage = layoutInflater.inflate(R.layout.swap_toast_message, null)
                val toastFinal = Toast(requireContext())
                toastFinal.duration = Toast.LENGTH_SHORT
                toastFinal.view = toastMessage
                toastFinal.show()
                return@setOnClickListener

            }
            val bottomSheetFragment =
                CategoryBottomSheetFragment() { selectedCategoryName, selectedCategoryId ->
                    binding.categoryEditText.text = selectedCategoryName

                    isCategorySelected = selectedCategoryName.isNotBlank()
                    updateSubmitButtonState()

                    if (selectedCategoryName.isNotBlank()) {
                        binding.category.setBackgroundResource(R.drawable.category_bg_selected)
                    } else {
                        binding.category.setBackgroundResource(R.drawable.category_bg)
                    }
                }
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }

        binding.inPerson.setOnClickListener {
            binding.inPerson.setBackgroundResource(R.drawable.swap_method_selected)
            binding.shipping.setBackgroundResource(R.drawable.swap_method)
            selectedSwapMethod="In-person Trade"
        }

        binding.shipping.setOnClickListener {
            binding.shipping.setBackgroundResource(R.drawable.swap_method_selected)
            binding.shipping.setBackgroundResource(R.drawable.swap_method)
            selectedSwapMethod="Shipping Trade"
        }

        val clickListener = View.OnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_locationFragment)
        }
        binding.swapAddLocation.setOnClickListener(clickListener)
        binding.swapLocationIcon.setOnClickListener(clickListener)

        binding.BtnSubmit.setOnClickListener {
            val newClothes = Clothes(
                imageList = listOf(
                    R.drawable.cloth_example,
                    R.drawable.cloth_example2
                ),
                binding.categoryEditText.text.toString(),
                binding.titleEditText.text.toString(),
                binding.swapLocationText.text.toString(),
                "just now",
                0,
                binding.descriptionEditText.text.toString(),
                swapMethod = selectedSwapMethod?:""
            )
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "newClothes",
                newClothes
            )

            findNavController().popBackStack()
        }

    }

    private fun openGallery() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                type = "image/*"
            }
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Pictures"))
    }


    private fun updateImageCounter() {
        val count = selectedImageUris.size
        binding.imageCounter.text = "$count/$MAX_SELECTION"
    }

    private fun updateSubmitButtonState() {
        val isReady = isTitleFilled && isCategorySelected && isImageSelected
        binding.BtnSubmit.isEnabled = isReady
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

