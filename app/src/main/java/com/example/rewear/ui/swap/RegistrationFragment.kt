package com.example.rewear.ui.swap

import android.app.Activity
import android.app.AlertDialog
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
import com.example.rewear.ai.CategoryResponse
import com.example.rewear.ai.RetrofitClient
import com.example.rewear.databinding.FragmentRegistrationBinding
import com.example.rewear.ui.home.Clothes
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import com.example.rewear.network.CreateItemRequest
import com.example.rewear.network.ItemResponse
import com.example.rewear.network.RetrofitClientApp

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val selectedImageUris = mutableListOf<Uri>()
    private var selectedSwapMethod: String? = null
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private var isTitleFilled = false
    private var isCategorySelected = false
    private var isImageSelected = false
    private var hasAiProcessingStarted: Boolean = false
    private var lastProcessedCategory: String? = null

    private lateinit var loadingDialog: AlertDialog

    companion object {
        private const val MAX_SELECTION = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    selectedImageUris.clear()
                    hasAiProcessingStarted = false
                    lastProcessedCategory = null
                    isCategorySelected = false
                    if (_binding != null) {
                        binding.categoryEditText.text = null
                        binding.category.setBackgroundResource(R.drawable.category_bg)
                    }
                    if (data?.clipData != null) {
                        val count = data.clipData!!.itemCount.coerceAtMost(MAX_SELECTION)
                        for (i in 0 until count) {
                            selectedImageUris.add(data.clipData!!.getItemAt(i).uri)
                        }
                    } else if (data?.data != null) {
                        selectedImageUris.add(data.data!!)
                    }

                    if (_binding != null) {
                        galleryAdapter.notifyDataSetChanged()
                        updateImageCounter()
                    }
                    isImageSelected = selectedImageUris.isNotEmpty()


                    if (selectedImageUris.isNotEmpty()) {
                        classifyClothingImage(selectedImageUris[0])
                        if (_binding != null) {
                            binding.aiBubble.visibility = View.GONE
                        }
                        hasAiProcessingStarted = true
                    } else {

                        if (_binding != null) {
                            binding.aiBubble.visibility = View.VISIBLE
                        }
                    }
                    updateSubmitButtonState()

                }

            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (hasAiProcessingStarted && selectedImageUris.isNotEmpty()) {
            binding.aiBubble.visibility = View.GONE
        } else {
            binding.aiBubble.visibility = View.VISIBLE
            if (selectedImageUris.isEmpty()) {
                hasAiProcessingStarted = false
            }
        }

        galleryAdapter = GalleryAdapter(selectedImageUris) {
            updateImageCounter()
            if (selectedImageUris.isEmpty()) {
                isImageSelected = false
                hasAiProcessingStarted = false
                lastProcessedCategory = null
                isCategorySelected = false
                binding.categoryEditText.text = null
                binding.category.setBackgroundResource(R.drawable.category_bg)
                binding.aiBubble.visibility = View.VISIBLE
                updateSubmitButtonState()
            }
        }
        binding.galleryRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = galleryAdapter
        }


        if (isCategorySelected && !lastProcessedCategory.isNullOrEmpty()) {
            binding.categoryEditText.setText(lastProcessedCategory)
            binding.category.setBackgroundResource(R.drawable.category_bg_selected)
        } else {
            binding.categoryEditText.text = null
            binding.category.setBackgroundResource(R.drawable.category_bg)

        }
        if (selectedSwapMethod != null) {
            when (selectedSwapMethod) {
                "In-person Trade" -> {
                    binding.inPerson.setBackgroundResource(R.drawable.swap_method_selected)
                    binding.shipping.setBackgroundResource(R.drawable.swap_method)
                }

                "Shipping Trade" -> {
                    binding.shipping.setBackgroundResource(R.drawable.swap_method_selected)
                    binding.inPerson.setBackgroundResource(R.drawable.swap_method)
                }

                else -> {
                    binding.inPerson.setBackgroundResource(R.drawable.swap_method)
                    binding.shipping.setBackgroundResource(R.drawable.swap_method)
                }
            }
        } else {
            binding.inPerson.setBackgroundResource(R.drawable.swap_method)
            binding.shipping.setBackgroundResource(R.drawable.swap_method)
        }

        updateImageCounter()
        updateSubmitButtonState()


        binding.titleEditText.addTextChangedListener {
            isTitleFilled = it.toString().isNotEmpty()
            updateSubmitButtonState()
        }

        binding.swapLocationText.movementMethod = ScrollingMovementMethod.getInstance()

        binding.BtnClose.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_homeFragment)
        }

        binding.galleryView.setOnClickListener {
            openGallery()
        }

        binding.category.setOnClickListener {
            if (selectedImageUris.isEmpty()) {
                val toastMessage = layoutInflater.inflate(R.layout.swap_toast_message, null)
                val toastFinal = Toast(requireContext())
                toastFinal.duration = Toast.LENGTH_SHORT
                toastFinal.view = toastMessage
                toastFinal.show()
                return@setOnClickListener
            }

            val bottomSheetFragment = CategoryBottomSheetFragment { selectedCategoryName, _ ->
                binding.categoryEditText.text = selectedCategoryName
                lastProcessedCategory = selectedCategoryName
                isCategorySelected = selectedCategoryName.isNotBlank()
                updateSubmitButtonState()

                if (selectedCategoryName.isNotBlank()) {
                    binding.category.setBackgroundResource(R.drawable.category_bg_selected)
                } else {
                    binding.category.setBackgroundResource(R.drawable.category_bg)
                }
                updateSubmitButtonState()
            }

            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }

        binding.inPerson.setOnClickListener {
            binding.inPerson.setBackgroundResource(R.drawable.swap_method_selected)
            binding.shipping.setBackgroundResource(R.drawable.swap_method)
            selectedSwapMethod = "In-person Trade"
        }

        binding.shipping.setOnClickListener {
            binding.shipping.setBackgroundResource(R.drawable.swap_method_selected)
            binding.inPerson.setBackgroundResource(R.drawable.swap_method)
            selectedSwapMethod = "Shipping Trade"
        }

        val clickListener = View.OnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_locationFragment)
        }

        binding.swapAddLocation.setOnClickListener(clickListener)
        binding.swapLocationIcon.setOnClickListener(clickListener)

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("selectedAddress")
            ?.observe(viewLifecycleOwner) { address ->
                binding.swapLocationText.text = address
            }

        binding.BtnSubmit.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val selectedCategoryString = lastProcessedCategory ?: ""
            val description = binding.descriptionEditText.text.toString().trim()
            val swapMethod = selectedSwapMethod
            val locationFromForm = binding.swapLocationText.text.toString()
            val imageUrlsToSend = listOf(
                "https://picsum.photos/id/1/400/300",
                "https://picsum.photos/id/2/400/300"
            )
            if (locationFromForm.isBlank()) {
                Toast.makeText(requireContext(), "Enter the address.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (selectedImageUris.isEmpty()) {
                Toast.makeText(requireContext(), "Select the image", Toast.LENGTH_SHORT).show()
                isImageSelected = false
                updateSubmitButtonState()
                return@setOnClickListener
            }

            if (title.isEmpty()) {
                binding.titleEditText.error = "Enter the title"
                return@setOnClickListener
            }
            if (selectedCategoryString.isBlank()) {
                Toast.makeText(requireContext(), "Select Category.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val createItemRequest = CreateItemRequest(
                title = title,
                category = selectedCategoryString,
                description = description.ifEmpty { null },
                swapMethod = swapMethod,
                imageUrls = imageUrlsToSend
            )

            RetrofitClientApp.apiService.createItem(
                address = locationFromForm,
                itemRequest = createItemRequest
            )
                .enqueue(object : Callback<ItemResponse> {
                    override fun onResponse(
                        call: Call<ItemResponse>,
                        response: Response<ItemResponse>
                    ) {
                        hideLoadingDialog()
                        if (response.isSuccessful && response.code() == 201) {
                            val newItemFromServer = response.body()
                            if (newItemFromServer != null) {
                                val imageUrisForUiTest = if (selectedImageUris.isNotEmpty()) {
                                    selectedImageUris.map { uri -> uri.toString() }
                                } else {
                                    newItemFromServer.imageUrls
                                }
                                val clothesForHome = Clothes(
                                    id = newItemFromServer.id.toString(),
                                    imageList = imageUrisForUiTest,
                                    label = newItemFromServer.category,
                                    name = newItemFromServer.title,
                                    location = locationFromForm,
                                    timeAgo = "1m ago",
                                    likeCount = 0,
                                    description = newItemFromServer.description ?: "",
                                    swapMethod = newItemFromServer.swapMethod ?: selectedSwapMethod
                                    ?: ""
                                )

                                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                    "newClothes", clothesForHome
                                )
                                findNavController().popBackStack()

                            }
                        } else {
                            val errorBody = response.errorBody()?.string() ?: "Unknown error"
                            Toast.makeText(
                                requireContext(),
                                "Fail to register an item. (CODE: ${response.code()})",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ItemResponse>, t: Throwable) {
                        hideLoadingDialog()
                        Toast.makeText(
                            requireContext(),
                            "Network error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
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

    private fun showLoadingDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.ai_loading_dialog, null)
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()
        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.white)
        loadingDialog.show()
    }

    private fun hideLoadingDialog() {
        if (::loadingDialog.isInitialized && loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    private fun classifyClothingImage(uri: Uri) {
        showLoadingDialog()

        val file = getFileFromUri(uri)
        if (file == null) {
            hideLoadingDialog()

            hasAiProcessingStarted = false
            lastProcessedCategory = null
            isCategorySelected = false
            if (_binding != null) {
                binding.categoryEditText.text = null
                binding.category.setBackgroundResource(R.drawable.category_bg)
                binding.aiBubble.visibility = View.VISIBLE
                updateSubmitButtonState()
            }
            return
        }

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        RetrofitClient.apiService.classifyClothing(body)
            .enqueue(object : Callback<CategoryResponse> {
                override fun onResponse(
                    call: Call<CategoryResponse>,
                    response: Response<CategoryResponse>
                ) {
                    hideLoadingDialog()
                    if (_binding == null) return

                    if (response.isSuccessful) {
                        val categoryFromAI = response.body()?.category ?: "Unknown"
                        lastProcessedCategory = categoryFromAI
                        binding.categoryEditText.setText(lastProcessedCategory)
                        isCategorySelected = true
                        updateSubmitButtonState()
                        binding.category.setBackgroundResource(R.drawable.category_bg_selected)
                        lastProcessedCategory = null
                        binding.categoryEditText.text = null
                        isCategorySelected = false
                        updateSubmitButtonState()
                        binding.category.setBackgroundResource(R.drawable.category_bg)
                    }
                }

                override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                    hideLoadingDialog()
                    if (_binding == null) return

                    lastProcessedCategory = null
                    binding.categoryEditText.text = null
                    isCategorySelected = false
                    updateSubmitButtonState()
                    binding.category.setBackgroundResource(R.drawable.category_bg)
                }
            })
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload", ".jpg", requireContext().cacheDir)
            tempFile.outputStream().use { output -> inputStream.copyTo(output) }
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
