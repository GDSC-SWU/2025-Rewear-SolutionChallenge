package com.example.rewear.ui.reform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rewear.R
import com.example.rewear.databinding.FragmentReformItemsBinding
import com.google.android.material.button.MaterialButton
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ReformItemsFragment : Fragment() {
    private var _binding: FragmentReformItemsBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReformItemsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("title") ?: return

        fetchRecommendations(title)

        binding.BtnBack.setOnClickListener {

            findNavController().popBackStack()
        }

        binding.btnNext.setOnClickListener {
            val selectedButtonId = binding.toggleGroup.checkedButtonId

            if (selectedButtonId != View.NO_ID) {
                val selectedButton =
                    binding.toggleGroup.findViewById<MaterialButton>(selectedButtonId)
                val selectedText = selectedButton.text.toString()

                val bundle = Bundle().apply {
                    putString("selected_option", selectedText)
                }
                findNavController().navigate(
                    R.id.action_reformItemsFragment_to_reformAiFragment,
                    bundle
                )
            } else {
                Toast.makeText(requireContext(), "옵션을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val checkedButton = group.findViewById<MaterialButton>(checkedId)

            for (i in 0 until group.childCount) {
                val button = group.getChildAt(i) as MaterialButton
                button.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightgray_bar
                    )
                )
            }

            if (isChecked) {
                checkedButton.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main2_opacity
                    )
                )
            }
        }
    }

    private fun fetchRecommendations(title: String) {
        val url = "https://clothing-api-949882041921.us-central1.run.app/recommend"

        val json = JSONObject()
        json.put("clothing_info", title)

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(requestBody).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "서버 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return
                    try {
                        val jsonResponse = JSONObject(body)
                        val recommendations = jsonResponse.getJSONArray("recommendations")

                        val reform1 = recommendations.optString(0, "추천 없음 1")
                        val reform2 = recommendations.optString(1, "추천 없음 2")
                        val reform3 = recommendations.optString(2, "추천 없음 3")


                        requireActivity().runOnUiThread {
                            requireActivity().runOnUiThread {
                                binding.btnReformItem1.apply {
                                    text = reform1
                                    maxLines = 2
                                    ellipsize = null
                                }
                                binding.btnReformItem2.apply {
                                    text = reform2
                                    maxLines = 2
                                    ellipsize = null
                                }
                                binding.btnReformItem3.apply {
                                    text = reform3
                                    maxLines = 2
                                    ellipsize = null
                                }
                            }

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "응답 데이터 처리 오류", Toast.LENGTH_SHORT)
                                .show()

                        }
                    }
                }
            }
        })
    }
}

