package com.example.rewear.ui.reform

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rewear.R
import com.example.rewear.databinding.ItemReformListBinding
import com.example.rewear.ui.reform.ReformFragment.ReformStatus

data class ReformItem(
    val id: Int,
    val title: String,
    val status: ReformFragment.ReformStatus,
    val imageUrl: String?,
    val corpName: String,
    val originalImageResId: Int? = null
)

class ReformAdapter : RecyclerView.Adapter<ReformAdapter.ReformViewHolder>() {

    private var allItems: List<ReformItem> = listOf()
    private var filteredItems: List<ReformItem> = listOf()

    // submitList 파라미터 타입을 List<ReformItem>으로 명확히 함
    fun submitList(items: List<ReformItem>) {
        allItems = items
        filteredItems = items // 초기에는 필터링되지 않은 전체 목록을 보여줌
        notifyDataSetChanged() // 데이터 변경 알림 (ListAdapter 사용 고려 가능)
    }

    // 필터링 함수는 그대로 유지 (필요시 status 타입을 ReformFragment.ReformStatus로 명시)
    fun filterByStatus(statusToFilter: ReformFragment.ReformStatus?) {
        filteredItems = if (statusToFilter == null) {
            allItems // status가 null이면 전체 목록
        } else {
            allItems.filter { it.status == statusToFilter }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReformAdapter.ReformViewHolder {
        val binding =
            ItemReformListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReformViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReformAdapter.ReformViewHolder, position: Int) {
        if (position < filteredItems.size) { // 리스트 범위 확인
            holder.bind(filteredItems[position])
        }
    }

    override fun getItemCount(): Int = filteredItems.size

    inner class ReformViewHolder(private val binding: ItemReformListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReformItem) {
            binding.clothingName.text = item.title

            // 상태에 따른 진행 상태 이미지 설정
            val statusImageRes = when (item.status) {
                ReformStatus.RECEIVED -> R.drawable.reform_received
                ReformStatus.IN_PROGRESS -> R.drawable.reform_inprogress
                ReformStatus.COMPLETED -> R.drawable.reform_completed
            }
            binding.reformProgress.setImageResource(statusImageRes)

            // *** 이미지 로딩 로직 수정 ***
            if (!item.imageUrl.isNullOrEmpty()) {
                // 1. imageUrl이 있으면 Glide로 웹 이미지 로드
                Glide.with(binding.clothing.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.cloth_example)
                    .into(binding.clothing)
            } else if (item.originalImageResId != null && item.originalImageResId != 0) {
                // 2. imageUrl이 없고 originalImageResId가 있으면 로컬 드로어블 로드
                binding.clothing.setImageResource(item.originalImageResId)
            } else {
                // 3. 둘 다 없으면 기본 플레이스홀더 이미지 설정
                binding.clothing.setImageResource(R.drawable.cloth_example)
            }

            // 업체명 설정 (ReformItem의 corpName이 non-null String이므로 ?: "알 수 없음" 불필요)
            binding.corpName.text = item.corpName
            // 만약 corpName이 비어있을 수 있는 경우를 대비하려면:
            // binding.corpName.text = if (item.corpName.isNotEmpty()) item.corpName else "정보 없음"
        }
    }
}