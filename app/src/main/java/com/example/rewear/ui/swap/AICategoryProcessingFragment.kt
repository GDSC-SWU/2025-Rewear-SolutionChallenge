package com.example.rewear.ui.swap

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rewear.R
import com.example.rewear.ai.CategoryResponse
import com.example.rewear.ai.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AICategoryProcessingFragment : Fragment() {

    private lateinit var selectedUris: List<Uri>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ai_category_processing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val uriList = requireArguments().getParcelableArrayList<Uri>("image_uris")
            if (uriList.isNullOrEmpty()) {
                Log.e("AI Error", "이미지가 전달되지 않았습니다.")
                findNavController().popBackStack()
                return
            }
            selectedUris = uriList
            classifyClothingImage(uriList[0])
        } catch (e: Exception) {
            Log.e("Bundle Error", "Uri 전달 중 오류 발생 : ${e.message}")
            findNavController().popBackStack()
        }
    }

    private fun classifyClothingImage(uri: Uri) {
        val file = getFileFromUri(uri)
        if (file == null) {
            Log.e("File Error", "파일 변환 실패")
            findNavController().popBackStack()
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
                    if (response.isSuccessful) {
                        val category = response.body()?.category ?: "Unknown"
                        val bundle = Bundle().apply {
                            putString("ai_result_category", category)
                            putParcelableArrayList("image_uris", ArrayList(selectedUris))
                        }
                        findNavController().navigate(
                            R.id.action_aiCategoryProcessingFragment_to_registrationFragment,
                            bundle
                        )
                    } else {
                        Log.e("AI Error", "Failed to classify clothing")
                        Log.e(
                            "AI Error",
                            "Code: ${response.code()}, Message: ${response.message()}"
                        )
                        Log.e("AI Error", "Error body: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                    Log.e("API Failure", "통신실패:${t.message}")
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
}