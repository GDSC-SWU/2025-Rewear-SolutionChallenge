package com.example.rewear.ui.reform

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.rewear.databinding.FragmentReformBinding
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.R
import com.example.rewear.network.ApiReformItem
import com.example.rewear.network.RetrofitClientApp
import kotlinx.coroutines.launch
import java.io.IOException


class ReformFragment : Fragment() {
    enum class ReformStatus {
        RECEIVED, IN_PROGRESS, COMPLETED;


        companion object {
            fun fromApiString(apiStatus: String): ReformStatus {
                return when (apiStatus.uppercase()) {
                    "PENDING" -> RECEIVED
                    "IN_PROGRESS" -> IN_PROGRESS
                    "COMPLETED" -> COMPLETED
                    else -> RECEIVED
                }
            }
        }
    }


    private var _binding: FragmentReformBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReformAdapter
    private var currentDisplayedItems: List<ReformItem> = listOf() // 현재 화면에 표시된 아이템 목록 (allFetchedItems 대신 사용)

    // userItemFromArgs와 hardcodedDummyItems는 클래스 멤버 변수로 두어 재사용 용이하게 할 수 있습니다.
    private var userItemFromArgs: ReformItem? = null
    private val hardcodedDummyItems: List<ReformItem> by lazy { // 한 번만 초기화되도록 lazy 사용
        listOf(
            ReformItem(-1, "Layering T-shirt", ReformStatus.RECEIVED, null, "더미 업체 A", R.drawable.cloth_example),
            ReformItem(-2, "Letter Open Hoodie", ReformStatus.IN_PROGRESS, null, "더미 업체 B", R.drawable.cloth_example2),
            ReformItem(-3, "Ballon pants", ReformStatus.IN_PROGRESS, null, "더미 업체 C", R.drawable.cloth_example),
            ReformItem(-4, "Check mini skirt", ReformStatus.COMPLETED, null, "더미 업체 D", R.drawable.cloth_example2)
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReformBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageResIdFromArgs = arguments?.getInt("imageResId") ?: 0
        val partnerNameFromArgs = arguments?.getString("partnerName") ?: ""
        if (partnerNameFromArgs.isNotEmpty()) {
            userItemFromArgs = ReformItem(
                0, "new", ReformStatus.RECEIVED, null, partnerNameFromArgs,
                if (imageResIdFromArgs != 0) imageResIdFromArgs else null
            )
        }


        setupRecyclerView()
        setupFilterButtons()
        fetchReformItems(apiStatusQuery = null, localFilterStatus = ReformStatus.RECEIVED, isForAllButton = true)
    }
    private fun setupRecyclerView() {

        adapter = ReformAdapter()
        binding.reformSwapRecyclerView.adapter = adapter
        binding.reformSwapRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.reform_border)?.let {
            divider.setDrawable(it)
        }
        binding.reformSwapRecyclerView.addItemDecoration(divider)
    }

