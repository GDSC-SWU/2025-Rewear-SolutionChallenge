package com.example.rewear.ui.reform

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.util.Base64
import com.example.rewear.R
import com.example.rewear.databinding.FragmentReformAiBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ReformAiFragment : Fragment() {
    private var _binding: FragmentReformAiBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReformAiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val originalItemTitle = arguments?.getString("originalItemTitle") ?: ""
        val originalItemImageResId = arguments?.getInt("originalItemImageResId", 0)

        val selectedOption = arguments?.getString("selected_option")

        selectedOption?.let {
            generatePreviewImage(it)
        }

        binding.BtnBack.setOnClickListener {

            findNavController().popBackStack()
        }

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_reformAiFragment_to_pickPartnerFragment)

        }

    }

    private fun generatePreviewImage(option: String) {
        val url = "https://clothing-api-949882041921.us-central1.run.app/generate-preview"
        val json = JSONObject().apply {
            put("option", option)
        }

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() ?: return
                try {
                    val json = JSONObject(responseBody)

                    if (json.has("image_base64")) {
                        val base64Image = json.getString("image_base64")

                        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
                        val bitmap =
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)


                        requireActivity().runOnUiThread {
                            binding.previewImageView.setImageBitmap(bitmap)
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
