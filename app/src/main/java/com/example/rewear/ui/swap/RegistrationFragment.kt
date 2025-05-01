package com.example.rewear.ui.swap

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.databinding.FragmentRegistrationBinding
import java.util.ArrayList
import com.example.rewear.R


class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

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
                    val imageUris= mutableListOf<Uri>()

                    if (data?.clipData != null) {
                        val count = data.clipData!!.itemCount.coerceAtMost(MAX_SELECTION)
                        for (i in 0 until count) {
                            imageUris.add(data.clipData!!.getItemAt(i).uri)
                        }

                    } else if (data?.data != null) {
                        imageUris.add(data.data!!)
                    }

                    val bundle=Bundle().apply{
                        putParcelableArrayList("image_uris", ArrayList(imageUris))
                    }

                    findNavController().navigate(R.id.action_registrationFragment_to_aiCategoryProcessingFragment,bundle)
                    galleryAdapter.notifyDataSetChanged()
                    updateImageCounter()

                    binding.aiBubble.visibility=View.GONE
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
            val bottomSheetFragment = CategoryBottomSheetFragment() { selectedCategoryName,selectedCategoryId ->
                binding.categoryEditText.text = selectedCategoryName
            }
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