    private fun fetchReformItems(apiStatusQuery: String?, localFilterStatus: ReformStatus?, isForAllButton: Boolean = false) {
        Log.d("ReformFragment", "fetchReformItems 호출: apiStatusQuery=$apiStatusQuery, localFilterStatus=$localFilterStatus, isForAllButton=$isForAllButton")
        viewLifecycleOwner.lifecycleScope.launch {
            val mappedItemsFromApi = mutableListOf<ReformItem>()

            // --- 섹션 1: API 호출 ---
            try {
                val response = RetrofitClientApp.reformApiService.getReformItems(status = apiStatusQuery) // 수정된 서비스 메소드 호출
                if (response.isSuccessful) {
                    response.body()?.let { mappedItemsFromApi.addAll(mapApiToUiModel(it)) }
                    Log.d("ReformFragment", "API 성공: ${mappedItemsFromApi.size}개 아이템 (쿼리: $apiStatusQuery)")
                } else {
                    val errorBodyString = try { response.errorBody()?.string() } catch (e: Exception) { null }
                    Log.e("ReformFragment", "API 오류 ${response.code()}: $errorBodyString (쿼리: $apiStatusQuery)")
                    Toast.makeText(requireContext(), "서버 오류 (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e("ReformFragment", "네트워크 오류 (IOException)", e)
                Toast.makeText(requireContext(), "네트워크 연결 오류", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("ReformFragment", "기타 오류: ${e.javaClass.simpleName}", e)
                Toast.makeText(requireContext(), "데이터 로딩 중 오류", Toast.LENGTH_LONG).show()
            }

            // --- 섹션 2: 로컬/더미 데이터 필터링 및 최종 목록 조합 ---
            val finalCombinedList = mutableListOf<ReformItem>()

            // 1. userItemFromArgs 추가 (조건에 맞게)
            userItemFromArgs?.let {
                if (isForAllButton || it.status == localFilterStatus) { // '전체' 버튼이거나 상태가 일치하면 추가
                    finalCombinedList.add(it)
                }
            }

            // 2. hardcodedDummyItems 추가 (조건에 맞게)
            val filteredHardcodedDummies = if (isForAllButton) {
                hardcodedDummyItems // '전체' 버튼이면 모든 하드코딩 더미 추가
            } else {
                hardcodedDummyItems.filter { it.status == localFilterStatus } // 아니면 상태가 일치하는 것만 추가
            }
            finalCombinedList.addAll(filteredHardcodedDummies)

            // 3. API에서 가져온 아이템 추가
            finalCombinedList.addAll(mappedItemsFromApi)

            // 중복 제거 (ID 기준, API 데이터가 우선순위를 가질 수 있도록 API 데이터를 나중에 추가하고, 그 전에 더미 추가)
            // 또는, 더미 데이터와 API 데이터 간 ID 충돌이 없다고 가정하거나, API 데이터만 표시하고 더미는 API 없을때만 표시하는 등 전략 필요.
            // 현재는 단순 합치기. 필요 시 ID 기반으로 중복 제거 로직 추가 가능.
            // 예: val distinctList = finalCombinedList.distinctBy { it.id }

            currentDisplayedItems = finalCombinedList.distinctBy { it.id } // ID 기준 중복 제거 예시


            Log.d("ReformFragment", "최종 리스트 크기: ${currentDisplayedItems.size}")

            // 빈 목록 UI 처리
            if (currentDisplayedItems.isEmpty()) {
                binding.reformSwapRecyclerView.visibility = View.GONE
                binding.reformedEmpty.visibility = View.VISIBLE
            } else {
                binding.reformSwapRecyclerView.visibility = View.VISIBLE
                binding.reformedEmpty.visibility = View.GONE
            }

            adapter.submitList(currentDisplayedItems)
            // updateFilterButtons는 setupFilterButtons에서 현재 선택된 버튼을 시각적으로 업데이트하기 위함이므로,
            // 여기서 직접 호출하기보다, 버튼 클릭 리스너에서 fetch 후 updateFilterButtons를 호출하는 것이 더 명확할 수 있음.
            // 하지만 현재 선택된 필터에 대한 데이터를 로드했으므로, 해당 버튼이 시각적으로 활성화되도록 유지하는 것은 좋음.
            // updateFilterButtons는 선택된 버튼을 인자로 받으므로, 어떤 버튼이 현재 이 fetch를 유발했는지 알아야 함.
            // 이 부분은 setupFilterButtons에서 처리하는 것이 더 적합.
        }
    }
    private fun mapApiToUiModel(apiItems: List<ApiReformItem>): List<ReformItem> {
        return apiItems.map { apiItem ->
            ReformItem(
                id = apiItem.id,
                title = apiItem.title,
                status = ReformStatus.fromApiString(apiItem.reformStatus),
                imageUrl = apiItem.imageUrls.firstOrNull(),
                corpName = apiItem.partnerName,
                originalImageResId = null
            )
        }
    }

    private fun setupFilterButtons() {
        updateFilterButtons(binding.btnAll) // 초기 선택 버튼 설정

        binding.btnAll.setOnClickListener {
            updateFilterButtons(binding.btnAll) // UI 즉시 업데이트
            // '전체'는 API로 PENDING (status=null 또는 "PENDING") + 로컬 더미는 RECEIVED 상태 + 모든 하드코딩 더미
            fetchReformItems(apiStatusQuery = null, localFilterStatus = ReformStatus.RECEIVED, isForAllButton = true)
        }
        binding.btnInProgress.setOnClickListener {
            updateFilterButtons(binding.btnInProgress)
            fetchReformItems(apiStatusQuery = "IN_PROGRESS", localFilterStatus = ReformStatus.IN_PROGRESS)
        }
        binding.btnCompleted.setOnClickListener {
            updateFilterButtons(binding.btnCompleted)
            fetchReformItems(apiStatusQuery = "COMPLETED", localFilterStatus = ReformStatus.COMPLETED)
        }
    }

    private fun updateFilterButtons(selectedButton: Button) {
        listOf(binding.btnAll, binding.btnInProgress, binding.btnCompleted).forEach { button ->
            button.isSelected = (button == selectedButton) // 이 상태 변경으로 XML의 StateListDrawable이 배경/텍스트색 자동 변경
            val fontId =
                if (button.isSelected) R.font.pretendard_semibold else R.font.pretendard_regular
            button.typeface = ResourcesCompat.getFont(requireContext(), fontId)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}