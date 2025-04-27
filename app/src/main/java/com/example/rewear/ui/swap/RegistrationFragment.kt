package com.example.rewear.ui.swap

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var galleryAdapter: GalleryAdapter

    companion object {
        private const val REQUEST_CODE_PICK_IMAGES = 1001
        private const val MAX_SELECTION = 5
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
            checkAIBubbleVisibility()
        }
        binding.galleryRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = galleryAdapter
        }
        updateImageCounter()
    }

    private fun openGallery() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                type = "image/*"
            }
        startActivityForResult(
            Intent.createChooser(intent, "Select Pictures"),
            REQUEST_CODE_PICK_IMAGES
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            data?.let {
                selectedImageUris.clear()

                if (it.clipData != null) {
                    val count = it.clipData!!.itemCount.coerceAtMost(MAX_SELECTION)
                    for (i in 0 until count) {
                        val imageUri = it.clipData!!.getItemAt(i).uri
                        selectedImageUris.add(imageUri)
                    }
                } else if (it.data != null) {
                    selectedImageUris.add(it.data!!)
                }
                galleryAdapter.notifyDataSetChanged()
                updateImageCounter()
                checkAIBubbleVisibility()
            }
        }
    }

    private fun updateImageCounter() {
        val count = selectedImageUris.size
        binding.imageCounter.text = "$count/$MAX_SELECTION"
    }

    private fun checkAIBubbleVisibility() {
        binding.aiBubble.visibility =
            if (selectedImageUris.isNotEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
