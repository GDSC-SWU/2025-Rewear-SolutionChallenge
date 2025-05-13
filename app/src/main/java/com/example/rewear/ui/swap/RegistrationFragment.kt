package com.example.rewear.ui.swap

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
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

                    if (data?.clipData != null) {
                        val count = data.clipData!!.itemCount.coerceAtMost(MAX_SELECTION)
                        for (i in 0 until count) {
                            selectedImageUris.add(data.clipData!!.getItemAt(i).uri)
                        }
                    } else if (data?.data != null) {
                        selectedImageUris.add(data.data!!)
                    }

                    galleryAdapter.notifyDataSetChanged()
                    updateImageCounter()
                    isImageSelected = selectedImageUris.isNotEmpty()
                    updateSubmitButtonState()

                    if (selectedImageUris.isNotEmpty()) {
                        classifyClothingImage(selectedImageUris[0])
                        binding.aiBubble.visibility = View.GONE
                    }
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

        galleryAdapter = GalleryAdapter(selectedImageUris) {
            updateImageCounter()
        }
        binding.galleryRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = galleryAdapter
        }

        updateImageCounter()

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
            val newClothes = Clothes(
                imageList = listOf(R.drawable.cloth_example, R.drawable.cloth_example2),
                binding.categoryEditText.text.toString(),
                binding.titleEditText.text.toString(),
                binding.swapLocationText.text.toString(),
                "just now",
                0,
                binding.descriptionEditText.text.toString(),
                swapMethod = selectedSwapMethod ?: ""
            )


            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "newClothes", newClothes
            )
            findNavController().popBackStack()
        }

        updateSubmitButtonState()
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
            Log.e("File Error", "파일 변환 실패")
            hideLoadingDialog()
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
                    if (response.isSuccessful) {
                        val category = response.body()?.category ?: "Unknown"
                        binding.categoryEditText.setText(category)
                        isCategorySelected = true
                        updateSubmitButtonState()
                        binding.category.setBackgroundResource(R.drawable.category_bg_selected)
                    } else {
                        Log.e("AI Error", "분류 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                    hideLoadingDialog()
                    Log.e("API Failure", "통신 실패: ${t.message}")
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
            Log.e("File Error", "URI 변환 실패 : ${e.message}")
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
